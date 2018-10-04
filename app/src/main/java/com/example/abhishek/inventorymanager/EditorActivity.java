package com.example.abhishek.inventorymanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText)findViewById(R.id.edit_item_name);
        mPriceEditText = (EditText)findViewById(R.id.edit_item_price);
        mQuantityEditText = (EditText)findViewById(R.id.edit_item_quantity);
        mSupplierEditText = (EditText)findViewById(R.id.edit_item_supplier);
        mSupplierPhoneEditText = (EditText)findViewById(R.id.edit_supplier_phone);
        mSave = (Button)findViewById(R.id.save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert_item();
                finish();
            }
        });
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
