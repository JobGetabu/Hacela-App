package com.job.hacelaapp.manageUsers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookSignUpFragment extends Fragment {


    private View mRootView;

    public FacebookSignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_facebook_sign_up, container, false);
        ButterKnife.bind(this,mRootView);

        return mRootView;
    }

}
