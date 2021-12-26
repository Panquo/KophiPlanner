package com.example.kophiplanner;

import java.io.Serializable;
import java.util.LinkedList;

public class Coffee implements Serializable {
    private int kID;
    private String kImage;
    private int quant;
    private String pays;

    public Coffee() {}

    public Coffee(int id,String img,int quant, String pays){
        this.kID=id;
        this.kImage=img;
        this.quant=quant;
        this.pays = pays;
    }

    public void setkID(int kID) {
        this.kID = kID;
    }

    public void setkImage(String kImage) {
        this.kImage = kImage;
    }

    public void setQuant(int quant){this.quant=quant;}

    public void setPays(String pays){this.pays=pays;}

    public int getkID() {
        return kID;
    }

    public String getkImage() {
        return kImage;
    }

    public int getStock() { return quant; }

    public String getPays() { return pays; }

    public boolean coffeeCp(Coffee c){
        int id1=c.getkID();
        if(kID==id1){
            return true;
        }
        return false;
    }

    public void affiche() {
        System.out.println("ID :"+ kID+" - Image : "+kImage+" - Stock : "+quant);
    }
}
