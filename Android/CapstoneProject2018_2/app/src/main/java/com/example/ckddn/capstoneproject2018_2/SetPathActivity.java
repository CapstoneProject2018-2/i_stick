package com.example.ckddn.capstoneproject2018_2;

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
import com.skt.Tmap.TMapPOIItem;
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
import java.util.ArrayList;

public class SetPathActivity extends AppCompatActivity {
    private String uno, pno;
    private String keyword;
    TMapView tMapView;
    PoiListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_path);

        uno = getIntent().getStringExtra("uno");
        pno = getIntent().getStringExtra("pno");

        final TMapData tMapData = new TMapData();
        final ListView listView;

        adapter = new PoiListViewAdapter();

        listView = (ListView) findViewById(R.id.poi_listView);
        listView.setAdapter(adapter);

        final EditText poiName_editText;
        poiName_editText = (EditText) findViewById(R.id.des_edit_text);
        Button searchBtn;
        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new PoiListViewAdapter();
                listView.setAdapter(adapter);
                keyword = poiName_editText.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    tMapData.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (TMapPOIItem poi : arrayList) {
                                        adapter.notifyDataSetChanged();
                                        adapter.putSearchResults(new POI(poi));
                                    }
                                    if (arrayList.size() > 0) {
                                        TMapPOIItem poi = arrayList.get(0);
                                    }
                                }
                            });
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TMapPoint poiPoint = arrayList.get(position).getPOIPoint();
                                    new SendDestTask().execute(
                                            "http://" + ServerInfo.ipAddress + "/parent/setDestination",
                                            poiPoint.getLongitude() + "", poiPoint.getLatitude() + "");
                                    // Toast.makeText(SetPathActivity.this,
                                    // arrayList.get(position).getPOIPoint().toString(), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n");

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
}
