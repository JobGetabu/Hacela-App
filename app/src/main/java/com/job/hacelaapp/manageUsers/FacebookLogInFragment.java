package com.job.hacelaapp.manageUsers;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookLogInFragment extends Fragment {

    @BindView(R.id.frgbtn_login_facebook)
    Button mlogiinfbButton;


    private View mRootView;

    public static final String TAG = "FacebookLogInFragment";

    CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private NoInternetDialog noInternetDialog;

    public FacebookLogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //dialogue cant sign up on no network
        noInternetDialog = new NoInternetDialog.Builder(FacebookLogInFragment.this)
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
        mRootView = inflater.inflate(R.layout.fragment_facebook_log_in, container, false);

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

    @OnClick(R.id.frgbtn_login_facebook)
    public void logInWithFbClick(){

        //check connection
        noInternetDialog.showDialog();

        Fragment fragment = FacebookLogInFragment.this;
        mlogiinfbButton.setEnabled(false);
        Log.d(TAG, "mlogiinfbButton disabled");

        LoginManager.getInstance().logInWithReadPermissions(fragment, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

                mlogiinfbButton.setEnabled(true);
                Log.d(TAG, "mloginfbButton enabled");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(getActivity(), "Log in cancelled", Toast.LENGTH_LONG).show();
                mlogiinfbButton.setEnabled(true);
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                mlogiinfbButton.setEnabled(true);
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

        final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Logging in...");
        pDialog.setCancelable(false);
        pDialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            //test with log cat the information passed
                            Log.d("USER INFO","User name: "+ user.getDisplayName());
                            Log.d("USER INFO", "email :"+user.getEmail());
                            Log.d("USER INFO", "phone number:  "+user.getPhoneNumber()); //null
                            Log.d("USER INFO", "ID: "+ user.getUid());
                            Log.d("USER INFO","photo url: "+ user.getPhotoUrl().toString());


                            final String device_token = FirebaseInstanceId.getInstance().getToken();
                            final String mCurrentUserid = mAuth.getCurrentUser().getUid();

                            // refactor this not to write to DB each time...check if account exists

                            DocumentReference docReference = mFirestore.collection("Users").document(mCurrentUserid);
                            docReference.get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                                    //update token only
                                                    updateTokenOnly(mCurrentUserid, device_token, pDialog);

                                                } else {
                                                    Log.d(TAG, "No such document");

                                                    writingToUsersAuth(mCurrentUserid);
                                                    //write to db
                                                    writingToUsers(pDialog, device_token, user, mCurrentUserid);

                                                    //TODO: since is first time send to profile completion screen or phone auth

                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                                //docExists[0] = null;
                                            }
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            pDialog.dismiss();
                            UserAuthToastExceptions(task);
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
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
        errorPrompt("Oops...", error);
    }

    private void writingToUsers(final SweetAlertDialog pDialog, String device_token, FirebaseUser user, String mCurrentUserid){
        Map<String, Object> userMap = new HashMap<>();

        userMap.put("devicetoken",device_token);
        userMap.put("username",user.getDisplayName());
        userMap.put("photourl",user.getPhotoUrl().toString());

        mFirestore.collection("Users").document(mCurrentUserid).set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> dbtask) {
                        if(dbtask.isSuccessful()){
                            pDialog.dismissWithAnimation();
                            sendToMain();
                        }else {
                            pDialog.dismiss();
                            errorPrompt();
                            Log.d(TAG, "onComplete: error "+dbtask.getException());
                        }
                    }
                });
    }

    private void updateTokenOnly(final String mCurrentUserid,
                                 final String device_token,
                                 final SweetAlertDialog pDialog){

        mFirestore.collection("Users").document(mCurrentUserid).update("devicetoken",device_token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> dbtask) {
                        if(dbtask.isSuccessful()){
                            pDialog.dismissWithAnimation();
                            sendToMain();
                        }else {
                            pDialog.dismiss();
                            errorPrompt();
                            Log.d(TAG, "onComplete: error "+dbtask.getException());
                        }
                    }
                });
    }

    //possibly first time log in
    private void writingToUsersAuth(String mCurrentUserid){
        Map<String, Object> userAuthMap = new HashMap<>();
        userAuthMap.put("phonenumber", "");
        userAuthMap.put("fbConnected", true);
        userAuthMap.put("googleConnected", false);

        // Set the value of 'UsersAuth'
        DocumentReference usersAuthRef = mFirestore.collection("UsersAuth").document(mCurrentUserid);

        usersAuthRef.set(userAuthMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: successful");
                        }else {
                            Log.d(TAG, "onComplete: userAuth database error"+task.getException());
                        }
                    }
                });
    }

    //can't be executed sequentially
    @Deprecated
    private Boolean userAlreadyExists(String currentUserid) {
        //hit the db and check if user info exists

        final Boolean[] docExists = new Boolean[1];
        DocumentReference docReference = mFirestore.collection("Users").document(currentUserid);
        docReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                docExists[0] = true;
                            } else {
                                Log.d(TAG, "No such document");
                                docExists[0] = false;
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            //docExists[0] = null;
                        }
                    }
                });
           return docExists[0];
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (noInternetDialog != null)
            noInternetDialog.onDestroy();
    }
}
