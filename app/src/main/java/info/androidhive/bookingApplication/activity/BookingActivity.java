package info.androidhive.bookingApplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import info.androidhive.bookingApplication.R;

public class BookingActivity extends Activity {

    private String sentName, sentDate, sentStatus,
            sentSiteLocation, sentLocation;
    private int sentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_single_booking);

        TextView idView, nameView, statusView, dateView, siteLocationView, locationView;
        ImageView img;

        sentID = 0;
        Intent i = this.getIntent();
        sentID = i.getIntExtra("id", 0);
        sentName = i.getStringExtra("name");
        sentStatus = i.getStringExtra("status");
        sentDate = i.getStringExtra("date");
        sentSiteLocation = i.getStringExtra("siteLocation");
        sentLocation = i.getStringExtra("location");

        img = findViewById(R.id.imgView);
        idView = findViewById(R.id.bookingIDView);
        nameView = findViewById(R.id.nameField);
        statusView = findViewById(R.id.statusField);
        dateView = findViewById(R.id.dateField);
        siteLocationView = findViewById(R.id.siteLocationField);
        locationView = findViewById(R.id.locationField2);

        idView.setText("Booking ID: " + sentID);
        nameView.setText("Name: " + sentName);
        statusView.setText("Status: " + sentStatus);
        dateView.setText("Date: " + sentDate);
        siteLocationView.setText("Site: " + sentSiteLocation);
        locationView.setText("Location: " + sentLocation);

        String imgType = sentName;
        if (imgType.startsWith("Room")) {
            img.setImageResource(R.drawable.room);
        } else if (imgType.startsWith("S-PC")) {
            img.setImageResource(R.drawable.standard);
        } else if (imgType.startsWith("SP-PC")) {
            img.setImageResource(R.drawable.special);
        } else if (imgType.startsWith("Booth")) {
            img.setImageResource(R.drawable.group);
        } else if (imgType.startsWith("Scan")) {
            img.setImageResource(R.drawable.scanner);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(BookingActivity.this, HomescreenActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void goBack(View v){
        Intent intent = new Intent(BookingActivity.this, HomescreenActivity.class);
        startActivity(intent);
        finish();
    }
    public void cancelBooking(View v){
        Intent intent = new Intent(BookingActivity.this, HomescreenActivity.class);
        String id = Integer.toString(sentID);
        intent.putExtra("sentID", id);
        intent.putExtra("sentName", sentName);
        intent.putExtra("sentSiteLocation", sentSiteLocation);
        intent.putExtra("sentLocation", sentLocation);
        intent.putExtra("condition", "cancelBooking");
        startActivity(intent);
        finish();
    }
}
