package info.androidhive.bookingApplication.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
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
import info.androidhive.bookingApplication.helper.RecentBookingsAdapter;
import info.androidhive.bookingApplication.helper.SQLiteHandler;
import info.androidhive.bookingApplication.helper.SessionManager;

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

        bookingList = new ArrayList<>();
//        Booking test1 = new Booking("Room 1", "12/12/2018", "Upcoming");
//        Booking test2 = new Booking("SP-PC 4", "5/2/2018", "Expired");
//        Booking test3 = new Booking("SP-PC 3", "5/2/2018", "Expired");
//        Booking test4 = new Booking("Booth-76", "5/2/2018", "Expired");
//        Booking test5 = new Booking("S-PC 1", "5/2/2018", "Expired");
//        Booking test6 = new Booking("Scan 1", "5/2/2018", "Expired");
//        bookingList.add(test1);
//        bookingList.add(test2);
//        bookingList.add(test2);
//        bookingList.add(test2);
//        bookingList.add(test2);
//        bookingList.add(test3);
//        bookingList.add(test4);
//        bookingList.add(test5);
//        bookingList.add(test6);

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
                    Log.d(TAG, "Speech input: " + speechText);

                    if (speechText.startsWith("reserve") || speechText.startsWith("Reserve")) {
                        if (speechText.contains("Room") || speechText.contains("room")) {

                            //                           "reserve room 4.1 on the 5th of April 2018 at 10:30 a.m. in adsetts level 4"

                            String method = "";
                            String name = "";
                            Double roomNum = 0.0;
                            String onWord = "";
                            String theWord = "";
                            String day = "";
                            String ofWord = "";
                            String month = "";
                            String atWord = "";
                            String hourAndMins = "";
                            String amOrPM = "";
                            String year = "";
                            String inWord = "";
                            String rSiteLocation = "";
                            String location = "";
                            String locNum = "";
                            StringTokenizer stringTokenizer = new StringTokenizer(speechText);

                            method = stringTokenizer.nextElement().toString();
                            name = stringTokenizer.nextElement().toString();
                            roomNum = Double.parseDouble(stringTokenizer.nextElement().toString());
                            onWord = stringTokenizer.nextElement().toString();
                            theWord = stringTokenizer.nextElement().toString();
                            day = stringTokenizer.nextElement().toString();
                            ofWord = stringTokenizer.nextElement().toString();
                            month = stringTokenizer.nextElement().toString();
                            year = stringTokenizer.nextElement().toString();
                            atWord = stringTokenizer.nextElement().toString();
                            hourAndMins = stringTokenizer.nextElement().toString();
                            amOrPM = stringTokenizer.nextElement().toString();
                            inWord = stringTokenizer.nextElement().toString();
                            rSiteLocation = stringTokenizer.nextElement().toString();
                            location = stringTokenizer.nextElement().toString();
                            locNum = stringTokenizer.nextElement().toString();

                            String rName = name + "-" + roomNum;
                            String rDateBooked = day + " " + month + " " + year;
                            String rLocation = location + " " + locNum;

                            capitalizeFirstLetter(rName);
                            capitalizeFirstLetter(rLocation);


//                        String[] arr = speechText.split(" ");
//                        for ( String word : arr) {
//                            if(word.startsWith("Room")){
//                                bookingName = word;
//                            }
//                            else if(){
//
//                            }
//                        }
//                        Date

                            createNewBooking(rName, rDateBooked, "Adsetts", "Level 4");
                        }
                    }
                }
                break;
            }

        }
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private void createNewBooking(final String rName, final String rDateBooked, final String rSiteLocation
            , final String rLocation) {
        // Tag used to cancel the request
        String tag_string_req = "req_createbooking";

        pDialog.setMessage("Booking new resource");
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
                        RecentBookingsAdapter adapter = new RecentBookingsAdapter(getContext(), bookingList);
                        recentBookingsList.setAdapter(adapter);
                    } else {
                        // Error occurred in database. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
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
