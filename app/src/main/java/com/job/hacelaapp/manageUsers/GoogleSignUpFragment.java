package com.job.hacelaapp.manageUsers;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.iid.FirebaseInstanceId;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UsersAccount;

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
public class GoogleSignUpFragment extends Fragment {


    @BindView(R.id.frgbtn_signup_google)
    Button mGoogleBtn;

    public static final int RC_SIGN_IN = 1001;
    public static final String TAG = "GoogleSignUpFragment";

    private View mRootView;

    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private NoInternetDialog noInternetDialog;

    public GoogleSignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //dialogue cant sign up on no network
        noInternetDialog = new NoInternetDialog.Builder(GoogleSignUpFragment.this)
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
        mRootView = inflater.inflate(R.layout.fragment_google_sign_up, container, false);
        ButterKnife.bind(this,mRootView);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(),gso);

        return mRootView;
    }

    @OnClick(R.id.frgbtn_signup_google)
    public void signUpWithGoogleClick(){

        //check connection
        noInternetDialog.showDialog();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mGoogleBtn.setEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Creating Account...");
        pDialog.setCancelable(false);
        pDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            //test with log cat the information passed
                            Log.d("USERINFO","Display name: "+ user.getDisplayName());
                            Log.d("USERINFO", "email :"+user.getEmail());
                            Log.d("USERINFO", "phone number:  "+user.getPhoneNumber()); //null
                            Log.d("USERINFO", "ID: "+ user.getUid());
                            Log.d("USERINFO","photo url: "+ user.getPhotoUrl().toString());


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
                            pDialog.dismissWithAnimation();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(mRootView.findViewById(R.id.frg_framelayout_google), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                            mGoogleBtn.setEnabled(true);
                        }

                        // ...
                    }
                });
    }

    private void sendToMain(){

        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        //since we cnt call finish
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
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

    //possibly first time log in
    private void writingToUsersAuth(String mCurrentUserid){
        Map<String, Object> userAuthMap = new HashMap<>();
        userAuthMap.put("phonenumber", "");
        userAuthMap.put("fbconnected", false);
        userAuthMap.put("googleconnected", true);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        if (noInternetDialog != null)
            noInternetDialog.onDestroy();

    }
}
