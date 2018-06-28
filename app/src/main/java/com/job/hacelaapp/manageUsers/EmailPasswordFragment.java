package com.job.hacelaapp.manageUsers;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.iid.FirebaseInstanceId;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UsersAccount;
import com.ybs.passwordstrengthmeter.PasswordStrength;

import java.util.HashMap;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static com.job.hacelaapp.util.Constants.USERSACCOUNTCOL;
import static com.job.hacelaapp.util.Constants.USERSAUTHCOL;
import static com.job.hacelaapp.util.Constants.USERSCOL;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordFragment extends Fragment implements TextWatcher {


    @BindView(R.id.reg_input_displayname)
    TextInputLayout inDisplayName;
    @BindView(R.id.reg_input_phonenumber)
    TextInputLayout inPhoneNumber;
    @BindView(R.id.reg_input_email)
    TextInputLayout inEmail;
    @BindView(R.id.reg_input_password)
    TextInputLayout inPassword;
    @BindView(R.id.reg_editinput_password)
    TextInputEditText inEditPassword;

    @BindView(R.id.reg_progressBar)
    ProgressBar inProgressBar;
    @BindView(R.id.reg_password_strength)
    TextView inPasswordStrength;

    public static final String TAG = "EmailPasswordFragment";
    private View mRootView;


    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private PhoneNumberUtil mPhoneNumberUtil;
    private NoInternetDialog noInternetDialog;


    public EmailPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //dialogue cant sign up on no network
        noInternetDialog = new NoInternetDialog.Builder(EmailPasswordFragment.this)
                .setBgGradientOrientation(45)
                .setCancelable(true)
                .setBgGradientStart(getResources().getColor(R.color.app_gradient_start))
                .setBgGradientEnd(getResources().getColor(R.color.app_gradient_end))
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_email_password, container, false);
        ButterKnife.bind(this, mRootView);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mPhoneNumberUtil = PhoneNumberUtil.createInstance(getActivity());

        preparePhoneNumber();

        inEditPassword.addTextChangedListener(this);


        return mRootView;
    }

    private void preparePhoneNumber() {
        inPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                String number = inPhoneNumber.getEditText().toString().trim();
                //loses focus
                if (!hasFocus) {
                    try {
                        Phonenumber.PhoneNumber kenyaNumberProto = mPhoneNumberUtil.parse(number, "KE");

                        String ss = mPhoneNumberUtil.format(kenyaNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                        inPhoneNumber.getEditText().setText(ss.trim().replaceAll("\\s", ""));

                    } catch (NumberParseException e) {
                        System.err.println("NumberParseException was thrown: " + e.toString());
                    }
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        updatePasswordStrengthView(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void updatePasswordStrengthView(String password) {

        // = (ProgressBar) mRootView.findViewById(R.id.reg_progressBar);
        //= (TextView) mRootView.findViewById(R.id.reg_password_strength);

        if (TextView.VISIBLE != inPasswordStrength.getVisibility())
            return;

        if (password.isEmpty()) {
            inPasswordStrength.setText("");
            inProgressBar.setProgress(0);
            return;
        }

        PasswordStrength str = PasswordStrength.calculateStrength(password);
        inPasswordStrength.setText(str.getText(getActivity()));
        inPasswordStrength.setTextColor(str.getColor());

        inProgressBar.getProgressDrawable().setColorFilter(str.getColor(), PorterDuff.Mode.SRC_IN);
        if (str.getText(getActivity()).equals("Weak")) {
            inProgressBar.setProgress(25);
        } else if (str.getText(getActivity()).equals("Medium")) {
            inProgressBar.setProgress(50);
        } else if (str.getText(getActivity()).equals("Strong")) {
            inProgressBar.setProgress(75);
        } else {
            inProgressBar.setProgress(100);
        }
    }

    @OnClick({R.id.reg_btn_signup})
    public void regBtnEmailPasswordClick() {

        final String username = inDisplayName.getEditText().getText().toString().trim();
        String phonenumber = inPhoneNumber.getEditText().getText().toString().trim();
        String email = inEmail.getEditText().getText().toString().trim();
        String password = inPassword.getEditText().getText().toString().trim();

        if (validate()) {

            //check connection
            noInternetDialog.showDialog();

            Phonenumber.PhoneNumber kenyaNumberProto = null;
            try {
                kenyaNumberProto = mPhoneNumberUtil.parse(phonenumber, "KE");
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
            }
            if (kenyaNumberProto != null) {
                String ss = mPhoneNumberUtil.format(kenyaNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

                phonenumber = ss.trim().replaceAll("\\s", "");

                Log.d(TAG, "regBtnEmailPasswordClick: PhoneNumber" + phonenumber);
            }

            final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
            pDialog.setTitleText("Creating Account...");
            pDialog.setCancelable(false);
            pDialog.show();

            final String finalPhonenumber = phonenumber;
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> authtask) {
                            if (authtask.isSuccessful()) {

                                String device_token = FirebaseInstanceId.getInstance().getToken();
                                String mCurrentUserid = mAuth.getCurrentUser().getUid();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("devicetoken", device_token);
                                userMap.put("username", username);
                                userMap.put("photourl", "");

                                Map<String, Object> userAuthMap = new HashMap<>();
                                userAuthMap.put("phonenumber", finalPhonenumber);
                                userAuthMap.put("fbConnected", false);
                                userAuthMap.put("googleConnected", false);

                                //create user account
                                UsersAccount usersAccount = new UsersAccount(0D,"Active");

                                // Get a new write batch
                                WriteBatch batch = mFirestore.batch();

                                // Set the value of 'Users'
                                DocumentReference usersRef = mFirestore.collection(USERSCOL).document(mCurrentUserid);
                                batch.set(usersRef, userMap);

                                // Set the value of 'UsersAuth'
                                DocumentReference usersAuthRef = mFirestore.collection(USERSAUTHCOL).document(mCurrentUserid);
                                batch.set(usersAuthRef, userAuthMap);

                                //set the value of 'UsersAccount'
                                DocumentReference usersAccountRef = mFirestore.collection(USERSACCOUNTCOL).document(mCurrentUserid);
                                batch.set(usersAccountRef, usersAccount);

                                // Commit the batch
                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> dbtask) {
                                        if (dbtask.isSuccessful()) {
                                            pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            pDialog.dismissWithAnimation();
                                            sendToLogin();
                                        } else {
                                            pDialog.dismiss();
                                            Log.d(TAG, "onComplete: error" + dbtask.getException().toString());
                                            errorPrompt();
                                        }
                                    }
                                });
                            } else {
                                //mdialog.dismiss();
                                //pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                pDialog.dismiss();
                                UserAuthToastExceptions(authtask);
                            }
                        }
                    });


        }
    }

    private void errorPrompt() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    private void errorPrompt(String title, String message) {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void UserAuthToastExceptions(@NonNull Task<AuthResult> authtask) {
        String error = "";
        try {
            throw authtask.getException();
        } catch (FirebaseAuthWeakPasswordException e) {
            error = "Weak Password!";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            error = "Invalid email";
        } catch (FirebaseAuthUserCollisionException e) {
            error = "Existing Account";
        } catch (Exception e) {
            error = "Unknown Error Occured";
            e.printStackTrace();
        }
        errorPrompt("Oops...", error);
    }

    public boolean validate() {
        boolean valid = true;

        String displayname = inDisplayName.getEditText().getText().toString();
        String phonenum = inPhoneNumber.getEditText().getText().toString();
        String email = inEmail.getEditText().getText().toString();
        String password = inPassword.getEditText().getText().toString();


        Phonenumber.PhoneNumber kenyaNumberProto = null;
        try {
            kenyaNumberProto = mPhoneNumberUtil.parse(phonenum, "KE");
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        if (displayname.isEmpty()) {
            inDisplayName.setError("enter a valid username");
            valid = false;
        } else {
            inDisplayName.setError(null);
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inEmail.setError(null);
        }

        if (phonenum.isEmpty() || !mPhoneNumberUtil.isValidNumber(kenyaNumberProto)) {
            inPhoneNumber.setError("enter a valid phone number");
            valid = false;
        } else {
            inPhoneNumber.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            inPassword.setError("at least 6 characters");
            valid = false;
        } else {
            inPassword.setError(null);
        }

        Log.d(TAG, "validate: " + valid + "phone -> " + kenyaNumberProto);
        return valid;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        if (noInternetDialog != null)
            noInternetDialog.onDestroy();

    }

}
