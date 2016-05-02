package com.android.bhuwan.wishper.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.bhuwan.wishper.R;
import com.android.bhuwan.wishper.adapters.MessageAdapter;
import com.android.bhuwan.wishper.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhuwan on 10/16/2015.
 */
public class InboxFragment extends ListFragment {

    private List<ParseObject> mInboxMessages;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);
        return rootView;
    }

    SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        retrieveMessages();
    }

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                mInboxMessages = objects;

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    String[] userNames = new String[mInboxMessages.size()];
                    int i = 0;
                    for (ParseObject message : mInboxMessages) {
                        userNames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }

                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mInboxMessages);
                        setListAdapter(adapter);
                    } else {
                        //to maintain scroll position
                        ((MessageAdapter) getListView().getAdapter()).refill(mInboxMessages);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mInboxMessages.get(position);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri mediaUri = Uri.parse(file.getUrl());
        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            Intent intent = new Intent(getActivity(), ViewImage.class);
            intent.setData(mediaUri);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, mediaUri);
            intent.setDataAndType(mediaUri, "video/*");
            startActivity(intent);
        }

        //Going to delete recipient who have been viewed
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
        //last recipient i.e. this message may have been sent to multiple recipients ..but if current
        //user is last user to view file then we delete the message
        if (ids.size() == 1) {
            message.deleteInBackground();
        }
        //if current user is not last user to view this message then this message should be
        //removed from current user's list and not others
        else {
            ids.remove(ParseUser.getCurrentUser().getObjectId());
            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            message.saveInBackground();
        }
    }
}
