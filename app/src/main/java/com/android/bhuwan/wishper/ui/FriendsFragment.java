package com.android.bhuwan.wishper.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.bhuwan.wishper.R;
import com.android.bhuwan.wishper.adapters.MessageAdapter;
import com.android.bhuwan.wishper.adapters.UserAdapter;
import com.android.bhuwan.wishper.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by bhuwan on 10/16/2015.
 */
public class FriendsFragment extends Fragment {

    private static final String TAG = FriendsFragment.class.getSimpleName();
    private ParseRelation<ParseUser> mRelations;
    private ParseUser mCurrentUser;
    private List<ParseUser> mList;
    private GridView mGridView;
    private TextView mEmptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mGridView = (GridView)rootView.findViewById(R.id.gridView);
        mEmptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(mEmptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                        UserAdapter adapter = new UserAdapter(getActivity(),friends);
                        mGridView.setAdapter(adapter);
                    } else {
                        //to maintain scroll position
                        ((UserAdapter) mGridView.getAdapter()).refill(friends);
                    }

                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.dialog_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}
