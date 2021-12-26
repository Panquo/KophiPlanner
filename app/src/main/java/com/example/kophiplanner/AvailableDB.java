package com.example.kophiplanner;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;

public class AvailableDB {

    private static final String TABLE_NAME = "Available";
    private static final String TABLE_TEMP = "Tempp";

    private static final String COLUMN_ID = "KID";


    private SQLiteDatabase db;
    private DBHandler dbHandler;
    private LinkedList<String> sp;

    public AvailableDB(Context context){
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

    public void deleteRow(LinkedList<Shop> shops){
        String qu="Alter table "+TABLE_NAME+" rename to "+TABLE_TEMP;
        db.execSQL(qu);
        String query1 = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT );";
        db.execSQL(query1);
        query1 = "INSERT INTO "+TABLE_NAME + " SELECT KID,";
        String q;
        for(int i=0;i<shops.size();i++){
            add_column(shops.get(i).getName());
            query1=query1+shops.get(i).getName();
            if(i!=shops.size()-1){
                query1=query1+" , ";
            }

        }
        query1= query1+" FROM "+TABLE_TEMP+";";
        if(shops.size()>0){
            db.execSQL(query1);
        }

        query1="DROP TABLE "+ TABLE_TEMP+";";
        db.execSQL(query1);
    }

    public void setShops(LinkedList<Shop> shops) {
        LinkedList<String> lol = new LinkedList<>();
        for (int i = 0; i < shops.size(); i++) {
            if (shops.get(i).getkID() != 0 && shops.get(i).getkID() != -1) {
                String shop = shops.get(i).getName();
                lol.add(shop);
            }
            sp = lol;
        }
    }

    public LinkedList<Available> selectAll() {
        LinkedList<Available> result= new LinkedList<>();
        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() !=0){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                Available av = cursorToAvailable(cursor, true);
                result.add(av);
            }
        }
        cursor.close();
        return result;
    }

    public void insert(Available av) {
        ContentValues values = new ContentValues();
        values.putNull(COLUMN_ID);
        for(int i=0;i<av.getShops().size();i++){
            if(av.getShops().get(i)){
                values.put(sp.get(i),1);
            }else{
                values.put(sp.get(i),0);
            }
        }
        db.insert(TABLE_NAME,null,values);
    }

    public void add_column(String column){
        String col = column.replace(' ','_');
        String ADD_COLUMN = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+column+" INTEGER DEFAULT 0;";
        db.execSQL(ADD_COLUMN);
    }

    public Available findByID(int id) {
        String query = "select * from " + TABLE_NAME + " WHERE "+ COLUMN_ID + " = " + id+";";
        Cursor cursor = db.rawQuery(query, null);
        //Log.e("NB",Integer.toString(cursor.getCount()));
        if(cursor.getCount()>0) {
            Available av = cursorToAvailable(cursor, false);
            cursor.close();
            return av;
        }else{
            cursor.close();
            Available avn=new Available(id,null);
            insert(avn);
            avn=findByID(id);
            return avn;
        }
        //return null;
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


    public void update(Available av) {
        ContentValues args = new ContentValues();
        int id = av.getkID();
        args.put(COLUMN_ID,id);
        for(int i=0;i<av.getShops().size();i++){
            if(av.getShops().get(i)){
                args.put(sp.get(i),1);
            }else{
                args.put(sp.get(i),0);
            }
        }
        db.update(TABLE_NAME,args,COLUMN_ID + " = " + id,null);
    }

    private Available cursorToAvailable(Cursor c,boolean multipleResult){
        if(!multipleResult){
            c.moveToFirst();
        }
        LinkedList<Boolean> av = new LinkedList<Boolean>();
        if(c.getColumnCount()>1){
            for (int i=1;i<c.getColumnCount();i++){
                if(c.getInt(i)==0) {
                    av.add(false);
                }else{
                    av.add(true);
                }
            }
        }

        @SuppressLint("Range") Available ava = new Available(c.getInt(c.getColumnIndex(COLUMN_ID)),av);
        return ava;
    }

    public void display(){
        LinkedList<Available> av = selectAll();
        //System.out.println("ici");
        for(int i = 0; i<av.size();i++) {
            av.get(i).affiche();
        }
    }

    public int getColumnCount(){
        String query = "select * from " + TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(query, null);
        //Log.e("NB",Integer.toString(cursor.getCount()));
        return cursor.getColumnCount();
    }
}
