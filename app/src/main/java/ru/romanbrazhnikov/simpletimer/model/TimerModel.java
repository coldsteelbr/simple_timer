package ru.romanbrazhnikov.simpletimer.model;

import java.util.concurrent.TimeUnit;

/**
 * Created by roman on 25.09.17.
 */

public class TimerModel {

    private long mDurationInNanos;

    public TimerModel() {
    }

    public void setDuration(String duration) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        String[] res = duration.split(":");
        hours = Integer.parseInt(res[0]);
        minutes = Integer.parseInt(res[1]);
        seconds = Integer.parseInt(res[2]);

        mDurationInNanos = (hours * 60 * 60 + minutes * 60 + seconds) * 1000000000;
    }

    public static String formatMillisToString(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis), // 5 hours
                TimeUnit.MILLISECONDS.toMinutes(millis) // 300 minutes - 5 hours to Minutes
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public String getDuration() {
        return formatMillisToString(mDurationInNanos);
    }

    public long getDurationInNanos() {
        return mDurationInNanos;
    }
}
