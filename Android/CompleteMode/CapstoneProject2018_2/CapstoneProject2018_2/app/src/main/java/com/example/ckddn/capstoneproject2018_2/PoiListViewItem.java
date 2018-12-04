package com.example.ckddn.capstoneproject2018_2;

public class PoiListViewItem {
    private String poiName;
    private String address;
    private double longitude;
    private double latitude;

    public void setPoiName(String poiName){this.poiName = poiName;}
    public void setAddress(String address){this.address = address;}

    public void setLongitude(Double longitude){this.longitude = longitude;}
    public void setLatitude(Double latitude){this.latitude = latitude;}
    public String getPoiName(){return this.poiName;}
    public String getAddress(){return this.address;}
    public double getLongitude(){return this.longitude;}
    public double getLatitude(){return this.latitude;}
}
