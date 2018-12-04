package com.example.ckddn.capstoneproject2018_2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {
    /*  implements!!! */
    //  info from LoginActivity
    String uno;
    String userId;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        uno = getIntent().getStringExtra("no");
        userId = getIntent().getStringExtra("id");

        textView = (TextView) findViewById(R.id.curLocation);
        Button button = (Button) findViewById(R.id.button);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, mLocationListener);
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, mLocationListener);

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Double longitude = location.getLongitude();
            Double latitude = location.getLatitude();
            Double altitude = location.getAltitude();
            float accuracy = location.getAccuracy();
            String provider = location.getProvider();
            textView.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}
