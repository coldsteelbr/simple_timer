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

    private Button bStartStop;
    private TextView tvDisplay;
    private EditText etDisplay;

    TimerExecutor TE;
    Runnable updateDisplayRunnable = new Runnable() {
        @Override
        public void run() {

            long currentTime = System.nanoTime() / 1000000; // nano4millis
            long timeLeft = TE.mStopTimeInNanos - currentTime;

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
    };

    long initDelay = 0;
    long stepInSeconds = 1;

    class TimerExecutor extends ScheduledThreadPoolExecutor {
        private long mStopTimeInNanos; // millis
        private long mDurationInMillis; // millis
        private Runnable mRunnable;

        private void runMe() {
            mStopTimeInNanos = System.nanoTime() * 1000000 + mDurationInMillis;
            this.scheduleAtFixedRate(mRunnable, initDelay, stepInSeconds, TimeUnit.SECONDS);
        }

        public TimerExecutor(int corePoolSize) {
            super(corePoolSize);
        }

        public void setDurationInMillis(long durationInMillis) {
            mDurationInMillis = durationInMillis;
        }


        public void setRunnable(Runnable runnable) {
            mRunnable = runnable;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

        TE = new TimerExecutor(1);

    }


    private void initWidgets() {
        bStartStop = findViewById(R.id.b_startStop);
        bStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etDisplay.setVisibility(View.GONE);
                tvDisplay.setVisibility(View.VISIBLE);
                TE.mDurationInMillis = 5000 * 60 * 60;
                TE.mStopTimeInNanos = System.nanoTime() / 1000000 + TE.mDurationInMillis;
                TE.scheduleAtFixedRate(updateDisplayRunnable, initDelay, stepInSeconds, TimeUnit.SECONDS);
            }
        });

        etDisplay = findViewById(R.id.et_timer_display);
        tvDisplay = findViewById(R.id.tv_timer_display);

    }

}
