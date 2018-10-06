package com.example.abhishek.inventorymanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.inventorymanager.data.ItemContract;
import com.example.abhishek.inventorymanager.data.ItemDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ITEM_LOADER = 0;
    ItemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(i);
            }
        });

        ListView itemListView = (ListView) findViewById(R.id.list_view_items);
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);
        mCursorAdapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(mCursorAdapter);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void insert_item(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME,"A brief History of time");
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE,400);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,10);
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER,"Penguin");
        contentValues.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE,"8987456210");
        Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, contentValues);
    }

    private void deleteAllItems(){
        int rowsDeleted = getContentResolver().delete(ItemContract.ItemEntry.CONTENT_URI, null, null);
        if(rowsDeleted>0)
            Toast.makeText(this, "All Items deleted", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Deletion failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insert_item();
                //finish();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY};

        return new CursorLoader(this, ItemContract.ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}