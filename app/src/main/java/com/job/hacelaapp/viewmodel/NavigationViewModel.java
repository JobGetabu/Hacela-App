package com.job.hacelaapp.viewmodel;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * Created by Job on Sunday : 6/24/2018.
 */
public class NavigationViewModel extends ViewModel {

    private MediatorLiveData<Integer> homeDestination = new MediatorLiveData<>();

    public MediatorLiveData<Integer> getHomeDestination() {
        return homeDestination;
    }

    public void setHomeDestination(Integer homeDestination) {
        this.homeDestination.setValue(homeDestination);
    }
}
