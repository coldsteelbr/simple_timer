package ru.romanbrazhnikov.simpletimer.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.romanbrazhnikov.simpletimer.R;
import ru.romanbrazhnikov.simpletimer.model.TimerModel;

public class MainActivity extends AppCompatActivity {

    private TimerModel mTimerModel = new TimerModel();
    private Handler mHandler = new Handler();

    private Button bStartStop;
    private TextView tvDisplay;
    private EditText etDisplay;

    final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    TimerExecutor TE;
    Runnable updateDisplay = new Runnable() {
        @Override
        public void run() {
            final Date date = new Date();
            final long currentTime = date.getTime();
            long toShow = TE.mStopTimeInMillis - currentTime;
            if (TE.mStopTimeInMillis < currentTime) {
                toShow = 0L;
                TE.shutdown();
            }

            final long finalToShow = toShow;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvDisplay.setText(dateFormat.format(new Date(finalToShow)));
                }
            });
        }
    };

    long initDelay = 0;
    long stepInSeconds = 1;

    class TimerExecutor extends ScheduledThreadPoolExecutor {
        private long mStopTimeInMillis;
        private long mDurationInMillis;
        private Runnable mRunnable;

        private void runMe() {
            this.scheduleAtFixedRate(mRunnable, initDelay, stepInSeconds, TimeUnit.SECONDS);
        }

        public TimerExecutor(int corePoolSize) {
            super(corePoolSize);
        }

        public void setDurationInMillis(long durationInMillis) {
            mDurationInMillis = durationInMillis;
        }

        public void setStopTime(long curTimeInMillis) {
            mStopTimeInMillis = curTimeInMillis + mDurationInMillis;
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

                TE.setDurationInMillis(5000);
                TE.setRunnable(updateDisplay);
                TE.setStopTime(new Date().getTime());
                TE.runMe();
                etDisplay.setVisibility(View.GONE);
                tvDisplay.setVisibility(View.VISIBLE);
            }
        });

        etDisplay = findViewById(R.id.et_timer_display);
        tvDisplay = findViewById(R.id.tv_timer_display);

    }

}
