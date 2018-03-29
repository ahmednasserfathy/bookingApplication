package info.androidhive.bookingApplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.bookingApplication.R;
import info.androidhive.bookingApplication.app.AppConfig;
import info.androidhive.bookingApplication.app.AppController;
import info.androidhive.bookingApplication.helper.ListViewResources;
import info.androidhive.bookingApplication.helper.Resource;

public class ShowResourcesActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = ShowResourcesActivity.class.getSimpleName();
    private String[] items2 = null;
    private ProgressDialog pDialog;
    private Spinner dropdown;
    private Spinner dropdown2;
    private Button btnFind;
    private ListView resourcesList;
    private ArrayList<Resource> newList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resources);

        //get the spinner from the xml.
        dropdown = findViewById(R.id.spinner);
        dropdown2 = findViewById(R.id.spinner2);
        btnFind = findViewById(R.id.btnFind);
        resourcesList = findViewById(R.id.resourcesList);
        resourcesList.setEmptyView(findViewById(R.id.empty));
        newList = new ArrayList<>();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //create a list of items for the spinner.
        String[] items = new String[]{"Adestts", "Charles Building", "Cantor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        btnFind.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Clear the list view
                ListViewResources sampleAdapter = (ListViewResources) resourcesList.getAdapter();
                if (sampleAdapter != null) {
                    sampleAdapter.clearData();
                    sampleAdapter.notifyDataSetChanged();
                }
                String sL = (String) dropdown.getSelectedItem();
                String l = (String) dropdown2.getSelectedItem();
                getResources(sL, l);
            }

        });
    }

    private Context getContext() {
        return this;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String text = (String) parent.getSelectedItem();
        switch (text) {
            case "Adestts":
                items2 = new String[]{"Level 1", "Level 2", "Level 3", "Level 4", "Level 5"};
                break;
            case "Charles Building":
                items2 = new String[]{"Level 1", "Level 2"};
                break;
            case "Cantor":
                items2 = new String[]{"Level 1", "Level 2", "Level 3", "Level 4"};
                break;
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        //set the spinners adapter to the previously created one.
        dropdown2.setAdapter(adapter2);
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getResources(final String siteLocation, final String location) {
        // Tag used to cancel the request
        String tag_string_req = "req_getResourcess";

        pDialog.setMessage("Getting resources ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_RESOURCESS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "GET_RESOURCE Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        try {
                            JSONArray data = jObj.getJSONArray("theData");
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    newList.add(new Resource((JSONObject) data.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ListViewResources viewAdapter = new ListViewResources(getContext(), newList);
                        resourcesList.setAdapter(viewAdapter);
                    } else {

                        // Error occurred in database. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        // Clear the list view
                        ListViewResources sampleAdapter = (ListViewResources) resourcesList.getAdapter();
                        sampleAdapter.clearData();
                        sampleAdapter.notifyDataSetChanged();
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
                // Posting params to getresoucre url
                Map<String, String> params = new HashMap<String, String>();
                params.put("siteLocation", siteLocation);
                params.put("location", location);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
