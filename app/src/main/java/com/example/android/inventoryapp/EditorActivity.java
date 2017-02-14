package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Skinner on 2/5/17.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //TODO Validation
    //TODO CHECKS FOR COMPLETION

    //Constants
    public static final String TAG = EditorActivity.class.getSimpleName();
    public static final int INVENTORY_LOADER = 0;

    //Variables
    private Uri mCurrentItemUri;
    private boolean mItemHasChanged;

    //Listeners
    private View.OnTouchListener mTouchListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    //Views
    @BindView(R.id.save_button) Button mSaveButton;
    @BindView(R.id.order_button) Button mOrderButton;
    @BindView(R.id.delete_button) Button mDeleteButton;
    @BindView(R.id.order_received_button) Button mOrderReceivedButton;
    @BindView(R.id.stock_edittext) EditText mStockEditText;
    @BindView(R.id.price_edittext) EditText mPriceEditText;
    @BindView(R.id.supplier_email_edittext) EditText mSupplierEmailEditText;
    @BindView(R.id.supplier_name_edittext) EditText mSupplierNameEditText;
    @BindView(R.id.item_name_edittext) EditText mItemNameEditText;
    @BindView(R.id.item_image_view) ImageView mItemImageView;
    @BindView(R.id.image_link_edittext) EditText mImageLinkView;

    //TODO: ALL OF YOUR VALIDATION AND WORK/CHECKS WILL BE DONE HERE.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {
            setTitle(R.string.add_item);
            mDeleteButton.setVisibility(View.GONE);
            mOrderReceivedButton.setVisibility(View.GONE);
        } else {
            setTitle(R.string.edit_item);
            getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItemDetails();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem();
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailSupplier();
            }
        });

        mOrderReceivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderReceived();
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_STOCK,
                InventoryEntry.COLUMN_IMAGE
        };

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data == null || data.getCount() < 1) {
            return;
        }

        if(data.moveToFirst()) {
            int itemNameIndex = data.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int supplierNameIndex = data.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int itemStockIndex = data.getColumnIndex(InventoryEntry.COLUMN_STOCK);
            int supplierEmailIndex = data.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            int priceIndex = data.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int imageIndex = data.getColumnIndex(InventoryEntry.COLUMN_IMAGE);

            String itemName = data.getString(itemNameIndex);
            String supplierName = data.getString(supplierNameIndex);
            Integer itemStock = data.getInt(itemStockIndex);
            String supplierEmail = data.getString(supplierEmailIndex);
            Double price = data.getDouble(priceIndex);
            byte[] imageBlob = data.getBlob(imageIndex);

            mItemNameEditText.setText(itemName);
            mSupplierNameEditText.setText(supplierName);
            mSupplierEmailEditText.setText(supplierEmail);
            mStockEditText.setText(Integer.toString(itemStock));
            mPriceEditText.setText(Double.toString(price));

            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBlob);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            mItemImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemNameEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierEmailEditText.setText("");
        mStockEditText.setText(Integer.toString(0));
        mPriceEditText.setText(Double.toString(0.00));
        mItemImageView.setImageBitmap(null);
    }

    private void orderReceived() {
        Integer currentStock = Integer.parseInt(mStockEditText.getText().toString().trim());
        Integer updatedOrder = currentStock + 10;

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_STOCK, updatedOrder);

        int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.update_stock_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.update_stock_succeeded), Toast.LENGTH_SHORT).show();
        }
    }

    private void emailSupplier() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, mSupplierEmailEditText.getText().toString().trim());

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void deleteItem() {

        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_succeeded), Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private Bitmap selectImage(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();;
            InputStream input = connection.getInputStream();
            Bitmap imageBitmap = BitmapFactory.decodeStream(input);
            return imageBitmap;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    private void saveItemDetails() {

        String itemName = mItemNameEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierEmail = mSupplierEmailEditText.getText().toString().trim();
        Double price = Double.parseDouble(mPriceEditText.getText().toString().trim());
        Integer stock = Integer.parseInt(mStockEditText.getText().toString().trim());
        Bitmap image = selectImage(mImageLinkView.getText().toString().trim());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] compressedImage = stream.toByteArray();


        if (mCurrentItemUri == null && TextUtils.isEmpty(itemName) &&
                TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierEmail) &&
                price == null && stock == null) {

            Toast.makeText(this, getString(R.string.fill_out_fields_warning),
                    Toast.LENGTH_SHORT).show();

            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
        values.put(InventoryEntry.COLUMN_PRICE, price);
        values.put(InventoryEntry.COLUMN_STOCK, stock);
        values.put(InventoryEntry.COLUMN_IMAGE, compressedImage);

        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_item_succeeded), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_succeeded), Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
}
