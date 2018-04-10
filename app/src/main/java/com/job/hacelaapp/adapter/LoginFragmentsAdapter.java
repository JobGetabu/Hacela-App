package com.job.hacelaapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.job.hacelaapp.manageUsers.EmailPasswordLogInFragment;
import com.job.hacelaapp.manageUsers.FacebookLogInFragment;
import com.job.hacelaapp.manageUsers.GoogleLogInFragment;

/**
 * Created by Job on Sunday : 4/8/2018.
 */
public class LoginFragmentsAdapter extends FragmentPagerAdapter {

    public LoginFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new EmailPasswordLogInFragment();
            case 1:
                return new FacebookLogInFragment();
            case 2:
                return new GoogleLogInFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
