package com.example.abhishek.inventorymanager.data;

import android.provider.BaseColumns;

public class ItemContract {
    public static abstract  class ItemEntry implements BaseColumns{

        public static final String TABLE_NAME = "items";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_SUPPLIER = "supplier";
        public static final String COLUMN_ITEM_SUPPLIER_PHONE = "supplierPhone";
    }
}
