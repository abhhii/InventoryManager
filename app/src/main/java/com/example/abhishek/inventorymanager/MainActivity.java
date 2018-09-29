package com.example.abhishek.inventorymanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.abhishek.inventorymanager.data.ItemContract;
import com.example.abhishek.inventorymanager.data.ItemDbHelper;

public class MainActivity extends AppCompatActivity {

    ItemDbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo(){
        mDbHelper = new ItemDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER,
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE};
        String selection = null;
        String[] seletionArgs = null;
        Cursor cursor = db.query(ItemContract.ItemEntry.TABLE_NAME,
                projection,
                selection,
                seletionArgs,
                null,null,null);
        TextView displayView = (TextView) findViewById(R.id.text_view_item);
        try{
            displayView.setText("Number of rows = " + cursor.getCount()+ "\n");

            int idColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE);
            Log.e("MainActivityAAAAAAAA",Integer.toString(nameColumnIndex));

            while (cursor.moveToNext()){
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentSupplierPhone = cursor.getString(phoneColumnIndex);
                displayView.append("\n" + currentID + " - "+
                                        currentName + "\t" +
                                        currentPrice+ "\t" +
                                        currentQuantity+ "\t" +
                                        currentSupplier + "\t" +
                                        currentSupplierPhone);
            }
        }
        catch (Exception e){
            Log.e("MainActivityERROR",e.getMessage());
        }
        finally {
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void insert_item(){
        mDbHelper = new ItemDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME,"A brief History of time");
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE,400);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,10);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER,"Penguin");
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE,"8987456210");
        long new_row_id = db.insert(ItemContract.ItemEntry.TABLE_NAME, null, contentValues);
        Log.v("MainActivityIDPRINT", new_row_id+"");
        displayDatabaseInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insert_item();
                //finish();
                return true;
            case R.id.action_delete_all_entries:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}