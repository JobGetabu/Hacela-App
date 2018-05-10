package com.job.hacelaapp.hacelaCore;


import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;
import com.job.hacelaapp.adapter.NoSwipePager;
import com.job.hacelaapp.adapter.ProfileSliderAdapter;
import com.job.hacelaapp.profileCore.DetailsFragment;
import com.job.hacelaapp.profileCore.GroupsFragment;
import com.job.hacelaapp.profileCore.StatsFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_page_toolbar)
    android.support.v7.widget.Toolbar mToolbar;
    @BindView(R.id.profile_sliding_tabs)
    TabLayout tabLayout;
    @BindView(R.id.profile_noswipepager)
    NoSwipePager mViewPager;

    private View mRootView;

    private ProfileSliderAdapter profileSliderAdapter;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,mRootView);

        setHasOptionsMenu(true);

        //add fragments to adapter
        profileSliderAdapter = new ProfileSliderAdapter(getChildFragmentManager());
        profileSliderAdapter.addFragments(new DetailsFragment());
        profileSliderAdapter.addFragments(new GroupsFragment());
        profileSliderAdapter.addFragments(new StatsFragment());

        mViewPager.setAdapter(profileSliderAdapter);
        mViewPager.setPagingEnabled(true);
        mViewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(mViewPager);

        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void createMenus(Toolbar actionBarToolBar, @MenuRes int menu){
        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(actionBarToolBar);
        actionBarToolBar.setTitle("");
        actionBarToolBar.inflateMenu(menu);
    }
}
