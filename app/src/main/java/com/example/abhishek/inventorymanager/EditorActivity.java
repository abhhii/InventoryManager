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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.inventorymanager.data.ItemContract;

import static android.widget.Toast.makeText;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int EXISTING_ITEM_LOADER = 0;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private TextView mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private Uri mCurrentItemUri;
    private boolean mItemHasChanged;
    Intent intent;
    Toast toast;
    Integer quantity_change = 1;

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
        else {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
                return true;
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
    protected void onCreate(final Bundle savedInstanceState) {
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
        mQuantityEditText = (TextView)findViewById(R.id.edit_item_quantity);
        mSupplierEditText = (EditText)findViewById(R.id.edit_item_supplier);
        mSupplierPhoneEditText = (EditText)findViewById(R.id.edit_supplier_phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);

        Button order = (Button)findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ mSupplierPhoneEditText.getText()));
                startActivity(phoneIntent);
            }
        });

        ImageButton increase = (ImageButton)findViewById(R.id.increment);
        ImageButton decrease = (ImageButton)findViewById(R.id.decrement);
        final ImageButton changeValue = (ImageButton)findViewById(R.id.change);

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityEditText.getText().toString();
                Integer quantity;
                if(quantityString.equals(""))
                    quantity = 0;
                else
                    quantity = Integer.parseInt(quantityString);
                quantity = quantity + quantity_change;
                mQuantityEditText.setText(quantity.toString());
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityEditText.getText().toString();
                Integer quantity;
                Log.e("sfrvrevsrvfdv", quantityString);
                if(quantityString.equals(""))
                    quantity = 0;
                else
                    quantity = Integer.parseInt(quantityString);
                if(quantity >= quantity_change){
                    quantity = quantity - quantity_change;
                    mQuantityEditText.setText(quantity.toString());
                }
            }
        });
        changeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
                builder.setTitle(R.string.change_increment);

                final EditText input = new EditText(EditorActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String temp = input.getText().toString();
                        if(temp.equals(""))
                            temp = "1";
                        quantity_change = Integer.parseInt(temp);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

    }

    private void saveItem(){
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplier = mSupplierEditText.getText().toString().trim();
        String supplier_phone = mSupplierPhoneEditText.getText().toString().trim();

        if( TextUtils.isEmpty(nameString) ||
            TextUtils.isEmpty(priceString) ||
            TextUtils.isEmpty(quantityString) ||
            TextUtils.isEmpty(supplier) ||
            TextUtils.isEmpty(supplier_phone)){
            if(toast != null && toast.getView().getWindowVisibility() == View.VISIBLE)
                toast.cancel();
            toast = makeText(this, "One or more required field is Empty!",Toast.LENGTH_LONG);
            toast.show();
//            return;
        }
        else{
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
                    if(toast != null && toast.getView().getWindowVisibility() == View.VISIBLE)
                        toast.cancel();
                    toast = makeText(this,"Insertion failed",Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    if(toast != null && toast.getView().getWindowVisibility() == View.VISIBLE)
                        toast.cancel();
                    toast = makeText(this,"Item inserted",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else {
                int rowsAffected = getContentResolver().update(mCurrentItemUri, contentValues, null, null);
                if(rowsAffected == 0 ){
                    if(toast != null && toast.getView().getWindowVisibility() == View.VISIBLE)
                        toast.cancel();
                    toast = makeText(this, "Update failed!",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    if(toast != null && toast.getView().getWindowVisibility() == View.VISIBLE)
                        toast.cancel();
                    toast = makeText(this, "Update successful",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
            finish();
        }
    }

    private void deleteItem(){
        if(mCurrentItemUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if(rowsDeleted>0){
                toast = makeText(this, R.string.deletion_toast, Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                toast = makeText(this, R.string.deletion_error_toast, Toast.LENGTH_SHORT);
                toast.show();
            }

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
