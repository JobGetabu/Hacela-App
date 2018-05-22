package com.job.hacelaapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.job.hacelaapp.dataSource.GroupDescription;
import com.job.hacelaapp.dataSource.Step4OM;

/**
 * Created by Job on Tuesday : 5/8/2018.
 */
public class CreateGroupViewModel extends ViewModel {


    private MutableLiveData<Integer> pageNumber;

    private MutableLiveData<String> groupFullName;

    private MutableLiveData<String> groupDisplayName;

    private MutableLiveData<GroupDescription> groupDescriptionMutableLiveData;

    private MutableLiveData<Step4OM> step4OMMutableLiveData;

    public CreateGroupViewModel() {

        pageNumber = new MutableLiveData<>();
        groupFullName = new MutableLiveData<>();
        groupDisplayName = new MutableLiveData<>();
        groupDescriptionMutableLiveData = new MutableLiveData<>();
        step4OMMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber.setValue(pageNumber);
    }

    public MutableLiveData<String> getGroupFullName() {
        return groupFullName;
    }

    public void setGroupFullName(String groupFullName) {
        this.groupFullName.setValue(groupFullName);
    }

    public MutableLiveData<String> getGroupDisplayName() {
        return groupDisplayName;
    }

    public void setGroupDisplayName(String groupDisplayName) {
        this.groupDisplayName.setValue(groupDisplayName);
    }

    public MutableLiveData<GroupDescription> getGroupDescriptionMutableLiveData() {
        return groupDescriptionMutableLiveData;
    }

    public void setGroupDescriptionMutableLiveData(GroupDescription groupDescription) {
        this.groupDescriptionMutableLiveData.setValue(groupDescription);
    }

    public MutableLiveData<Step4OM> getStep4OMMutableLiveData() {
        return step4OMMutableLiveData;
    }

    public void setStep4OMMutableLiveData(Step4OM step4OMMutableLiveData) {
        this.step4OMMutableLiveData.setValue(step4OMMutableLiveData);
    }
}
