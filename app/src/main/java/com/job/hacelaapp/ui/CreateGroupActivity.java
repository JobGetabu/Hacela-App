package com.job.hacelaapp.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.job.hacelaapp.R;
import com.job.hacelaapp.adapter.CreateGroupsAdapter;
import com.job.hacelaapp.adapter.NoSwipePager;
import com.job.hacelaapp.groupCore.StepFiveFragment;
import com.job.hacelaapp.groupCore.StepFourFragment;
import com.job.hacelaapp.groupCore.StepOneFragment;
import com.job.hacelaapp.groupCore.StepThreeFragment;
import com.job.hacelaapp.groupCore.StepTwoFragment;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateGroupActivity extends AppCompatActivity {

    @BindView(R.id.create_groupdots_indicator)
    DotsIndicator mDots;
    @BindView(R.id.create_grouppager)
    NoSwipePager mPager;

    private CreateGroupViewModel createGroupViewModel;
    private CreateGroupsAdapter createGroupsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);


        //init fragments
        createGroupsAdapter = new CreateGroupsAdapter(getSupportFragmentManager());
        createGroupsAdapter.addFragments(new StepOneFragment());
        createGroupsAdapter.addFragments(new StepTwoFragment());
        createGroupsAdapter.addFragments(new StepThreeFragment());
        createGroupsAdapter.addFragments(new StepFourFragment());
        createGroupsAdapter.addFragments(new StepFiveFragment());

        //set up pager
        mPager.setAdapter(createGroupsAdapter);
        mPager.setPagingEnabled(false);
        mPager.setOffscreenPageLimit(5);

        mDots.setViewPager(mPager);

        //now lets set up the model
        createGroupViewModel = ViewModelProviders.of(this).get(CreateGroupViewModel.class);

        setmPagerPage();
    }

    private void setmPagerPage(){

        ViewModelProviders.of(this).get(CreateGroupViewModel.class).getPageNumber().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {

                mPager.setCurrentItem(integer);
            }
        });
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }
}
