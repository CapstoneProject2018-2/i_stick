package com.example.ckddn.capstoneproject2018_2;

import com.skt.Tmap.TMapPoint;

public class PathItem {
    private int turnType;
    private TMapPoint point;

    public PathItem(int turnType, TMapPoint point) {
        this.turnType = turnType;
        this.point = point;
    }

    public int getTurnType() {
        return turnType;
    }

    public void setTurnType(int turnType) {
        this.turnType = turnType;
    }

    public TMapPoint getPoint() {
        return point;
    }

    public void setPoint(TMapPoint point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "turnType: " + this.turnType + "\nLon: " + point.getLongitude() + "\nLat: " + point.getLatitude() + "\n";
    }
}
