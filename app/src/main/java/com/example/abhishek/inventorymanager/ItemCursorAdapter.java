package com.example.abhishek.inventorymanager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.inventorymanager.data.ItemContract;

public class ItemCursorAdapter extends CursorAdapter{

    public ItemCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView nameText = (TextView)view.findViewById(R.id.item_name);
        final TextView quantityText = (TextView)view.findViewById(R.id.quantity);
        TextView priceText = (TextView)view.findViewById(R.id.item_price);

        final String name = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME));
        final Integer quantity = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY));
        final Integer price = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE));
        Log.v("XXXXXXXXXX","All is well");
        final String supplier = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER));
        Log.v("XXXXXXXXXX","Pass 1");
        final String supplier_phone = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE));
        Log.v("XXXXXXXXXX","Pass 2");

        nameText.setText(name);
        quantityText.setText(Integer.toString(quantity));
        priceText.setText(Integer.toString(price));

        final int position = cursor.getPosition();

        Button sale = (Button)view.findViewById(R.id.sale);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(position);
                Integer currentQuantity = Integer.parseInt(quantityText.getText().toString());
                if(currentQuantity == 0){
                    Toast.makeText(view.getContext(), "No item available. Please place an order", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentQuantity-=1;
                quantityText.setText(Integer.toString(currentQuantity));
                Uri currentUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, position);
                ContentValues contentValues = new ContentValues();
                contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, name);
                contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE, price);
                contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, currentQuantity);
                contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER, supplier);
                contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE, supplier_phone);
                int rowsAffected = context.getContentResolver().update(currentUri, contentValues, null, null);
            }
        });
    }
}
