package com.job.hacelaapp.repository;

import android.arch.lifecycle.LiveData;

import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by Job on Thursday : 4/12/2018.
 */
public class FirebaseQueryLiveData extends LiveData<QuerySnapshot> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

}