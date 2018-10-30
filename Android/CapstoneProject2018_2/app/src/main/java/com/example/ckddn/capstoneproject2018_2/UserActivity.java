package com.example.ckddn.capstoneproject2018_2;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

//LocationManager & LocationListener를 이용한 실시간 위치 찍기
//ver 1.0
//양인수


public class UserActivity extends AppCompatActivity {

    TextView textView;
    ToggleButton getLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        textView = (TextView)findViewById(R.id.user_location_result);
        textView.setText("위치정보 미수신중"); //DEFAULT

        getLoc = (ToggleButton)findViewById(R.id.getLoc);

        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        getLoc.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try{
                    if(getLoc.isChecked()){
                        textView.setText("수신중...");

                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                                100, // 0.1초
                                1, // 1m 이상 움직이면 갱신한다
                                mLocationListener); //위치가 바뀌면 onLocationChanged로 인해서 갱신된다.
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                100, // 0.1초
                                1, // 1m 이상 움직이면 갱신
                                mLocationListener);
                    }else{
                        textView.setText("위치정보 미수신중");
                        lm.removeUpdates(mLocationListener);
                    }
                }catch (SecurityException ex){}
            }
        });

    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도

            //getAccuracy는 horizontal accuracy라는 값을 사용하는데 정확한 정체는 확인이 힘들다
            //값이 높을 수록 좌표의 신뢰성이 높다고 생각하면 될듯

            String provider = location.getProvider();   //위치제공자


            /*이곳에 네트워크에 위치 보내는 코드 작성*/


            textView.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);
        }
        //LocationListener 때문에 넣어둔 함수들.
        //어짜피 폰에 찍히기 때문에 Log는 무시해도 무방하다.
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
}
