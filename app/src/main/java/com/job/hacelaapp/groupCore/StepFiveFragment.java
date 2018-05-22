package com.job.hacelaapp.groupCore;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.GroupContributionDefault;
import com.job.hacelaapp.dataSource.GroupDescription;
import com.job.hacelaapp.dataSource.Groups;
import com.job.hacelaapp.dataSource.Penalty;
import com.job.hacelaapp.dataSource.Savings;
import com.job.hacelaapp.dataSource.Step4OM;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFiveFragment extends Fragment {


    @BindView(R.id.stepfive_switch)
    SwitchCompat switchCompat;
    @BindView(R.id.stepfive_penalty)
    TextInputLayout mPenalty;

    private View mRootView;

    private static final String TAG = "stepfive";

    private CreateGroupViewModel createGroupViewModel;
    private NoInternetDialog noInternetDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String currentUserId;

    public StepFiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_step_five, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createGroupViewModel = ViewModelProviders.of(getActivity()).get(CreateGroupViewModel.class);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        //build no net dialogue
        setUpNoNetDialogue();

    }

    private void setUpNoNetDialogue() {
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setBgGradientOrientation(45)
                .setCancelable(true)
                .setBgGradientStart(getResources().getColor(R.color.app_gradient_start))
                .setBgGradientEnd(getResources().getColor(R.color.app_gradient_end))
                .build();
    }

    @OnClick(R.id.stepfive_switch)
    public void stepFiveSwitch() {

        if (switchCompat.isChecked()) {
            mPenalty.setEnabled(true);
        } else {
            mPenalty.setEnabled(false);
        }
    }

    private String groupfullname;
    private String groupdisplayname;
    private GroupDescription groupDesp;
    private Step4OM step4data;

    @OnClick(R.id.stepfive_fab)
    public void stepFiveFinish() {

        //get all observables here


        createGroupViewModel.getGroupFullName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    groupfullname = s;
                }
            }
        });
        createGroupViewModel.getGroupDisplayName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    groupdisplayname = s;
                }
            }
        });
        createGroupViewModel.getGroupDescriptionMutableLiveData().observe(this, new Observer<GroupDescription>() {
            @Override
            public void onChanged(@Nullable GroupDescription groupDescription) {
                if (groupDescription != null)
                    groupDesp = groupDescription;
            }
        });
        createGroupViewModel.getStep4OMMutableLiveData().observe(this, new Observer<Step4OM>() {
            @Override
            public void onChanged(@Nullable Step4OM step4OM) {
                if (step4OM != null)
                    step4data = step4OM;
            }
        });

        //Toast.makeText(getContext(), "\n" + groupfullname + "\n" + groupdisplayname + "\n" + groupDesp + "\n" + step4data, Toast.LENGTH_LONG).show();

        if (step4data == null){
            return;
        }

        Groups groups = new Groups();
        groups.setDisplayname(groupdisplayname);
        groups.setGroupname(groupfullname);
        groups.setDescription(groupDesp);

        GroupContributionDefault groupContributionDefault = new GroupContributionDefault();
        groupContributionDefault.setCycleamount(step4data.getAmount());
        groupContributionDefault.setCycleinterval(step4data.getIntervalPeriod());

        Savings savings;
        if (step4data.getSavings() == 0) {
            //no savings selected
            savings = new Savings(false, 0);
            step4data.setSavings(0);
        } else {
            savings = new Savings(true, step4data.getSavings());
        }

        groupContributionDefault.setSavings(savings);

        if (switchCompat.isChecked()) {
            //validate amount
            if (!validate()) {
                return;
            }

            Penalty penalty = new Penalty("late payment", Double.parseDouble(mPenalty.getEditText().getText().toString()));
            groupContributionDefault.setPenalty(penalty);

        }

        //Toast.makeText(getContext(), "" + groups.toString() + "\n" + groupContributionDefault.toString(), Toast.LENGTH_LONG).show();

        //uploading to server

        String groupId = mFirestore.collection("Groups").document().getId();

        //init
        DocumentReference GROUPREF = mFirestore.collection("Groups").document(groupId);
        DocumentReference GROUPDEFREF = mFirestore.collection("GroupsContributionDefault").document(groupId);

        //check connection
        noInternetDialog.showDialog();

        final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Creating Group...");
        pDialog.setCancelable(false);
        pDialog.show();


        // Get a new write batch
        WriteBatch batch = mFirestore.batch();
        //upload the group
        batch.set(GROUPREF, groups);
        //upload group default
        batch.set(GROUPDEFREF,groupContributionDefault);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            sendToMain();
                        }
                    });

                }else {
                    pDialog.dismiss();
                    Log.d(TAG, "onComplete: error" + task.getException().toString());
                    errorPrompt();
                }
            }
        });

    }

    private void errorPrompt() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    public boolean validate() {
        boolean valid = true;

        String amount = mPenalty.getEditText().getText().toString();

        if (amount.isEmpty() | !TextUtils.isDigitsOnly(amount)) {
            mPenalty.setError("enter a valid amount");
            valid = false;
        } else {
            mPenalty.setError(null);
        }
        return valid;
    }

    @Override
    public void onDestroy() {

        if (noInternetDialog != null)
            noInternetDialog.onDestroy();

        super.onDestroy();
    }
}
