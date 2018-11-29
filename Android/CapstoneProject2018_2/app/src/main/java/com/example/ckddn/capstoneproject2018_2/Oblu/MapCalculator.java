package com.example.ckddn.capstoneproject2018_2.Oblu;


//class implemented by Yang Insu
//algorithm & structure implemented by Son Changwoo, Yang Insu

/*This class deals with Dead Reckoning Coordinate Translation.*/
/*Receives current position and changed vectors and translates them to later positions in format of lat,long*/

/* 추측항법 계산 함수 클래스*/
/* 현재위치, 바뀐 벡터값을 통해 추측되는 다음 위치를 위도 경도 형식으로 반환*/

public class MapCalculator {

    private static final double E_RADIUS = 6378137; //wgs84 system radius of Earth.

    public static double[] getHeadingVectors(float azimuth){ // 방위각을 통해 움직임 벡터의 단위벡터를 계산한다.
        double sin_phi = (float)Math.sin(Math.toRadians(azimuth));
        double cos_phi = (float)Math.cos(Math.toRadians(azimuth));

        double directionVector[] = {sin_phi,cos_phi};

        return directionVector;
    }

    /* headingVector = sin_phi, cos_phi, scalars = dx, dy
     * calculate vector rotation and return movementVectors that based on long, lat coordination style */
    public static double[] getMovementVectors(double headingVector[], double scalars[]){
        double[] movementVectors = new double[2];

        movementVectors[0] = headingVector[1] * scalars[0] - headingVector[0]*scalars[1]; // delta_x
        movementVectors[1] = headingVector[0] * scalars[0] + headingVector[1]*scalars[1]; // delta_y
        movementVectors[2] = scalars[2];
        return movementVectors;
    }

    /* calculate theta of longitude and latitude and return */
    public static double[] TranslateCoordinates(double[] movementVectors, double latitude) {    //  altitude도 추가로 받아온다
        double delta_latitude, delta_longitude;

        delta_latitude = (360 * movementVectors[0]) / (2 * Math.PI * E_RADIUS);
        delta_longitude = (360 * movementVectors[1]) / (2 * Math.PI * Math.cos(Math.toRadians(latitude)) * E_RADIUS);

        double[] translatedCoordinates = {delta_latitude, delta_longitude};

        return translatedCoordinates;
    }


    // 추가 improvement 사항 11/29
    // 지구반지름에 altitude와 movementVector[2] altitude방향의 벡터값을 넣어 더 정확한 지구반지름으로 위경도를 계산
    // altitude는 고정으로 WGS84지구의 반지름에서 더하여 사용
    // movementVector[2]를 계산에 이용할 시기를 세가지 경우로 나누어 어느것이 더 정확한 위치를 배출하는 지 test

    /* calculate theta of longitude and latitude and return with precalculate delta_z */
    public static double[] TranslateCoordinatesWithPreAlti(double[] movementVectors, double latitude, double altitude) {
        double delta_latitude, delta_longitude;
        // case 1: 계산전 movementVectors[2] 미리 적용
        double calibratedERadius = E_RADIUS + altitude - movementVectors[2];

        delta_latitude = (360 * movementVectors[0]) / (2 * Math.PI * calibratedERadius);
        delta_longitude = (360 * movementVectors[1]) / (2 * Math.PI * Math.cos(Math.toRadians(latitude)) * calibratedERadius);

        double[] translatedCoordinates = {delta_latitude, delta_longitude};

        return translatedCoordinates;
    }

    /* calculate theta of longitude and latitude and return with postcalculate delta_z */
    public static double[] TranslateCoordinatesWithPostAlti(double[] movementVectors, double latitude, double altitude) {
        double delta_latitude, delta_longitude;
        // case 2: 계산후 적용(고도만 더함(현재의 altitude는 이전의 movement값을 반영))
        double calibratedERadius = E_RADIUS + altitude;

        delta_latitude = (360 * movementVectors[0]) / (2 * Math.PI * calibratedERadius);
        delta_longitude = (360 * movementVectors[1]) / (2 * Math.PI * Math.cos(Math.toRadians(latitude)) * calibratedERadius);

        double[] translatedCoordinates = {delta_latitude, delta_longitude};

        return translatedCoordinates;
    }

    /* calculate theta of longitude and latitude and return with precalculate half of delta_z */
    public static double[] TranslateCoordinatesWithHalfAlti(double[] movementVectors, double latitude, double altitude) {
        double delta_latitude, delta_longitude;
        // case 3: 계산전 movementVector[2]의 절반만큼만 미리 적용(이전위치높이와 예측위치 높이의 중간지점을 기준으로 계산)
        double calibratedERadius = E_RADIUS + altitude - (movementVectors[2]/2);


        delta_latitude = (360 * movementVectors[0]) / (2 * Math.PI * calibratedERadius);
        delta_longitude = (360 * movementVectors[1]) / (2 * Math.PI * Math.cos(Math.toRadians(latitude)) * calibratedERadius);

        double[] translatedCoordinates = {delta_latitude, delta_longitude};

        return translatedCoordinates;
    }


    /* calculate estimated current locations using delta thetas
     * delta is angle that is translatedCoordinations of delta_x, y
     * o_coor is array that contains latitude and longitude*/
    public static double[] CalculateMovement(double[] delta, double[] o_coor){
        double dr_latitude =0;
        double dr_longitude =0;

        dr_latitude = o_coor[0]-delta[0];
        dr_longitude = o_coor[1] - delta[1];

        double[] dr_coordinates = {dr_latitude, dr_longitude};

        return dr_coordinates;
    }

    /* calculate estimated current locations using delta thetas */
    public static double[] CalculateMovement(double[] delta, double latitude, double longtitude){
        double dr_latitude =0;
        double dr_longitude =0;

        dr_latitude = latitude - delta[0];
        dr_longitude = longtitude - delta[1];

        double[] dr_coordinates = {dr_latitude, dr_longitude};

        return dr_coordinates;
    }

    /* calculate estimated current locations using delta thetas */
    public static double[] CalculateDRPosition(float azimuth, double[] scalars, double longtitude, double latitude, double altitude){
        double[] headingVectors = getHeadingVectors(azimuth);
        double[] movementVectors = getMovementVectors(headingVectors, scalars);
        double[] delta_coor = TranslateCoordinates(movementVectors, latitude);
        double[] dr_coordinates = CalculateMovement(delta_coor, latitude, longtitude);

        return dr_coordinates;
    }
}


