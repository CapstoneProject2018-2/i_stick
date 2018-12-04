package com.example.ckddn.capstoneproject2018_2.Oblu;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ckddn.capstoneproject2018_2.R;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//디버그용 위치를 찍어주는 클래스
//implemented by Insu Yang

public class PointDrawer {

    public static ArrayList<TMapPoint> tMapPointArrayList = new ArrayList<TMapPoint>();

//    public static void addPoint(TMapPoint pos){
//        tMapPointArrayList.add(pos);
//    }

    public TMapMarkerItem item = new TMapMarkerItem();

    public static void drawPoint(TMapPoint pos, int dotflag, Context context, TMapView tMapView, int itemID){

        if(dotflag == 1){ //dr point를 그려줄 때
            TMapMarkerItem item1 = new TMapMarkerItem();
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.blue_point_resize);

            item1.setTMapPoint(pos);
            item1.setName("" + System.currentTimeMillis());
            item1.setVisible(item1.VISIBLE);
            item1.setIcon(bitmap);
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_point_resize);
            tMapView.addMarkerItem("" +itemID, item1);


        }else if(dotflag ==0){ //gps point를 그려줄 때4
            TMapMarkerItem item2 = new TMapMarkerItem();
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_point);

            item2.setTMapPoint(pos);
            item2.setName("" + System.currentTimeMillis());
            item2.setVisible(item2.VISIBLE);
            item2.setIcon(bitmap);
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gps_point);

            tMapView.addMarkerItem("" +itemID, item2);
        }


    }


}
