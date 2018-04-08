package com.job.hacelaapp.manageUsers;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.job.hacelaapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordLogInFragment extends Fragment {

    @BindView(R.id.login_input_email_number)
    TextInputLayout emailOrNumber;
    @BindView(R.id.login_input_password)
    TextInputLayout mPassword;


    private View mRootView;

    public EmailPasswordLogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_email_password_log_in, container, false);
        ButterKnife.bind(this,mRootView);
        
        return  mRootView;
    }

    @OnClick(R.id.login_tv_signup)
    public void tvLoginSignUp(){
        Intent registerIntent = new Intent(getActivity(),RegisterActivity.class);
        startActivity(registerIntent);
    }
    
    @OnClick(R.id.login_tv_forgotpassword)
    public void tvLoginForgotPassword(){
        Toast.makeText(getActivity(), "TODO: Implement forgot password", Toast.LENGTH_SHORT).show();
    }
}
