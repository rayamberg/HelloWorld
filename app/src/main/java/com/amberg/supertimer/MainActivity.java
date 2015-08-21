package com.amberg.supertimer;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /* For the future, to properly capture time with varying objects, like if users
    will eventually be able to program their own timers, you'll want to split the works
    and rests into individual objects of some sort. Those objects will know their own
    durations, and this along with the millis_remaining variable, can tell the clock what to put
    on the display. For example, imagine a millis_remaining of 76sec encounters an interval
    object with a duration of 10sec. Using a list like mTimerEvent, the timer will know that
    at 76sec, it needs to go to the interval object. While it is using this interval object,
    the timer will always subtract 66sec (the difference of 76sec and 10sec) from its value.
    Once millis_remaining is 66sec, it is done with the interval object and can move on to the
    next object, be it rest, or whatever. One more thing: to check whether it needs to move onto
    the next object, it checks whether it is greater than the first item in the array mTimerEvent.
    When it's done, it deletes it from the array and goes to the next object until done.
     */
    private static final String TAG = "MainActivity";
    private EditText mTextIntervals, mTextRest, mTextSets;
    private Button mStartButton, mCancelButton;
    private TextView mClockText;
    private SoundPool mSP;
    private int mSoundID = -1; //trying to use this to have sounds only play once.
    //the above seems like a dumb hack.
    private int mSoundWhistle, mSoundCheer, mSoundBeepBeep;
    /* Implementing CountDownTimer within the activity, but at some
    * point it might be good to see how to encapsulate it into its own class file */
    public class SuperTimer extends CountDownTimer {
        private long mWork, mRest, mModulus, mInitialSecs;
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
            /* TODO: Do not display time as sum of work + rest.*/
            long displayTime;
            long nextEvent = mTimerEvents.get(0);

            displayTime = getDisplayTime(millis_remaining, nextEvent);
            if (displayTime == 0) {
                if (isRest) { //we're at the end of rest period
                    if (mSoundID != mSoundWhistle) {
                        mSP.play(mSoundWhistle, 1f, 1f, 1, 0, 1f);
                        Log.d(TAG, "Playing Whistle!");
                        mSoundID = mSoundWhistle;
                    }
                } else { //we're at the end of the work period
                    if (mSoundID != mSoundBeepBeep) {
                        mSP.play(mSoundBeepBeep, 1f, 1f, 1, 0, 1f);
                        Log.d(TAG, "Playing Beep Beep!");
                        mSoundID = mSoundBeepBeep;
                    }
                }
                mTimerEvents.remove(0);
                nextEvent = mTimerEvents.get(0);
                displayTime = getDisplayTime(millis_remaining, nextEvent);
                isRest = !isRest;
            }

            mClockText.setText("" + displayTime);
        }

        @Override
        public void onFinish() {
            Toast.makeText(MainActivity.this, "Timer Done!", Toast.LENGTH_SHORT).show();
            mSP.play(mSoundCheer, 1f, 1f, 0, 0, 1f);
            reset();
        }

        private long getDisplayTime(long millis_remaining, long secs_end) {
            return (millis_remaining / 1000) + 1 - secs_end;
        }
    }

    private SuperTimer st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find views
        mClockText = (TextView)findViewById(R.id.text_clock);
        mStartButton = (Button)findViewById(R.id.button_start);
        mCancelButton = (Button)findViewById(R.id.button_cancel);
        mCancelButton.setEnabled(false);
        mTextIntervals = (EditText)findViewById(R.id.text_intervals);
        mTextRest = (EditText)findViewById(R.id.text_rest);
        mTextSets = (EditText)findViewById(R.id.text_sets);

        //set listeners
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Start button clicked");
                int work, rest, sets;
                work = Integer.parseInt(mTextIntervals.getText().toString());
                rest = Integer.parseInt(mTextRest.getText().toString());
                sets = Integer.parseInt(mTextSets.getText().toString());
                mClockText.setText(Integer.toString(work));
                mStartButton.setEnabled(false);
                mCancelButton.setEnabled(true);
                mTextIntervals.setEnabled(false);
                mTextRest.setEnabled(false);
                mTextSets.setEnabled(false);
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
                Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                reset();
                st.cancel();
            }
        });

        //Prepare SoundPool to play sounds
        mSP = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSP.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

            }
        });
        //prepare sounds
        mSoundWhistle = mSP.load(this, R.raw.referee_whistle, 1);
        mSoundCheer = mSP.load(this, R.raw.ovation, 1);
        mSoundBeepBeep = mSP.load(this, R.raw.beepbeep, 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reset() {
        mClockText.setText(R.string.text_clock);
        mStartButton.setEnabled(true);
        mCancelButton.setEnabled(false);
        mTextIntervals.setEnabled(true);
        mTextRest.setEnabled(true);
        mTextSets.setEnabled(true);
        mSoundID = -1;
    }
}
