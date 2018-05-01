package com.job.hacelaapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.appExecutor.DefaultExecutorSupplier;
import com.job.hacelaapp.appExecutor.Priority;
import com.job.hacelaapp.appExecutor.PriorityRunnable;
import com.job.hacelaapp.dataSource.UserBasicInfo;
import com.job.hacelaapp.repository.FirebaseQueryLiveData;

/**
 * Created by Job on Sunday : 4/22/2018.
 */
public class DetailsEditActivityViewModel extends AndroidViewModel {


    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    //few db references
    private String currentUserId;

    private DocumentReference USERSREF;
    private DocumentReference USERSAUTHREF;
    private DocumentReference USERSPROFILE;


    private FirebaseQueryLiveData mLiveDataUsersdata;

    public MediatorLiveData<UserBasicInfo> getUsersLiveData() {
        return usersLiveData;
    }

    private MediatorLiveData<UserBasicInfo> usersLiveData = new MediatorLiveData<>();

    public DetailsEditActivityViewModel(@NonNull Application application, FirebaseAuth mAuth, FirebaseFirestore mFirestore) {
        super(application);

        //firebase
        this.mAuth = mAuth;
        this.mFirestore = mFirestore;
        this.currentUserId = mAuth.getCurrentUser().getUid();

        //init
        USERSREF = mFirestore.collection("Users").document(currentUserId);
        USERSAUTHREF = mFirestore.collection("UsersAuth").document(currentUserId);
        USERSPROFILE = mFirestore.collection("UsersProfile").document(currentUserId);

        Log.d("DetailsEditActivity", "DetailsEditActivityViewModel: " + mAuth.getCurrentUser().getEmail());
        Log.d("DetailsEditActivity", "DetailsEditActivityViewModel: " + USERSREF);


        mLiveDataUsersdata = new FirebaseQueryLiveData(USERSREF);

        // Set up the MediatorLiveData to convert DataSnapshot objects into POJO objects
        usersLiveData.addSource(mLiveDataUsersdata, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {

                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .submit(new PriorityRunnable(Priority.HIGH) {
                                @Override
                                public void run() {
                                    // do some background work here at high priority.
                                    usersLiveData.postValue(documentSnapshot.toObject(UserBasicInfo.class));
                                }
                            });
                } else {
                    usersLiveData.setValue(null);
                }
            }
        });
    }



    /**
     * Factory for instantiating the viewmodel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private FirebaseAuth mAuth;
        private FirebaseFirestore mFirestore;

        public Factory(@NonNull Application mApplication, FirebaseAuth mAuth, FirebaseFirestore mFirestore) {
            this.mApplication = mApplication;
            this.mAuth = mAuth;
            this.mFirestore = mFirestore;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new DetailsEditActivityViewModel(mApplication, mAuth, mFirestore);
        }
    }
}

