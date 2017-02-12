package com.example.android.inventoryapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Skinner on 2/5/17.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Constants
    public static final String TAG = EditorActivity.class.getSimpleName();
    public static final int INVENTORY_LOADER = 0;

    //Views
    @BindView(R.id.save_button) Button mSaveButton;
    @BindView(R.id.image_button) Button mImageButton;
    @BindView(R.id.order_button) Button mOrderButton;
    @BindView(R.id.delete_button) Button mDeleteButton;
    @BindView(R.id.order_received_button) Button mOrderReceivedButton;
    @BindView(R.id.stock_edittext) EditText mStockEditText;
    @BindView(R.id.price_edittext) EditText mPriceEditText;
    @BindView(R.id.supplier_email_edittext) EditText mSupplierEmailEditText;
    @BindView(R.id.supplier_name_edittext) EditText mSupplierNameEditText;
    @BindView(R.id.item_name_edittext) EditText mItemNameEditText;

    //TODO: ALL OF YOUR VALIDATION AND WORK/CHECKS WILL BE DONE HERE.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
