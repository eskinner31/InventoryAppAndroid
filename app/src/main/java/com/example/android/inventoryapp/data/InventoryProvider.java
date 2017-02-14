package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Skinner on 2/5/17.
 */

public class InventoryProvider extends ContentProvider {
    public static final String TAG = InventoryProvider.class.getSimpleName();

    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_INVENTORY, INVENTORY);

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match) {
            case INVENTORY:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case INVENTORY:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for" + uri);
        }

    }

    private Uri insertItem(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String itemName = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (itemName == null) {
                throw new IllegalArgumentException("Item needs a name");
            }

        String supplierName = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Need a supplier");
            }

        String supplierEmail = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            if (supplierEmail == null) {
                throw new IllegalArgumentException("Need email");
            }
            if (!supplierEmail.contains("@") && !supplierEmail.contains(".")) {
                throw new IllegalArgumentException("Not a valid email");
            }

        int quantityInStock = values.getAsInteger(InventoryEntry.COLUMN_STOCK);
            if (quantityInStock < 1) {
                throw new IllegalArgumentException("Need at least one unit");
            }

        Double price = values.getAsDouble(InventoryEntry.COLUMN_PRICE);
            if (price <= 0) {
                throw new IllegalArgumentException("Need a valid price");
            }

        byte[] image = values.getAsByteArray(InventoryEntry.COLUMN_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Need an image");
            }

        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(TAG, "failed to insert row " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete not available for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case INVENTORY:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = INVENTORY_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported " + uri);
        }
    }


    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db;

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String itemName = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (itemName == null) {
                throw new IllegalArgumentException("Item needs a name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Need a supplier");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplierEmail = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            if (supplierEmail == null) {
                throw new IllegalArgumentException("Need email");
            }
            if (!supplierEmail.contains("@") && !supplierEmail.contains(".")) {
                throw new IllegalArgumentException("Not a valid email");
            }
        }


        if (values.containsKey(InventoryEntry.COLUMN_STOCK)) {
            int quantityInStock = values.getAsInteger(InventoryEntry.COLUMN_STOCK);
            if (quantityInStock < 1) {
                throw new IllegalArgumentException("Need at least one unit");
            }
        }


        if (values.containsKey(InventoryEntry.COLUMN_PRICE)) {
            Double price = values.getAsDouble(InventoryEntry.COLUMN_PRICE);
            if (price <= 0) {
                throw new IllegalArgumentException("Need a valid price");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_IMAGE)) {
            byte[] image = values.getAsByteArray(InventoryEntry.COLUMN_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Need an image");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
