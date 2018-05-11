package com.job.hacelaapp.groupCore;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.R;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFourFragment extends Fragment {


    private View mRootView;
    private CreateGroupViewModel createGroupViewModel;

    public StepFourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createGroupViewModel = ViewModelProviders.of(getActivity()).get(CreateGroupViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView =  inflater.inflate(R.layout.fragment_step_four, container, false);
        ButterKnife.bind(this,mRootView);
        return mRootView;
    }

    @OnClick(R.id.stepfour_fab)
    public void toStepFive(){
        createGroupViewModel.setPageNumber(4);
    }

}
