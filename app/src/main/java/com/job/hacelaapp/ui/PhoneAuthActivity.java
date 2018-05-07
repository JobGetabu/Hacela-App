package com.job.hacelaapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.job.hacelaapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static com.job.hacelaapp.util.Constants.PHONEAUTH_DETAILS;

public class PhoneAuthActivity extends AppCompatActivity {

    @BindView(R.id.phoneauth_mytoolbar)
    Toolbar mToolbar;
    @BindView(R.id.phoneauth_phonenum)
    TextInputLayout mPhoneNumber;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        // Get the ActionBar here to configure the way it behaves.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_custom); // set a custom icon for the default home button
        getSupportActionBar().setDisplayShowHomeEnabled(true); // show or hide the default home button
    }

    @OnClick(R.id.phoneauth_saveButton)
    public void savePhoneClick(){

        if (validateOnclick(mPhoneNumber)){
            String phonenumber = mPhoneNumber.getEditText().getText().toString();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(PHONEAUTH_DETAILS, phonenumber);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }


    }

    private boolean validateOnclick(final TextInputLayout inputLayout) {

        PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.createInstance(this);
        boolean valid = true;

        String phonenum = inputLayout.getEditText().getText().toString();

        Phonenumber.PhoneNumber kenyaNumberProto = null;
        try {
            kenyaNumberProto = mPhoneNumberUtil.parse(phonenum, "KE");
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        if (phonenum.isEmpty() || !mPhoneNumberUtil.isValidNumber(kenyaNumberProto)) {
            inputLayout.setError("enter a valid phone number");
            valid = false;
        } else {
            inputLayout.setError(null);
        }
        return valid;
    }
}
