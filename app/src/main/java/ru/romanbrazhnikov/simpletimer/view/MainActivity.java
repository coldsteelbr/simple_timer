package ru.romanbrazhnikov.simpletimer.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    TimerExecutor TE;
    Runnable updateDisplayRunnable = new TimerRunnable();

    class TimerRunnable implements Runnable {
        @Override
        public void run() {

            long currentTime = Math.round(System.nanoTime() / 1000000000L) * 1000; // nano4millis
            long timeLeft = TE.mStopTimeInMillis - currentTime - 1000;

            if (timeLeft <= 0) {
                timeLeft = 0;
                TE.shutdown();
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

    //
    // LISTENERS
    //

    class StartClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            etDisplay.setVisibility(View.GONE);
            tvDisplay.setVisibility(View.VISIBLE);

            bStartStop.setText("Stop");
            bStartStop.setEnabled(false);
            TE = new TimerExecutor(1);
            // TODO: implement binding Model<->Widgets
            mTimerModel.setDuration(etDisplay.getText().toString());
            TE.mDurationInMillis = mTimerModel.getDurationInMillis();
            TE.mStopTimeInMillis = Math.round(System.nanoTime() / 1000000000) * 1000 + TE.mDurationInMillis;
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
            etDisplay.setVisibility(View.VISIBLE);
            tvDisplay.setVisibility(View.GONE);
            bCancel.setVisibility(View.GONE);
            if (!TE.isTerminated()) {
                return;
            }

        }
    }
}
