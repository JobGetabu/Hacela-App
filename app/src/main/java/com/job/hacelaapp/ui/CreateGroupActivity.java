package com.job.hacelaapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.job.hacelaapp.R;
import com.job.hacelaapp.adapter.CreateGroupsAdapter;
import com.job.hacelaapp.adapter.NoSwipePager;
import com.job.hacelaapp.groupCore.StepOneFragment;
import com.job.hacelaapp.groupCore.StepTwoFragment;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateGroupActivity extends AppCompatActivity {

    @BindView(R.id.create_groupdots_indicator)
    DotsIndicator mDots;
    @BindView(R.id.create_grouppager)
    NoSwipePager mPager;

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

        //set up pager
        mPager.setAdapter(createGroupsAdapter);
        mPager.setPagingEnabled(false);
        mPager.setOffscreenPageLimit(2);

        mDots.setViewPager(mPager);
    }
}
