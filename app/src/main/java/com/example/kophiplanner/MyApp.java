package com.example.kophiplanner;

import android.app.Application;

import java.util.LinkedList;

public class MyApp extends Application {
    CoffeeDB db;
    Boolean CAMERA_STATUS;



    public void setCAMERA_STATUS(Boolean cam){
        this.CAMERA_STATUS=cam;
    }
}
