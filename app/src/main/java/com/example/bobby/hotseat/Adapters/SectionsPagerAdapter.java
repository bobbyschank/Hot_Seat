package com.example.bobby.hotseat.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.bobby.hotseat.UI.ChooseFragment;
import com.example.bobby.hotseat.UI.FriendsFragment;
import com.example.bobby.hotseat.UI.InboxFragment;
import com.example.bobby.hotseat.R;


/**
 * Created by bobby on 6/6/16.
 */

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        //return MainActivity.InboxFragment.newInstance(position + 1);
        switch(position) {
            case 0:
                return new InboxFragment();
            case 1:
                return new ChooseFragment();
            case 2:
                return new FriendsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section_inbox);
            case 1:
                return mContext.getString(R.string.title_section_choose);
            case 2:
                return mContext.getString(R.string.title_section_friends);
        }
        return null;
    }
}