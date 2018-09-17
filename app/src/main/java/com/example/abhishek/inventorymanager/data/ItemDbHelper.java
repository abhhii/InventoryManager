package com.example.abhishek.inventorymanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final  String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE "+
                ItemContract.ItemEntry.TABLE_NAME+"("+
                ItemContract.ItemEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ItemContract.ItemEntry.COLUMN_ITEM_NAME+" TEXT NOT NULL,"+
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE+ " INTEGER NOT NULL,"+
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY+ " INTEGER NOT NULL,"+
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER+ " TEXT,"+
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE+ " TEXT);";
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
