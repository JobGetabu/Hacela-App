package com.job.hacelaapp.manageUsers;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.R;
import com.ybs.passwordstrengthmeter.PasswordStrength;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordFragment extends Fragment implements TextWatcher {


    @BindView(R.id.reg_input_displayname)
    TextInputLayout inDisplayName;
    @BindView(R.id.reg_input_phonenumber)
    TextInputLayout inPhoneNumber;
    @BindView(R.id.reg_input_email)
    TextInputLayout inEmail;
    @BindView(R.id.reg_input_password)
    TextInputLayout inPassword;
    @BindView(R.id.reg_editinput_password)
    TextInputEditText inEditPassword;

    @BindView(R.id.reg_progressBar)
    ProgressBar inProgressBar;
    @BindView(R.id.reg_password_strength)
    TextView inPasswordStrength;

    private View mRootView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public EmailPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_email_password, container, false);
        ButterKnife.bind(this,mRootView);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();


        inEditPassword.addTextChangedListener(this);


        return mRootView;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        updatePasswordStrengthView(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void updatePasswordStrengthView(String password) {

       // = (ProgressBar) mRootView.findViewById(R.id.reg_progressBar);
        //= (TextView) mRootView.findViewById(R.id.reg_password_strength);

        if (TextView.VISIBLE != inPasswordStrength.getVisibility())
            return;

        if (password.isEmpty()) {
            inPasswordStrength.setText("");
            inProgressBar.setProgress(0);
            return;
        }

        PasswordStrength str = PasswordStrength.calculateStrength(password);
        inPasswordStrength.setText(str.getText(getActivity()));
        inPasswordStrength.setTextColor(str.getColor());

        inProgressBar.getProgressDrawable().setColorFilter(str.getColor(), PorterDuff.Mode.SRC_IN);
        if (str.getText(getActivity()).equals("Weak")) {
            inProgressBar.setProgress(25);
        } else if (str.getText(getActivity()).equals("Medium")) {
            inProgressBar.setProgress(50);
        } else if (str.getText(getActivity()).equals("Strong")) {
            inProgressBar.setProgress(75);
        } else {
            inProgressBar.setProgress(100);
        }
    }

    @OnClick({R.id.reg_btn_signup})
    public void regBtnEmailPasswordClick(){

        //TODO: perform user registration and simple database write ie token, displayname
    }
}
