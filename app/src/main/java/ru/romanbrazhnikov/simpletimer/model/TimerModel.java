package ru.romanbrazhnikov.simpletimer.model;

/**
 * Created by roman on 25.09.17.
 */

public class TimerModel {

    // widgets
    public interface ModelBinder {
        void toModel();

        void toView();
    }

    private ModelBinder mBinder;

    private long mDurationInMillieconds;

    public TimerModel() {
    }

    public TimerModel(ModelBinder binder) {
        mBinder = binder;
        mBinder.toView();
    }

    public void setDuration(String duration) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        String[] res = duration.split(":");
        hours = Integer.parseInt(res[0]);
        minutes = Integer.parseInt(res[1]);
        seconds = Integer.parseInt(res[2]);

        mDurationInMillieconds = (hours * 60 * 60 + minutes * 60 + seconds) * 1000;
    }
}
