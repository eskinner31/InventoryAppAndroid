package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Skinner on 2/5/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public static String TAG = InventoryCursorAdapter.class.getSimpleName();

    public InventoryCursorAdapter(Context context, Cursor c) { super(context, c, 0); }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);

        return view;
    }

    /**
     * I was not able to implement the viewholder pattern with butterknife, is there
     * a special way to do it with Butterknife?
     *
     * Are there any resources for using the recyclerview with a cursoradapter?
     *
     * What are the options for setting up the click listener for the sale button;
     * in the adapter vs. the activity?
     *
     *
     */
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        TextView itemNameView = (TextView) view.findViewById(R.id.item_name);
        TextView supplierNameView = (TextView) view.findViewById(R.id.supplier_name);
        TextView stockView = (TextView) view.findViewById(R.id.stock);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        Button saleButtonView = (Button) view.findViewById(R.id.sale_button);


        int itemNameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int supplierNameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
        int itemStockIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_STOCK);
        int priceIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int itemId = cursor.getColumnIndex(InventoryEntry._ID);

        final Integer id = cursor.getInt(itemId);
        String itemName = cursor.getString(itemNameIndex);
        String supplierName = cursor.getString(supplierNameIndex);
        Double itemPrice = cursor.getDouble(priceIndex);
        final Integer itemStock = cursor.getInt(itemStockIndex);

        itemNameView.setText(itemName);
        supplierNameView.setText(supplierName);
        priceView.setText(Double.toString(itemPrice));
        stockView.setText(Integer.toString(itemStock));

        saleButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemStock < 1) {
                    return;
                }

                int updatedQuantity = itemStock - 1;
                ContentValues values = new ContentValues();
                values.put(InventoryEntry.COLUMN_STOCK, updatedQuantity);
                Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                view.getContext().getContentResolver().update(uri, values, null, null);
            }
        });
    }
}
