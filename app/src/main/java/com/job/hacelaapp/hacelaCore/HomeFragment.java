package com.job.hacelaapp.hacelaCore;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;
import com.job.hacelaapp.manageUsers.LoginActivity;
import com.job.hacelaapp.ui.CreateGroupActivity;
import com.job.hacelaapp.ui.GroupControlActivity;
import com.job.hacelaapp.ui.GroupInviteActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.home_page_toolbar)
    Toolbar mToolbar;

    private static String TAG = "homeFrg";
    @BindView(R.id.homebar_image)
    CircleImageView homebarImage;
    @BindView(R.id.homebar_groupname)
    Chip homebarGroupname;
    @BindView(R.id.homebar_groupbalance)
    Chip homebarGroupbalance;
    @BindView(R.id.homebar_userbalance)
    Chip homebarUserbalance;
    @BindView(R.id.homebar_grouplist)
    RecyclerView homebarGrouplist;
    private View mRootView;

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, mRootView);

        createMenus(mToolbar, R.menu.main_home_menu);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        handleAppInvite();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.main_home_menu_signout:
                mAuth.signOut();
                signOutGoogle();
                signOutFaceBook();
                sendToLogin();
                break;
            case R.id.main_home_menu_creategroup:
                sendToCreateGroup();
                break;
            case R.id.main_home_menu_viewgroup:
                sendToViewGroup();
                break;
            case R.id.main_home_menu_controlgroup:
                sendToControlGroup();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendToCreateGroup() {
        Intent createIntent = new Intent(getActivity(), CreateGroupActivity.class);
        startActivity(createIntent);
    }

    private void sendToViewGroup() {
        Intent createIntent = new Intent(getActivity(), GroupInviteActivity.class);
        startActivity(createIntent);
    }

    private void sendToControlGroup() {
        Intent createIntent = new Intent(getActivity(), GroupControlActivity.class);
        startActivity(createIntent);
    }

    private void signOutGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void signOutFaceBook() {
        LoginManager.getInstance().logOut();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
    }

    private void createMenus(Toolbar actionBarToolBar, @MenuRes int menu) {
        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(actionBarToolBar);
        actionBarToolBar.setTitle("");
        actionBarToolBar.inflateMenu(menu);
    }

    private void handleAppInvite() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getActivity().getIntent())
                .addOnSuccessListener(getActivity(), new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d(TAG, "onSuccess: " + deepLink);
                        }
                        //
                        // If the user isn't signed in and the pending Dynamic Link is
                        // an invitation, sign in the user anonymously, and record the
                        // referrer's UID.
                        //

                        //TODO handle anonymous login

                       /* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null
                                && deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby")) {
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            createAnonymousAccountWithReferrerInfo(referrerUid);
                        }*/

                        //display a snack with group id
                        if (deepLink != null) {
                            String referredGroupUid = deepLink.getQueryParameter("invitedto");
                            String query = deepLink.getQuery();

                            Log.d(TAG, "onSuccess: " + query);
                            Log.d(TAG, "onSuccess: " + deepLink.getQueryParameters("invitedto"));

                            if (referredGroupUid != null)
                                Snackbar.make(
                                        getActivity().findViewById(android.R.id.content),
                                        referredGroupUid,
                                        Snackbar.LENGTH_INDEFINITE).show();
                            Toast.makeText(getContext()
                                    , "" + referredGroupUid, Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    @OnClick({R.id.homebar_groupname, R.id.homebar_groupbalance, R.id.homebar_userbalance})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.homebar_groupname:
                break;
            case R.id.homebar_groupbalance:
                break;
            case R.id.homebar_userbalance:
                break;
        }
    }
}
