package ar.com.tzulberti.archerytraining.activities.retentions;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;

/**
 * Created by tzulberti on 4/24/17.
 */

public class RetentionExerciseActivity extends BaseArcheryTrainingActivity {

    private final static int MIN_WARNING_START = 1;

    private TextToSpeech textToSpeech;
    private boolean shouldStop;

    private int seriesAmount;
    private int seriesSleepTime;
    private int repetitionsAmount;
    private int repetitionsDuration;
    private int startIn;

    private CountDownTimer currentTimer;

    private int currentSerie;
    private int currentRepetition;

    private TextView timeLeftText;
    private TextView statusText;
    private TextView textSerieInfo;
    private TextView textRepetionsInfo;

    private boolean canGoBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retention_exercise);

        this.canGoBack = false;

        Intent arguments = this.getIntent();
        this.seriesAmount = arguments.getIntExtra(ConfigureRetentionActivity.SERIES_AMOUNT, 0);
        this.seriesSleepTime = arguments.getIntExtra(ConfigureRetentionActivity.SERIES_SLEEP_TIME, 0);
        this.repetitionsAmount = arguments.getIntExtra(ConfigureRetentionActivity.REPETITIONS_AMOUNT, 0);
        this.repetitionsDuration = arguments.getIntExtra(ConfigureRetentionActivity.REPETITIONS_DURATION, 0);
        this.startIn = arguments.getIntExtra(ConfigureRetentionActivity.START_IN, 0);

        this.timeLeftText = (TextView) this.findViewById(R.id.textTimeLeft);
        this.statusText = (TextView) this.findViewById(R.id.textStatus);
        this.textSerieInfo = (TextView) this.findViewById(R.id.textSerieInfo);
        this.textRepetionsInfo = (TextView) this.findViewById(R.id.textRepetionsInfo);

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
        this.statusText.setText(R.string.waitingToStart);
        this.timeLeftText.setBackgroundResource(R.color.colorBlue);
        this.timeLeftText.setText(Integer.toString(startIn));


        this.currentRepetition = 1;
        this.currentSerie = 1;

        // start the count down before starting to do the exercise
        this.currentTimer = new CountDownTimer(startIn * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsMissing = millisUntilFinished / 1000;
                timeLeftText.setText(Long.toString(secondsMissing));
                if (secondsMissing <= MIN_WARNING_START) {
                    timeLeftText.setBackgroundResource(R.color.colorYellow);
                }
            }

            public void onFinish() {
                // after that time start the exercise
                startSeries();
            }
        }.start();
    }


    /**
     * Runn the current serie (if it should or if the program hasn' been stopped).
     * <p>
     * Also, it will rest if running between series
     */
    private void startSeries() {
        final Resources resources = getResources();
        if (this.shouldStop) {
            return;
        }

        if (this.currentSerie > this.seriesAmount) {
            // finished reading all the series
            this.readValue(resources.getText(R.string.finishedStatus));
            this.statusText.setText(R.string.finishedStatus);
            this.timeLeftText.setBackgroundResource(R.color.colorBlue);
            this.timeLeftText.setText("");
            return;
        }

        this.textSerieInfo.setText(Integer.toString(this.currentSerie) + " / " + Integer.toString(this.seriesAmount));

        // make sure of reseting this value for the different series
        this.currentRepetition = 1;

        if (this.currentSerie == 1) {
            this.startRepetition();
        } else {
            // it has finished the first serie so it should go to the next one
            // after sleeping a given number of seconds
            this.readValue(resources.getText(R.string.finishedSerie));
            this.timeLeftText.setText(Integer.toString(seriesSleepTime));
            this.timeLeftText.setBackgroundResource(R.color.colorGreen);
            this.statusText.setText(resources.getText(R.string.restStatus));

            this.currentTimer = new CountDownTimer(seriesSleepTime * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long secondsMissing = millisUntilFinished / 1000;
                    timeLeftText.setText(Long.toString(secondsMissing));
                    if (secondsMissing <= MIN_WARNING_START) {
                        timeLeftText.setBackgroundResource(R.color.colorYellow);
                    }
                }

                public void onFinish() {
                    startRepetition();
                }
            }.start();
        }

    }


    private void startRepetition() {
        final Resources resources = getResources();
        if (this.shouldStop) {
            return;
        }

        if (this.currentRepetition > this.repetitionsAmount) {
            // finished the current serie and it is time to go to the next one
            this.currentSerie += 1;
            this.startSeries();
            return;
        }

        this.textRepetionsInfo.setText(Integer.toString(this.currentRepetition) + " / " + Integer.toString(this.repetitionsAmount));

        // set the values because on the first execution of onTick will be
        // executed after one second
        this.readValue(Integer.toString(repetitionsDuration));
        this.statusText.setText(resources.getText(R.string.runningStatus));
        timeLeftText.setText(Integer.toString(repetitionsDuration));
        timeLeftText.setBackgroundResource(R.color.colorRed);

        // do the current repetition


        this.currentTimer = new CountDownTimer(repetitionsDuration * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long secondsMissing = millisUntilFinished / 1000;
                timeLeftText.setText(Long.toString(secondsMissing));
                readValue(Long.toString(secondsMissing));
            }

            public void onFinish() {
                //readValue("0");
                readValue(resources.getText(R.string.downBow));
                currentRepetition += 1;
                statusText.setText(resources.getText(R.string.restStatus));
                timeLeftText.setBackgroundResource(R.color.colorGreen);
                timeLeftText.setText(Integer.toString(repetitionsDuration));

                // if this is the last repetiton of the serie there is no need
                // to sleep because that is going to be given by the serie
                if (currentRepetition > repetitionsAmount) {
                    currentSerie += 1;
                    startSeries();
                    return;
                }

                // sleep between the repetitions
                currentTimer = new CountDownTimer(repetitionsDuration * 1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long secondsMissing = millisUntilFinished / 1000;
                        timeLeftText.setText(Long.toString(secondsMissing));

                        if (secondsMissing <= MIN_WARNING_START) {
                            timeLeftText.setBackgroundResource(R.color.colorYellow);
                        }
                    }

                    public void onFinish() {
                        readValue(resources.getText(R.string.upBow));
                        startRepetition();
                    }
                }.start();
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
        this.statusText.setText(R.string.stoppedStatus);
        this.shouldStop = true;
        this.currentTimer.cancel();
    }
}
