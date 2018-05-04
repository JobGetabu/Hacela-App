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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UserAuthInfo;
import com.job.hacelaapp.dataSource.UserBasicInfo;
import com.job.hacelaapp.dataSource.UsersProfile;
import com.job.hacelaapp.util.ImageProcessor;
import com.job.hacelaapp.viewmodel.DetailsEditActivityViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {


    TextView mUserName;
    CircleImageView mProfileImage;


    @BindView(R.id.frg_details_phonenumber)
    TextInputLayout mPhonenumber;
    @BindView(R.id.frg_details_tvprofilecompletion)
    TextView mProfileCompletiontext;
    @BindView(R.id.frg_details_email)
    TextInputLayout mEmailaddress;
    @BindView(R.id.frg_details_idnumber)
    TextInputLayout mIdNumber;
    @BindView(R.id.frg_details_tick_fbicon)
    ImageView mFbtick;
    @BindView(R.id.frg_details_tick_googleicon)
    ImageView mGoogleTick;
    @BindView(R.id.frg_details_tick_emailicon)
    ImageView mEmailTick;

    @BindView(R.id.frg_details_profession)
    TextInputLayout mProfession;
    @BindView(R.id.frg_details_typeofbiz)
    TextInputLayout mTypeofBiz;
    @BindView(R.id.frg_details_income)
    TextInputLayout mIncome;

    private View mRootView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    private DetailsEditActivityViewModel model;
    private ImageProcessor imageProcessor;

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
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mUserName = getActivity().findViewById(R.id.profile_frg_username);
        mProfileImage = getActivity().findViewById(R.id.profile_frg_image);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        imageProcessor = new ImageProcessor();

        DetailsEditActivityViewModel.Factory factory = new DetailsEditActivityViewModel.Factory(
                getActivity().getApplication(), mAuth, mFirestore);

        model = ViewModelProviders.of(this, factory)
                .get(DetailsEditActivityViewModel.class);

        //UI observers

        setUpBasicInfo(model);
        setUpAuthInfo(model);
        setUpProfileInfo(model);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.profile_menu_edit:
                Intent detailsEditIntent = new Intent(getActivity(), DetailsEditActivity.class);
                startActivity(detailsEditIntent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //set ui with Users data
    private void setUpBasicInfo(DetailsEditActivityViewModel model) {
        MediatorLiveData<UserBasicInfo> data = model.getUsersLiveData();

        data.observe(this, new Observer<UserBasicInfo>() {
            @Override
            public void onChanged(@Nullable UserBasicInfo userBasicInfo) {

                if (userBasicInfo != null) {

                    mUserName.setText(userBasicInfo.getUsername());
                    mEmailaddress.getEditText().setText(mCurrentUser.getEmail());
                    imageProcessor.setMyImage(mProfileImage, userBasicInfo.getPhotourl());
                }
            }
        });
    }

    //set ui with UsersAuth data
    private void setUpAuthInfo(DetailsEditActivityViewModel model) {

        MediatorLiveData<UserAuthInfo> data = model.getUserAuthInfoMediatorLiveData();

        data.observe(this, new Observer<UserAuthInfo>() {
            @Override
            public void onChanged(@Nullable UserAuthInfo userAuthInfo) {
                if (userAuthInfo != null) {
                    if (!userAuthInfo.getPhonenumber().isEmpty())
                    mPhonenumber.getEditText().setText(userAuthInfo.getPhonenumber());
                    Log.d(TAG, "onChanged: "+userAuthInfo.toString());
                    if (userAuthInfo.getFbconnected()) mFbtick.setVisibility(View.VISIBLE);
                    else mFbtick.setVisibility(View.INVISIBLE);
                    if (userAuthInfo.getGoogleconnected()) mGoogleTick.setVisibility(View.VISIBLE);
                    else mGoogleTick.setVisibility(View.INVISIBLE);
                    if (userAuthInfo.getGoogleconnected()) mEmailTick.setVisibility(View.VISIBLE);
                    else mEmailTick.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    //set ui with UsersProfile data
    private void setUpProfileInfo(DetailsEditActivityViewModel model){

        MediatorLiveData<UsersProfile> data = model.getUsersProfileMediatorLiveData();

        data.observe(this, new Observer<UsersProfile>() {
            @Override
            public void onChanged(@Nullable UsersProfile usersProfile) {

                if (usersProfile != null){

                    mProfession.getEditText().setText(usersProfile.getProfession());
                    mTypeofBiz.getEditText().setText(usersProfile.getTypeOfBusiness());
                    mIncome.getEditText().setText(usersProfile.getIncome());
                }
            }
        });

    }
}
