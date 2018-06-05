package com.job.hacelaapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.appExecutor.DefaultExecutorSupplier;
import com.job.hacelaapp.appExecutor.Priority;
import com.job.hacelaapp.appExecutor.PriorityRunnable;
import com.job.hacelaapp.dataSource.Groups;
import com.job.hacelaapp.repository.FirebaseDocumentLiveData;

import static com.job.hacelaapp.util.Constants.GROUPCOL;
import static com.job.hacelaapp.util.Constants.GROUPCONTRIBUTIONCOL;

/**
 * Created by Job on Monday : 5/28/2018.
 */
public class GroupInfoViewModel extends AndroidViewModel {

    private FirebaseFirestore mFirestore;
    private String groupId;

    public static final String TAG = "GroupVM";

    //few db references
    private DocumentReference GROUPREF;
    private DocumentReference GROUPDEFREF;;

    private MediatorLiveData<Groups> groupsMediatorLiveData = new MediatorLiveData<>();

    public GroupInfoViewModel(@NonNull Application application, FirebaseFirestore mFirestore,String groupId) {

        super(application);
        this.mFirestore = mFirestore;
        this.groupId = groupId;

        //init db refs
        GROUPREF = mFirestore.collection(GROUPCOL).document(groupId);
        GROUPDEFREF = mFirestore.collection(GROUPCONTRIBUTIONCOL).document(groupId);

        // Set up the MediatorLiveData to convert DataSnapshot objects into POJO objects
        workOnGroupLiveData();
    }

    private void workOnGroupLiveData(){
        FirebaseDocumentLiveData mData = new FirebaseDocumentLiveData(GROUPREF);

        groupsMediatorLiveData.addSource(mData, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null){
                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .submit(new PriorityRunnable(Priority.HIGH) {
                                @Override
                                public void run() {
                                    // do some background work here at high priority.
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
        private FirebaseFirestore mFirestore;
        private String groupId;

        public Factory(@NonNull Application mApplication, FirebaseFirestore mFirestore,String groupId) {
            this.mApplication = mApplication;
            this.mFirestore = mFirestore;
            this.groupId = groupId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new GroupInfoViewModel(mApplication, mFirestore,groupId);
        }
    }
}
