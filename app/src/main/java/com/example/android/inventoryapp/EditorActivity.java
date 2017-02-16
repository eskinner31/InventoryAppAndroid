package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //Variables
    private Uri mCurrentItemUri;
    private boolean mItemHasChanged;
    private Bitmap mImageStorage;

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
    @BindView(R.id.image_button) Button mImageButton;

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
                deleteDialog();
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

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispathTakePictureIntent();
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
            mImageStorage = bitmap;
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
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {mSupplierEmailEditText.getText().toString().trim()});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_line));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.body));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageStorage = imageBitmap;
        }
    }

    private void dispathTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void deleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_warning_message);
        builder.setPositiveButton(R.string.affirmative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     *  What are better practices for checking/storing images?
     *
     *  Would it be better to save to sdCard vs sqLite? Or just store a uri to the file
     *  location?
     */
    private void saveItemDetails() {

        if (mImageStorage == null) {
            Toast.makeText(this, getString(R.string.need_image),
                    Toast.LENGTH_SHORT).show();

            return;
        }


        String itemName = mItemNameEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierEmail = mSupplierEmailEditText.getText().toString().trim();
        String priceAsText = mPriceEditText.getText().toString().trim();
        String stockAsText = mStockEditText.getText().toString().trim();

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(supplierName) ||
                TextUtils.isEmpty(supplierEmail) || TextUtils.isEmpty(priceAsText) ||
                TextUtils.isEmpty(stockAsText)) {

            Toast.makeText(this, getString(R.string.fill_out_fields_warning),
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if (!supplierEmail.contains("@") || !supplierEmail.contains(".")) {
            Toast.makeText(this, getString(R.string.need_valid_email), Toast.LENGTH_SHORT).show();

            return;
        }

        //Converting values after verifying they are present
        Double price = Double.parseDouble(priceAsText);
        Integer stock = Integer.parseInt(stockAsText);
        Bitmap image = mImageStorage;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] compressedImage = stream.toByteArray();


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
