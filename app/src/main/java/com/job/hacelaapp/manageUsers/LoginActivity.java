package com.job.hacelaapp.manageUsers;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

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
    @BindView(R.id.login_btn_twitter)
    ImageButton lbtnTwitter;
    @BindView(R.id.login_main_pager)
    ViewPager mainLoginPager;
    @BindView(R.id.login_dots_indicator)
    WormDotsIndicator ldotsIndicator;

    LoginFragmentsAdapter loginFragmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

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

    @OnClick(R.id.login_btn_twitter)
    public void lbtnTwitterclick(){
        mainLoginPager.setCurrentItem(2);
        mPageChange(2);
    }

    @OnClick(R.id.login_btn_google)
    public void lbtnGoogleclick(){
        mainLoginPager.setCurrentItem(3);
        mPageChange(3);
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
                lbtnEmail.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_message_white));
                lbtnFacebook.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fb_icon_custom));
                lbtnTwitter.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_twitter_custom));
                lbtnGoogle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_googleplus_icon_custom));
                break;

            case 1:
                lbtnFacebook.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fb_icon_white));
                lbtnEmail.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_message_custom));
                lbtnTwitter.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_twitter_custom));
                lbtnGoogle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_googleplus_icon_custom));
                break;

            case 2:
                lbtnTwitter.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_twitter_icon_white));
                lbtnEmail.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_message_custom));
                lbtnFacebook.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fb_icon_custom));
                lbtnGoogle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_googleplus_icon_custom));
                break;

            case 3:
                lbtnGoogle.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_googleplus_icon_white));
                lbtnEmail.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_message_custom));
                lbtnFacebook.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fb_icon_custom));
                lbtnTwitter.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_twitter_custom));
                break;
            default:
                break;

        }
    }

}
