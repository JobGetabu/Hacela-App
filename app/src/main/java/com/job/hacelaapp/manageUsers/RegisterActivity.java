package com.job.hacelaapp.manageUsers;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.job.hacelaapp.R;

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
    @BindView(R.id.register_btn_twitter)
    ImageButton btnTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.register_btn_email)
    public void btnEmailclick(){
        btnEmail.setImageDrawable(getDrawable(R.drawable.ic_email_white_24dp));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.register_btn_facebook)
    public void btnFacebookclick(){
        btnFacebook.setImageDrawable(getDrawable(R.drawable.ic_fb_icon_white));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.register_btn_twitter)
    public void btnTwitterclick(){
        btnTwitter.setImageDrawable(getDrawable(R.drawable.ic_twitter_icon_white));
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.register_btn_google)
    public void btnGoogleclick(){
        btnGoogle.setImageDrawable(getDrawable(R.drawable.ic_googleplus_icon_white));
    }
}
