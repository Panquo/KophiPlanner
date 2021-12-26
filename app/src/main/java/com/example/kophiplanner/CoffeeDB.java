package com.example.kophiplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

public class CoffeeDB {

    private static final String TABLE_NAME = "Coffee";

    private static final String COLUMN_ID = "KID";
    private static final String COLUMN_IMG = "KIMG";
    private static final String COLUMN_QUANTITY = "QUANT";
    private static final String COLUMN_PAYS = "PAYS";


    private SQLiteDatabase db;
    private DBHandler dbHandler;

    public CoffeeDB(Context context){
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

    public LinkedList<Coffee> selectAll() {
        LinkedList<Coffee> result= new LinkedList<>();
        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() !=0){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                Coffee coffee = cursorToCoffee(cursor, true);
                result.add(coffee);
            }
        }
        cursor.close();
        return result;
    }

    public LinkedList<Coffee> selectAll(String s,boolean b) {
        LinkedList<Coffee> result= new LinkedList<>();
        if(s=="name"){
            String query;
            if (b){
                query = "select * from " + TABLE_NAME + " ORDER BY "+COLUMN_PAYS;
            }else{
                query = "select * from " + TABLE_NAME + " ORDER BY "+COLUMN_PAYS+" DESC";
            }
            Cursor cursor = db.rawQuery(query, null);
            if(cursor.getCount() !=0){
                for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                    Coffee kof = cursorToCoffee(cursor, true);
                    result.add(kof);
                }
            }
            cursor.close();
        }else if(s=="quantity"){
            String query;
            if (b){
                query = "select * from " + TABLE_NAME + " ORDER BY "+COLUMN_QUANTITY;
            }else{
                query = "select * from " + TABLE_NAME + " ORDER BY "+COLUMN_QUANTITY+" DESC";
            }
            Cursor cursor = db.rawQuery(query, null);
            if(cursor.getCount() !=0){
                for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                    Coffee kof = cursorToCoffee(cursor, true);
                    result.add(kof);
                }
            }
            cursor.close();
        }
        return result;
    }

    public int getlastID() {
        int result=0;
        String query = "select Max("+COLUMN_ID+") from " + TABLE_NAME+";";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() !=0){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                cursor.moveToFirst();
                result=cursor.getInt(0);
            }
        }
        cursor.close();
        return result;
    }

    public LinkedList<Coffee> selectAllNonEmpty() {
        LinkedList<Coffee> result= new LinkedList<>();
        String query = "select * from " + TABLE_NAME+" WHERE "+COLUMN_QUANTITY+"!=0;";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() !=0){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                Coffee coffee = cursorToCoffee(cursor, true);
                result.add(coffee);
            }
        }
        cursor.close();
        return result;
    }

    public void insert(Coffee coffee) {
        ContentValues values = new ContentValues();
        values.putNull(COLUMN_ID);
        values.put(COLUMN_IMG,coffee.getkImage());
        values.put(COLUMN_QUANTITY,coffee.getStock());
        values.put(COLUMN_PAYS,coffee.getPays());
        db.insert(TABLE_NAME,null,values);
    }

    public Coffee findByID(int id) {
        String query = "select * from " + TABLE_NAME + " WHERE "+ COLUMN_ID + " = " + id+";";
        Cursor cursor = db.rawQuery(query, null);
        Coffee coffee = cursorToCoffee(cursor,false);
        cursor.close();
        return coffee;
    }

    public Coffee findByPays(String pays) {
        String query = "select * from " + TABLE_NAME + " WHERE "+ COLUMN_PAYS + " LIKE '" + pays+"';";
        Cursor cursor = db.rawQuery(query, null);
        Coffee coffee=null;
        if( cursor != null && cursor.moveToFirst() ) {
            coffee = cursorToCoffee(cursor, false);

        }
        cursor.close();
        return coffee;
    }

    public int delete(int id) {
        return db.delete(TABLE_NAME,COLUMN_ID+" = "+id,null);
    }

    public void update(Coffee cof) {
        ContentValues args = new ContentValues();
        int id = cof.getkID();
        String img = cof.getkImage();
        int qt = cof.getStock();
        String pays = cof.getPays();
        args.put(COLUMN_ID,id);
        args.put(COLUMN_IMG,img);
        args.put(COLUMN_QUANTITY,qt);
        args.put(COLUMN_PAYS,pays);
        db.update(TABLE_NAME,args,COLUMN_ID + " = " + id,null);
    }

    private Coffee cursorToCoffee(Cursor c,boolean multipleResult){
        if(!multipleResult){
            c.moveToFirst();
        }
        Coffee kof = new Coffee(c.getInt(0),c.getString(1),c.getInt(2),c.getString(3));
        return kof;
    }

    public void display(){
        LinkedList<Coffee> coffees = selectAll();
        //System.out.println("ici");
        for(int i = 0; i<coffees.size();i++) {
            coffees.get(i).affiche();
        }
    }
}
