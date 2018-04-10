package com.job.hacelaapp.manageUsers;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
    private void UserAuthToastExceptions(@NonNull Task<AuthResult> authtask) {
        String error = "";
        try {
            throw authtask.getException();
        } catch (FirebaseAuthWeakPasswordException e) {
            error = "Weak Password!";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            error = "Invalid email";
            Log.d(TAG, "UserAuthToastExceptions: ", e);
        } catch (FirebaseAuthUserCollisionException e) {
            error = "Existing Account";
        } catch (Exception e) {
            error = "Unknown Error Occured";
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.login_btn_signup)
    public void loginWithEmailPasswordClick(){

        String email = logEmail.getEditText().getText().toString();
        String password = mPassword.getEditText().getText().toString();

        Log.d(TAG, "loginWithEmailPasswordClick: Email & password:"+email+"   "+password);

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mdialog = new ProgressDialog(getActivity());
            mdialog.setTitle("Log in");
            mdialog.setMessage("Please wait logging in...");
            mdialog.setCanceledOnTouchOutside(false);
            mdialog.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> authtask) {

                            if (authtask.isSuccessful()){

                                mdialog.dismiss();
                                sendToMain();
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
}
