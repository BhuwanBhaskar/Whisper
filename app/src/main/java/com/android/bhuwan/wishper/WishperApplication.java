package com.android.bhuwan.wishper;

import android.app.Application;
import android.util.Log;

import com.android.bhuwan.wishper.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

/**
 * Created by bhuwan on 10/9/2015.
 */
public class WishperApplication extends Application {
    @Override
    public void onCreate() {
        Log.d("@buwan","calling parse");
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ZedHC8gnW5tTLlPwHqOG7IhASBY6JCDqFFPZyIgh", "2DjFEXAcwvyfcxJrukOVgU1O6ZVUuDNnCu9W9b3o");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstallion(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.USER_ID, user.getObjectId());
        installation.saveInBackground();

    }
}
