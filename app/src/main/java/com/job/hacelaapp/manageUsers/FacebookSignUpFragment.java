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
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.iid.FirebaseInstanceId;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UsersAccount;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.job.hacelaapp.util.Constants.USERSACCOUNTCOL;
import static com.job.hacelaapp.util.Constants.USERSAUTHCOL;
import static com.job.hacelaapp.util.Constants.USERSCOL;

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

    private NoInternetDialog noInternetDialog;

    public FacebookSignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //dialogue cant sign up on no network
        noInternetDialog = new NoInternetDialog.Builder(FacebookSignUpFragment.this)
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

    @OnClick(R.id.frgbtn_signup_facebook)
    public void signupWithfbClick(){

        Fragment fragment = FacebookSignUpFragment.this;
        mfbButton.setEnabled(false);
        Log.d(TAG, "mfbButton disabled");

        //check connection
        noInternetDialog.showDialog();

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

        final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Creating Account...");
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
                            Log.d("USER INFO","Display name: "+ user.getDisplayName());
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

                                                    //logging in with no pre account
                                                    //region create fresh account

                                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                    pDialog.setTitleText("Account doesn't exists! \n Creating one...");

                                                    //write to db
                                                    writingToUsers(pDialog, device_token, user, mCurrentUserid);

                                                    //endregion

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

    //possibly first time log in
    private void writingToUsers(final SweetAlertDialog pDialog, String device_token, FirebaseUser user, String mCurrentUserid){

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("devicetoken",device_token);
        userMap.put("username",user.getDisplayName());
        userMap.put("photourl",user.getPhotoUrl().toString());

        Map<String, Object> userAuthMap = new HashMap<>();
        userAuthMap.put("phonenumber", "");
        userAuthMap.put("fbconnected", false);
        userAuthMap.put("googleconnected", true);

        //create user account
        UsersAccount usersAccount = new UsersAccount(0D,"Active");

        DocumentReference usersAuthRef = mFirestore.collection(USERSAUTHCOL).document(mCurrentUserid);
        DocumentReference usersRef = mFirestore.collection(USERSCOL).document(mCurrentUserid);
        DocumentReference usersAccountRef = mFirestore.collection(USERSACCOUNTCOL).document(mCurrentUserid);

        // Get a new write batch
        WriteBatch batch = mFirestore.batch();

        batch.set(usersRef,userMap);
        batch.set(usersAuthRef,userAuthMap);
        batch.set(usersAccountRef, usersAccount);

        batch.commit()
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

        pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

        pDialog.setTitleText("Account already exists \n Logging you in...");

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        if (noInternetDialog != null)
            noInternetDialog.onDestroy();

    }
}
