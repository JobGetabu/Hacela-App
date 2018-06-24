package com.job.hacelaapp.ui;


import android.app.Activity;
import android.app.Fragment;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UserAuthInfo;
import com.job.hacelaapp.dataSource.UserBasicInfo;
import com.job.hacelaapp.viewmodel.AccountViewModel;
import com.job.hacelaapp.viewmodel.NavigationViewModel;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.job.hacelaapp.util.Constants.PHONEAUTH_DETAILS;
import static com.job.hacelaapp.util.Constants.USERSACCOUNTCOL;
import static com.job.hacelaapp.util.Constants.USERSAUTHCOL;
import static com.job.hacelaapp.util.Constants.USERSTRANSACTIONCOL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayFragment extends BottomSheetDialogFragment {

    @BindView(R.id.pay_phonenumber)
    TextView payPhonenumber;
    @BindView(R.id.pay_username)
    TextView payUsername;
    @BindView(R.id.pay_textamount)
    TextView payTextamount;
    @BindView(R.id.pay_amountinput)
    TextInputLayout payAmountinput;
    @BindView(R.id.pay_editImg)
    ImageButton editImagbtn;


    private View mRootView;

    private AccountViewModel model;
    private NavigationViewModel navigationViewModel;

    public static final String TAG = "PayFragment";
    private static final int PHONE_NUMBER_REQUEST_CODE = 1114;

    private String userOnlineName = "";
    private String mResultPhoneNumber="";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    //starter progress
    private SweetAlertDialog pDialog;
    private NoInternetDialog noInternetDialog;

    public PayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.addfunds_checkout, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        setUpNoNetDialogue();
        textWatcher();

        navigationViewModel = ViewModelProviders.of(getActivity()).get(NavigationViewModel.class);
        //read db data
        AccountViewModel.Factory factory = new AccountViewModel.Factory(
                this.getActivity().getApplication(), mAuth, mFirestore);

        model = ViewModelProviders.of(this, factory)
                .get(AccountViewModel.class);

        //setup ui observers
        setUpUi();
    }

    private void setUpUi() {
        MediatorLiveData<UserAuthInfo> data = model.getUsersAuthMediatorLiveData();

        payUsername.setText(mCurrentUser.getDisplayName());

        data.observe(this, new Observer<UserAuthInfo>() {
            @Override
            public void onChanged(@Nullable UserAuthInfo userAuthInfo) {

                if (userAuthInfo != null) {
                    if (!userAuthInfo.getPhonenumber().isEmpty() || userAuthInfo.getPhonenumber() != null) {
                        payPhonenumber.setText(userAuthInfo.getPhonenumber());
                    } else {
                        //set up phone number in profile
                        payPhonenumber.setText("");
                    }
                }
            }
        });
    }

    private void setUpNoNetDialogue() {
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setBgGradientOrientation(45)
                .setCancelable(true)
                .setBgGradientStart(getResources().getColor(R.color.app_gradient_start))
                .setBgGradientEnd(getResources().getColor(R.color.app_gradient_end))
                .build();
    }

    public void navHome(int where) {
        //intent back home and clear backstack
        Intent mainIntent = new Intent(getContext(), MainActivity.class);
        //since we cnt call finish
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

        navigationViewModel.setHomeDestination(where);
    }

    @OnClick(R.id.pay_amountinput)
    public void onPayAmountinputClicked() {
    }

    @OnClick(R.id.pay_paybtn)
    public void onPayPaybtnClicked() {
        noInternetDialog.showDialog();

        if (!noInternetDialog.isShowing()) {
            if (validateOnPay()) {
                if (payPhonenumber.getText().toString().isEmpty()) {
                    sendToPhoneActivity();
                } else {

                    showWaitDialogue();
                    simulatingMpesaTransaction();


                    if (pDialog == null) {
                        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                        pDialog.setCancelable(false);
                        pDialog.setContentText("Oops Something went wrong");
                        pDialog.show();
                        dismiss();
                    }


                    final String amount = payAmountinput.getEditText().getText().toString();
                    final double am = Double.parseDouble(amount);
                    final DocumentReference userAccountRef = mFirestore.collection(USERSACCOUNTCOL).document(mCurrentUser.getUid());

                    updatePhonenumber();

                    payTransaction(amount, am, userAccountRef);


                }
            }
        }
    }

    private void updatePhonenumber() {
        if (!mResultPhoneNumber.isEmpty()){
            //push the number update
            DocumentReference userAuthRef = mFirestore.collection(USERSAUTHCOL).document(mCurrentUser.getUid());
            userAuthRef.update("phonenumber",mResultPhoneNumber)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "phone number updated success!");
                            }else {
                                Log.w(TAG, "phone number updated failure.", task.getException());
                            }
                        }
                    });

        }
    }

    private void payTransaction(final String amountText, final double am,
                                final DocumentReference userAccountRef) {
        mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userAccountRef);
                double newBalance = snapshot.getDouble("balance") + am;
                transaction.update(userAccountRef, "balance", newBalance);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");


                String userTransKey =mFirestore.collection(USERSTRANSACTIONCOL).document().getId();
                DocumentReference userTransRef  =mFirestore.collection(USERSTRANSACTIONCOL).document(userTransKey);

                //we add a transaction
                Map<String,Object> userTransMap = new HashMap<>();
                userTransMap.put("userid",mCurrentUser.getUid());
                userTransMap.put("transactionid",userTransKey);
                userTransMap.put("type","Deposit");
                userTransMap.put("status","Pending");
                userTransMap.put("timestamp", FieldValue.serverTimestamp());
                userTransMap.put("amount",am);

                userTransRef.set(userTransMap)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setCancelable(false);
                                    pDialog.setContentText("Succefully added " + amountText + " to your account");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            dismiss();
                                            //navHome(1);
                                        }
                                    });

                                }else {

                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setContentText("Oops Something went wrong");
                                    dismiss();

                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);

                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                pDialog.setContentText("Oops Something went wrong");
                dismiss();

            }
        });
    }

    @Override
    public void onDestroy() {

        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

        if (noInternetDialog != null) {
            noInternetDialog.onDestroy();
        }

        super.onDestroy();
    }

    @OnClick(R.id.pay_editImg)
    public void onPayEditIcon() {
        payAmountinput.setVisibility(View.VISIBLE);
        String am = payTextamount.getText().toString();
        String newstr = am.replaceAll("KES ", "")
                .replaceAll("/-", "")
                .replaceAll(",", "");
        payAmountinput.getEditText().setText(newstr);
        payTextamount.setVisibility(View.GONE);
        editImagbtn.setVisibility(View.GONE);
    }

    @OnClick({R.id.pay_phonenumber, R.id.pay_username, R.id.kngdpay,
            R.id.kjbsavk6pay, R.id.klsdnldvkj, R.id.main_sdbjldv,
            R.id.textView6pay})
    public void onHidePayInputField() {
        payAmountinput.setVisibility(View.GONE);
        payTextamount.setVisibility(View.VISIBLE);
        editImagbtn.setVisibility(View.VISIBLE);
        String am = payAmountinput.getEditText().getText().toString();
        //payTextamount.setText("KES " + am + "/-");
        double temp = 0;
        try {
            temp = Double.parseDouble(am);
        } catch (Exception e) {
            Log.e(TAG, "onHideInputField: ", e);
        }
        payTextamount.setText(formatMyMoney(temp) + "/-");
    }

    private void textWatcher() {

        payAmountinput.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (-1 != payAmountinput.getEditText().getText().toString().indexOf("\n")) {

                    payAmountinput.setVisibility(View.GONE);
                    payTextamount.setVisibility(View.VISIBLE);
                    editImagbtn.setVisibility(View.VISIBLE);
                    String am = payAmountinput.getEditText().getText().toString();

                    //payTextamount.setText("KES " + am + "/-");
                    double temp = 0;
                    try {
                        temp = Double.parseDouble(am);
                    } catch (Exception e) {
                        Log.e(TAG, "onHideInputField: ", e);
                    }
                    payTextamount.setText(formatMyMoney(temp) + "/-");
                }
            }
        });
    }

    private void showWaitDialogue() {

        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Simulating M-pesa Transaction ...");
        pDialog.setCancelable(true);
        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (userOnlineName.isEmpty()) {
                    dismiss();
                }
            }
        });
        pDialog.show();
    }

    private void simulatingMpesaTransaction() {
        //init view-model
        DocumentReference UserRef = mFirestore.collection("Users")
                .document(mCurrentUser.getUid());

        UserRef
                .get()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            UserBasicInfo userBasicInfo = task.getResult().toObject(UserBasicInfo.class);
                            //we need the user name
                            if (userBasicInfo != null) {
                                userOnlineName = userBasicInfo.getUsername();

                            }

                            if (pDialog != null) {

                                pDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.setCancelable(false);
                                pDialog.setTitleText("Updating account ...");
                            }
                        } else {

                            Log.e(TAG, "Error getting documents: ", task.getException());
                            if (pDialog != null) {
                                pDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private boolean validateOnPay() {

        boolean valid = true;

        String am = payAmountinput.getEditText().getText().toString();

        if (am.isEmpty() || am.equals("0")) {
            payAmountinput.setError("Amount is not valid");

            payTextamount.setVisibility(View.GONE);
            editImagbtn.setVisibility(View.GONE);
            payAmountinput.setVisibility(View.VISIBLE);

            valid = false;
        } else {
            payTextamount.setVisibility(View.VISIBLE);
            editImagbtn.setVisibility(View.VISIBLE);
            payAmountinput.setVisibility(View.GONE);
            payAmountinput.setError(null);
        }

        if (Double.parseDouble(am) < 10) {
            payAmountinput.setError("Amount must be greater than 10");

            payTextamount.setVisibility(View.GONE);
            editImagbtn.setVisibility(View.GONE);
            payAmountinput.setVisibility(View.VISIBLE);

            valid = false;
        } else {
            payTextamount.setVisibility(View.VISIBLE);
            editImagbtn.setVisibility(View.VISIBLE);
            payAmountinput.setVisibility(View.GONE);
            payAmountinput.setError(null);
        }

        return valid;
    }

    public String formatMyMoney(Double money) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        Log.d(TAG, "formatMyMoney: " + formatter.format(money));
        return String.format("KES %,.0f", money);
    }

    private void sendToPhoneActivity() {
        final Intent i = new Intent(getContext(), PhoneAuthActivity.class);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.set_Phone_Number)
                .setMessage(R.string.this_will_be_the_number_you)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startActivityForResult(i, PHONE_NUMBER_REQUEST_CODE);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PHONE_NUMBER_REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {

                    mResultPhoneNumber = data.getStringExtra(PHONEAUTH_DETAILS);
                    // TODO Update your TextView
                    payPhonenumber.setText(data.getStringExtra(PHONEAUTH_DETAILS));
                }
                break;
        }
    }
}
