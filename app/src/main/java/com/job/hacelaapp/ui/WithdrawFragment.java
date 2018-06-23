package com.job.hacelaapp.ui;

import android.app.Activity;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UserAuthInfo;
import com.job.hacelaapp.viewmodel.AccountViewModel;

import java.text.DecimalFormat;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.job.hacelaapp.util.Constants.PHONEAUTH_DETAILS;

/**
 * Created by Job on Tuesday : 6/19/2018.
 */
public class WithdrawFragment extends BottomSheetDialogFragment {

    @BindView(R.id.withdraw_phonenumber)
    TextView withdrawPhonenumber;
    @BindView(R.id.withdraw_username)
    TextView withdrawUsername;
    @BindView(R.id.withdraw_textamount)
    TextView withdrawTextamount;
    @BindView(R.id.withdraw_editImg)
    ImageButton withdrawEditImg;
    @BindView(R.id.withdraw_amountinput)
    TextInputLayout withdrawAmountinput;
    @BindView(R.id.withdraw_btn)
    MaterialButton withdrawBtn;


    private View mRootView;
    private AccountViewModel model;

    public static final String TAG = "WithDrawFrag";
    private static final int PHONE_NUMBER_REQUEST_CODE = 1544;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    //starter progress
    private SweetAlertDialog pDialog;
    private NoInternetDialog noInternetDialog;


    public WithdrawFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.withdraw_checkout, container, false);
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
        withdrawBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        //read db data
        AccountViewModel.Factory factory = new AccountViewModel.Factory(
                getActivity().getApplication(), mAuth, mFirestore);

        model = ViewModelProviders.of(this, factory)
                .get(AccountViewModel.class);

        //setup ui observers
        setUpUi();

    }

    private void setUpUi(){
        MediatorLiveData<UserAuthInfo> data = model.getUsersAuthMediatorLiveData();

        withdrawUsername.setText(mCurrentUser.getDisplayName());

        data.observe(this, new Observer<UserAuthInfo>() {
            @Override
            public void onChanged(@Nullable UserAuthInfo userAuthInfo) {

                if(userAuthInfo != null){
                    if (!userAuthInfo.getPhonenumber().isEmpty() || userAuthInfo.getPhonenumber() != null){
                        withdrawPhonenumber.setText(userAuthInfo.getPhonenumber());
                    }else {
                        //set up phone number in profile
                        withdrawPhonenumber.setText("");
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

    @OnClick(R.id.withdraw_editImg)
    public void onWithdrawEditImgClicked() {

        withdrawAmountinput.setVisibility(View.VISIBLE);
        String am = withdrawTextamount.getText().toString();
        String newstr = am.replaceAll("KES ", "")
                .replaceAll("/-", "")
                .replaceAll(",", "");
        withdrawAmountinput.getEditText().setText(newstr);
        withdrawTextamount.setVisibility(View.GONE);
        withdrawEditImg.setVisibility(View.GONE);
    }

    @OnClick(R.id.withdraw_btn)
    public void onWithdrawBtnClicked() {

        noInternetDialog.showDialog();

    }

    @OnClick({R.id.withdraw_phonenumber, R.id.withdraw_username,
            R.id.withdrawal_djbkbvkj, R.id.kjbsavk6paywithdrawal_, R.id.gsdgsdgs,
            R.id.w_textView6pay})
    public void onHideWithdrawInputField() {
        withdrawAmountinput.setVisibility(View.GONE);
        withdrawTextamount.setVisibility(View.VISIBLE);
        withdrawEditImg.setVisibility(View.VISIBLE);
        String am = withdrawAmountinput.getEditText().getText().toString();
        //payTextamount.setText("KES " + am + "/-");
        double temp = 0;
        try{
            temp = Double.parseDouble(am);
        }catch (Exception e){
            Log.e(TAG, "onHideInputField: ",e);
        }
        withdrawTextamount.setText(formatMyMoney(temp) + "/-");
    }

    private void textWatcher() {

        withdrawAmountinput.getEditText().addTextChangedListener(new TextWatcher() {

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
                if (-1 != withdrawAmountinput.getEditText().getText().toString().indexOf("\n")) {

                    withdrawAmountinput.setVisibility(View.GONE);
                    withdrawTextamount.setVisibility(View.VISIBLE);
                    withdrawEditImg.setVisibility(View.VISIBLE);
                    String am = withdrawAmountinput.getEditText().getText().toString();
                    withdrawTextamount.setText("KES " + am + "/-");
                }
            }
        });
    }

    private boolean validateOnPay() {

        boolean valid = true;

        String am = withdrawAmountinput.getEditText().getText().toString();

        if (am.isEmpty() || am.equals("0")) {
            withdrawAmountinput.setError("Amount is not valid");

            withdrawTextamount.setVisibility(View.GONE);
            withdrawEditImg.setVisibility(View.GONE);
            withdrawAmountinput.setVisibility(View.VISIBLE);

            valid = false;
        } else {
            withdrawTextamount.setVisibility(View.VISIBLE);
            withdrawEditImg.setVisibility(View.VISIBLE);
            withdrawAmountinput.setVisibility(View.GONE);
            withdrawAmountinput.setError(null);
        }

        if (Double.parseDouble(am) < 50) {
            withdrawAmountinput.setError("Amount must be greater than 50");

            withdrawTextamount.setVisibility(View.GONE);
            withdrawEditImg.setVisibility(View.GONE);
            withdrawAmountinput.setVisibility(View.VISIBLE);

            valid = false;
        } else {
            withdrawTextamount.setVisibility(View.VISIBLE);
            withdrawEditImg.setVisibility(View.VISIBLE);
            withdrawAmountinput.setVisibility(View.GONE);
            withdrawAmountinput.setError(null);
        }

        return valid;
    }

    public String formatMyMoney(Double money){
        DecimalFormat formatter = new DecimalFormat("#,###");
        Log.d(TAG, "formatMyMoney: "+formatter.format(money));
        return String.format("KES %,.0f", money);
    }

    private void sendToPhoneActivity(){
        final Intent i = new Intent(getContext(),PhoneAuthActivity.class);

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

        switch (requestCode){
            case (PHONE_NUMBER_REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Update your TextView
                    withdrawPhonenumber.setText(data.getStringExtra(PHONEAUTH_DETAILS));
                }
                break;
        }
    }
}
