package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Constants
    private static final String TAG = InventoryActivity.class.getSimpleName();
    private static final int INVENTORY_LOADER = 0;

    //Views
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.inventory_list_view) RecyclerView mInventoryListView;

    //Listeners
    //TODO: ATTEMPT BINDING LISTENERS WITH BUTTERKNIFE ANNOTATIONS

    //Adapters
    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent creationIntent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(creationIntent);
            }
        });

        // TODO: 2/6/17 set up adapter and listeners 
        // TODO: 2/6/17 set up empty list view 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sample_insert:
                insertHelperData();
                return true;
            case R.id.sample_remove_all:
                deleteAll();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //DEFINE THE PROJECTION FOR WHAT WE WANT BACK ON THE LIST VIEW
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_STOCK
        };

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);
    }


    private void insertHelperData() {
        //DO STUFF
    }

    private void deleteAll() {
        //DO STUFF
    }
}
