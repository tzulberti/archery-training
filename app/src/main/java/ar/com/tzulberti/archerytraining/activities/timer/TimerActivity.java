package ar.com.tzulberti.archerytraining.activities.timer;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;

/**
 * Activity that shows a timer
 */
public class TimerActivity extends BaseArcheryTrainingActivity {

    private TextToSpeech textToSpeech;
    private boolean shouldStop;

    private int changeBackgroundAt;
    private int time;
    private int startIn;
    private int voiceCountDown;

    private CountDownTimer currentTimer;

    private TextView timeLeftText;

    private boolean canGoBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity);

        this.canGoBack = false;

        Intent arguments = this.getIntent();

        this.changeBackgroundAt = arguments.getIntExtra(ConfigureTimerActivity.CHANGE_AT_SECONDS, 0);
        this.time = arguments.getIntExtra(ConfigureTimerActivity.TIME, 0);
        this.startIn = arguments.getIntExtra(ConfigureTimerActivity.START_IN, 0);
        this.voiceCountDown = arguments.getIntExtra(ConfigureTimerActivity.VOICE_COUNT_DOWN, 0);

        this.timeLeftText = this.findViewById(R.id.textTimeLeft);

        this.shouldStop = false;
        this.textToSpeech = new TextToSpeech(this.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.SUCCESS) {
                    // TODO check what we can do in this case
                }
                start();
            }
        });
    }



    public void start() {
        this.timeLeftText.setBackgroundResource(R.color.colorBlue);
        this.timeLeftText.setText(String.valueOf(this.startIn));

        // start the count down before starting to do the exercise
        this.currentTimer = new CountDownTimer(startIn * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long secondsMissing = millisUntilFinished / 1000;
                timeLeftText.setText(String.valueOf(secondsMissing));
            }

            public void onFinish() {
                // after that time start the exercise
                startTimer();
            }
        }.start();
    }




    /**
     * Runn the current serie (if it should or if the program hasn't been stopped).
     * <p>
     * Also, it will rest if running between series
     */
    private void startTimer() {
        if (this.shouldStop) {
            return;
        }

        this.timeLeftText.setBackgroundResource(R.color.colorGreen);
        this.timeLeftText.setText(String.valueOf(this.time));

        this.currentTimer = new CountDownTimer(this.time * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long secondsMissing = millisUntilFinished / 1000;
                if (shouldStop) {
                    this.cancel();
                }

                timeLeftText.setText(String.valueOf(secondsMissing));
                if (secondsMissing <= changeBackgroundAt) {
                    timeLeftText.setBackgroundResource(R.color.colorYellow);
                }
                if (secondsMissing <= voiceCountDown) {
                    readValue(String.valueOf(secondsMissing));
                }
            }

            public void onFinish() {
                timeLeftText.setText("0");
                readValue("0");
                timeLeftText.setBackgroundResource(R.color.colorRed);
            }

        }.start();
    }

    public void readValue(CharSequence text) {
        if (this.shouldStop) {
            return;
        }

        int res = this.textToSpeech.speak(String.valueOf(text), TextToSpeech.QUEUE_FLUSH, null);
        if (res != TextToSpeech.SUCCESS) {
            // TODO check what we can do in this case
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.shouldStop = false;
        this.start();
    }


    public void stop(View v) {
        this.stop();
    }

    @Override
    public void onBackPressed() {
        if (this.canGoBack()) {
            super.onBackPressed();
        }
    }


    public boolean canGoBack() {
        if (! this.canGoBack) {
            // the first time, stop all the things, and then recall this method
            // so the user can go back
            this.stop();
            this.canGoBack = true;
            this.onBackPressed();
        }
        return true;

    }

    public void stop() {
        this.textToSpeech.stop();
        this.shouldStop = true;
        this.currentTimer.cancel();
    }
}
