package com.job.hacelaapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * Created by Job on Tuesday : 5/8/2018.
 */
public class CreateGroupViewModel extends ViewModel {


    private MutableLiveData<Integer> pageNumber;

    private MutableLiveData<String> groupFullName;

    private MutableLiveData<String> groupDisplayName;

    public CreateGroupViewModel() {

        pageNumber = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber.setValue(pageNumber);
    }
}
