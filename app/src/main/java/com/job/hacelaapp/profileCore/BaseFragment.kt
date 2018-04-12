package com.job.hacelaapp.profileCore

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

/**
 * Created by Job on Thursday : 4/12/2018.
 */
abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ToolbarManager(builder(), view).prepareToolbar()
    }

    protected abstract fun builder(): FragmentToolbar
}

/*
This is how to use this BaseFragment class

@Override
protected FragmentToolbar builder() {
    return new FragmentToolbar.Builder()
            .withId(R.id.toolbar)
            .withTitle(R.string.toolbar_title)
            .onHomePressedDefaultAction()
            .build();
}*/


/*
more complex

@Override
    protected FragmentToolbar builder() {
        return new FragmentToolbar.Builder()
                .withId(R.id.toolbar)
                .withTitle(R.string.toolbar_title)
                .withMenu(R.menu.menu_options)
                .withSearchAndFilters(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(final String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(final String newText) {
                        doActionWithQuery(newText);
                        return false;
                    }
                }, filterClick -> {
                    openDrawer();
                    return false;
                })
                .withSearchHint(R.string.search_view_hint)
                .onHomePressedDefaultAction()
                .build();
    }
 */
