package com.job.hacelaapp.manageUsers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwittersignUpFragment extends Fragment {


    private View mRootView;

    public static final String TAG = "TwittersignUpFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public TwittersignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_twittersign_up, container, false);
        ButterKnife.bind(this,mRootView);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        return mRootView;
    }

}
