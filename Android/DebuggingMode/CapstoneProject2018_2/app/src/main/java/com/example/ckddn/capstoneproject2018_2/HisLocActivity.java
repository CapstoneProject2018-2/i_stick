package com.example.ckddn.capstoneproject2018_2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
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
//implememnted by 양인수, 손창우

public class HisLocActivity extends AppCompatActivity implements View.OnClickListener {
    private String pno, uno, userName, userMobile;    //  parent no and managed user no
    double gap, longitude, latitude; //  user's lastest locInfo

    TMapView tMapView = null;
    LinearLayout linearLayoutTmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_loc);

        /* get Extras */
        pno = getIntent().getStringExtra("pno");
        uno = getIntent().getStringExtra("uno");
        userName = getIntent().getStringExtra("userName");
        userMobile = getIntent().getStringExtra("userMobile");

        /* give and check permission(CALL_PHONE) */
        ActivityCompat.requestPermissions(this, IStickInfo.parent_permissions, PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /* set Click Listener */
        findViewById(R.id.call_user).setOnClickListener(this);
        findViewById(R.id.call_emergency).setOnClickListener(this);
        findViewById(R.id.currentLocBtn).setOnClickListener(this);

        // Display User location at First page
        linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey( "85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n" );
        new ShowLocTask().execute("http://" + IStickInfo.ipAddress +"/parent/reqLoc");
    }

    @Override
    public void onClick(View v) {   //  Button EventHandler
        String tel = "tel:" + userMobile;
        switch (v.getId()) {
            case R.id.call_user:    //  전화걸기
                startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));    //  사용자의 핸드폰에 전화
                break;
            case R.id.call_emergency:   //  응급전화
                startActivity(new Intent("android.intent.action.CALL", Uri.parse("112")));  //  112에 신고
                break;
            case R.id.currentLocBtn:    //  현재위치
                new ShowLocTask().execute("http://" + IStickInfo.ipAddress +"/parent/reqLoc");
                break;
        }
    }

    /* Request User Location */
    public class ShowLocTask extends AsyncTask<String, String, String> {
        String TAG = "ReqLocTask>>>";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linearLayoutTmap.removeView(tMapView);
            tMapView = new TMapView(getApplicationContext());
            tMapView.setSKTMapApiKey("85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n");

        }

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
            try {
                JSONObject jsonObject = new JSONObject(result);
                gap = jsonObject.getDouble("gap");
                longitude = jsonObject.getDouble("longitude");
                latitude = jsonObject.getDouble("latitude");
                if (gap > 300000) { // 5분이 넘은 정보
                    Toast.makeText(getApplicationContext(), "5분이 넘은 위치입니다. 정확하지 않을 수 있습니다.", Toast.LENGTH_LONG).show();
                } else {        //  5분 이내의 정보
                    Toast.makeText(getApplicationContext(),  userName + "님의 위치 정보\n시간: " + jsonObject.getInt("gap")/1000 + "초 전 위치", Toast.LENGTH_LONG).show();
                }

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
