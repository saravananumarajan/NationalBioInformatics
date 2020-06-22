package com.example.nationalbioinformatics;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nationalbioinformatics.adapter.AsyncMarker;
import com.example.nationalbioinformatics.adapter.Singleton;
import com.example.nationalbioinformatics.collectionofdata.Hospitals;
import com.example.nationalbioinformatics.collectionofdata.MyItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener {

    private GoogleMap mMap;
    Button bt;
    public static String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3,
                2, MapsActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
        bt.setOnClickListener(this);
    }

    private void init() {
        bt = findViewById(R.id.hospital);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hospital:
                getHospitalData();

                break;
        }
    }

    private void getHospitalData() {

        String url = "https://api.rootnet.in/covid19-in/hospitals/medical-colleges";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    Log.e("data", data);
                    JSONObject medicalclg = new JSONObject(data);
                    String medicalclgs = medicalclg.getString("medicalColleges");
                    Log.e("medical", medicalclgs);
                    JSONArray jsonArray = new JSONArray(medicalclgs);

                    Hospitals[] hospitals = new Hospitals[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String state = jsonObject1.getString("state");
                        String name = jsonObject1.getString("name");
                        String city = jsonObject1.getString("city");
                        String ownership = jsonObject1.getString("ownership");
                        //Log.e("state",state);
                        hospitals[i] = new Hospitals(state, name, city, ownership);
                    }
                    drawMarker(hospitals);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Singleton.getInstance(this).addtoRequestqueue(stringRequest);
    }

    private void drawMarker(Hospitals[] hospitals) {
        int count=0;
        LatLng[] latLng=null;
        ArrayList arrayList=new ArrayList<>();

        for (int i = 0; i < hospitals.length; i++) {
            if (hospitals[i].getState().equals(state)) {
                arrayList.add(hospitals[i].getName());
            }
        }
        AsyncMarker asyncMarker = new AsyncMarker(this, arrayList, mMap);
        asyncMarker.execute();


    }


    @Override
    public void onLocationChanged(Location location) {
        String latitude= String.valueOf(location.getLatitude());
        String longitude= String.valueOf(location.getLongitude());
        Log.e("latitude",latitude);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
             state = addresses.get(0).getAdminArea();
Log.e("statefor",state);
            String countryName = addresses.get(0).getAddressLine(2);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}