package com.job.hacelaapp.manageUsers;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordLogInFragment extends Fragment {

    @BindView(R.id.login_input_email)
    TextInputLayout logEmail;
    @BindView(R.id.login_input_password)
    TextInputLayout mPassword;


    public static final String TAG = "EmailPassLogInFragment";

    private View mRootView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    ProgressDialog mdialog;

    public EmailPasswordLogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_email_password_log_in, container, false);
        ButterKnife.bind(this,mRootView);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        
        return  mRootView;
    }

    @OnClick({R.id.login_tv_signup,R.id.side_tv_to_sign_up})
    public void tvLoginSignUp(){
        Intent registerIntent = new Intent(getActivity(),RegisterActivity.class);
        startActivity(registerIntent);
    }
    
    @OnClick(R.id.login_tv_forgotpassword)
    public void tvLoginForgotPassword(){
        Toast.makeText(getActivity(), "TODO: Implement forgot password", Toast.LENGTH_SHORT).show();
    }

    private void sendToMain(){

        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        //since we cnt call finish
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    @OnClick(R.id.login_btn_signup)
    public void loginWithEmailPasswordClick(){

        String email = logEmail.getEditText().getText().toString();
        String password = mPassword.getEditText().getText().toString();

        Log.d(TAG, "loginWithEmailPasswordClick: Email & password:"+email+"   "+password);

        if (validate()){

            /*
            mdialog = new ProgressDialog(getActivity());
            mdialog.setTitle("Log in");
            mdialog.setMessage("Please wait logging in...");
            mdialog.setCanceledOnTouchOutside(false);
            mdialog.show();
            */

            final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Logging in...");
            pDialog.setCancelable(false);
            pDialog.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> authtask) {

                            if (authtask.isSuccessful()){

                                //mdialog.dismiss();
                                pDialog.dismissWithAnimation();
                                sendToMain();
                            }else {
                                //mdialog.dismiss();
                                pDialog.dismiss();
                                UserAuthToastExceptions(authtask);
                            }

                        }
                    });
        }else {
            // Error prompting logic ... Done!
        }
    }

    private void errorPrompt(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    private void errorPrompt(String title,String message){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(message)
                .show();
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
        //Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        errorPrompt("Oops...",error);
    }

    public boolean validate() {
        boolean valid = true;

        String email = logEmail.getEditText().toString();
        String password = mPassword.getEditText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            logEmail.setError("enter a valid email address");
            valid = false;
        } else {
            logEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 ) {
            mPassword.setError("between at least 6 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }
}
