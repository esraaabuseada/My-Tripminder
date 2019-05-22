package com.esraa.android.plannertracker.TripDetails;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.esraa.android.plannertracker.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import java.util.ArrayList;
import java.util.List;

public class StaticMap extends AppCompatActivity {
    private MapView mapView;
    List<LatLng> points = new ArrayList<LatLng>();
    private double lat1, lat2;
    private double lon1, lon2;
    CameraPosition cameraPosition;
    private static final String token = "sk.eyJ1IjoibWlsa3lyYW5nZXIiLCJhIjoiY2pzOTBzOXlxMTZ6ZDN6czhiNTJjY2JrdCJ9.TVE3NN-juPXRMYr14hRBFA";

    public StaticMap()
    {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, token);
        setContentView(R.layout.activity_static_map);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        Intent intent = getIntent();
        lat1 = intent.getDoubleExtra("lat1", 0);
        lon1 = intent.getDoubleExtra("lon1", 0);
        lat2 = intent.getDoubleExtra("lat2", 0);
        lon2 = intent.getDoubleExtra("lon2", 0);

        points.add(new LatLng(lat1, lon1));
        points.add(new LatLng(lat2, lon2));
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat1, lon1))
                .zoom(17)
                .bearing(0)
                .tilt(30)
                .build();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap)
            {

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition), 7000);

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded()
                {
                    @Override
                    public void onStyleLoaded(@NonNull Style style)
                    {

                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lon1)));
                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(lat2, lon2)));
                        mapboxMap.addPolyline(new PolylineOptions().width(2).addAll(points));


                    }
                });
            }
        });

    }

    public void setLat1(double lat1) {
        this.lat1 = lat1;
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public void setLon1(double lon1) {
        this.lon1 = lon1;
    }

    public void setLon2(double lon2) {
        this.lon2 = lon2;
    }

    public double getLat1() {
        return lat1;
    }

    public double getLat2() {
        return lat2;
    }

    public double getLon1() {
        return lon1;
    }

    public double getLon2() {
        return lon2;
    }
}