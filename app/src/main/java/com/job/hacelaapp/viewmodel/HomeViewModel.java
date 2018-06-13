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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.job.hacelaapp.appExecutor.DefaultExecutorSupplier;
import com.job.hacelaapp.appExecutor.Priority;
import com.job.hacelaapp.appExecutor.PriorityRunnable;
import com.job.hacelaapp.dataSource.GroupAccount;
import com.job.hacelaapp.dataSource.GroupMembers;
import com.job.hacelaapp.dataSource.Groups;
import com.job.hacelaapp.dataSource.UserBasicInfo;
import com.job.hacelaapp.dataSource.UsersAccount;
import com.job.hacelaapp.repository.FirebaseDocumentLiveData;
import com.job.hacelaapp.repository.FirebaseQueryLiveData;

import java.text.DecimalFormat;
import java.util.List;

import static com.job.hacelaapp.util.Constants.GROUPACCOUNTCOL;
import static com.job.hacelaapp.util.Constants.GROUPCOL;
import static com.job.hacelaapp.util.Constants.GROUPMEMBERCOL;
import static com.job.hacelaapp.util.Constants.USERSACCOUNTCOL;
import static com.job.hacelaapp.util.Constants.USERSCOL;

/**
 * Created by Job on Wednesday : 6/13/2018.
 */
public class HomeViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String currentUserId;
    private String groupId;

    public static final String TAG = "HomeVM";

    //few db references
    private DocumentReference userAccountRef;
    private CollectionReference groupMembersRef;
    private DocumentReference groupRef;
    private DocumentReference groupAccountRef;
    private DocumentReference usersRef;



    private MediatorLiveData<UsersAccount> usersAccountMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<List<GroupMembers>> membersListMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Groups> groupsMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<GroupAccount> groupAccountMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<UserBasicInfo> usersLiveData = new MediatorLiveData<>();

    public HomeViewModel(@NonNull Application application,FirebaseAuth mAuth, FirebaseFirestore mFirestore) {

        super(application);
        this.mAuth = mAuth;
        this.mFirestore = mFirestore;
        this.currentUserId = mAuth.getCurrentUser().getUid();

        //init db refs
        userAccountRef = mFirestore.collection(USERSACCOUNTCOL).document(currentUserId);
        groupMembersRef = mFirestore.collection(GROUPMEMBERCOL);
        usersRef = mFirestore.collection(USERSCOL).document(currentUserId);

        // Set up the MediatorLiveData to convert DataSnapshot objects into POJO objects
        workOnUsersLiveData();
        workOnUsersAccountLiveData();
        workOnUsersGroups(currentUserId);
    }

    private void workOnUsersLiveData(){
        FirebaseDocumentLiveData mData = new FirebaseDocumentLiveData(usersRef);
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

    private void workOnUsersGroups(String currentUserId) {
        Query query = groupMembersRef
                .whereEqualTo("userid", currentUserId)
                .whereEqualTo("ismember", true);

        FirebaseQueryLiveData mData = new FirebaseQueryLiveData(query);

        membersListMediatorLiveData.addSource(mData, new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(@Nullable final QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){

                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .submit(new Runnable() {
                                @Override
                                public void run() {
                                    membersListMediatorLiveData.postValue(queryDocumentSnapshots.toObjects(GroupMembers.class));
                                }
                            });
                }else {
                    membersListMediatorLiveData.setValue(null);
                }
            }
        });
    }

    private void workOnUsersAccountLiveData() {
        FirebaseDocumentLiveData mData = new FirebaseDocumentLiveData(userAccountRef);

        usersAccountMediatorLiveData.addSource(mData, new Observer<DocumentSnapshot>() {
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

    public String formatMyMoney(Double money){
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        Log.d(TAG, "formatMyMoney: "+formatter.format(money));
        return String.format("Ksh %,.2f", money);
        //return "Ksh "+formatter.format(money);
    }

    public void workOnGroupLiveData(String groupId){

        groupRef = mFirestore.collection(GROUPCOL).document(groupId);
        FirebaseDocumentLiveData mData = new FirebaseDocumentLiveData(groupRef);

        groupsMediatorLiveData.addSource(mData, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null){
                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                           .submit(new Runnable() {
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

    public void workOnGroupBalanceLiveData(String groupId){
        groupAccountRef = mFirestore.collection(GROUPACCOUNTCOL).document(groupId);

        FirebaseDocumentLiveData mData = new FirebaseDocumentLiveData(groupAccountRef);

        groupAccountMediatorLiveData.addSource(mData, new Observer<DocumentSnapshot>() {
            @Override
            public void onChanged(@Nullable final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null){
                    DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                            .submit(new Runnable() {
                                @Override
                                public void run() {
                                    // do some background work here at high priority.
                                    groupAccountMediatorLiveData.postValue(documentSnapshot.toObject(GroupAccount.class));
                                }
                            });
                }else {
                    groupAccountMediatorLiveData.setValue(null);
                }
            }
        });
    }

    public MediatorLiveData<UsersAccount> getUsersAccountMediatorLiveData() {
        return usersAccountMediatorLiveData;
    }

    public void setUsersAccountMediatorLiveData(UsersAccount usersAccountMediatorLiveData) {
        this.usersAccountMediatorLiveData.setValue(usersAccountMediatorLiveData);
    }

    public MediatorLiveData<List<GroupMembers>> getMembersListMediatorLiveData() {
        return membersListMediatorLiveData;
    }

    public void setMembersListMediatorLiveData(List<GroupMembers> membersListMediatorLiveData) {
        this.membersListMediatorLiveData.setValue(membersListMediatorLiveData);
    }

    public MediatorLiveData<Groups> getGroupsMediatorLiveData() {
        return groupsMediatorLiveData;
    }

    public void setGroupsMediatorLiveData(Groups groupsMediatorLiveData) {
        this.groupsMediatorLiveData.setValue(groupsMediatorLiveData);
    }

    public MediatorLiveData<GroupAccount> getGroupAccountMediatorLiveData() {
        return groupAccountMediatorLiveData;
    }

    public void setGroupAccountMediatorLiveData(GroupAccount groupAccountMediatorLiveData) {
        this.groupAccountMediatorLiveData.setValue(groupAccountMediatorLiveData);
    }

    public MediatorLiveData<UserBasicInfo> getUsersLiveData() {
        return usersLiveData;
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
            return (T) new HomeViewModel(mApplication, mAuth, mFirestore);
        }
    }
}
