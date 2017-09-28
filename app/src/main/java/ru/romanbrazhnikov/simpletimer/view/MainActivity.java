package ru.romanbrazhnikov.simpletimer.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import ru.romanbrazhnikov.simpletimer.R;
import ru.romanbrazhnikov.simpletimer.model.TimerModel;

public class MainActivity extends AppCompatActivity {

    private TimerModel mTimerModel = new TimerModel();

    // WIDGETS
    private Button bStartStop;
    private Button bCancel;
    private TextView tvDisplay;
    private EditText etDisplay;

    // LISTENERS
    private StartClickListener mStartListener = new StartClickListener();
    private StopClickListener mStopClickListener = new StopClickListener();
    private CancelClickListener mCancelClickListener = new CancelClickListener();

    private boolean mIsCanceled = true;

    TimerExecutor TE;
    Runnable updateDisplayRunnable = new TimerRunnable();

    class TimerRunnable implements Runnable {
        @Override
        public void run() {

            long currentTime = Math.round(System.nanoTime() / 1000000000L) * 1000; // nano4millis
            long timeLeft = TE.mStopTimeInMillis - currentTime;

            if (timeLeft <= 0) {
                timeLeft = 0;
                TE.shutdown();

                AlarmActivity.showActivity(MainActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetScreen();
                        mIsCanceled = true;
                    }
                });
            }

            Log.d("Run", "Timeleft: " + timeLeft);
            final long finalTimeLeft = timeLeft;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvDisplay.setText(TimerModel.formatMillisToString(finalTimeLeft));
                }
            });
        }
    }


    long initDelay = 0;
    long stepInSeconds = 1;

    class TimerExecutor extends ScheduledThreadPoolExecutor {
        private long mStopTimeInMillis;
        private long mDurationInMillis;


        public TimerExecutor(int corePoolSize) {
            super(corePoolSize);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

    }


    private void initWidgets() {
        // Buttons
        bStartStop = findViewById(R.id.b_startStop);
        bStartStop.setOnClickListener(mStartListener);
        bCancel = findViewById(R.id.b_cancel);
        bCancel.setOnClickListener(mCancelClickListener);

        // Text fields
        etDisplay = findViewById(R.id.et_timer_display);
        tvDisplay = findViewById(R.id.tv_timer_display);
    }

    private void resetScreen() {
        etDisplay.setVisibility(View.VISIBLE);
        tvDisplay.setVisibility(View.GONE);
        bCancel.setVisibility(View.GONE);
        bStartStop.setText("Start");
        bStartStop.setOnClickListener(mStartListener);
    }

    //
    // LISTENERS
    //

    class StartClickListener implements View.OnClickListener {
        private String mPatternString = "^[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}$";
        private Pattern mPattern = Pattern.compile(mPatternString);

        @Override
        public void onClick(View view) {

            // if doesn't match the pattern - do nothing
            if (!mPattern.matcher(etDisplay.getText().toString()).find()) {
                // ERROR handling
                Toast.makeText(MainActivity.this, "Wrong time format", Toast.LENGTH_SHORT).show();
                return;
            }

            etDisplay.setVisibility(View.GONE);
            tvDisplay.setVisibility(View.VISIBLE);

            bStartStop.setText("Stop");
            bStartStop.setEnabled(false);
            TE = new TimerExecutor(1);
            // TODO: implement binding Model<->Widgets
            if (mIsCanceled) {
                mTimerModel.setDuration(etDisplay.getText().toString());
            } else {
                mTimerModel.setDuration(tvDisplay.getText().toString());
            }
            TE.mDurationInMillis = mTimerModel.getDurationInMillis();
            TE.mStopTimeInMillis = Math.round(System.nanoTime() / 1000000000) * 1000 + TE.mDurationInMillis;
            mIsCanceled = false;
            TE.scheduleAtFixedRate(updateDisplayRunnable, initDelay, stepInSeconds, TimeUnit.SECONDS);

            view.setOnClickListener(mStopClickListener);
            bStartStop.setEnabled(true);
            bCancel.setVisibility(View.VISIBLE);
        }
    }

    class StopClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            bStartStop.setEnabled(false);
            TE.shutdown();
            bStartStop.setText("Start");
            bStartStop.setOnClickListener(mStartListener);
            bStartStop.setEnabled(true);
        }
    }

    class CancelClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (!TE.isTerminated() && !TE.isTerminating() && !TE.isShutdown()) {
                // if timer is going - stop
                mStopClickListener.onClick(null);
            }
            resetScreen();
            mIsCanceled = true;
        }
    }
}
