package com.job.hacelaapp.manageUsers;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookSignUpFragment extends Fragment {

    @BindView(R.id.frgbtn_signup_facebook)
    Button mfbButton;

    private View mRootView;

    public static final String TAG = "FacebookSignUpFragment";

    CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private ProgressDialog mdialog;

    public FacebookSignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_facebook_sign_up, container, false);
        ButterKnife.bind(this,mRootView);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        //init fb login callback
        mCallbackManager = CallbackManager.Factory.create();

        return mRootView;
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
        } catch (FirebaseAuthUserCollisionException e) {
            error = "Existing Account";
        } catch (Exception e) {
            error = "Unknown Error Occured";
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.frgbtn_signup_facebook)
    public void signupWithfbClick(){

        Fragment fragment = FacebookSignUpFragment.this;
        mfbButton.setEnabled(false);
        Log.d(TAG, "mfbButton disabled");
        LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

                mfbButton.setEnabled(true);
                Log.d(TAG, "mfbButton disabled");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getActivity(), "Log in cancelled", Toast.LENGTH_LONG).show();
                mfbButton.setEnabled(true);
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                mfbButton.setEnabled(true);
                // ...
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        mdialog = new ProgressDialog(getActivity());
        mdialog.setTitle("Logging in");
        mdialog.setMessage("Please wait logging in...");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //test with log cat the information passed
                            Log.d("USER INFO","Display name: "+ user.getDisplayName());
                            Log.d("USER INFO", "email :"+user.getEmail());
                            Log.d("USER INFO", "phone number:  "+user.getPhoneNumber()); //null
                            Log.d("USER INFO", "ID: "+ user.getUid());
                            Log.d("USER INFO","photo url: "+ user.getPhotoUrl().toString());


                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            String mCurrentUserid = mAuth.getCurrentUser().getUid();

                            Map<String, Object> userMap = new HashMap<>();

                            userMap.put("device_token",device_token);
                            userMap.put("displayname",user.getDisplayName());
                            userMap.put("photourl",user.getPhotoUrl().toString());

                            mFirestore.collection("Users").document(mCurrentUserid).set(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> dbtask) {
                                            if(dbtask.isSuccessful()){
                                                mdialog.dismiss();
                                                sendToMain();
                                            }else {
                                                mdialog.dismiss();
                                                Log.d(TAG, "onComplete: error"+dbtask.getException().toString());
                                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            mdialog.dismiss();
                            UserAuthToastExceptions(task);
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
}
