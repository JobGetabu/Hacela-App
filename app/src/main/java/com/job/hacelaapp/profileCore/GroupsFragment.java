package com.job.hacelaapp.profileCore;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View mRootView;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.frag_profile_groups, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.profile_page_toolbar);
        createMenus(toolbar, 0);
        setHasOptionsMenu(true);


        return mRootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.profile_menu_edit:
                Intent detailsEditIntent = new Intent(getActivity(), DetailsEditActivity.class);
                startActivity(detailsEditIntent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void createMenus(Toolbar actionBarToolBar, @MenuRes int menu) {
        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(actionBarToolBar);
        actionBarToolBar.setTitle("");
        if (menu == 0)
            actionBarToolBar.getMenu().clear();
        else
            actionBarToolBar.inflateMenu(menu);
    }
}
