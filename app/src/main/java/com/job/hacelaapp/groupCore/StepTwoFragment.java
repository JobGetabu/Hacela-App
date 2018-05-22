package com.job.hacelaapp.groupCore;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.R;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepTwoFragment extends Fragment {

    @BindView(R.id.steptwo_fulname)
    TextInputLayout mGrpfulName;
    @BindView(R.id.steptwo_displayname)
    TextInputLayout mGrpDisName;

    private View mRootView;
    private CreateGroupViewModel createGroupViewModel;


    public StepTwoFragment() {
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
        mRootView= inflater.inflate(R.layout.fragment_step_two, container, false);
        ButterKnife.bind(this,mRootView);

        return mRootView;
    }

    @OnClick(R.id.steptwo_fab)
    public void toStepThree(){
        if (validate()){
            createGroupViewModel.setPageNumber(2);

        }
    }

    public boolean validate() {
        boolean valid = true;

        String fulname = mGrpfulName.getEditText().getText().toString();
        String disname = mGrpDisName.getEditText().getText().toString();

        if (fulname.isEmpty()) {
            mGrpfulName.setError("enter a valid group name");
            valid = false;
        } else {
            mGrpfulName.setError(null);
        }

        if (disname.isEmpty()) {
            mGrpDisName.setError("enter a valid group display name");
            valid = false;
        } else {
            mGrpDisName.setError(null);
        }
        return valid;
    }

}
