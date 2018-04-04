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
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
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

import static info.androidhive.bookingApplication.helper.SpeechPatterns.finishBookingTest;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.reservePC;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.reservePCAtCharles;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.reserveRoom;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.reserveRoomAtCharles;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.cancelBooking;
import static info.androidhive.bookingApplication.helper.SpeechPatterns.finishBooking;

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
    private String name, siteLocation, location;
    private boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_homescreen);

        bookingList = new ArrayList<>();
        btnLogout = findViewById(R.id.btnLogout);
        recentBookingsList = findViewById(R.id.currentBookingsList);
        btnCreateBooking = findViewById(R.id.btnCreatebooking);
        ImageButton banShowResources = findViewById(R.id.banShowResources);
        recentBookingsList.setEmptyView(findViewById(R.id.empty));
        final TextView timeView = findViewById(R.id.timeView);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String condition = extras.getString("condition");
            if(condition.equals("cancelBooking"))
            {
                String sentID, sentName, sentSiteLocation, sentLocation;
                sentID = extras.getString("sentID");
                sentName = extras.getString("sentName");
                sentSiteLocation = extras.getString("sentSiteLocation");
                sentLocation = extras.getString("sentLocation");
                removeBooking(sentID, sentName, sentSiteLocation, sentLocation);
            }
        }else{
            getAllBookings(userID);
        }

        c = new Clock(this);
        c.AddClockTickListner(new OnClockTickListner() {
            @Override
            public void OnMinuteTick(Time currentTime) {

                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String currentDateTimeString = sdf.format(d);
                timeView.setText(currentDateTimeString);

                for (Booking booking : bookingList) {
                    if (booking.getStatus().equals("Upcoming")
                            && !isValidBookDate(booking.getDateBooked())) {
                        booking.setStatus("In progress");
                        updateBookingStatus("In progress", booking.getName(),
                                booking.getSiteLocation(), booking.getLocation());
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
        });

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
        // Set an item click listener for ListView
        recentBookingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Booking selectedItem = (Booking) parent.getItemAtPosition(position);
                c.StopTick();
                Intent intent = new Intent(HomescreenActivity.this, BookingActivity.class);
                intent.putExtra("id", selectedItem.getID());
                intent.putExtra("name", selectedItem.getName());
                intent.putExtra("status", selectedItem.getStatus());
                intent.putExtra("date", selectedItem.getDateBooked());
                intent.putExtra("siteLocation", selectedItem.getSiteLocation());
                intent.putExtra("location", selectedItem.getLocation());
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
        c.StopTick();
        Intent intent = new Intent(HomescreenActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Create a new request to the GOOGLE API to allow the app
     * to have access to recognize intent
     */
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

    /**
     * Handle speech recognizer results
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText = result.get(0);
                    speechText = "reserve room 1 on the 5th of april at 10:34 p.m. in Library level 1";
                    Log.d(TAG, "Speech input: " + speechText);


                    // Get number of words in the command
                    speechText = speechText.toLowerCase();
                    String trim = speechText.trim();
                    int numOfWords = trim.split("\\s+").length;

                    // Create a new booking
                    if (speechText.startsWith("reserve") ||
                            speechText.startsWith("book")) {

                        String[] params = null;
                        if (numOfWords == 15 || numOfWords == 16 || numOfWords == 17) {

                            // If its a meeting room/group booth
                            if (speechText.contains("room") || speechText.contains("booth")
                                    || speechText.contains("scan")) {
                                if (numOfWords == 15)
                                    params = reserveRoom(speechText);
                                else if (numOfWords == 16 && speechText.contains("charles"))
                                    params = reserveRoomAtCharles(speechText);
                            } else if (speechText.contains("pc")) {
                                if (numOfWords == 16)
                                    params = reservePC(speechText);
                                else if (numOfWords == 17 && speechText.contains("charles"))
                                    params = reservePCAtCharles(speechText);
                            }
                        }
                        if (params != null) {
                            if (isValidBookDate(params[1]))
                                createNewBooking(params[0], params[1], params[2], params[3]);
                            else
                                Toast.makeText(getApplicationContext(),
                                        "Invalid booking date!", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getApplicationContext(),
                                    "Invalid command!", Toast.LENGTH_LONG).show();
                    } else if (speechText.startsWith("cancel") ||
                            speechText.startsWith("finish") ||
                            speechText.startsWith("remove") ||
                            speechText.startsWith("delete")) {

                        if (numOfWords == 4) {
                            String theID = cancelBooking(speechText);
                            if (theID.contains("id")) {
                                theID = theID.replace("id", "");
                            }
                            try {
                                int id = Integer.parseInt(theID);
                                for (Booking b : bookingList) {
                                    if (b.getID() == id) {
                                        bookingList.remove(b);
                                        removeBooking(Integer.toString(id), b.getName(),
                                                b.getSiteLocation(), b.getLocation());
                                        break;
                                    }
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(),
                                        "Booking ID is not valid", Toast.LENGTH_LONG).show();
                            }
                        } else if (numOfWords == 3) {
                            String theID = finishBooking(speechText);
                            if (theID.contains("id")) {
                                theID = theID.replace("id", "");
                            }
                            try {
                                int id = Integer.parseInt(theID);
                                for (Booking b : bookingList) {
                                    if (b.getID() == id) {
                                        bookingList.remove(b);
                                        removeBooking(Integer.toString(id), b.getName(),
                                                b.getSiteLocation(), b.getLocation());
                                        break;
                                    }
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(),
                                        "Booking ID is not valid", Toast.LENGTH_LONG).show();
                            }
                        } else if (numOfWords == 2) {
                            String theID = finishBookingTest(speechText);
                            if (theID.contains("id")) {
                                theID = theID.replace("id", "");
                            }
                            try {
                                int id = Integer.parseInt(theID);
                                for (Booking b : bookingList) {
                                    if (b.getID() == id) {
                                        bookingList.remove(b);
                                        removeBooking(Integer.toString(id), b.getName(),
                                                b.getSiteLocation(), b.getLocation());
                                        break;
                                    }
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(),
                                        "Booking ID is not valid", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else if (speechText.contains("out") ||
                            speechText.contains("log") ||
                            speechText.contains("sign")) {
                        logoutUser();
                    }
                }
                break;
            }
        }
    }

    /**
     * Check if the date is valid
     */
    private boolean isValidBookDate(String date) {

        StringTokenizer stringTokenizer = new StringTokenizer(date);
        String day, month, hourAndMins, fullDate;

        // Separate each word
        hourAndMins = stringTokenizer.nextElement().toString() + " "
                + stringTokenizer.nextElement().toString();
        day = stringTokenizer.nextElement().toString();
        month = stringTokenizer.nextElement().toString();
        day = day.replace(",", "");

        if (day.contains("st")) {
            day = day.replace("st", "");
        } else if (day.contains("nd")) {
            day = day.replace("nd", "");
        } else if (day.contains("rd")) {
            day = day.replace("rd", "");
        } else if (day.contains("th")) {
            day = day.replace("th", "");
        }

        fullDate = "2018 " + month + " " + day + " " + hourAndMins;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMMM d hh:mm aa");
        Date currentDate = new Date();
        try {
            Date date1 = sdf.parse(fullDate);
            if (currentDate.equals(date1) || currentDate.before(date1)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create a new booking based on parameters
     */
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
                        valid = true;
                        name = rName;
                        siteLocation = rSiteLocation;
                        location = rLocation;
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
                        valid = false;
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (valid) {
                    c.StopTick();
                    Intent intent = new Intent(HomescreenActivity.this, AlarmActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("siteLocation", siteLocation);
                    intent.putExtra("location", location);
                    startActivity(intent);
                    finish();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                valid = false;
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
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * LRetrieve all books and updates them on the ListView
     */
    private void getAllBookings(final String userID) {
        // Tag used to cancel the request
        String tag_string_req = "req_getBookings";

        // Progress box
        pDialog.setMessage("Getting recent bookings..");
        showDialog();

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
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateBookingStatus(final String status, final String rName, final String rSiteLocation, final String rLocation) {
        // Tag used to cancel the request
        String tag_string_req = "req_updatebooking";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_BOOKING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update booking: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        RecentBookingsAdapter adapter = new RecentBookingsAdapter(
                                getContext(), bookingList);
                        recentBookingsList.setAdapter(adapter);
                    } else {
                        // Error occurred in database. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                valid = false;
                Log.e(TAG, "Booking update error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to createbooking url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", status);
                params.put("name", rName);
                params.put("siteLocation", rSiteLocation);
                params.put("location", rLocation);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void removeBooking(final String id, final String rName, final String rSiteLocation, final String rLocation) {
        // Tag used to cancel the request
        String tag_string_req = "req_removebooking";

        // Progress box
        pDialog.setMessage("Checking booking...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REMOVE_BOOKING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Remove booking " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext(),
                                "Booking removal/completion done",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // Error occurred in database. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getAllBookings(userID);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e(TAG, "Booking removal error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to createbooking url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("name", rName);
                params.put("siteLocation", rSiteLocation);
                params.put("location", rLocation);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            c.StopTick();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
