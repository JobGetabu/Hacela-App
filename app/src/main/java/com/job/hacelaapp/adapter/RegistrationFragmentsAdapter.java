package com.job.hacelaapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.job.hacelaapp.manageUsers.EmailPasswordFragment;
import com.job.hacelaapp.manageUsers.FacebookSignUpFragment;
import com.job.hacelaapp.manageUsers.GoogleSignUpFragment;

/**
 * Created by Job on Sunday : 4/1/2018.
 */
public class RegistrationFragmentsAdapter extends FragmentPagerAdapter {


    public RegistrationFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new EmailPasswordFragment();
            case 1:
                return new FacebookSignUpFragment();
            case 2:
                return new GoogleSignUpFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
