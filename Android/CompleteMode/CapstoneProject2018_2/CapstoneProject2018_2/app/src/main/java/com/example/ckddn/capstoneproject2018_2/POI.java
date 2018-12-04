package com.example.ckddn.capstoneproject2018_2;

import com.skt.Tmap.TMapPOIItem;

public class POI {
    TMapPOIItem item;
    public POI(TMapPOIItem item){
        this.item = item;
    }

    public String POIgetName(){
        return item.getPOIName();
    }

    public String POIgetAddress(){
        return item.getPOIAddress();
    }

    public double POIgetLongitude(){
        String POIfrontLon = item.frontLon;
        double longitude = Double.parseDouble(POIfrontLon);
        return longitude;
    }

    public double POIgetLatitude(){
        String POIfrontLat = item.frontLat;
        double latitude = Double.parseDouble(POIfrontLat);
        return latitude;
    }


    @Override
    public String toString(){
        return item.getPOIName();
    }

}
