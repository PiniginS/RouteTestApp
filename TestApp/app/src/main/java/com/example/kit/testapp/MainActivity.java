package com.example.kit.testapp;

import android.Manifest;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.kit.testapp.classes.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMap);
        mapFragment.getMapAsync(this);
    }


    public void jsonLoad(View view) {//button function
        TextView t = (TextView) findViewById(R.id.text_test);
        TextView t2 = (TextView) findViewById(R.id.text_json_url);
        Route route = new Route(t2.getText().toString());
        List<LatLng> routePoints = route.getRoutePoints();
        if (routePoints.size() == 0) {
            t.setText("No route points");
            return;
        }
        if (routePoints.size() > 1) {
            googleMap.addPolyline(new PolylineOptions()//add polyline to map
                    .addAll(routePoints)
                    .width(5));

            LatLngBounds.Builder latLanBuilder = LatLngBounds.builder();
            for (LatLng point : routePoints) {
                latLanBuilder.include(point);
            }
            LatLngBounds bounds = latLanBuilder.build();

            double dist = latlng2distance(bounds.northeast, bounds.southwest);//count distance

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), (float) zoomCount(dist)));//move camera
            t.setText("Distance=" + dist + " Zoom=" + zoomCount(dist));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(routePoints.get(0), 19));//move camera
            t.setText("Single point");
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }

    private double latlng2distance(LatLng l1, LatLng l2) {//distance between first and last route points
        double R = 6372795;//earth radius

        double lat1 = l1.latitude;
        double lat2 = l2.latitude;
        double long1 = l1.longitude;
        double long2 = l2.longitude;

        lat1 *= Math.PI / 180;//coords to rad
        lat2 *= Math.PI / 180;
        long1 *= Math.PI / 180;
        long2 *= Math.PI / 180;

        double cl1 = Math.cos(lat1);//sin\cos for la and lo
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));//math for distance
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;
        double ad = Math.atan2(y, x);
        return ad * R;// metres count
    }

    private double zoomCount(double dist) {//count zoom from distance
        return -1.37344 * Math.log(3.39148 * Math.pow(10, -8) * dist);//log function for zoom count
    }
}

