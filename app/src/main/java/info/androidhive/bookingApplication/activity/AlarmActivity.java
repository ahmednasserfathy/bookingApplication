package info.androidhive.bookingApplication.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import info.androidhive.bookingApplication.R;

import android.widget.EditText;
import android.widget.Toast;

public class AlarmActivity extends Activity {
    private EditText days, ethr, etmin, etsec;
    private String name, siteLocation, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_reminder);

        Intent i = this.getIntent();
        name = i.getStringExtra("name");
        siteLocation = i.getStringExtra("siteLocation");
        location = i.getStringExtra("location");

        days = findViewById(R.id.days);
        ethr = findViewById(R.id.ethr);
        etmin = findViewById(R.id.etmin);
        etsec = findViewById(R.id.etsec);
    }

    public void onClickSetAlarm(View v) {

        int daysN = 0;
        int hr = 0;
        int min = 0;
        int sec = 0;
        int result = 1;

        String sdays = days.getText().toString();
        String shr = ethr.getText().toString();
        String smin = etmin.getText().toString();
        String ssec = etsec.getText().toString();

        if (sdays.equals(""))
            daysN = 0;
        else {
            daysN = Integer.parseInt(days.getText().toString());
            daysN = daysN * 24 * 60 * 60 * 1000;
        }

        if (shr.equals(""))
            hr = 0;
        else {
            hr = Integer.parseInt(ethr.getText().toString());
            hr = hr * 60 * 60 * 1000;
        }

        if (smin.equals(""))
            min = 0;
        else {
            min = Integer.parseInt(etmin.getText().toString());
            min = min * 60 * 1000;
        }

        if (ssec.equals(""))
            sec = 0;
        else {
            sec = Integer.parseInt(etsec.getText().toString());
            sec = sec * 1000;
        }
        result = daysN + hr + min + sec;
        Toast.makeText(getApplicationContext(),
                "Reminder added!", Toast.LENGTH_LONG).show();

        final int _id = (int) System.currentTimeMillis();
        Intent intent = new Intent(getBaseContext(), AlarmReceiever.class);
        intent.putExtra("name", name);
        intent.putExtra("siteLocation", siteLocation);
        intent.putExtra("location", location);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), _id, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + result, pendingIntent);
        Intent intent1 = new Intent(AlarmActivity.this, HomescreenActivity.class);
        startActivity(intent1);
        finish();
    }

    public void noReminder(View v) {
        Intent intent = new Intent(AlarmActivity.this, HomescreenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(AlarmActivity.this, HomescreenActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}