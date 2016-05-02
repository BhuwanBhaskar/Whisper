package com.android.bhuwan.wishper.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.bhuwan.wishper.R;
import com.android.bhuwan.wishper.adapters.UserAdapter;
import com.android.bhuwan.wishper.utils.FileHelper;
import com.android.bhuwan.wishper.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhuwan on 10/20/2015.
 */
public class RecipientsFragment extends Fragment {

    private static final String TAG = RecipientsFragment.class.getSimpleName();

    private MenuItem menuItem;

    private ParseRelation<ParseUser> mRelations;
    private ParseUser mCurrentUser;
    private List<ParseUser> mList;
    private Uri mMediaUri;
    private String mFileType;
    private GridView mGridView;
    private TextView mEmptyTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mEmptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(mEmptyTextView);

        mGridView.setOnItemClickListener(mOnItemClickListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMediaUri = getActivity().getIntent().getData();
        mFileType = getActivity().getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        setHasOptionsMenu(true);
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mCurrentUser = ParseUser.getCurrentUser();
        mRelations = mCurrentUser.getRelation(ParseConstants.KEY_RELATION);
        ParseQuery<ParseUser> query = mRelations.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mList = friends;
                    String[] userNames = new String[mList.size()];
                    int i = 0;
                    for (ParseUser user : mList) {
                        userNames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), friends);
                        mGridView.setAdapter(adapter);
                    } else {
                        //to maintain scroll position
                        ((UserAdapter) mGridView.getAdapter()).refill(friends);
                    }
                    Log.e(TAG, "set adapter called");
                } else {
                    Log.e(TAG, e.getMessage());
                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.dialog_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    */
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_recipients, menu);
        menuItem = menu.getItem(0);
        menuItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_send){
            ParseObject message = createMessage();
            if(message != null){
                Log.d(TAG, "Going to send message");
                sendMessage(message);
                Log.d(TAG, "Message should now be sent..Finishing Activity");
                getActivity().finish();
            }else{
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                builder.setMessage(R.string.error_selecting_media)
                        .setTitle(R.string.error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                */
                Log.e(TAG, "this Message was not sent");
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d(TAG, "Message Sent !!! Displaying Toast...");
                    //pushNotifications();
                    ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
                    Log.d(TAG, "Installtion got");
                    query.whereContainedIn(ParseConstants.USER_ID, getRecipientIds());
                    Log.d(TAG, "query created");
                    ParsePush push = new ParsePush();
                    push.setQuery(query);
                    Log.d(TAG, "query set");
                    push.setMessage("Message has been sent");
                    Log.e(TAG, "Query has been set");
                    push.sendInBackground();
                    Log.e(TAG, "Query has been sent");

                   // Toast.makeText(getListView().getContext(), R.string.message_sent, Toast.LENGTH_LONG).show();
                }else{
                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(R.string.error_sending_message)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    */
                    Log.e(TAG, "Message was not sent");
                }
            }
        });
    }

    private void pushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.USER_ID, getRecipientIds());

        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message, ParseUser.getCurrentUser().getUsername()));
        Log.e(TAG, "Query has been set");
        push.sendInBackground();
        Log.e(TAG, "Query has been sent");
    }

    private ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_IDS, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(getActivity(), mMediaUri);
        if(fileBytes != null){
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(getActivity(), mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

            return message;
        }else{
            return null;
        }

    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipients = new ArrayList<>();

        for(int i = 0; i < mGridView.getCount(); i++){
            if(mGridView.isItemChecked(i)){
                recipients.add(mList.get(i).getObjectId());
            }
        }
        return recipients;
    }


    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView imageView = (ImageView) view.findViewById(R.id.checkImage);
            if(mGridView.getCheckedItemCount() > 0){
                menuItem.setVisible(true);
            }else{
                menuItem.setVisible(false);
            }

            if (mGridView.isItemChecked(position)) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    };
}
