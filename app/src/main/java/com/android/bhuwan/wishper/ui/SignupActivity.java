package com.android.bhuwan.wishper.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.bhuwan.wishper.R;
import com.android.bhuwan.wishper.WishperApplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        mUsername = (EditText) findViewById(R.id.user_name);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.pwd);
        Button mButton = (Button) findViewById(R.id.sign_up);
        Button mCancelButton = (Button) findViewById(R.id.cancel);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = mPassword.getText().toString().trim();
                String userName = mUsername.getText().toString().trim();
                String email = mEmail.getText().toString().trim();

                if (pwd.isEmpty() || userName.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setTitle(R.string.dialog_title)
                            .setMessage(R.string.alter_msg)
                            .setPositiveButton(R.string.ok, null)
                            .show();
                } else {

                    ParseUser newUser = new ParseUser();
                    newUser.setEmail(email);
                    newUser.setUsername(userName);
                    newUser.setPassword(pwd);
                    mProgressBar.setVisibility(View.VISIBLE);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminate(false);
                            if (e == null) {
                                //success
                                WishperApplication.updateParseInstallion(ParseUser.getCurrentUser());
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setTitle(R.string.success)
                                        .setMessage(R.string.signed_up_msg)
                                        .setPositiveButton(R.string.ok, null)
                                        .show();
                            }
                        }
                    });
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
