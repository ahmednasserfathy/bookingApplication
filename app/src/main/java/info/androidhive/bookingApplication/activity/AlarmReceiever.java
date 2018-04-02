package info.androidhive.bookingApplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getStringExtra("name");
        String siteLocation = intent.getStringExtra("siteLocation");
        String location = intent.getStringExtra("location");

        Toast.makeText(context, "Reminder alert!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(context, ReminderActivity.class);

        i.putExtra("name", name);
        i.putExtra("siteLocation", siteLocation);
        i.putExtra("location", location);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
