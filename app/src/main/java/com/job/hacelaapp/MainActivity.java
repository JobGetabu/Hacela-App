package com.job.hacelaapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.job.hacelaapp.adapter.BottomBarAdapter;
import com.job.hacelaapp.adapter.NoSwipePager;
import com.job.hacelaapp.hacelaCore.AccountFragment;
import com.job.hacelaapp.hacelaCore.HomeFragment;
import com.job.hacelaapp.hacelaCore.ProfileFragment;
import com.job.hacelaapp.manageUsers.LoginActivity;
import com.job.hacelaapp.viewmodel.NavigationViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainactivity_bottom_navigation)
    AHBottomNavigation mBottomNavigation;
    @BindView(R.id.mainactivity_noswipepager)
    NoSwipePager mNoSwipePager;

    public static final String TAG = "MainActivity";


    private FirebaseAuth mAuth;

    private boolean notificationVisible = false;
    private BottomBarAdapter pagerAdapter;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private NavigationViewModel navigationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        authListner();

        navigationViewModel = ViewModelProviders.of(this).get(NavigationViewModel.class);

        //bottom nav items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(fetchString(R.string.bottomnav_title_1),
                fetchDrawable(R.drawable.ic_home_trans));
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(fetchString(R.string.bottomnav_title_2),
                fetchDrawable(R.drawable.ic_account));
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(fetchString(R.string.bottomnav_title_3),
                fetchDrawable(R.drawable.ic_chat_trans));
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(fetchString(R.string.bottomnav_title_0),
                fetchDrawable(R.drawable.ic_profile_trans));

        mBottomNavigation.addItem(item1);
        mBottomNavigation.addItem(item2);
        //mBottomNavigation.addItem(item3);
        mBottomNavigation.addItem(item4);

        mBottomNavigation.setOnTabSelectedListener(onTabSelectedListener);

        mBottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        mBottomNavigation.setAccentColor(fetchColor(R.color.colorPrimary));
        mBottomNavigation.setInactiveColor(fetchColor(R.color.bottomtab_item_resting));

        mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        //translucent bottom navigation
        //mBottomNavigation.setTranslucentNavigationEnabled(true);



        //Color ripple effect will enable it at the finish of design
        //  Enables color Reveal effect
        //mBottomNavigation.setColored(true);
        // Colors for selected (active) and non-selected items (in color reveal mode).
        //mBottomNavigation.setColoredModeColors(fetchColor(R.color.colorPrimary),fetchColor(R.color.bottomtab_item_resting));

        mNoSwipePager.setPagingEnabled(false);

        //caches data in fragments
        mNoSwipePager.setOffscreenPageLimit(3);

        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(new HomeFragment());
        pagerAdapter.addFragments(new AccountFragment());
        //pagerAdapter.addFragments(new ChatFragment());
        pagerAdapter.addFragments(new ProfileFragment());

        //Quick Return Animation
        mBottomNavigation.setBehaviorTranslationEnabled(true);

        mNoSwipePager.setAdapter(pagerAdapter);

        mBottomNavigation.setCurrentItem(0);
        mNoSwipePager.setCurrentItem(0);

        //createFakeNotification();

        navHome();
        refreshAdapter();
    }

    private void navHome() {
        navigationViewModel.getHomeDestination().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    mBottomNavigation.setCurrentItem(integer);
                    mNoSwipePager.setCurrentItem(integer);
                }
            }
        });
    }

    private void refreshAdapter(){
        navigationViewModel.getRefreshData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null){
                    pagerAdapter.notifyDataSetChanged();
                }
            }
        });
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
        return ContextCompat.getColor(this, color);
    }

    /**
     * Show or hide the bottom navigation with animation
     */
    public void showOrHideBottomNavigation(boolean show) {
        if (show) {
            mBottomNavigation.restoreBottomNavigation(true);
        } else {
            mBottomNavigation.hideBottomNavigation(true);
        }
    }

    public void manageFabButton(android.support.design.widget.FloatingActionButton floatingActionButton){
        mBottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }


    AHBottomNavigation.OnTabSelectedListener onTabSelectedListener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {

            //change fragments
            if (!wasSelected) {
                pagerAdapter.notifyDataSetChanged();
                mNoSwipePager.setCurrentItem(position);
                Log.d(TAG, "onTabSelected: AT :" + position);

            }

            // remove notification badge..
            int lastItemPos = mBottomNavigation.getItemsCount() - 1;
            if (notificationVisible && position == lastItemPos) {
                mBottomNavigation.setNotification(new AHNotification(), lastItemPos);
            }

            //fragment change logic
            switch (position) {
                case 0:

                    break;

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


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case R.id.main_home_menu_signout:
                mAuth.signOut();
                signOutGoogle();
                signOutFaceBook();
                sendToLogin();
                break;
            *//*case R.id.profile_menu_edit:
                Toast.makeText(this, "Edit profile", Toast.LENGTH_SHORT).show();
                break;*//*
        }

        return true;
    }*/

    //auth state listener for live changes
    private void authListner() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // Sign in logic here.
                    sendToLogin();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: check if User doc exists dead trigger approach
        //handling sloppy connections conundrums
    }
}
