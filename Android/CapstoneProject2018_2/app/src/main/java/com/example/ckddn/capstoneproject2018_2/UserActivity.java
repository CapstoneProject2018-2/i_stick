package com.example.ckddn.capstoneproject2018_2;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//LocationManager & LocationListener를 이용한 실시간 위치 찍기
//ver 1.0
//양인수


public class UserActivity extends AppCompatActivity {
    final private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
    private String uno;
    private String userId;
    TextView textView;
    ToggleButton getLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        uno = getIntent().getStringExtra("no");
        userId = getIntent().getStringExtra("id");

        textView = (TextView)findViewById(R.id.user_location_result);
        textView.setText("위치정보 미수신중"); //DEFAULT

        getLoc = (ToggleButton)findViewById(R.id.getLoc);

        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, permissions, PackageManager.PERMISSION_GRANTED);
        getLoc.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
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


            /* 이곳에 네트워크에 위치 보내는 코드 작성 */
            new SendLocTask().execute("http://" + ServerInfo.ipAddress +"/user", longitude+"", latitude+"");


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


    // by ckddn
    public class SendLocTask extends AsyncTask<String, String, String> {
        String TAG = "SendLocTask>>>";
        @Override
        protected String doInBackground(String... strings) {
            try {   //  json accumulate
                JSONObject locationInfo = new JSONObject();
                locationInfo.accumulate("uno", uno);
                locationInfo.accumulate("longitude", strings[1]);
                locationInfo.accumulate("latitude", strings[2]);

                Log.d(TAG, "doInBackground: create json");
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {   //  for HttpURLConnection
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");  //  POST방식
                    conn.setRequestProperty("Cache-Control", "no-cache");        // 컨트롤 캐쉬 설정(?)
                    conn.setRequestProperty("Content-Type", "application/json"); // json형식 전달
                    conn.setRequestProperty("Accept", "application/text");       // text형식 수신
                    conn.setRequestProperty("Accept", "application/json");       // json형식 수신
                    conn.setDoOutput(true); //  OutputStream으로 POST데이터 전송
                    conn.setDoInput(true);  //  InputStream으로 서버로부터 응답 전달받음
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    //  버퍼생성
                    writer.write(locationInfo.toString());
                    writer.flush();
                    writer.close();
                    //  send Sign In Info to Server...
                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while((line = reader.readLine()) != null) {
                        //  readLine : string or null(if end of data...)
                        buffer.append(line);
                        Log.d(TAG, "doInBackground: readLine, " + line);
                    }
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    //  이상한 URL일 때
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equals("ok"))
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
