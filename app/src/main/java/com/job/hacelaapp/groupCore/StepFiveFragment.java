package com.job.hacelaapp.groupCore;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.GroupDescription;
import com.job.hacelaapp.dataSource.Step4OM;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFiveFragment extends Fragment {


    @BindView(R.id.stepfive_switch)
    SwitchCompat switchCompat;
    @BindView(R.id.stepfive_penalty)
    TextInputLayout mPenalty;

    private View mRootView;

    private static final String TAG = "stepfive";

    private CreateGroupViewModel createGroupViewModel;

    public StepFiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_step_five, container, false);
        ButterKnife.bind(this,mRootView);

        return mRootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createGroupViewModel = ViewModelProviders.of(getActivity()).get(CreateGroupViewModel.class);

    }

    @OnClick(R.id.stepfive_switch)
    public void stepFiveSwitch(){

        if(switchCompat.isChecked()){
            mPenalty.setEnabled(true);
        }else {
            mPenalty.setEnabled(false);
        }
    }

    private String groupfullname;
    private String groupdisplayname;
    private GroupDescription groupDesp;
    private Step4OM step4data;

    @OnClick(R.id.stepfive_fab)
    public void stepFiveFinish(){

        //get all observables here


        createGroupViewModel.getGroupFullName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s!=null){
                    groupfullname = s;
                }
            }
        });
        createGroupViewModel.getGroupDisplayName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s!=null){
                    groupdisplayname = s;
                }
            }
        });
        createGroupViewModel.getGroupDescriptionMutableLiveData().observe(this, new Observer<GroupDescription>() {
            @Override
            public void onChanged(@Nullable GroupDescription groupDescription) {
                if (groupDescription != null)
                    groupDesp = groupDescription;
            }
        });
        createGroupViewModel.getStep4OMMutableLiveData().observe(this, new Observer<Step4OM>() {
            @Override
            public void onChanged(@Nullable Step4OM step4OM) {
                if (step4OM != null)
                    step4data = step4OM;
            }
        });

        Toast.makeText(getContext(), "\n"+groupfullname+"\n"+groupdisplayname+"\n"+groupDesp+"\n"+step4data, Toast.LENGTH_LONG).show();
    }
}
