package com.example.ckddn.capstoneproject2018_2;


import android.Manifest;

public interface ServerInfo {
    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
    /*  setting server ip address   */
//    String ipAddress = "35.243.118.35:5555";   //  Google Cloud Platform 외부 고정 ip
    String ipAddress = "192.168.0.28:5555";   //  Google Cloud Platform 외부 고정 ip
}
