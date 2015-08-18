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
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //Sounds will play funny because Soundpool needs to be checking if the sound is playing.
    private static final String TAG = "MainActivity";
    private Button mStartButton, mCancelButton;
    private TextView mClockText;
    private SoundPool mSP;
    private int mSoundID = -1; //trying to use this to have sounds only play once.
    //the above seems like a dumb hack.
    private int mSoundWhistle, mSoundCheer, mSoundBeepBeep;
    /* Implementing CountDownTimer within the activity, since I'm
    not sure how one would go about sending the tick updates to the
    activity quickly and efficiently otherwise (Intents maybe?)
     */
    public class SuperTimer extends CountDownTimer {
        private long mWork, mRest, mTotal;
        private int mSets;

        SuperTimer(long work, long rest, int sets) {
            super((sets*(work + rest) - rest)*1000, 500);
            mWork = work;
            mRest = rest;
            mTotal = mWork + mRest;
            mSets = sets;
        }

        @Override
        public void onTick(long millis_remaining) {
            long displayTime;
            //if we're not on last set
            if (millis_remaining > mWork * 1000)
                displayTime = (((millis_remaining / 1000) + 1 + mRest) % mTotal);
            else
                displayTime = (((millis_remaining / 1000) + 1) % mTotal);

            if (displayTime == 0) {
                displayTime += mTotal;
                if (mSoundID != mSoundWhistle) {
                    mSP.play(mSoundWhistle, 1f, 1f, 1, 0, 1f);
                    mSoundID = mSoundWhistle;
                }
            }

            mClockText.setText("" + displayTime);
            /* The block here deals with resting or the special case when you're
            on the last set and there is no rest interval. Normally you add the
            rest interval to the work interval, but the last set is _only_ the
            work interval, since you're done afterward!
             */
            if (mClockText.getText().equals(Long.toString(mRest))
                    && millis_remaining > mWork*1000) {
                if (mSoundID != mSoundBeepBeep) {
                    mSP.play(mSoundBeepBeep, 1f, 1f, 0, 0, 1f);
                    Log.d(TAG, "Playing Beep Beep!");
                    mSoundID = mSoundBeepBeep;
                }
                mClockText.setText("Rest!");
            } else if (mClockText.getText().equals(Long.toString(mWork))
                    && millis_remaining < mWork*1000 ) {
                if (mSoundID != mSoundWhistle) {
                    mSP.play(mSoundWhistle, 1f, 1f, 0, 0, 1f);
                    Log.d(TAG, "Playing Whistle!");
                    mSoundID = mSoundWhistle;
                }
            }
        }

        @Override
        public void onFinish() {
            mClockText.setText("Done!");
            mSP.play(mSoundCheer, 1f, 1f, 0, 0, 1f);
            mSoundID = -1;
            mStartButton.setEnabled(true);
            mCancelButton.setEnabled(false);
        }
    }

    SuperTimer st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find views
        mClockText = (TextView)findViewById(R.id.text_clock);
        mStartButton = (Button)findViewById(R.id.button_start);
        mCancelButton = (Button)findViewById(R.id.button_cancel);
        mCancelButton.setEnabled(false);

        //set listeners
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Start button clicked");
                mClockText.setText(Integer.toString(60));
                mStartButton.setEnabled(false);
                mCancelButton.setEnabled(true);
                st.start();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel button clicked");
                mClockText.setText("Canceled");
                mStartButton.setEnabled(true);
                mCancelButton.setEnabled(false);
                mSoundID = -1;
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

        //prepare timer
        st = new SuperTimer(20, 10, 3);

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
}
