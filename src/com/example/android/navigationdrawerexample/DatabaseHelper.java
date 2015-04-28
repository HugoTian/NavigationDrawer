package com.example.android.navigationdrawerexample;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.navigationdrawerexample.DatabaseConstants.TABLE_NAME;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_TITLE;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_CONTENT;
import static com.example.android.navigationdrawerexample.DatabaseConstants.CODE_LINK;


public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "event.db";
    private final static int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	

        final String INIT_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                                  _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                  
                                  CODE_TITLE + " TEXT, " +
                                  CODE_CONTENT + " TEXT, " +
                                  CODE_LINK + " TEXT);"; 
                                  
                                  
    	
        db.execSQL(INIT_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
	
	
}
