package com.android.bhuwan.wishper.adapters;

/**
 * Created by bhuwan on 10/15/2015.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.bhuwan.wishper.ui.FriendsFragment;
import com.android.bhuwan.wishper.ui.InboxFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0)
            return new InboxFragment();
        else if(position == 1)
            return new FriendsFragment();

        return null;
    }

    @Override
    public int getCount() {

        return 2;
    }

    //Since we are showing only icons so let us return null here
    @Override
    public CharSequence getPageTitle(int position) {
        /*
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section_1);
            case 1:
                return mContext.getString(R.string.title_section_2);
        }
        */
        return null;
    }

}
