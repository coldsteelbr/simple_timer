package ru.romanbrazhnikov.simpletimer.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ru.romanbrazhnikov.simpletimer.R;

public class AlarmActivity extends AppCompatActivity {

    private Button bAlarmOkay;
    private AlarmOkayClickListener mAlarmOkayClickListener = new AlarmOkayClickListener();


    public static void showActivity(Context context) {
        Intent intent = new Intent(context, AlarmActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        bAlarmOkay = findViewById(R.id.b_alarm_okay);
        bAlarmOkay.setOnClickListener(mAlarmOkayClickListener);
    }

    class AlarmOkayClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            finish();
        }
    }
}
