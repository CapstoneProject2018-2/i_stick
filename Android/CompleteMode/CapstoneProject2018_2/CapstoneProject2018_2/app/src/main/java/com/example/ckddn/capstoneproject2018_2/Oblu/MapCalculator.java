package com.example.ckddn.capstoneproject2018_2.Oblu;


//class implemented by Yang Insu
//algorithm & structure implemented by Son Changwoo, Yang Insu

/*This class deals with Dead Reckoning Coordinate Translation.*/
/*Receives current position and changed vectors and translates them to later positions in format of lat,long*/

/* 추측항법 계산 함수 클래스*/
/* 현재위치, 바뀐 벡터값을 통해 추측되는 다음 위치를 위도 경도 형식으로 반환*/

import java.lang.reflect.Array;

public class MapCalculator {

    private static final double E_RADIUS = 6378137; //wgs84 system radius of Earth.


    public static double[] getHeadingVectors(float azimuth){ // 방위각을 통해 움직임 벡터의 단위벡터를 계산한다.
        double sin_phi = (float)Math.sin(Math.toRadians(azimuth));
        double cos_phi = (float)Math.cos(Math.toRadians(azimuth));

        double directionVector[] = {sin_phi,cos_phi};

        return directionVector;
    }

    //움지임의 방향 벡터를
    public static double[] getMovementVectors(double headingVector[], double scalars[]){
        double[] movementVectors = new double[2];

        movementVectors[0] = headingVector[1] * scalars[0] - headingVector[0]*scalars[1]; //x
        movementVectors[1] = headingVector[0] * scalars[0] + headingVector[1]*scalars[1]; //y

        return movementVectors;
    }

    public static double[] TranslateCoordinates(double[] movementVectors, double latitude) {
        double delta_latitude, delta_longitude;

        delta_latitude = (360 * movementVectors[0]) / (2 * Math.PI * E_RADIUS);
        delta_longitude = (360 * movementVectors[1]) / (2 * Math.PI * Math.sin(Math.toRadians(90 - Math.abs(latitude))) * E_RADIUS);

        double[] translatedCoordinates = {delta_latitude, delta_longitude};

        return translatedCoordinates;
    }

    public static double[] CalculateMovement(double[] delta, double[] o_coor){
        double dr_latitude =0;
        double dr_longitude =0;

        dr_latitude = o_coor[0]-delta[0];
        dr_longitude = o_coor[1] - delta[1];

        double[] dr_coordinates = {dr_latitude, dr_longitude};

        return dr_coordinates;
    }

    public static double[] CalculateMovement(double[] delta, double latitude, double longtitude){
        double dr_latitude =0;
        double dr_longitude =0;

        dr_latitude = latitude - delta[0];
        dr_longitude = longtitude - delta[1];

        double[] dr_coordinates = {dr_latitude, dr_longitude};

        return dr_coordinates;
    }
}


