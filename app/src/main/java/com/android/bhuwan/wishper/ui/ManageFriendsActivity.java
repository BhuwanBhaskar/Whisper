package com.android.bhuwan.wishper.ui;

import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.bhuwan.wishper.R;

public class ManageFriendsActivity extends AppCompatActivity {

    private static final String TAG = ManageFriendsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate Called");
        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            Log.e(TAG, "calling fm");
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new FriendsListFragment())
                    .commit();
        }
        setupActionBar();
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
