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
import com.job.hacelaapp.appExecutor.Priority;
import com.job.hacelaapp.appExecutor.PriorityRunnable;
import com.job.hacelaapp.dataSource.UserAuthInfo;
import com.job.hacelaapp.dataSource.UserBasicInfo;
import com.job.hacelaapp.dataSource.UsersProfile;
import com.job.hacelaapp.repository.FirebaseQueryLiveData;

/**
 * Created by Job on Sunday : 4/22/2018.
 */
public class DetailsEditActivityViewModel extends AndroidViewModel {


    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String currentUserId;

    //few db references
    private DocumentReference USERSREF;
    private DocumentReference USERSAUTHREF;
    private DocumentReference USERSPROFILE;


    public static final String TAG = "DetailsEditVM";


    private MediatorLiveData<UserBasicInfo> usersLiveData = new MediatorLiveData<>();
    private MediatorLiveData<UserAuthInfo> userAuthInfoMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<UsersProfile> usersProfileMediatorLiveData = new MediatorLiveData<>();


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

        // Set up the MediatorLiveData to convert DataSnapshot objects into POJO objects

        workOnUsersLiveData();
        workOnUserAuthInfoMediatorLiveData();
        workOnUsersProfileMediatorLiveData();
    }

    private void workOnUsersLiveData(){
        FirebaseQueryLiveData mData = new FirebaseQueryLiveData(USERSREF);
        usersLiveData.addSource(mData, new Observer<DocumentSnapshot>() {
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

    private void workOnUserAuthInfoMediatorLiveData(){
        FirebaseQueryLiveData mData = new FirebaseQueryLiveData(USERSAUTHREF);
        userAuthInfoMediatorLiveData.addSource(mData, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null){

                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .execute(new Runnable() {
                                @Override
                                public void run() {
                                    // do some background work here.
                                    userAuthInfoMediatorLiveData.postValue(documentSnapshot.toObject(UserAuthInfo.class));
                                }
                            });

                }else {
                    userAuthInfoMediatorLiveData.setValue(null);
                }
            }
        });
    }

    private void workOnUsersProfileMediatorLiveData(){
        FirebaseQueryLiveData mData = new FirebaseQueryLiveData(USERSPROFILE);
        usersProfileMediatorLiveData.addSource(mData, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null){

                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .execute(new Runnable() {
                                @Override
                                public void run() {
                                    // do some background work here.
                                    usersProfileMediatorLiveData.postValue(documentSnapshot.toObject(UsersProfile.class));
                                }
                            });

                }else {
                    usersProfileMediatorLiveData.setValue(null);
                }
            }
        });
    }


    public MediatorLiveData<UserBasicInfo> getUsersLiveData() {
        return usersLiveData;
    }

    public MediatorLiveData<UserAuthInfo> getUserAuthInfoMediatorLiveData() {
        return userAuthInfoMediatorLiveData;
    }

    public MediatorLiveData<UsersProfile> getUsersProfileMediatorLiveData() {
        return usersProfileMediatorLiveData;
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

