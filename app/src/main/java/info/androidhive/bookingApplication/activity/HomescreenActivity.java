package info.androidhive.bookingApplication.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.androidhive.bookingApplication.R;
import info.androidhive.bookingApplication.helper.Booking;
import info.androidhive.bookingApplication.helper.RecentBookingsAdapter;
import info.androidhive.bookingApplication.helper.SQLiteHandler;
import info.androidhive.bookingApplication.helper.SessionManager;

public class HomescreenActivity extends AppCompatActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton btnLogout;
    private SQLiteHandler db;
    private SessionManager session;
    private ImageButton btnCreateBooking;
    private String speechText = "";
    private ListView recentBookingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_homescreen);

        btnLogout = findViewById(R.id.btnLogout);
        recentBookingsList = findViewById(R.id.currentBookingsList);
        btnCreateBooking = findViewById(R.id.btnCreatebooking);
        ImageButton banShowResources = findViewById(R.id.banShowResources);
        recentBookingsList.setEmptyView(findViewById(R.id.empty));
        TextView timeView = findViewById(R.id.timeView);

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentDateTimeString = sdf.format(d);
        timeView.setText(currentDateTimeString);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        final ArrayList<Booking> bookingList = new ArrayList<>();
        Booking test1 = new Booking("Room 1", "12/12/2018", "Upcoming");
        Booking test2 = new Booking("SP-PC 4", "5/2/2018", "Expired");
        Booking test3 = new Booking("SP-PC 3", "5/2/2018", "Expired");
        Booking test4 = new Booking("Booth-76", "5/2/2018", "Expired");
        Booking test5 = new Booking("S-PC 1", "5/2/2018", "Expired");
        Booking test6 = new Booking("Scan 1", "5/2/2018", "Expired");
        bookingList.add(test1);
        bookingList.add(test2);
        bookingList.add(test2);
        bookingList.add(test2);
        bookingList.add(test2);
        bookingList.add(test3);
        bookingList.add(test4);
        bookingList.add(test5);
        bookingList.add(test6);

        RecentBookingsAdapter adapter = new RecentBookingsAdapter(this, bookingList);
        recentBookingsList.setAdapter(adapter);

        // Create new booking
        btnCreateBooking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askSpeechInput();
            }
        });
        // Show Resources
        banShowResources.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomescreenActivity.this, ShowResourcesActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(HomescreenActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Which resource and at what time would you like?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    public String getSpeechText() {
        return speechText;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText = result.get(0);
                }
                break;
            }

        }
    }
}
