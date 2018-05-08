package com.job.hacelaapp.hacelaCore;


import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.MainActivity;
import com.job.hacelaapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    @BindView(R.id.chat_toolbar)
    Toolbar mToolbar;

    private View mRootView;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this,mRootView);
        setHasOptionsMenu(true);

        /*((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");*/

        createMenus(mToolbar, R.menu.income_menu);
        return  mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.income_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void createMenus(Toolbar actionBarToolBar, @MenuRes int menu){
        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(actionBarToolBar);
        actionBarToolBar.setTitle("");
        actionBarToolBar.inflateMenu(menu);
    }
}
