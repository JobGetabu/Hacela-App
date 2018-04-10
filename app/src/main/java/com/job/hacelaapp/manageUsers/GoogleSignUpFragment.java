package com.job.hacelaapp.manageUsers;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private ProgressDialog  mdialog;

    public GoogleSignUpFragment() {
        // Required empty public constructor
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

        mdialog = new ProgressDialog(getActivity());
        mdialog.setTitle("Registration");
        mdialog.setMessage("Please wait while we create your account...");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

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
                                                mGoogleBtn.setEnabled(true);
                                                Toast.makeText(getActivity(), "Welcome signed in as "+user.getEmail(), Toast.LENGTH_SHORT).show();
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
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(mRootView.findViewById(R.id.frg_framelayout_google), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
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
}
