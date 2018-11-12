package com.example.insu0.maptest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

/*POI LISTVIEW VERSION*/
/*implemented by Insu Yang */


public class MainActivity extends AppCompatActivity{

    TMapView tMapView;
    String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TMapData tMapData = new TMapData();

        final ListView listview;
        final PoiListViewAdapter adapter;

        adapter = new PoiListViewAdapter();

        listview = (ListView)findViewById(R.id.poi_listView);
        listview.setAdapter(adapter);

        final EditText poiName_editText;
        poiName_editText = (EditText)findViewById(R.id.des_edit_text);
        Button searchBtn;
        searchBtn = (Button)findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = poiName_editText.getText().toString();
                if(!TextUtils.isEmpty(keyword)){
                    tMapData.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for(TMapPOIItem poi : arrayList){
                                        adapter.putSearchResult(new POI(poi));
                                    }
                                    if(arrayList.size()>0){
                                        TMapPOIItem poi = arrayList.get(0);
                                    }
                                }
                            });

                            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Toast.makeText(MainActivity.this, arrayList.get(position).getPOIPoint().toString(),Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    });
                }
            }
        });



        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey( "85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n" );





    }





}


