package com.android.bhuwan.wishper.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bhuwan.wishper.R;
import com.android.bhuwan.wishper.adapters.UserAdapter;
import com.android.bhuwan.wishper.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.List;

/**
 * Created by bhuwan on 10/17/2015.
 */
public class FriendsListFragment extends Fragment {
    private static final String TAG = FriendsListFragment.class.getSimpleName();
    private ParseRelation<ParseUser> mRelations;
    private ParseUser mCurrentUser;
    private List<ParseUser> mList;
    private GridView mGridView;
    private TextView mEmptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
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

        mCurrentUser = ParseUser.getCurrentUser();
        mRelations = mCurrentUser.getRelation(ParseConstants.KEY_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> mUsers, ParseException e) {
                if (e == null) {
                    // Success
                    mList = mUsers;
                    String[] userNames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        userNames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        //to maintain scroll position
                        ((UserAdapter) mGridView.getAdapter()).refill(mUsers);
                    }

                    addFriendsCheckMark();
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
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    private void addFriendsCheckMark() {
        mRelations.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < mList.size(); i++) {
                        ParseUser user = mList.get(i);
                        for (ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Fetching Data Failed");
                    Toast.makeText(getActivity(), "Network Problem", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView imageView = (ImageView) view.findViewById(R.id.checkImage);
            if (mGridView.isItemChecked(position)) {
                mRelations.add(mList.get(position));

                imageView.setVisibility(View.VISIBLE);
            } else {
                //Remove unchecked friends from relation
                mRelations.remove(mList.get(position));
                imageView.setVisibility(View.INVISIBLE);
            }
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.e(TAG, " Saved");
                       // Toast.makeText(getListView().getContext(), "Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, " Save Failed");
                        //Toast.makeText(getListView().getContext(), "Save Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

}
