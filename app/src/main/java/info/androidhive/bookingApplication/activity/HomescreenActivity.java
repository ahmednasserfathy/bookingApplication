package info.androidhive.bookingApplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import info.androidhive.bookingApplication.R;
import info.androidhive.bookingApplication.app.AppConfig;
import info.androidhive.bookingApplication.app.AppController;
import info.androidhive.bookingApplication.helper.Booking;
import info.androidhive.bookingApplication.helper.Clock;
import info.androidhive.bookingApplication.helper.OnClockTickListner;
import info.androidhive.bookingApplication.helper.RecentBookingsAdapter;
import info.androidhive.bookingApplication.helper.SQLiteHandler;
import info.androidhive.bookingApplication.helper.SessionManager;

import static info.androidhive.bookingApplication.helper.SpeechPatterns.reservePC;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.reservePCAtCharles;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.reserveRoom;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.reserveRoomAtCharles;

public class HomescreenActivity extends AppCompatActivity {

    private static final String TAG = HomescreenActivity.class.getSimpleName();
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton btnLogout;
    private SQLiteHandler db;
    private SessionManager session;
    private ImageButton btnCreateBooking;
    private String speechText = "";
    private ListView recentBookingsList;
    private ProgressDialog pDialog;
    private ArrayList<Booking> bookingList;
    private String userID;
    private Clock c;

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

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        c = new Clock(this);
        c.AddClockTickListner(new OnClockTickListner() {
            @Override
            public void OnMinuteTick(Time currentTime) {

                for (Booking booking : bookingList) {
                    if (booking.getStatus().equals("Upcoming")) {

                        StringTokenizer stringTokenizer = new StringTokenizer(booking.getDateBooked());
                        String day, month, hourAndMins;

                        // Separate each word
                        hourAndMins = stringTokenizer.nextElement().toString() + " "
                                + stringTokenizer.nextElement().toString();
                        day = stringTokenizer.nextElement().toString();
                        month = stringTokenizer.nextElement().toString();
                        day = day.replace(",", "");

                        if (hourAndMins.equals(DateFormat.format("h:mm aa"
                                , currentTime.toMillis(true)).toString())) {

                            Format formatter = new SimpleDateFormat("MMMM");
                            String currentMonth = formatter.format(new Date());

                            if (month.equals(currentMonth)) {

                                if (day.contains("st")) {
                                    day = day.replace("st", "");
                                } else if (day.contains("nd")) {
                                    day = day.replace("nd", "");
                                } else if (day.contains("rd")) {
                                    day = day.replace("rd", "");
                                } else if (day.contains("th")) {
                                    day = day.replace("th", "");
                                }

                                Format formatter2 = new SimpleDateFormat("d");
                                String currentDay = formatter2.format(new Date());

                                if (day.equals(currentDay)) {
                                    booking.setStatus("In progress");
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Booking initiated!")
                                            .setMessage("Your booking: " + booking.getName() + " at "
                                                    + booking.getSiteLocation() + ", "
                                                    + booking.getLocation() + " has started")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    RecentBookingsAdapter adapter = new RecentBookingsAdapter(getContext(), bookingList);
                                                    recentBookingsList.setAdapter(adapter);
                                                }
                                            }).show();
                                }
                            }
                        }
                    }
                }

            }
        });

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentDateTimeString = sdf.format(d);
        timeView.setText(currentDateTimeString);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        userID = user.get("uid");
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        bookingList = new ArrayList<>();
        getAllBookings(userID);

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
                c.StopTick();
                Intent intent = new Intent(HomescreenActivity.this, ShowResourcesActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //logoutUser();
                c.StopTick();
                Intent intent = new Intent(HomescreenActivity.this, AlarmActivity.class);
                startActivity(intent);
                finish();
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
        c.StopTick();
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
                "What would you like to do?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
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
                    Log.d(TAG, "Speech input: " + speechText);


                    // Get number of words in the command
                    speechText = speechText.toLowerCase();
                    String trim = speechText.trim();
                    int numOfWords = trim.split("\\s+").length;

                    // Create a new booking
                    if (speechText.startsWith("reserve")) {

                        String[] params = null;
                        if (numOfWords == 15 || numOfWords == 16 || numOfWords == 17) {

                            // If its a meeting room/group booth
                            if (speechText.contains("room") || speechText.contains("booth")
                                    || speechText.contains("scan")) {
                                if (numOfWords == 15)
                                    params = reserveRoom(speechText);
                                else if (numOfWords == 16)
                                    params = reserveRoomAtCharles(speechText);
                            } else if (speechText.contains("pc")) {
                                if (numOfWords == 16)
                                    params = reservePC(speechText);
                                else if (numOfWords == 17)
                                    params = reservePCAtCharles(speechText);
                            }
                        }

                        if (params != null)
                            createNewBooking(params[0], params[1], params[2], params[3]);
                        else
                            Toast.makeText(getApplicationContext(),
                                    "Invalid command!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
        }
    }

    private void createNewBooking(final String rName, final String rDateBooked, final String rSiteLocation
            , final String rLocation) {
        // Tag used to cancel the request
        String tag_string_req = "req_createbooking";

        // Progress box
        pDialog.setMessage("Checking resource");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATE_BOOKING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Create booking Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        try {
                            JSONArray data = jObj.getJSONArray("theData");
                            try {
                                bookingList.add(new Booking((JSONObject) data.get(0)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Error occurred in database. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                    RecentBookingsAdapter adapter = new RecentBookingsAdapter(getContext(), bookingList);
                    recentBookingsList.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Resources Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to createbooking url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userID", userID);
                params.put("name", rName);
                params.put("dateBooked", rDateBooked);
                params.put("siteLocation", rSiteLocation);
                params.put("location", rLocation);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getAllBookings(final String userID) {
        // Tag used to cancel the request
        String tag_string_req = "req_getBookings";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_BOOKINGS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "GET BOOKINGS Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        try {
                            JSONArray data = jObj.getJSONArray("theData");
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    bookingList.add(new Booking((JSONObject) data.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RecentBookingsAdapter viewAdapter = new RecentBookingsAdapter(getContext(), bookingList);
                        recentBookingsList.setAdapter(viewAdapter);
                    } else {
                        // Clear the list view
                        RecentBookingsAdapter sampleAdapter = (RecentBookingsAdapter) recentBookingsList.getAdapter();
                        if (sampleAdapter != null) {
                            sampleAdapter.clearData();
                            sampleAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get bookings error: " + error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to getbookings url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userID", userID);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private Context getContext() {
        return this;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
