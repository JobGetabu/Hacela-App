package com.job.hacelaapp.profileCore;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {


    private View mRootView;
    public static View refView;
    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.frag_profile_details, container, false);

        refView = mRootView;
        return mRootView;
    }

    public static void makesnack(){
        Snackbar.make(refView.findViewById(R.id.detail_nestedScrollView),"Override TODO: Implement",Snackbar.LENGTH_LONG).show();
    }

}
