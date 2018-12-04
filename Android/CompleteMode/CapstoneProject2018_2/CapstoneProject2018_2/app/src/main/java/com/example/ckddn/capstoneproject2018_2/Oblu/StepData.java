package com.example.ckddn.capstoneproject2018_2.Oblu;

/**
 * Created by my on 2/20/2018.
 */

public class StepData {
    private double x;
    private double y;
    private double z;
    private double heading;
    private double distance;
    private int stepCount;

    public StepData(double x, double y, double z, double distance, int stepCount){
        this.x= x;
        this.y=y;
        this.z= z;
        this.distance= distance;
        this.stepCount= stepCount;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getStepCount() {
        return stepCount;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }
}
