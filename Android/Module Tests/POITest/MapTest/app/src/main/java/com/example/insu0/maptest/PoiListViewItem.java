package com.example.insu0.maptest;

import com.skt.Tmap.TMapPOIItem;

public class PoiListViewItem {

    private String poiName;
    private String address;
    private double longitude;
    private double lattitude;

    public void setPoiName(String poiName){
        this.poiName = poiName;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public void setLongitude(Double longitude){
        this.longitude = longitude;
    }

    public void setLattitude(Double lattitude){
        this.lattitude = lattitude;
    }

    public String getPoiName(){
        return this.poiName;
    }

    public String getAddress(){
        return this.address;
    }

    public double getLongitude(){
        return  this.longitude;
    }

    public double getLattitude(){
        return  this.lattitude;
    }

}
