package com.job.hacelaapp.manageUsers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordFragment extends Fragment {


    public EmailPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_password, container, false);
    }

}
