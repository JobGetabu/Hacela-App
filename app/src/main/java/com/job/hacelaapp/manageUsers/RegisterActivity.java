package com.job.hacelaapp.manageUsers;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.job.hacelaapp.R;
import com.job.hacelaapp.adapter.RegistrationFragmentsAdapter;
import com.job.hacelaapp.util.IsPlayServicesAvailable;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_btn_email)
    ImageButton btnEmail;
    @BindView(R.id.register_btn_facebook)
    ImageButton btnFacebook;
    @BindView(R.id.register_btn_google)
    ImageButton btnGoogle;
    @BindView(R.id.register_main_pager)
    ViewPager mainRegisterPager;
    @BindView(R.id.register_dots_indicator)
    WormDotsIndicator dotsIndicator;

    RegistrationFragmentsAdapter registrationFragmentsAdapter;

    //TODO: Add phone number registration method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        registrationFragmentsAdapter = new RegistrationFragmentsAdapter(getSupportFragmentManager());
        mainRegisterPager.setAdapter(registrationFragmentsAdapter);
        dotsIndicator.setViewPager(mainRegisterPager);
        mainRegisterPager.addOnPageChangeListener(onPageChangeListener);

        mainRegisterPager.setCurrentItem(0);
        mPageChange(0);

        IsPlayServicesAvailable playServicesAvailable = new IsPlayServicesAvailable();
        playServicesAvailable.checksForGoogleServicesAvailability(this,RegisterActivity.this);
    }

    @OnClick(R.id.register_btn_email)
    public void btnEmailclick(){
        mainRegisterPager.setCurrentItem(0);
        mPageChange(0);
    }

    @OnClick(R.id.register_btn_facebook)
    public void btnFacebookclick(){
        mainRegisterPager.setCurrentItem(1);
        mPageChange(1);
    }

    @OnClick(R.id.register_btn_google)
    public void btnGoogleclick(){
        mainRegisterPager.setCurrentItem(2);
        mPageChange(2);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    private Drawable fetchDrawable(@DrawableRes int mdrawable) {
        // Facade Design Pattern
        return ContextCompat.getDrawable(this, mdrawable);
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
                    btnEmail.setImageDrawable(fetchDrawable(R.drawable.ic_message_white));
                    btnFacebook.setImageDrawable(fetchDrawable(R.drawable.ic_fb_icon_custom));
                    btnGoogle.setImageDrawable(fetchDrawable(R.drawable.ic_googleplus_icon_custom));
                    break;

                case 1:
                    btnFacebook.setImageDrawable(fetchDrawable(R.drawable.ic_fb_icon_white));
                    btnEmail.setImageDrawable(fetchDrawable(R.drawable.ic_message_custom));
                    btnGoogle.setImageDrawable(fetchDrawable(R.drawable.ic_googleplus_icon_custom));
                    break;

                case 2:
                    btnGoogle.setImageDrawable(fetchDrawable(R.drawable.ic_googleplus_icon_white));
                    btnEmail.setImageDrawable(fetchDrawable(R.drawable.ic_message_custom));
                    btnFacebook.setImageDrawable(fetchDrawable(R.drawable.ic_fb_icon_custom));
                    break;
                    default:
                        break;

            }
        }
}
