package com.job.hacelaapp.manageUsers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;
import com.job.hacelaapp.adapter.LoginFragmentsAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_btn_email)
    ImageButton lbtnEmail;
    @BindView(R.id.login_btn_facebook)
    ImageButton lbtnFacebook;
    @BindView(R.id.login_btn_google)
    ImageButton lbtnGoogle;
    @BindView(R.id.login_main_pager)
    ViewPager mainLoginPager;
    @BindView(R.id.login_dots_indicator)
    WormDotsIndicator ldotsIndicator;

    LoginFragmentsAdapter loginFragmentsAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        //TODO: Handle offline detection

        loginFragmentsAdapter = new LoginFragmentsAdapter(getSupportFragmentManager());
        mainLoginPager.setAdapter(loginFragmentsAdapter);

        ldotsIndicator.setViewPager(mainLoginPager);
        mainLoginPager.addOnPageChangeListener(onPageChangeListener);

        mainLoginPager.setCurrentItem(0);
        mPageChange(0);
    }

    @OnClick(R.id.login_btn_email)
    public void lbtnEmailclick(){
        mainLoginPager.setCurrentItem(0);
        mPageChange(0);
    }

    @OnClick(R.id.login_btn_facebook)
    public void lbtnFacebookclick(){
        mainLoginPager.setCurrentItem(1);
        mPageChange(1);
    }

    @OnClick(R.id.login_btn_google)
    public void lbtnGoogleclick(){
        mainLoginPager.setCurrentItem(2);
        mPageChange(2);
    }


    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            mPageChange(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    };

    private void mPageChange(int position){
        switch (position){
            case 0:
                lbtnEmail.setImageDrawable(fetchDrawable(R.drawable.ic_message_white));
                lbtnFacebook.setImageDrawable(fetchDrawable(R.drawable.ic_fb_icon_custom));
                lbtnGoogle.setImageDrawable(fetchDrawable(R.drawable.ic_googleplus_icon_custom));
                break;

            case 1:
                lbtnFacebook.setImageDrawable(fetchDrawable(R.drawable.ic_fb_icon_white));
                lbtnEmail.setImageDrawable(fetchDrawable(R.drawable.ic_message_custom));
                lbtnGoogle.setImageDrawable(fetchDrawable(R.drawable.ic_googleplus_icon_custom));
                break;

            case 2:
                lbtnGoogle.setImageDrawable(fetchDrawable(R.drawable.ic_googleplus_icon_white));
                lbtnEmail.setImageDrawable(fetchDrawable(R.drawable.ic_message_custom));
                lbtnFacebook.setImageDrawable(fetchDrawable(R.drawable.ic_fb_icon_custom));
                break;
            default:
                break;

        }
    }

    private Drawable fetchDrawable(@DrawableRes int mdrawable) {
        // Facade Design Pattern
        return ContextCompat.getDrawable(this, mdrawable);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain(){

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

}
