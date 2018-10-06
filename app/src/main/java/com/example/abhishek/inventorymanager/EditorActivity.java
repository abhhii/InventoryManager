package com.example.abhishek.inventorymanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.abhishek.inventorymanager.data.ItemContract;
import com.example.abhishek.inventorymanager.data.ItemDbHelper;

public class EditorActivity extends AppCompatActivity {

    ItemDbHelper mDbHelper;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    Intent intent;
    Uri mCurrentItemUri;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        intent = getIntent();
        mCurrentItemUri = intent.getData();

        if(mCurrentItemUri == null)
        {
            setTitle("Add an Item");
        }
        else
            setTitle("Edit an Item");

        mNameEditText = (EditText)findViewById(R.id.edit_item_name);
        mPriceEditText = (EditText)findViewById(R.id.edit_item_price);
        mQuantityEditText = (EditText)findViewById(R.id.edit_item_quantity);
        mSupplierEditText = (EditText)findViewById(R.id.edit_item_supplier);
        mSupplierPhoneEditText = (EditText)findViewById(R.id.edit_supplier_phone);
    }

    private void insert_item(){
        mDbHelper = new ItemDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String nameString = mNameEditText.getText().toString().trim();
        int price = Integer.parseInt(mPriceEditText.getText().toString().trim());
        int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        String supplier = mSupplierEditText.getText().toString().trim();
        String supplier_phone = mSupplierPhoneEditText.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME,nameString);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE,price);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,quantity);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER,supplier);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE,supplier_phone);
        long new_row_id = db.insert(ItemContract.ItemEntry.TABLE_NAME, null, contentValues);
        Log.v("MainActivityIDPRINT", new_row_id+"");
    }
}
