package com.example.abhishek.inventorymanager;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mCurrentItemUri == null)
            return null;
        String[] projection = new String[]{
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER,
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE
        };

        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null || data.getCount()<1)
            return;
        if(data.moveToFirst()){
            int nameColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER);
            int phoneColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE);

            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplier = data.getString(supplierColumnIndex);
            String phone = data.getString(phoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantity);
            mSupplierEditText.setText(supplier);
            mSupplierPhoneEditText.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
}
