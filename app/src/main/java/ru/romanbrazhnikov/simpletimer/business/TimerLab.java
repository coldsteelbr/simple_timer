package ru.romanbrazhnikov.simpletimer.business;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by roman on 25.09.17.
 */
// TODO: implement thru Dagger as a singleton
public class TimerLab {
    private static TimerLab mInstance = null;
    private ScheduledExecutorService pool = null;

    private TimerLab() {
        pool = Executors.newScheduledThreadPool(1);
    }

    public TimerLab getInstance() {
        if (mInstance == null) {
            mInstance = new TimerLab();
        }
        return mInstance;
    }


}
