package com.job.hacelaapp.manageUsers;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookLogInFragment extends Fragment {


    public static final String FACEBOOK_SIGN_IN = "FACEBOOK_SIGN_IN";

    public FacebookLogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facebook_log_in, container, false);
    }

    private void sendToMain(){

        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.putExtra(FACEBOOK_SIGN_IN,"FACEBOOK_SIGN_IN");
        //since we cnt call finish
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

}
