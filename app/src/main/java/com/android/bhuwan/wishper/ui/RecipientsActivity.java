package com.android.bhuwan.wishper.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.bhuwan.wishper.R;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Log.e(TAG, "onCreate Called");
        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            Log.e(TAG, "calling fm");
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new RecipientsFragment())
                    .commit();
        }
        Log.e(TAG,"Recipient List should be displayed by now");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
