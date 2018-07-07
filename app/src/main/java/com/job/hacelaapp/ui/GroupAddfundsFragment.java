package com.job.hacelaapp.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupAddfundsFragment extends BottomSheetDialogFragment {


    public static final String TAG = "ContrFrag";

    public GroupAddfundsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_addfunds, container, false);
    }

}
