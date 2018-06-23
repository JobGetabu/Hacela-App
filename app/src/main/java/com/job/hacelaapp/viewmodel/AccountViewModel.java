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
import com.job.hacelaapp.dataSource.UserAuthInfo;
import com.job.hacelaapp.dataSource.UsersAccount;
import com.job.hacelaapp.repository.FirebaseDocumentLiveData;

import java.text.DecimalFormat;

import static com.job.hacelaapp.util.Constants.USERSACCOUNTCOL;
import static com.job.hacelaapp.util.Constants.USERSAUTHCOL;

/**
 * Created by Job on Saturday : 6/16/2018.
 */
public class AccountViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String currentUserId;

    public static final String TAG = "AccountVM";

    //few db references
    private DocumentReference userAccountRef;
    private DocumentReference groupRef;
    private DocumentReference groupAccountRef;
    private DocumentReference userAuthRef;

    //live datas
    private FirebaseDocumentLiveData mDataUsersAccountLiveData;
    FirebaseDocumentLiveData mDataUsersAuthLiveData;


    //mediators
    private MediatorLiveData<UsersAccount> usersAccountMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<UserAuthInfo> usersAuthMediatorLiveData = new MediatorLiveData<>();

    public AccountViewModel(@NonNull Application application,FirebaseAuth mAuth, FirebaseFirestore mFirestore) {
        super(application);
        this.mAuth = mAuth;
        this.mFirestore = mFirestore;
        this.currentUserId = mAuth.getCurrentUser().getUid();

        //init db refs
        userAccountRef = mFirestore.collection(USERSACCOUNTCOL).document(currentUserId);
        userAuthRef = mFirestore.collection(USERSAUTHCOL).document(currentUserId);

        //init livedatas
        mDataUsersAccountLiveData = new FirebaseDocumentLiveData(userAccountRef);
        mDataUsersAuthLiveData = new FirebaseDocumentLiveData(userAuthRef);


        // Set up the MediatorLiveData to convert DataSnapshot objects into POJO objects
        workOnUsersAccountLiveData();
        workOnUsersAuthLiveData();

    }

    private void workOnUsersAccountLiveData() {
        usersAccountMediatorLiveData.addSource(mDataUsersAccountLiveData, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null){
                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .submit(new PriorityRunnable(Priority.HIGH) {
                                @Override
                                public void run() {
                                    // do some background work here at high priority.
                                    usersAccountMediatorLiveData.postValue(documentSnapshot.toObject(UsersAccount.class));
                                }
                            });
                }else {
                    usersAccountMediatorLiveData.setValue(null);
                }
            }
        });
    }

    private void workOnUsersAuthLiveData(){

        usersAuthMediatorLiveData.addSource(mDataUsersAuthLiveData, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null){

                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .submit(new PriorityRunnable(Priority.HIGH) {
                                @Override
                                public void run() {
                                    // do some background work here at high priority.
                                    usersAuthMediatorLiveData.postValue(documentSnapshot.toObject(UserAuthInfo.class));
                                }
                            });
                }else {
                    usersAuthMediatorLiveData.setValue(null);
                }
            }
        });
    }

    public String formatMyMoney(Double money){
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        Log.d(TAG, "formatMyMoney: "+formatter.format(money));
        return String.format("Ksh %,.2f", money);
        //return "Ksh "+formatter.format(money);
    }

    public MediatorLiveData<UsersAccount> getUsersAccountMediatorLiveData() {
        return usersAccountMediatorLiveData;
    }

    public void setUsersAccountMediatorLiveData(UsersAccount usersAccountMediatorLiveData) {
        this.usersAccountMediatorLiveData.setValue(usersAccountMediatorLiveData);
    }

    public MediatorLiveData<UserAuthInfo> getUsersAuthMediatorLiveData() {
        return usersAuthMediatorLiveData;
    }

    public void setUsersAuthMediatorLiveData(UserAuthInfo usersAuthMediatorLiveData) {
        this.usersAuthMediatorLiveData.setValue(usersAuthMediatorLiveData);
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
            return (T) new AccountViewModel(mApplication, mAuth, mFirestore);
        }
    }
}
