package com.example.ckddn.capstoneproject2018_2;

import com.skt.Tmap.TMapPoint;

public class PathItem {
    public static final boolean NODETYPE_POINT = true;
    public static final boolean NODETYPE_LINESTRING = false;
    public static final int TURNTYPE_LEFT = 12;
    public static final int TURNTYPE_RIGHT = 13;


    private static final int NON_TURNTYPE = -1;

    private boolean nodeType;
    private int turnType;
    private TMapPoint point;

    public PathItem(boolean nodeType, TMapPoint point) {    //  LineString
        this.nodeType = nodeType;
        this.turnType = NON_TURNTYPE;
        this.point = point;
    }

    public PathItem(boolean nodeType, int turnType, TMapPoint point) {
        this.nodeType = nodeType;
        this.turnType = turnType;
        this.point = point;
    }

    public boolean getNodeType() {
        return this.nodeType;
    }

    public void setNodeType(boolean nodeType) {
        this.nodeType = nodeType;
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
        if (this.nodeType == true) {
            return "nodeType: POINT, turnType: " + this.turnType + "\nLon: " + point.getLongitude() + "\nLat: " + point.getLatitude() + "\n";
        } else {
            return "nodeType: LINESTRING\nLon: " + point.getLongitude() + "\nLat: " + point.getLatitude() + "\n";
        }
    }
}
