package info.androidhive.bookingApplication.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;

import info.androidhive.bookingApplication.R;
import info.androidhive.bookingApplication.helper.Booking;
import info.androidhive.bookingApplication.helper.ListViewAdapter;
import info.androidhive.bookingApplication.helper.SQLiteHandler;
import info.androidhive.bookingApplication.helper.SessionManager;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.homescreen);
        /////////////////////////////////////////////////////////////
        // Variables initialization
        /////////////////////////////////////////////////////////////
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
        recentBookingsList = (ListView) findViewById(R.id.currentBookingsList);
        btnCreateBooking = (ImageButton) findViewById(R.id.btnCreatebooking);
        /////////////////////////////////////////////////////////////
        // Database stuff
        /////////////////////////////////////////////////////////////
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        /////////////////////////////////////////////////////////////
        // Recent Bookings listview logic
        /////////////////////////////////////////////////////////////
//        final ArrayList<Recipe> recipeList = Recipe.getRecipesFromFile("recipes.json", this);
//        String[] listItems = new String[recipeList.size()];
//        for(int i = 0; i < recipeList.size(); i++){
//            Recipe recipe = recipeList.get(i);
//            listItems[i] = recipe.title;
//        }

        final ArrayList<Booking> bookingList = new ArrayList<Booking>();
        Booking test1 = new Booking("Meeting Room", "12/12/2018", "Upcoming");
        Booking test2 = new Booking("PC", "5/2/2018", "Expired");
        bookingList.add(test1);
        bookingList.add(test2);
        bookingList.add(test2);
        bookingList.add(test2);
        bookingList.add(test2);

        ListViewAdapter adapter = new ListViewAdapter(this, bookingList);
        recentBookingsList.setAdapter(adapter);
        /////////////////////////////////////////////////////////////
        // ActionListeners
        /////////////////////////////////////////////////////////////
        // Create new booking
        btnCreateBooking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                askSpeechInput();
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
