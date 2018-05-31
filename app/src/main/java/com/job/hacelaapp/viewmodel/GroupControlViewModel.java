package com.job.hacelaapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Job on Monday : 5/28/2018.
 */
public class GroupControlViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String currentUserId;
    private String groupId;

    public static final String TAG = "GroupVM";

    private DocumentReference GROUPREF;
    private DocumentReference GROUPMEMBERREF;
    private DocumentReference GROUPCONTRIBUTIONDEFAULTREF;
    private DocumentReference GROUPADMINREF;


    public GroupControlViewModel(@NonNull Application application,FirebaseAuth mAuth, FirebaseFirestore mFirestore,String groupId) {

        super(application);
        this.mAuth = mAuth;
        this.mFirestore = mFirestore;
        this.currentUserId = mAuth.getCurrentUser().getUid();
        this.groupId = groupId;

        //init db refs

        // Set up the MediatorLiveData to convert DataSnapshot objects into POJO objects

    }

    /**
     * Factory for instantiating the viewmodel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private FirebaseAuth mAuth;
        private FirebaseFirestore mFirestore;
        private String groupId;

        public Factory(@NonNull Application mApplication, FirebaseAuth mAuth, FirebaseFirestore mFirestore, String groupId) {
            this.mApplication = mApplication;
            this.mAuth = mAuth;
            this.mFirestore = mFirestore;
            this.groupId = groupId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new DetailsEditActivityViewModel(mApplication, mAuth, mFirestore);
        }
    }
}
