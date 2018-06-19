package com.job.hacelaapp.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.R;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

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

    public static final String TAG = "WithDrawFrag";

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
    }

    @OnClick(R.id.withdraw_btn)
    public void onWithdrawBtnClicked() {
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
}
