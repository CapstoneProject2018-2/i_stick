package com.example.ckddn.capstoneproject2018_2;

import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapData.FindAllPOIListenerCallback;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

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
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;


public class SetPathActivity extends AppCompatActivity {
//    final TMapData tMapData = new TMapData();

    private ListView listView;
    private EditText poiName_editText;

    private String uno, pno, pMobile;
    private String keyword;
    private TMapView tMapView;
    private PoiListViewAdapter adapter;

    private TMapPoint userMapPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_path);

        uno = getIntent().getStringExtra("uno");
        pno = getIntent().getStringExtra("pno");
        pMobile = getIntent().getStringExtra("pMobile");

        listView = (ListView) findViewById(R.id.poi_listView);
        poiName_editText = (EditText) findViewById(R.id.des_edit_text);

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n");


        /* get User's lastest position */
        new ReqLocTask().execute("http://" + IStickInfo.ipAddress + "/parent/reqLoc");

        Button searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetAroundPOIList().execute();
            }
        });

    }


    public class GetAroundPOIList extends AsyncTask<String, String, String> {
        String TAG = "GetAroundPOIList";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapter = new PoiListViewAdapter();
        }

        @Override
        protected String doInBackground(String... strings) {
            adapter = new PoiListViewAdapter();
            keyword = poiName_editText.getText().toString();
            if (!TextUtils.isEmpty(keyword)) {
                try {
                    /* 도보로 길찾기는 직선거리 8km이내로 제한되어 있음, 기준 정하기: 시각장애인 더 제한 nRadius: 5 */
                    final ArrayList<TMapPOIItem> array = new TMapData().findAroundKeywordPOI(userMapPoint, keyword,5, 500);
//                    Log.d(TAG, "doInBackground: " + array.size());
                    for (TMapPOIItem poi : array) {
                        adapter.notifyDataSetChanged();
                        adapter.putSearchResults(new POI(poi));
                    }
                    if (array.size() > 0) {
                        TMapPOIItem poi = array.get(0);
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TMapPoint poiPoint = array.get(position).getPOIPoint();
                            new SendDestTask().execute(
                                    "http://" + IStickInfo.ipAddress + "/parent/setDestination",
                                        Double.toString(poiPoint.getLongitude()), Double.toString(poiPoint.getLatitude()));
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(adapter);
        }
    }

    public class SendDestTask extends AsyncTask<String, String, String> {
        String TAG = "SendDestTask>>>";

        @Override
        protected String doInBackground(String... strings) {
            try { // json accumulate
                JSONObject destInfo = new JSONObject();
                destInfo.accumulate("uno", uno);
                destInfo.accumulate("pno", pno);
                destInfo.accumulate("longitude", strings[1]);
                destInfo.accumulate("latitude", strings[2]);
                destInfo.accumulate("mobile", pMobile);

                Log.d(TAG, "doInBackground: create json");
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try { // for HttpURLConnection
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST"); // POST방식
                    conn.setRequestProperty("Cache-Control", "no-cache"); // 컨트롤 캐쉬 설정(?)
                    conn.setRequestProperty("Content-Type", "application/json"); // json형식 전달
                    conn.setRequestProperty("Accept", "application/text"); // text형식 수신
                    conn.setRequestProperty("Accept", "application/json"); // json형식 수신
                    conn.setDoOutput(true); // OutputStream으로 POST데이터 전송
                    conn.setDoInput(true); // InputStream으로 서버로부터 응답 전달받음
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    // 버퍼생성
                    writer.write(destInfo.toString());
                    writer.flush();
                    writer.close();
                    // send Sign In Info to Server...
                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        // readLine : string or null(if end of data...)
                        buffer.append(line);
                        Log.d(TAG, "doInBackground: readLine, " + line);
                    }
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    // 이상한 URL일 때
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
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    /* Request User Location */
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
                    while ((line = reader.readLine()) != null) {
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
                userMapPoint = new TMapPoint(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                Log.d(TAG, "onPostExecute: " + userMapPoint.toString());
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }
    }

}
