package com.job.hacelaapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.job.hacelaapp.adapter.BottomBarAdapter;
import com.job.hacelaapp.adapter.NoSwipePager;
import com.job.hacelaapp.hacelaCore.AccountFragment;
import com.job.hacelaapp.hacelaCore.ChatFragment;
import com.job.hacelaapp.hacelaCore.HomeFragment;
import com.job.hacelaapp.hacelaCore.ProfileFragment;
import com.job.hacelaapp.manageUsers.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainactivity_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.mainactivity_bottom_navigation)
    AHBottomNavigation mBottomNavigation;
    @BindView(R.id.mainactivity_noswipepager)
    NoSwipePager mNoSwipePager;


    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private boolean notificationVisible = false;
    private BottomBarAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        //bottom nav items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(fetchString(R.string.bottomnav_title_0),
                fetchDrawable(R.drawable.ic_edit_profile));
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(fetchString(R.string.bottomnav_title_1),
                fetchDrawable(R.drawable.ic_home_trans));
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(fetchString(R.string.bottomnav_title_2),
                fetchDrawable(R.drawable.ic_account));
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(fetchString(R.string.bottomnav_title_3),
                fetchDrawable(R.drawable.ic_chat_trans));

        mBottomNavigation.addItem(item1);
        mBottomNavigation.addItem(item2);
        mBottomNavigation.addItem(item3);
        mBottomNavigation.addItem(item4);

        mBottomNavigation.setOnTabSelectedListener(onTabSelectedListener);

        mBottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        mBottomNavigation.setInactiveColor(fetchColor(R.color.bottomtab_item_resting));

        mBottomNavigation.setAccentColor(fetchColor(R.color.colorPrimary));
        mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        //translucent bottom navigation
        //mBottomNavigation.setTranslucentNavigationEnabled(true);

        //Quick Return Animation
        mBottomNavigation.setBehaviorTranslationEnabled(true);


        //Color ripple effect will enable it at the finish of design
        //  Enables color Reveal effect
        //mBottomNavigation.setColored(true);
        // Colors for selected (active) and non-selected items (in color reveal mode).
        //mBottomNavigation.setColoredModeColors(Color.WHITE,fetchColor(R.color.bottomtab_item_resting));


        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        //disable swiping
        mNoSwipePager.setPagingEnabled(false);
        //add fragments here
        pagerAdapter.addFragments(new ProfileFragment());
        pagerAdapter.addFragments(new HomeFragment());
        pagerAdapter.addFragments(new AccountFragment());
        pagerAdapter.addFragments(new ChatFragment());

        mNoSwipePager.setAdapter(pagerAdapter);

        mNoSwipePager.setCurrentItem(2);
        mBottomNavigation.setCurrentItem(1);

        //click events
    }

    private Drawable fetchDrawable(@DrawableRes int mdrawable) {
        // Facade Design Pattern
        return ContextCompat.getDrawable(this, mdrawable);
    }
    private String fetchString(@StringRes int mystring) {
        // Facade Design Pattern
        return getResources().getString(mystring);
    }
    private int fetchColor(@ColorRes int color) {
        // Facade Design Pattern
        return ContextCompat.getColor(this,color);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_home_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         int id = item.getItemId();
         switch (id){
             case R.id.main_home_menu_signout:
                 mAuth.signOut();
                 signOutGoogle();
                 signOutFaceBook();

                 sendToLogin();
                 //TODO: sign out other providers too
                 break;
         }

         return true;
    }

    private void signOutGoogle() {

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void signOutFaceBook(){
        LoginManager.getInstance().logOut();
    }

    AHBottomNavigation.OnTabSelectedListener onTabSelectedListener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {

            //change fragments
            if (!wasSelected) {
                mNoSwipePager.setCurrentItem(position);
            }

            // remove notification badge..
            int lastItemPos = mBottomNavigation.getItemsCount() - 1;
            if (notificationVisible && position == lastItemPos) {
                mBottomNavigation.setNotification(new AHNotification(), lastItemPos);
            }

            return true;
        }
    };


    private void createFakeNotification() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AHNotification notification = new AHNotification.Builder()
                        .setText("2")
                        .setBackgroundColor(fetchColor(R.color.colorAccent))
                        .setTextColor(Color.BLACK)
                        .build();
                // Adding notification to last item.
                mBottomNavigation.setNotification(notification, mBottomNavigation.getItemsCount() - 1);
                notificationVisible = true;
            }
        }, 1000);
    }
}
