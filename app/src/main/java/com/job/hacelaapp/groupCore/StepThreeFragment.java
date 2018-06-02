package com.job.hacelaapp.groupCore;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.GroupDescription;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepThreeFragment extends Fragment {

    @BindView(R.id.stepthree_radioGroup)
    RadioGroup radioGroupType;

    private View mRootView;
    private FirebaseAuth mAuth;
    private CreateGroupViewModel createGroupViewModel;

    public StepThreeFragment() {
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
        mRootView = inflater.inflate(R.layout.fragment_step_three, container, false);
        ButterKnife.bind(this,mRootView);

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        return mRootView;
    }

    @OnClick(R.id.stepthree_fab)
    public void toStepFour(){

        selectedGroupType();
        createGroupViewModel.setPageNumber(3);
    }

    private void selectGroupType(String groupStr) {
        if (groupStr.equals("Merry-go-Round"))
            radioGroupType.check(R.id.stepthree_merryonly);
        else
            radioGroupType.check(R.id.stepthree_merryandsave);
    }

    @NonNull
    private void selectedGroupType() {
        int selectedId = radioGroupType.getCheckedRadioButtonId();

        GroupDescription groupDescription = new GroupDescription();

        switch (selectedId) {
            case R.id.stepthree_merryonly:
                groupDescription.setTypeofgroup("Merry-go-Round");
                groupDescription.setCreatedby(mAuth.getCurrentUser().getDisplayName());
                createGroupViewModel.setGroupDescriptionMutableLiveData(groupDescription);
               break;
            case R.id.stepthree_merryandsave:
                groupDescription.setTypeofgroup("Merry-go-Round and Savings");
                groupDescription.setCreatedby(mAuth.getCurrentUser().getDisplayName());
                createGroupViewModel.setGroupDescriptionMutableLiveData(groupDescription);
                break;
            default:

        }
    }

}
