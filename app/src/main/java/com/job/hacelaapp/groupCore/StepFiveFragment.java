package com.job.hacelaapp.groupCore;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.GroupAccount;
import com.job.hacelaapp.dataSource.GroupContributionDefault;
import com.job.hacelaapp.dataSource.GroupDescription;
import com.job.hacelaapp.dataSource.GroupMembers;
import com.job.hacelaapp.dataSource.Penalty;
import com.job.hacelaapp.dataSource.Savings;
import com.job.hacelaapp.dataSource.Step4OM;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;

import java.util.HashMap;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;

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
    private static final int REQUEST_INVITE = 34655;

    private CreateGroupViewModel createGroupViewModel;
    private NoInternetDialog noInternetDialog;
    private SweetAlertDialog pDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String currentUserId;
    private String currentUserName;
    private String groupId;

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
        currentUserName = mAuth.getCurrentUser().getDisplayName();
        groupId = mFirestore.collection("Groups").document().getId();

        //build no net dialogue
        setUpNoNetDialogue();
        //avoid leaking progress dialogue
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);

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

        if (step4data == null) {
            return;
        }

        //group object
        /*
        Groups groups = new Groups();
        groups.setDisplayname(groupdisplayname);
        groups.setGroupname(groupfullname);
        groups.setDescription(groupDesp);
        */

        Map<String,Object> groupsConstionMap = new HashMap<>();
        groupsConstionMap.put("constitutionurl","");
        groupsConstionMap.put("constitutiondescr","");



        Map<String,Object> groupDespMap = new HashMap<>();
        groupDespMap.put("typeofgroup",groupDesp.getTypeofgroup());
        groupDespMap.put("description",groupDesp.getDescription());
        groupDespMap.put("createdate", FieldValue.serverTimestamp());
        groupDespMap.put("createdby",groupDesp.getCreatedby());

        Map<String,Object> groupsMap  = new HashMap<>();
        groupsMap.put("groupname",groupdisplayname);
        groupsMap.put("displayname",groupfullname);
        groupsMap.put("photourl","");
        groupsMap.put("description",groupDespMap);
        groupsMap.put("constitution",groupsConstionMap);

        //group contribution object
        GroupContributionDefault groupContributionDefault = new GroupContributionDefault();
        groupContributionDefault.setCycleamount(step4data.getAmount());
        groupContributionDefault.setCycleinterval(step4data.getIntervalPeriod());
        groupContributionDefault.setCycleperiod(step4data.getContPeriod());

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


        GroupAccount groupAccount = new GroupAccount(0);

        Map<String, Object> groupAdminsMap = new HashMap<>();
        groupAdminsMap.put("userid", currentUserId);
        groupAdminsMap.put("position", "Chairperson");
        groupAdminsMap.put("fromdate", FieldValue.serverTimestamp());
        groupAdminsMap.put("status", "Active");

        GroupMembers members = new GroupMembers(currentUserId, "Chairperson",currentUserName);

        //uploading to server

        //init
        DocumentReference GROUPREF = mFirestore.collection("Groups").document(groupId);
        DocumentReference GROUPDEFREF = mFirestore.collection("GroupsContributionDefault").document(groupId);
        DocumentReference GROUPACCREF = mFirestore.collection("GroupsAccount").document(groupId);
        DocumentReference GROUPADMINREF = mFirestore.collection("GroupsAdmin").document(groupId).collection("Admins").document(currentUserId);
        DocumentReference GROUPMEMBERREF = mFirestore.collection("GroupMembers").document(groupId).collection("Members").document(currentUserId);
        DocumentReference USERSPROFILE = mFirestore.collection("UsersProfile").document(currentUserId);
        DocumentReference USERSPROFILEGROUP = mFirestore.collection("UsersProfile").document(currentUserId).collection("Groups").document(groupId);


        //check connection
        noInternetDialog.showDialog();

        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Creating Group...");
        pDialog.setCancelable(false);
        pDialog.show();

        //update the user profile
        Map<String,Object> groupsUsersMap = new HashMap<>();
        groupsUsersMap.put("groupId",groupId);
        groupsUsersMap.put("isMember",true);
        groupsUsersMap.put("startDate",FieldValue.serverTimestamp());
        groupsUsersMap.put("endDate","");

        Map<String,Boolean> groupsidsMap = new HashMap<>();
        groupsidsMap.put(groupId, true);


        //TODO:data integrity check point here


        // Get a new write batch
        WriteBatch batch = mFirestore.batch();
        //upload the group
        batch.set(GROUPREF, groupsMap);
        //upload group default
        batch.set(GROUPDEFREF, groupContributionDefault);
        //upload group admins
        batch.set(GROUPADMINREF, groupAdminsMap);
        //upload group accounts
        batch.set(GROUPACCREF, groupAccount);
        //upload group members
        batch.set(GROUPMEMBERREF, members );
        //upload update userprofile groups
        batch.update(USERSPROFILE,"groups",groupsidsMap);
        //upload update userprofile-subcollection groups
        batch.set(USERSPROFILEGROUP, groupsUsersMap, SetOptions.merge());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            sendToInviteScreen();

                        }
                    });

                } else {
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

    private boolean validate() {
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

    //Todo extra email template setup
    //Manage and control all your chama activities from your phone  groupdisplayname

    private void sendToInviteScreen() {
        pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Just a moment...");
        pDialog.setCancelable(false);
        pDialog.show();

        String link = "https://f8mhr.app.goo.gl/?invitedto=" + groupId;

        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage("Manage and control all your " + groupdisplayname + " activities from your phone")
                .setDeepLink(Uri.parse(link))
                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);

                    //TODO send to group page
                    sendToMain();
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                //close this process to avoid recreation of group.

                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
                pDialog.setTitleText("No members invited");
                pDialog.setContentText("Don't worry You can still invite them in the group");
                pDialog.setCancelable(false);
                pDialog.show();
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        //TODO send to group page
                        sendToMain();
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {

        if (noInternetDialog != null)
            noInternetDialog.onDestroy();

        super.onDestroy();
    }
}
