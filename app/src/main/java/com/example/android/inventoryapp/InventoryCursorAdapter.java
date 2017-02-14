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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Skinner on 2/5/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) { super(context, c, 0); }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);

        return view;
    }

    /**
     * Is this the proper way to implement the ViewHolder Patter with Butterknife?
     *
     * Are there any resources for using the recyclerview with a cursoradapter?
     *
     * What are the options for setting up the click listener for the sale button;
     * in the adapter vs. the activity
     */
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int itemNameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int supplierNameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
        int itemStockIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_STOCK);
        int itemId = cursor.getColumnIndex(InventoryEntry._ID);

        final Integer id = cursor.getInt(itemId);
        String itemName = cursor.getString(itemNameIndex);
        String supplierName = cursor.getString(supplierNameIndex);
        final Integer itemStock = cursor.getInt(itemStockIndex);

        viewHolder.itemNameView.setText(itemName);
        viewHolder.supplierNameView.setText(supplierName);
        viewHolder.stockView.setText(Integer.toString(itemStock));

        viewHolder.saleButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemStock <= 0) {
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

    static class ViewHolder {
        @BindView(R.id.item_name) TextView itemNameView;
        @BindView(R.id.supplier_name) TextView supplierNameView;
        @BindView(R.id.stock) TextView stockView;
        @BindView(R.id.sale_button) Button saleButtonView;

        public ViewHolder(View view)  {
            ButterKnife.bind(this, view);
        }
    }
}
