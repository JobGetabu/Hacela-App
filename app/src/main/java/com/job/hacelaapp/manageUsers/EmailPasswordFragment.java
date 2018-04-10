package com.job.hacelaapp.manageUsers;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ybs.passwordstrengthmeter.PasswordStrength;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    ProgressDialog  mdialog;

    public EmailPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_email_password, container, false);
        ButterKnife.bind(this,mRootView);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();


        inEditPassword.addTextChangedListener(this);


        return mRootView;
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
    public void regBtnEmailPasswordClick(){

        final String displayname = inDisplayName.getEditText().getText().toString();
        final String phonenumber = inPhoneNumber.getEditText().getText().toString();
        String email = inEmail.getEditText().getText().toString();
        String password = inPassword.getEditText().getText().toString();

        if(!TextUtils.isEmpty(displayname) && !TextUtils.isEmpty(phonenumber) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)){

            mdialog = new ProgressDialog(getActivity());
            mdialog.setTitle("Registration");
            mdialog.setMessage("Please wait while we create your account...");
            mdialog.setCanceledOnTouchOutside(false);
            mdialog.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> authtask) {
                            if (authtask.isSuccessful()){

                                String device_token = FirebaseInstanceId.getInstance().getToken();
                                String mCurrentUserid = mAuth.getCurrentUser().getUid();

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("device_token",device_token);
                                userMap.put("displayname",displayname);
                                userMap.put("photourl","");

                                Map<String, Object> userAuthMap = new HashMap<>();
                                userAuthMap.put("phonenumber",phonenumber);
                                userAuthMap.put("fbConnected",false);
                                userAuthMap.put("GoogleConnected",false);


                                // Get a new write batch
                                WriteBatch batch = mFirestore.batch();

                                // Set the value of 'Users'
                                DocumentReference UsersRef = mFirestore.collection("Users").document(mCurrentUserid);
                                batch.set(UsersRef, userMap);

                                // Set the value of 'UsersAuth'
                                DocumentReference UsersAuthRef = mFirestore.collection("UsersAuth").document(mCurrentUserid);
                                batch.set(UsersAuthRef, userAuthMap);

                                // Commit the batch
                                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> dbtask) {
                                        if(dbtask.isSuccessful()){
                                            mdialog.dismiss();
                                            sendToLogin();
                                        }else {
                                            mdialog.dismiss();
                                            Log.d(TAG, "onComplete: error"+dbtask.getException().toString());
                                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else {
                                UserAuthToastExceptions(authtask);
                                mdialog.dismiss();
                            }
                        }
                    });


        }else {
            //TODO: Error prompting logic
        }

    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
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
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }
}
