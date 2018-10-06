package com.example.abhishek.inventorymanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.inventorymanager.data.ItemContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int EXISTING_ITEM_LOADER = 0;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private Uri mCurrentItemUri;
    private boolean mItemHasChanged;
    Intent intent;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentItemUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                saveItem();
                finish();
            case R.id.action_delete:
                showDeleteConfirmDialog();
                return true;
            case android.R.id.home:
                if(!mItemHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!mItemHasChanged){
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
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
            invalidateOptionsMenu();
        }
        else
            setTitle("Edit an Item");

        mNameEditText = (EditText)findViewById(R.id.edit_item_name);
        mPriceEditText = (EditText)findViewById(R.id.edit_item_price);
        mQuantityEditText = (EditText)findViewById(R.id.edit_item_quantity);
        mSupplierEditText = (EditText)findViewById(R.id.edit_item_supplier);
        mSupplierPhoneEditText = (EditText)findViewById(R.id.edit_supplier_phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
    }

    private void saveItem(){
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplier = mSupplierEditText.getText().toString().trim();
        String supplier_phone = mSupplierPhoneEditText.getText().toString().trim();

        if( TextUtils.isEmpty(nameString) ||
            TextUtils.isEmpty(priceString) ||
            TextUtils.isEmpty(quantityString)){
            return; }

        int price = Integer.parseInt(priceString);
        int quantity = Integer.parseInt(quantityString);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME,nameString);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE,price);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,quantity);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER,supplier);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE,supplier_phone);

        if(mCurrentItemUri == null){
            Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, contentValues);
            if(newUri == null){
                Toast.makeText(this,"Insertion failed",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Item inserted",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, contentValues, null, null);
            if(rowsAffected == 0 )
                Toast.makeText(this, "Update failed!",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Update successful",Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem(){
        if(mCurrentItemUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if(rowsDeleted>0)
                Toast.makeText(this, R.string.deletion_toast, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.deletion_error_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirm_toast);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.edit_confirm_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        if(data == null || data.getCount()<1){
            return;
        }
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
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
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
