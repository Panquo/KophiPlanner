package com.example.kophiplanner;

import java.io.Serializable;
import java.util.LinkedList;

public class Shop implements Serializable {
    private int kID;
    private String img;
    private String name;

    public Shop() {}

    public Shop(int id, String img,String name){
        this.kID=id;
        this.img=img;
        this.name=name;
    }

    public void setkID(int kID) {
        this.kID = kID;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setName(String name) { this.name = name; }

    public int getkID() {
        return kID;
    }

    public String getImg() {
        return img;
    }

    public String getName() { return name; }

    public void affiche() {
        System.out.println("ID :" + kID);
        System.out.println("img : "+img);
    }
}
