package com.amberg.supertimer;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * MainFragment contains the code to display the main portion of SuperTimer as a fragment.
 * It is where the timer will actually be stopped, started, and watched.
 *  For the future, to properly capture time with varying objects, like if users
 *  will eventually be able to program their own timers, you'll want to split the works
 *  and rests into individual objects of some sort. Those objects will know their own
 *  durations, and this along with the millis_remaining variable, can tell the clock what to put
 *  on the display. For example, imagine a millis_remaining of 76sec encounters an interval
 *  object with a duration of 10sec. Using a list like mTimerEvent, the timer will know that
 *  at 76sec, it needs to go to the interval object. While it is using this interval object,
 *  the timer will always subtract 66sec (the difference of 76sec and 10sec) from its value.
 *  Once millis_remaining is 66sec, it is done with the interval object and can move on to the
 *  next object, be it rest, or whatever. One more thing: to check whether it needs to move onto
 *  the next object, it checks whether it is greater than the first item in the array mTimerEvent.
 *  When it's done, it deletes it from the array and goes to the next object until done.
 */

public class MainFragment extends Fragment {
    private static final String TAG = "MainActivity";
    private EditText mTextWork, mTextRest, mTextSets;
    private Button mStartButton, mCancelButton;
    private TextView mClockText, mCurrentSetText;
    private LinearLayout mSettingsBox;
    private SoundPool mSP;
    private int mSoundID = -1; //trying to use this to have sounds only play once.
    //the above seems like a dumb hack.
    private int mSoundWhistle, mSoundCheer, mSoundRest;
    /* Implementing CountDownTimer within the activity, but at some
    * point it might be good to see how to encapsulate it into its own class file */
    public class SuperTimer extends CountDownTimer {
        private long mWork, mRest, mInitialSecs;
        private boolean isRest = false;
        private int mSets, mCurrentSet;
        private ArrayList<Long> mTimerEvents = new ArrayList<>();

        SuperTimer(long work, long rest, int sets) {
            /* TODO: Error Checking */
            super((sets * (work+rest) - rest) * 1000, 500);
            mInitialSecs = (sets * (work+rest) - rest);
            mWork = work;
            mRest = rest;
            mSets = sets;
            mCurrentSet = 1; //this will change in a later version

            /* mTimerEvents should store the milliseconds remaining on
            the clock for every event. */
            for (int i = 0; i < mSets; i++) {
                long endSet = mInitialSecs - (i+1)*(mWork) - i*mRest;
                long endRest = mInitialSecs - (i+1)*(mWork) - (i+1)*mRest;
                mTimerEvents.add(endSet);

                //The event list ends at zero. Don't add negative time events.
                if (endRest < 0)
                    break;
                mTimerEvents.add(endRest);
            }
        }

        @Override
        public void onTick(long millis_remaining) {
            long displayTime;
            long nextEvent = mTimerEvents.get(0);

            displayTime = displayTime(millis_remaining, nextEvent);
            if (displayTime == 0) {
                if (isRest) { //we're at the end of rest period
                    if (mSoundID != mSoundWhistle) {
                        mSP.play(mSoundWhistle, 1f, 1f, 1, 0, 1f);
                        Log.d(TAG, "Playing Whistle!");
                        mSoundID = mSoundWhistle;
                        mCurrentSet += 1;
                        mCurrentSetText.setText("Set: " + Integer.toString(mCurrentSet)
                                + " of " + Integer.toString(mSets));
                    }
                } else { //we're at the end of the work period
                    if (mSoundID != mSoundRest) {
                        mSP.play(mSoundRest, 1f, 1f, 1, 0, 1f);
                        Log.d(TAG, "Playing Beep Beep!");
                        mSoundID = mSoundRest;
                    }
                }
                mTimerEvents.remove(0);
                nextEvent = mTimerEvents.get(0);
                displayTime = displayTime(millis_remaining, nextEvent);
                isRest = !isRest;
            }

            mClockText.setText(formatTimeString(displayTime));
        }

        @Override
        public void onFinish() {
            Toast.makeText(getActivity(), "Timer Done!", Toast.LENGTH_SHORT).show();
            mSP.play(mSoundCheer, 1f, 1f, 0, 0, 1f);
            reset();
        }

        private long displayTime(long millis_remaining, long secs_end) {
            return (millis_remaining / 1000) + 1 - secs_end;
        }
    }

    private SuperTimer st;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* construct/initialize objects here */
        //Prepare SoundPool to play sounds
        mSP = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSP.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

            }
        });
        //prepare sounds
        mSoundWhistle = mSP.load(getActivity(), R.raw.referee_whistle, 1);
        mSoundCheer = mSP.load(getActivity(), R.raw.ovation, 1);
        mSoundRest = mSP.load(getActivity(), R.raw.rest, 1);
    }

    /* The following method inflates a view and needs to be overridden for a fragment */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, parent, false);

        /* Wire up view objects to the view */
        mClockText = (TextView)v.findViewById(R.id.text_clock);
        mCurrentSetText = (TextView)v.findViewById(R.id.text_currentSet);
        mStartButton = (Button)v.findViewById(R.id.button_start);
        mCancelButton = (Button)v.findViewById(R.id.button_cancel);
        mCancelButton.setEnabled(false);
        mTextWork = (EditText)v.findViewById(R.id.text_work);
        mTextRest = (EditText)v.findViewById(R.id.text_rest);
        mTextSets = (EditText)v.findViewById(R.id.text_sets);
        mSettingsBox = (LinearLayout)v.findViewById(R.id.settingsBox);

        //set listeners
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Start button clicked");
                int work, rest, sets;
                work = Integer.parseInt(mTextWork.getText().toString());
                rest = Integer.parseInt(mTextRest.getText().toString());
                sets = Integer.parseInt(mTextSets.getText().toString());
                mClockText.setText(formatTimeString(work));
                /* Display that we're on Set 1 of X sets. That number 1 is hard coded, but
                eventually will need to be changed to reflect whether we're on a work set
                 */
                mCurrentSetText.setText("Set: 1 of " + mTextSets.getText());
                mCurrentSetText.setVisibility(View.VISIBLE);
                mStartButton.setEnabled(false);
                mCancelButton.setEnabled(true);
                mSettingsBox.setVisibility(View.INVISIBLE);
                st = new SuperTimer(work, rest, sets);
                st.start();
                mSP.play(mSoundWhistle, 1f, 1f, 1, 0, 1f);
                Log.d(TAG, "Playing Whistle!");
                mSoundID = mSoundWhistle;
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel button clicked");
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
                reset();
                st.cancel();
            }
        });

        return v;
    }

    private void reset() {
        mClockText.setText(R.string.text_clock);
        mCurrentSetText.setVisibility(View.GONE);
        mStartButton.setEnabled(true);
        mCancelButton.setEnabled(false);
        mTextWork.setEnabled(true);
        mTextRest.setEnabled(true);
        mTextSets.setEnabled(true);
        mSoundID = -1;
        mSettingsBox.setVisibility(View.VISIBLE);
    }

    private String formatTimeString(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
