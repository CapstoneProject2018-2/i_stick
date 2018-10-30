package com.example.ckddn.capstoneproject2018_2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

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
import java.util.TimerTask;


//내 담당 관리대상의 위치 보기
//implememnted by 양인수
//server implemented by 손창우

public class HisLocActivity extends AppCompatActivity {
    private String pno, uno;    //  parent no and managed user no
    double gap, longitude, latitude; //  user's lastest locInfo



    TMapView tMapView = null;
    TMapGpsManager tmapgps = null;
    Button currentLocBtn;
    LinearLayout linearLayoutTmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_loc);

        linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey( "85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n" );


        pno = getIntent().getStringExtra("pno");
        uno = getIntent().getStringExtra("uno");
        new ReqLocTask().execute("http://" + ServerInfo.ipAddress +"/parent/reqLoc");

        double curLongitude, curLatitude;




//        TMapMarkerItem curMarker = new TMapMarkerItem();
//        TMapPoint curPoint = new TMapPoint(latitude,longitude);
//        curMarker.setTMapPoint(curPoint);
//        curMarker.setVisible(TMapMarkerItem.VISIBLE);
//        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_place_black_24dp);
//        curMarker.setIcon(bitmap);
//        tMapView.setLocationPoint(latitude,longitude);
//        tMapView.setCenterPoint(latitude,longitude,false);
//        tMapView.addMarkerItem("현 위치", curMarker);
//
//        tMapView.setIconVisibility(true);
//        tMapView.setZoomLevel(15);
//        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
//        linearLayoutTmap.addView( tMapView );







    }


    public class ReqLocTask extends AsyncTask<String, String, String> {
        String TAG = "ReqLocTask>>>";
        @Override
        protected String doInBackground(String... strings) {
            try {   //  json accumulate
                JSONObject reqUserNo = new JSONObject();
                reqUserNo.accumulate("uno", uno);

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
                    writer.write(reqUserNo.toString());
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
//            Toast.makeText(getApplicationContext(),result.toString(), Toast.LENGTH_LONG).show();
            try {
                JSONObject jsonObject = new JSONObject(result);
                gap = jsonObject.getDouble("gap");
        longitude = jsonObject.getDouble("longitude");
        latitude = jsonObject.getDouble("latitude");
                Toast.makeText(getApplicationContext(), "User Last Location from server, gap: " + jsonObject.getString("gap") +
                "\nlongitude: " + jsonObject.getString("longitude") + "\nlatitude: " + jsonObject.getString("latitude"), Toast.LENGTH_LONG).show();

                tMapView.setLocationPoint(longitude,latitude);
                tMapView.setCenterPoint(longitude,latitude);
                tMapView.setCompassMode(false);
                tMapView.setIconVisibility(true);
                tMapView.setZoomLevel(15);
                tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
                tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
                tMapView.setTrackingMode(false);
                tMapView.setSightVisible(false);
                linearLayoutTmap.addView(tMapView);

    } catch (JSONException e) {
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
    }
}
    }
}
