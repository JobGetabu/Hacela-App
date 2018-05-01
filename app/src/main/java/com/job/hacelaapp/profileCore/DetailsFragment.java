package com.job.hacelaapp.profileCore;


import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UserBasicInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    @BindView(R.id.frg_details_phonenumber)
    TextInputLayout mPhonenumber;

    private View mRootView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private DetailsEditActivityViewModel model;

    public static final String TAG = "DetailsFragment";

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.frag_profile_details, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this,mRootView);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        DetailsEditActivityViewModel.Factory factory = new DetailsEditActivityViewModel.Factory(
                getActivity().getApplication(), mAuth, mFirestore);

        model = ViewModelProviders.of(this, factory)
                .get(DetailsEditActivityViewModel.class);

        MediatorLiveData<UserBasicInfo> data = model.getUsersLiveData();

        data.observe(this, new Observer<UserBasicInfo>() {
            @Override
            public void onChanged(@Nullable UserBasicInfo userBasicInfo) {
                Log.d(TAG, "onChanged: data retrieved is "+userBasicInfo.toString());
                mPhonenumber.getEditText().setText(userBasicInfo.getUsername());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.profile_menu_edit:
                Intent detailsEditIntent = new Intent(getActivity(),DetailsEditActivity.class);
                startActivity(detailsEditIntent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
