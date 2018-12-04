package com.example.ckddn.capstoneproject2018_2.Oblu;

//algorithm & structure implemented by Son Changwoo

/* This class deals with Seperating LineString to important point that contains turnType.*/
/* Receives LinePoints and calculate degree and find points and theirs turnTypes */

/* LineString 정보중 교차로가 나오지 않더라도 많이 굽은 길이 존재할 때
 * LineString 의 Point 들을 분석하여 심하게 꺾인 각도를 인식, 새로운 turnType을 가진 Point형 생성하여 pathlist에 추가할 수 있게 도와주는 클래스
* */

import android.util.Log;

import com.example.ckddn.capstoneproject2018_2.PathItem;
import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;

public class LineSeperator {
    private static final int SEPERATORABLE_LINEPOINT_NUM = 12;  //  나누기 위한 LineString Point의 최소 갯수
    private static final int INDEX_INTERVAL = 5;                //  벡터를 묶기위한 interval 개수, SEPERATORABLE_LINEPOINT_NUM/2 보다 작아야 한다.
    private static final double THRESHOLD_ANGLE = Math.PI/5;    //  임계각... 최적화

    private static ArrayList<LinePointVector> getDegrees(ArrayList<TMapPoint> linePoints) {
        String TAG = "getDegrees";
        if (linePoints.size() < SEPERATORABLE_LINEPOINT_NUM)
            return null;
        ArrayList<LinePointVector> vectors = new ArrayList<LinePointVector>();
        for (int i = 0; i < linePoints.size() - INDEX_INTERVAL; i++) {
            TMapPoint p1 = linePoints.get(i);
            TMapPoint p2 = linePoints.get(i + INDEX_INTERVAL);
            vectors.add(new LinePointVector((p2.getLongitude() - p1.getLongitude()), (p2.getLatitude() - p1.getLatitude())));
        }
        return vectors;
    }

    public static ArrayList<PathItem> getPointIdxs(ArrayList<TMapPoint> linePoints) {
        String TAG = "getPointIdxs";
        ArrayList<LinePointVector> vectors = getDegrees(linePoints);
        if (vectors == null) return null;
        ArrayList<PathItem> points = new ArrayList<PathItem>();
        for (int i = 0; i < vectors.size() - INDEX_INTERVAL; i++) {
            LinePointVector v1 = vectors.get(i);
            LinePointVector v2 = vectors.get(i + INDEX_INTERVAL);
            double angle = Math.asin( (v1.getDeltaLongitude()*v2.getDeltaLatitude() - v1.getDeltaLatitude()*v2.getDeltaLongitude()) /
                    (Math.sqrt(Math.pow(v1.getDeltaLongitude(),2) + Math.pow(v1.getDeltaLatitude(),2)) * Math.sqrt(Math.pow(v2.getDeltaLongitude(),2) + Math.pow(v2.getDeltaLatitude(),2)) ) );
            Log.d(TAG, "getPointIdxs: angle " + angle);
            if (Math.abs(angle) > THRESHOLD_ANGLE) {
                if (angle > 0) {    //  Left
                    points.add(new PathItem(PathItem.NODETYPE_POINT, PathItem.TURNTYPE_LEFT, linePoints.get(i + INDEX_INTERVAL)));
                } else {    //  Right
                    points.add(new PathItem(PathItem.NODETYPE_POINT, PathItem.TURNTYPE_RIGHT, linePoints.get(i + INDEX_INTERVAL)));
                }
                i += INDEX_INTERVAL ; //  start searching POINT after
            }
        }
        return points;
    }

    /* Inner Class for Vector calculate for point making */
    private static class LinePointVector {
        private double delta_longitude;
        private double delta_latitude;

        public LinePointVector(double delta_longitude, double delta_latitude) {
            this.delta_longitude = delta_longitude;
            this.delta_latitude = delta_latitude;
        }

        public double getDeltaLongitude() {
            return delta_longitude;
        }

        public void setDeltaLongitude(double delta_longitude) {
            this.delta_longitude = delta_longitude;
        }

        public double getDeltaLatitude() {
            return delta_latitude;
        }

        public void setDeltaLatitude(double delta_latitude) {
            this.delta_latitude = delta_latitude;
        }
    }

}
