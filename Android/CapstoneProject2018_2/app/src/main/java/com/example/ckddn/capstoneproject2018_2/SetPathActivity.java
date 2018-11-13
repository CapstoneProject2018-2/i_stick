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

        final TMapData tMapData = new TMapData();
        final ListView listView;

        adapter = new PoiListViewAdapter();

        listView = (ListView)findViewById(R.id.poi_listView);
        listView.setAdapter(adapter);

        final EditText poiName_editText;
        poiName_editText = (EditText)findViewById(R.id.des_edit_text);
        Button searchBtn;
        searchBtn = (Button)findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adapter = new PoiListViewAdapter();
                                    listView.setAdapter(adapter);
                                    keyword = poiName_editText.getText().toString();
                                    if(!TextUtils.isEmpty(keyword)){
                                        tMapData.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
                                            @Override
                                            public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                                                runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for(TMapPOIItem poi : arrayList){
                                        adapter.notifyDataSetChanged();
                                        adapter.putSearchResults(new POI(poi));
                                    }
                                    if(arrayList.size()>0){
                                        TMapPOIItem poi = arrayList.get(0);
                                    }
                                    }
                            });
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Toast.makeText(SetPathActivity.this, arrayList.get(position).getPOIPoint().toString(),Toast.LENGTH_LONG).show();

                                }
                            });




                        }
                    });
                }
            }
        });

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey( "85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n" );

        uno = getIntent().getStringExtra("uno");
        pno = getIntent().getStringExtra("pno");



    }
}
