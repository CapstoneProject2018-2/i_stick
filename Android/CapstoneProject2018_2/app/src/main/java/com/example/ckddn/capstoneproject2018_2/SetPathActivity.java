package com.example.ckddn.capstoneproject2018_2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

/* 181109 add AsyncTask by ckddn
*  should implement getting longitude and latitude using POI services
*/

public class SetPathActivity extends AppCompatActivity {
    private String uno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_path);

        uno = getIntent().getStringExtra("uno");
        Button send = (Button) findViewById(R.id.send_path);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendDestTask().execute("http://" + ServerInfo.ipAddress + "/parent/setDestination");
            }
        });
    }


    public class SendDestTask extends AsyncTask<String, String, String> {
        String TAG = "SendDestTask>>>";
        @Override
        protected String doInBackground(String... strings) {
            try {   //  json accumulate
                JSONObject destInfo = new JSONObject();
                destInfo.accumulate("uno", uno);

                /* longitude와 latitude는 보호자가 POI검색으로 받아온 목적지의 위도경도이다 */
                destInfo.accumulate("longitude", 126.9707345);
                destInfo.accumulate("latitude", 37.4818587);

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
                    writer.write(destInfo.toString());
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
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
