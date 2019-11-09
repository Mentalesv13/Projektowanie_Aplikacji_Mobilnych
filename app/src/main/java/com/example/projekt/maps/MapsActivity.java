package com.example.projekt.maps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.projekt.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    //TextView locationText;
    Marker mTheater;
    Marker myMarker;
    LocationManager locationManager;
    Button btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                MapsActivity.this.onBackPressed();
            }
        });

        getLocation();

    }


    void getLocation() {
        //Toast.makeText(MapsActivity.this, "GPS", Toast.LENGTH_LONG).show();
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

        String address="xxx";
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            // locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
            //  addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
            address=addresses.get(0).getAddressLine(0);

        }catch(Exception e)
        {

        }


        LatLng MyPosition = new LatLng(location.getLatitude(),location.getLongitude());
        myMarker= mMap.addMarker(new MarkerOptions().
                position(MyPosition)
                .title(address)
                .snippet("you are there")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        myMarker.setTag(0);


    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MapsActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(MapsActivity.this, "GPS and Internet are enabled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            mMap.getUiSettings().setMapToolbarEnabled(true);


            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }


        LatLng TheaterPosition = new LatLng(51.780090,19.472870);


        mTheater = mMap.addMarker(new MarkerOptions().
                position(TheaterPosition)
                .title("Music Theater")
                .snippet("most popular theatre in Lodz"));
        mTheater.showInfoWindow();

        mTheater.setTag(0);
        // mTheater.setIcon(BitmapDescriptorFactory.fromPath("@drawable/icon.png"));
        // mTheater.setIcon(BitmapDescriptorFactory.fromFile("@drawable/icon.png"));
        // .icon(BitmapDescriptorFactory.fromBitmap()));
        float zoom=13;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TheaterPosition,zoom));

        mMap.setOnMarkerClickListener(this);
        // mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 400, null);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(myMarker.getPosition(), mTheater.getPosition())
                .width(10)
                .color(Color.RED));
        //line.setClickable(true);



        if (marker.equals(mTheater)) {

            Toast.makeText(this, "to navigate to "+
                            marker.getTitle() +
                            " click blue arrow in right bottom corner",
                    Toast.LENGTH_LONG).show();
        }


        return false;
    }
}
