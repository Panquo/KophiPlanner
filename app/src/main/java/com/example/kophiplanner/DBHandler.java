package com.example.kophiplanner;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {
    private static final String TAG = "TAG";
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "CoffeePlannerDB.db";
    private static final String TABLE_NAME1 = "Coffee";
    private static final String TABLE_NAME2 = "Available";
    private static final String TABLE_NAME3 = "Shop";
    private static final String COFFEE_ID = "KID";
    private static final String SHOP_ID = "SID";
    private static final String COFFEE_IMG = "KIMG";
    private static final String SHOP_IMG = "SIMG";
    private static final String COLUMN_QUANTITY = "QUANT";
    private static final String COLUMN_PAYS = "PAYS";
    private static final String SHOP_NAME = "SName";


    private final Context context;

     DBHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
         this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME1 + " ( " + COFFEE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COFFEE_IMG + " TEXT NOT NULL,"+ COLUMN_QUANTITY+" INTEGER,"+COLUMN_PAYS+" TEXT NOT NULL UNIQUE );";
        db.execSQL(CREATE_TABLE);
        CREATE_TABLE = "CREATE TABLE " + TABLE_NAME2 + " ( " + COFFEE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT );";
        db.execSQL(CREATE_TABLE);
        CREATE_TABLE = "CREATE TABLE " + TABLE_NAME3 + " ( " + SHOP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ SHOP_IMG+" TEXT NOT NULL,"+ SHOP_NAME+" TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "Updating table from " + oldVersion + " to " + newVersion);
        // You will not need to modify this unless you need to do some android specific things.
        // When upgrading the database, all you need to do is add a file to the assets folder and name it:
        // from_1_to_2.sql with the version that you are upgrading to as the last version.

        try {
            for (int i = oldVersion; i < newVersion; ++i) {
                String migrationName = String.format(Locale.getDefault(),"from_%d_to_%d.sql", i, (i + 1));
                Log.d(TAG, "Looking for migration file: " + migrationName);

                readAndExecuteSQLScript(db, context, migrationName);

            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception running upgrade script:", exception);
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "Downdating table from " + oldVersion + " to " + newVersion);
        // You will not need to modify this unless you need to do some android specific things.
        // When upgrading the database, all you need to do is add a file to the assets folder and name it:
        // from_1_to_2.sql with the version that you are upgrading to as the last version.
        try {
            for (int i = oldVersion; i > newVersion; --i) {
                String migrationName = String.format(Locale.getDefault(),"from_%d_to_%d.sql", i, (i - 1));
                Log.d(TAG, "Looking for migration file: " + migrationName);
                readAndExecuteSQLScript(db, context, migrationName);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception running upgrade script:", exception);
        }
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "SQL script file name is empty");
            return;
        }

        Log.d(TAG, "Script found. Executing...");
        AssetManager assetManager = ctx.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException:", e);
                }
            }
        }

    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }


}
