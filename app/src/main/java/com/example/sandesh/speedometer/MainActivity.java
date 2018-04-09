package com.example.sandesh.speedometer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    long dist2=0;
    long dist=0;
    boolean read_flag=false;
    boolean initial_flag=false;
    double initial_dist=0.0;
    List<LatLng> latlng=new ArrayList<LatLng>();

    private Button mapButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });

        if ((ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 777);
                }

            }
        }

        if ((ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 777);
                }

            }
        }

        Thread distance=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    Thread t=Thread.currentThread();
                    Log.d("diatnce","Thread priority is"+t.getPriority());
                    if (read_flag) {
                        //dist += SphericalUtil.computeLength(latlng);
                    }
                }
            }
        });
        distance.setPriority(10);
        //distance.start();
        Thread main=Thread.currentThread();
        main.setPriority(5);
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                TextView txt=(TextView)findViewById(R.id.id1);
                if(location==null){
                    Log.d("listner","got no location");
                    txt.setText("--m/s");
                }
                else{
                    Thread t=Thread.currentThread();
                    Log.d("listner","My thread priority is"+t.getPriority());
                    Log.d("listner","got location");
                    float speed=location.getSpeed();
                    Log.d("listner","Latitude"+location.getLatitude()+"Longitude"+location.getLongitude());
                    latlng.add(new LatLng(location.getLatitude(),location.getLongitude()));
                    Log.d("listner","distrance is"+SphericalUtil.computeLength(latlng));
                    long dist1=(long)SphericalUtil.computeLength(latlng);
                    if(dist1>dist2 && initial_flag){
                        Log.d("listner","distance changed\n old one:"+dist2+" and new distance"+dist1);
                        dist+=dist1-dist2;
                        dist2=dist1;
                    }
                    if(!initial_flag) {//check flag
                        Log.d("listner","initial disatnce"+dist1);
                        initial_flag=true;
                        initial_dist=dist1;
                        dist-=initial_dist;
                        dist2=dist1;
                    }
                    txt.setText("Speed:\n"+speed+"m/s"+"\ndistance\n"+dist+"m");
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}

        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    public void openMapActivity() {
        Intent showMapActivity = new Intent(this, MapVisualization.class);
        startActivity(showMapActivity);
    }
}
