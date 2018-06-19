package com.job.hacelaapp.ui;


import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UserBasicInfo;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.job.hacelaapp.util.Constants.USERSACCOUNTCOL;

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
    @BindView(R.id.pay_cancel)
    MaterialButton payCancel;
    @BindView(R.id.pay_paybtn)
    MaterialButton payPaybtn;
    @BindView(R.id.pay_editImg)
    ImageButton editImagbtn;
    private View mRootView;

    public static final String TAG = "PayFragment";

    private String userOnlineName = "";

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

    }

    private void setUpNoNetDialogue() {
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setBgGradientOrientation(45)
                .setCancelable(true)
                .setBgGradientStart(getResources().getColor(R.color.app_gradient_start))
                .setBgGradientEnd(getResources().getColor(R.color.app_gradient_end))
                .build();
    }


    @OnClick(R.id.pay_amountinput)
    public void onPayAmountinputClicked() {
    }

    @OnClick(R.id.pay_cancel)
    public void onPayCancelClicked() {
        dismiss();
    }

    @OnClick(R.id.pay_paybtn)
    public void onPayPaybtnClicked() {
        noInternetDialog.showDialog();

        if (!noInternetDialog.isShowing()) {
            if (validateOnPay()) {
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
                double am = Double.parseDouble(amount);
                DocumentReference userAccountRef = mFirestore.collection(USERSACCOUNTCOL).document(mCurrentUser.getUid());
                userAccountRef.update("balance", am)

                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    pDialog.setCancelable(false);
                                    pDialog.setContentText("Succefully added " + amount + " to your account");
                                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            dismiss();
                                        }
                                    });

                                } else {
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setContentText("Oops Something went wrong");
                                    dismiss();
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onDestroy() {

        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
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
    public void onHideInputField() {
        payAmountinput.setVisibility(View.GONE);
        payTextamount.setVisibility(View.VISIBLE);
        editImagbtn.setVisibility(View.VISIBLE);
        String am = payAmountinput.getEditText().getText().toString();
        payTextamount.setText("KES " + am + "/-");
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
                    payTextamount.setText("KES " + am + "/-");
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
                        if (task.isSuccessful()){

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
                        }else {

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

        if (Integer.parseInt(am) < 10) {
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

}
