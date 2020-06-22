package com.example.nationalbioinformatics.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nationalbioinformatics.collectionofdata.MyItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AsyncMarker extends AsyncTask<Void,String,ArrayList<LatLng>> {
    Context context;
    ArrayList address;
    GoogleMap mMap;
ProgressDialog progressDialog;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("We are getting your Hospitals");
        progressDialog.show();
    }

    private ClusterManager<MyItem> mClusterManager;
    @Override
    protected ArrayList<LatLng> doInBackground(Void... voids) {
        ArrayList<LatLng> p1 = new ArrayList<LatLng>();
        for(int i=0;i<address.size();i++) {
            p1.add(getLocationFromAddress(address.get(i).toString()));
        }
        return p1;
    }
    public AsyncMarker(Context context, ArrayList address, GoogleMap map)
    {
        this.context=context;
        this.address=address;
        this.mMap=map;
    }
    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latLng=null;
        try {
            address = coder.getFromLocationName(strAddress, 1);
            if(address!=null) {
                Address location = address.get(0);
                Log.e("enter","enter");
                  latLng=new LatLng(location.getLatitude(),location.getLongitude());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> latLng) {
        super.onPostExecute(latLng);
setUpClusterer(latLng);
progressDialog.dismiss();
    }


    private void setUpClusterer(ArrayList<LatLng> arrayList) {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arrayList.get(0), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(context, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems(arrayList);
    }

    private void addItems(ArrayList<LatLng> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            MyItem offsetItem = new MyItem(arrayList.get(i).latitude, arrayList.get(i).longitude);
            mClusterManager.addItem(offsetItem);
        }
    }


}
