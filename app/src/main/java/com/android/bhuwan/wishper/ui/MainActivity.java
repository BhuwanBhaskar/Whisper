package com.android.bhuwan.wishper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.android.bhuwan.wishper.R;
import com.android.bhuwan.wishper.adapters.SectionsPagerAdapter;
import com.android.bhuwan.wishper.utils.ParseConstants;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TAKE_PHOTOS = 0;
    private static final int TAKE_VIDEO = 1;
    private static final int SELECT_IMAGES = 2;
    private static final int SELECT_VIDEOS = 3;

    private static final int MEDIA_IMAGE = 4;
    private static final int MEDIA_VIDEO = 5;

    private static final String KEY_IMAGE_URI = "Image_Uri";
    private static final String KEY_VIDEO_URI = "Video_Uri";
    private static final int FILE_SIZE = 10*1024*1024;   //10MB size
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    protected Uri mMediaUri;
    private static final String WishperPrefs = "WishperPrefs";
    SharedPreferences sharedPreferences ;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private DialogInterface.OnClickListener mDialogInterface = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case TAKE_PHOTOS:  //snap pics
                    captureImage();
                    break;
                case TAKE_VIDEO: //record videos
                    captureVideos();
                    break;
                case SELECT_IMAGES: //select photos
                    Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    photoIntent.setType("image/*");
                    startActivityForResult(photoIntent, SELECT_IMAGES);
                    break;
                case SELECT_VIDEOS:  //Select Videos
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    startActivityForResult(intent, SELECT_VIDEOS);
                    break;
            }
        }

        private void captureVideos() {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            mMediaUri = getOutputMediaLocation(MEDIA_VIDEO);
            Log.d(TAG,"Path is : " + mMediaUri);
            if(mMediaUri == null){
                Toast.makeText(MainActivity.this, R.string.error_capture_image, Toast.LENGTH_LONG).show();
            }else{
                sharedPreferences.edit().putString(KEY_VIDEO_URI, mMediaUri + "").apply();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                Log.d(TAG, "@bhu" + intent.getData() + "");
                startActivityForResult(intent, TAKE_VIDEO);
            }
        }
    };

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = getOutputMediaLocation(MEDIA_IMAGE);
        Log.d(TAG,"Path is : " + mMediaUri);
        if(mMediaUri == null){
            Toast.makeText(MainActivity.this, R.string.error_capture_image, Toast.LENGTH_LONG).show();
        }else{
            sharedPreferences.edit().putString(KEY_IMAGE_URI, mMediaUri + "").apply();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            Log.d(TAG, "@bhu" + intent.getData() + "");
            startActivityForResult(intent, TAKE_PHOTOS);
        }
    }

    private Uri getOutputMediaLocation(int media) {
        if(isExternalLocationAvailable()){
            //Get external storage directory
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    MainActivity.this.getString(R.string.app_name));

            //Create our subdirectory
            if(!file.exists()){
                if(!file.mkdirs()){
                    Log.e(TAG,"Failed to create App Folder");
                    Toast.makeText(MainActivity.this,"Failed to create app folder",Toast.LENGTH_LONG).show();
                }
            }

            //create file name
            Date current = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(current);
            String path = file + File.separator;
            Log.d(TAG, " " + "@bhu" + path);
            //create file
            File fileName;
            if(MEDIA_IMAGE == media){
                fileName = new File(path + "IMG_" + timeStamp + ".jpg");
            }else if(MEDIA_VIDEO == media){
                fileName = new File(path + "VID_" + timeStamp + ".mp4");
            }else
                return null;

            Log.d(TAG, " " + Uri.fromFile(fileName));
            return Uri.fromFile(fileName);
        }
        else{
            return null;
        }
    }

    private boolean isExternalLocationAvailable() {
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //int titleColor = getResources().getColor(R.color.text_color);
        //String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#250054'>" + getString(R.string.app_name) + "</font>"));
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser == null) {
            LogIn();
        }
        else{
            Log.i(TAG, currentUser.getUsername());
        }
        sharedPreferences = getSharedPreferences(WishperPrefs, Context.MODE_PRIVATE);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //setupTabIcons();
        {
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_inbox);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_friends);
        }
    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(MainActivity.this,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_PHOTOS){
            if(resultCode == RESULT_OK){
                /*
                Not Working
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA,mUri);
                values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
                this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                */
                String mUri = sharedPreferences.getString(KEY_IMAGE_URI, "");
                addMediaToGallery(mUri);

                //scanFile(mUri);    //Not working standalone
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == TAKE_VIDEO){
            if(resultCode == RESULT_OK) {
                String mUri = sharedPreferences.getString(KEY_VIDEO_URI, "");
                addMediaToGallery(mUri);
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == SELECT_IMAGES || requestCode == SELECT_VIDEOS){
            if(resultCode == RESULT_OK && data != null){
                mMediaUri = data.getData();
                Log.d(TAG, "Media Selected : " + mMediaUri + "");

            }else{
                Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
            }
            if(requestCode == SELECT_VIDEOS){
                //Limit file size for videos to 10MB
                int fileSize = 0;
                InputStream inputStream = null;

                try {
                    inputStream = getContentResolver().openInputStream(mMediaUri);
                    fileSize = inputStream.available();
                } catch (FileNotFoundException e) {
                    Log.d(TAG,"File is not found" + e);
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    Log.d(TAG,"Error IO" + e);
                    e.printStackTrace();
                    return;
                }
                finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(fileSize >= FILE_SIZE){
                    Toast.makeText(this, R.string.video_size_warning, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        //Now we have selected media..time to send to recipients :)
        if(resultCode == RESULT_OK){
            Intent intent = new Intent(this, RecipientsActivity.class);
            intent.setData(mMediaUri);
            if(requestCode == TAKE_PHOTOS || requestCode == SELECT_IMAGES){
                intent.putExtra(ParseConstants.KEY_FILE_TYPE, ParseConstants.TYPE_IMAGE);
            }else if(requestCode == TAKE_VIDEO || requestCode == SELECT_VIDEOS){
                intent.putExtra(ParseConstants.KEY_FILE_TYPE, ParseConstants.TYPE_VIDEO);
            }
            startActivity(intent);
        }
    }

    private void addMediaToGallery(String mUri) {
        Log.d(TAG, "Broadcast sent : " + sharedPreferences.getString(KEY_VIDEO_URI, mUri));
        Uri myUri = Uri.parse(mUri);

        Toast.makeText(this, "Media saved to + " + myUri + " ", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(myUri);
        this.sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void LogIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        switch(itemId){
            case R.id.action_logout :
                Log.i(TAG, ParseUser.getCurrentUser().getUsername());
                ParseUser.logOut();
                LogIn();
                return true;

            case R.id.action_manage_friends :
                Intent intent = new Intent(this, ManageFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this,
                        R.style.AboutDialog);
                builder.setTitle("Choose ").setItems(R.array.camera_choices, mDialogInterface);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
