package com.example.android.inventoryapp.data;

import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Skinner on 2/5/17.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String TAG = InventoryDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";
}
