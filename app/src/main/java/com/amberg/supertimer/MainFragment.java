package com.amberg.supertimer;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
    private TextView mClockText, mCurrentSetText, mSetType;
    private LinearLayout mSettingsBox;
    private SoundPool mSP;
    private int mSoundID = -1; //trying to use this to have sounds only play once.
    //the above seems like a dumb hack.
    private int mSoundWhistle, mSoundCheer, mSoundRest;
    private TimerEventStore mTimerEventStore = new TimerEventStore();

    /* Implementing CountDownTimer within the activity, but at some
    * point it might be good to see how to encapsulate it into its own class file */
    public class SuperTimer extends CountDownTimer {
        private long mWork, mRest;
        private boolean isRest = false;
        private int mSets, mCurrentSet;
        private ArrayList<Long> mTimerEvents = new ArrayList<>();

        SuperTimer(long work, long rest, int sets) {
            /* TODO: Error Checking */
            super((sets * (work+rest) - rest) * 1000, 500);
            mWork = work;
            mRest = rest;
            mSets = sets;
            mCurrentSet = 1; //this will change in a later version

            for (int i = 0; i < mSets; i++) {
                TimerEvent workEvent = new TimerEvent(mWork, TimerEvent.Type.WORK);
                mTimerEventStore.add(workEvent);
                TimerEvent restEvent = new TimerEvent(mRest, TimerEvent.Type.REST);
                mTimerEventStore.add(restEvent);
            }
            //remove last rest event
            mTimerEventStore.remove(mTimerEventStore.size() - 1);
        }

        @Override
        public void onTick(long millis_remaining) {
            long displayTime;
            TimerEvent currentEvent = mTimerEventStore.currentEvent();
            long eventEnd = (millis_remaining / 1000) + 1 - currentEvent.getDuration();
            //long nextEvent = mTimerEvents.get(0);

            displayTime = (millis_remaining / 1000) + 1 - eventEnd;

            if (displayTime == 0) {
                //remove the current event from the list since we're done and get the next
                mTimerEventStore.remove(0);
                currentEvent = mTimerEventStore.currentEvent();

                if (currentEvent.getType() == TimerEvent.Type.WORK) {
                    if (mSoundID != mSoundWhistle) {
                        mSP.play(mSoundWhistle, 1f, 1f, 1, 0, 1f);
                        Log.d(TAG, "Playing Whistle Sound!");
                        mSoundID = mSoundWhistle;
                        mSetType.setText("WORK");
                        mSetType.setTextColor(Color.parseColor("#ff0000"));
                        mCurrentSet += 1;
                        mCurrentSetText.setText("Set: " + Integer.toString(mCurrentSet)
                                + " of " + Integer.toString(mSets));
                    }
                } else if (currentEvent.getType() == TimerEvent.Type.REST){
                    if (mSoundID != mSoundRest) {
                        mSetType.setText("REST");
                        mSetType.setTextColor(Color.parseColor("#0000ff"));
                        mSP.play(mSoundRest, 1f, 1f, 1, 0, 1f);
                        Log.d(TAG, "Playing Rest Sound!");
                        mSoundID = mSoundRest;
                    }
                }
                //displayTime = displayTime(millis_remaining, nextEvent);
            }

            mClockText.setText(formatTimeString(displayTime));
        }

        @Override
        public void onFinish() {
            Toast.makeText(getActivity(), "Timer Done!", Toast.LENGTH_SHORT).show();
            mSP.play(mSoundCheer, 1f, 1f, 0, 0, 1f);
            reset();
        }
    }

    private SuperTimer st;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Prevent fade to black */
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        mSetType = (TextView)v.findViewById(R.id.text_set_type);
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
                mSetType.setText("WORK");
                mSetType.setTextColor(Color.parseColor("#ff0000"));
                mSetType.setVisibility(View.VISIBLE);
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
        mSetType.setVisibility(View.GONE);
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
