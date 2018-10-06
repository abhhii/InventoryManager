package com.example.abhishek.inventorymanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ItemProvider extends ContentProvider{

    private static final int ITEMS = 100;
    private static final int ITEMS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEMS_ID);
    }

    public static final String LOG_TAG = ItemProvider.class.getSimpleName();
    private ItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(this.getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME, projection,selection,selectionArgs, null, null, sortOrder);
                break;
            case ITEMS_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEMS_ID:
                return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri "+ uri + "with match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not supported for "+ uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match;
        match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match){
            case ITEMS_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEMS:
                rowsDeleted = database.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete operation not supported for "+uri);
        }
        if(rowsDeleted!=0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                return updateItem(uri, values, selection, selectionArgs);
            case ITEMS_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        String name = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
        if(name == null)
            throw new IllegalArgumentException("Item requires a name");
        Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
        if(price == null)
            throw new IllegalArgumentException("Item requires a price");

        Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
        if(quantity == null)
            throw new IllegalArgumentException("Item requires a quantity");

//        String suppplier = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER);
//        String supplier_phone = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE)

        long id = database.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);
        if(id == -1)
        {
            Log.v(LOG_TAG, "Failed to insert row for "+ uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    private int updateItem(Uri uri, ContentValues values, String selection, String selectionArgs[]){
        if(values.size() == 0)
            return 0;
        if(values.containsKey(ItemContract.ItemEntry.COLUMN_ITEM_NAME)){
            String name = values.getAsString(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            if(name == null)
                throw new IllegalArgumentException("Item requires a name");
        }
        if(values.containsKey(ItemContract.ItemEntry.COLUMN_ITEM_PRICE)){
            Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
            if(price == null)
                throw new IllegalArgumentException("Item requires a price");
        }
        if (values.containsKey(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY)){
            Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
            if(quantity == null)
                throw new IllegalArgumentException("Item requires a quanity");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ItemContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
