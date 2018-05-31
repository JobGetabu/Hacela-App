package com.job.hacelaapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.appExecutor.DefaultExecutorSupplier;
import com.job.hacelaapp.dataSource.Groups;
import com.job.hacelaapp.repository.FirebaseDocumentLiveData;

/**
 * Created by Job on Monday : 5/28/2018.
 */
public class GroupControlViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String currentUserId;
    private String groupId;

    public static final String TAG = "GroupVM";

    //few db refs
    private DocumentReference GROUPREF;
    private DocumentReference GROUPMEMBERREF;
    private DocumentReference GROUPADMINREF;
    private DocumentReference GROUPCONTRIBUTIONDEFAULTREF;

    //live-data
    private MediatorLiveData<Groups> groupsMediatorLiveData = new MediatorLiveData<>();


    public GroupControlViewModel(@NonNull Application application,FirebaseAuth mAuth, FirebaseFirestore mFirestore,String groupId) {

        super(application);
        this.mAuth = mAuth;
        this.mFirestore = mFirestore;
        this.currentUserId = mAuth.getCurrentUser().getUid();
        this.groupId = groupId;

        //init db refs
       GROUPREF = mFirestore.collection("Groups").document(groupId);
       GROUPMEMBERREF = mFirestore.collection("GroupMembers").document(groupId);
       GROUPADMINREF = mFirestore.collection("GroupsAdmin").document(groupId);
       GROUPCONTRIBUTIONDEFAULTREF = mFirestore.collection("GroupsContributionDefault").document(groupId);

        // Set up the MediatorLiveData to convert DataSnapshot objects into POJO objects

        workOnGroupsLiveData();
    }

    private void workOnGroupsLiveData(){
        FirebaseDocumentLiveData mData = new FirebaseDocumentLiveData(GROUPREF);

        groupsMediatorLiveData.addSource(mData, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null){

                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .execute(new Runnable() {
                                @Override
                                public void run() {
                                    // do some background work here.
                                    groupsMediatorLiveData.postValue(documentSnapshot.toObject(Groups.class));
                                }
                            });

                }else {
                    groupsMediatorLiveData.setValue(null);
                }
            }
        });

    }


    public MediatorLiveData<Groups> getGroupsMediatorLiveData() {
        return groupsMediatorLiveData;
    }

    public void setGroupsMediatorLiveData(Groups groupsMediatorLiveData) {
        this.groupsMediatorLiveData.setValue(groupsMediatorLiveData);
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
