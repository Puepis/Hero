package com.example.phili.pinger;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

// Volley
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

// Time
import java.util.Calendar;
import java.util.Date;

// Google API for Location
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends Activity {

    // Widgets
    private EditText input;
    private Button enter;
    private TextView mainText;
    private TextView smallText;
    private String device_location;
    private ImageButton checkButton;
    private ImageButton xButton;
    private String currentCheck = "safe";
    private boolean safetyCheck = true;
    private boolean injuredCheck = false;
    private final int LOCATION_REQUEST_CODE = 1000;

    // Location client
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Widgets
        input = findViewById(R.id.text_input);
        enter = findViewById(R.id.enter_button);
        mainText = findViewById(R.id.textViewMain);
        smallText = findViewById(R.id.textViewSmall);
        checkButton = findViewById(R.id.button_1);
        xButton = findViewById(R.id.button_2);

        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (currentCheck.equals("safe")) {
                    currentCheck = "injured";
                    safetyCheck = (v.getId() == R.id.button_1);
                    mainText.setText("Are you injured?");

                }
                else {
                    currentCheck = "None";
                    injuredCheck = (v.getId() == R.id.button_1);
                    smallText.setText("Hang Tight");
                    mainText.setText("Help is on the way");
                }
            }
        };

        // Event listener
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRequest();
            }
        });
        checkButton.setOnClickListener(buttonListener);
        xButton.setOnClickListener(buttonListener);


        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);

        }

        // Get Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Retrieved last known location
                        if (location != null) {
                            device_location = String.format("Latitude: %f, Longitude: %f", location.getLatitude(), location.getLongitude());
                            Log.d("Location", device_location);
                }
            }
        });
    }

    public void onRequestPermissionResult(int requestCode,
                                          @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    public void postRequest() {

        // Uses Volley (HTTP Library)

        try {

            // Instantiate RequestQueue
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://4129fd90.ngrok.io/JSON_insert";

            JSONObject jsonBody = new JSONObject();

            // Time
            Date currentTime = Calendar.getInstance().getTime();

            // Construct json body from input fields
            jsonBody.put("name", input.getText().toString());
            jsonBody.put("location", device_location);

            if (!safetyCheck)
                    jsonBody.put("safe","Unsafe");
            else if (injuredCheck)
                jsonBody.put("safe", "safe - injured");
            else jsonBody.put("safe", "safe - uninjured");

            jsonBody.put("name", input.getText().toString());
            jsonBody.put("date", currentTime);



            // Convert to string
            final String requestBody = jsonBody.toString();

            // Making a POST request
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            smallText.setText("Hang tight");
                            mainText.setText("Help is on the way");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mainText.setText(error.toString());
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            Log.d("OUTPUT", requestBody.toString());

            // Add request to queue
            queue.add(postRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
