package com.example.kophiplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

public class ShopDB {

    private static final String TABLE_NAME = "Shop";

    private static final String COLUMN_ID = "SID";
    private static final String COLUMN_IMAGE = "SImg";
    private static final String COLUMN_NAME = "SName";


    private SQLiteDatabase db;
    private DBHandler dbHandler;

    public ShopDB(Context context){
        dbHandler = new DBHandler(context);
    }

    public void open(){
        db = dbHandler.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    /*public SQLiteDatabase getBdd(){
        return db;
    }*/

    public LinkedList<Shop> selectAll() {
        LinkedList<Shop> result= new LinkedList<>();
        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() !=0){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                Shop shop = cursorToShop(cursor, true);
                result.add(shop);
            }
        }
        cursor.close();
        return result;
    }



    public void insert(Shop shop) {
        ContentValues values = new ContentValues();
        values.putNull(COLUMN_ID);
        values.put(COLUMN_IMAGE,shop.getImg());
        values.put(COLUMN_NAME,shop.getName());
        db.insert(TABLE_NAME,null,values);
    }

    public long shopSize() {
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return count;
    }
    /*
    public Coffee findByPays(String pays) {
        String query = "select * from " + TABLE_NAME + " WHERE "+ COLUMN_PAYS + " LIKE '" + pays+"';";
        Cursor cursor = db.rawQuery(query, null);
        Coffee coffee=null;
        if( cursor != null && cursor.moveToFirst() ) {
            coffee = cursorToCoffee(cursor, false);

        }
        cursor.close();
        return coffee;
    }*/

    public int delete(int id) {
        return db.delete(TABLE_NAME,COLUMN_ID+" = "+id,null);
    }


    public void update(Shop shp) {
        ContentValues args = new ContentValues();
        int id = shp.getkID();
        String img = shp.getImg();
        String name = shp.getName();
        args.put(COLUMN_ID,id);
        args.put(COLUMN_IMAGE,img);
        args.put(COLUMN_NAME,name);
        db.update(TABLE_NAME,args,COLUMN_ID + " = " + id,null);
    }

    private Shop cursorToShop(Cursor c,boolean multipleResult){
        if(!multipleResult){
            c.moveToFirst();
        }
        Shop shop = new Shop(c.getInt(0),c.getString(1),c.getString(2));
        return shop;
    }



    public void display(){
        LinkedList<Shop> shops = selectAll();
        //System.out.println("ici");
        for(int i = 0; i<shops.size();i++) {
            shops.get(i).affiche();
        }
    }
}
