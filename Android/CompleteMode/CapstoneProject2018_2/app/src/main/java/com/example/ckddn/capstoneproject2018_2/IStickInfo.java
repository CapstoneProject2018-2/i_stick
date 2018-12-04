package com.example.ckddn.capstoneproject2018_2;


import android.Manifest;

public interface IStickInfo {
    String[] user_permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
    String[] common_permissions = {Manifest.permission.CALL_PHONE};
    /*  setting server ip address   */
    String ipAddress = "35.243.118.35:5555";   //  Google Cloud Platform 외부 고정 ip
//    String ipAddress = "192.168.0.16:5555";   //  Local ip for Debugging

}
