package com.job.hacelaapp.profileCore

import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View

/**
 * Created by Job on Thursday : 4/12/2018.
 */
class ToolbarManager constructor(
        private var builder: FragmentToolbar,
        private var container: View) {

    fun prepareToolbar() {
        if (builder.resId != FragmentToolbar.NO_TOOLBAR) {
            val fragmentToolbar = container.findViewById(builder.resId) as Toolbar

            if (builder.title != -1) {
                fragmentToolbar.setTitle(builder.title)
            }

            if (builder.menuId != -1) {
                fragmentToolbar.inflateMenu(builder.menuId)
            }

            if (!builder.menuItems.isEmpty() && !builder.menuClicks.isEmpty()){
                val menu = fragmentToolbar.menu
                for ((index, menuItemId) in builder.menuItems.withIndex()) {
                    (menu.findItem(menuItemId) as MenuItem).setOnMenuItemClickListener(builder.menuClicks[index])
                }
            }
        }
    }
}