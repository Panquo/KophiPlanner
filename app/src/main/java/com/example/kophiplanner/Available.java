package com.example.kophiplanner;

import java.io.Serializable;
import java.util.LinkedList;

public class Available implements Serializable {
    private int kID;
    private LinkedList<Boolean> shops;

    public Available() {}

    public Available(int id, LinkedList<Boolean> shops){
        this.kID=id;
        this.shops = shops;
    }

    public void setkID(int kID) {
        this.kID = kID;
    }

    public void setShops(LinkedList<Boolean> shops) {this.shops = shops;}

    public int getkID() {
        return kID;
    }

    public LinkedList<Boolean> getShops() {return shops;}

    public void affiche() {
        System.out.println("ID :"+ kID);
        for (int i=0;i<shops.size();i++){
            if(shops.get(i)){
                System.out.println("Shop "+i+" : "+shops.get(i));
            }
        }
    }

    public void affShops(){
        System.out.println("//--------------------SHOPS-------------------//");
        for (int i=0;i<shops.size();i++){
                System.out.println("Shop "+i+" : "+shops.get(i));
        }
        System.out.println("//--------------------------------------------//");
    }
}
