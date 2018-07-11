package com.job.hacelaapp.hacelaCore;


import android.annotation.SuppressLint;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.job.hacelaapp.R;
import com.job.hacelaapp.appExecutor.DefaultExecutorSupplier;
import com.job.hacelaapp.common.UserTransViewHolder;
import com.job.hacelaapp.dataSource.UsersAccount;
import com.job.hacelaapp.dataSource.UsersProfile;
import com.job.hacelaapp.dataSource.UsersTransaction;
import com.job.hacelaapp.ui.GroupAddfundsFragment;
import com.job.hacelaapp.ui.PayFragment;
import com.job.hacelaapp.ui.WithdrawFragment;
import com.job.hacelaapp.util.OnSwipeTouchListener;
import com.job.hacelaapp.viewmodel.AccountViewModel;
import com.job.hacelaapp.viewmodel.NavigationViewModel;
import com.ramotion.foldingcell.FoldingCell;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.job.hacelaapp.util.Constants.GROUPACCOUNTCOL;
import static com.job.hacelaapp.util.Constants.GROUPCOL;
import static com.job.hacelaapp.util.Constants.USERSACCOUNTCOL;
import static com.job.hacelaapp.util.Constants.USERSPROFILECOL;
import static com.job.hacelaapp.util.Constants.USERSTRANSACTIONCOL;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.account_accounttype)
    TextView accountAccounttype;
    @BindView(R.id.account_balance)
    TextView accountAccountBalance;
    @BindView(R.id.account_imgleft)
    ImageButton accountImgleft;
    @BindView(R.id.account_imgright)
    ImageButton accountImgright;
    @BindView(R.id.account_translist)
    RecyclerView accountTranslist;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.account_fab_deposit)
    FloatingActionButton accountFabDeposit;
    @BindView(R.id.account_fab_withdraw)
    FloatingActionButton accountFabWithdraw;
    @BindView(R.id.account_fab_stats)
    FloatingActionButton accountFabStats;
    @BindView(R.id.account_fab_menu)
    FloatingActionButton fabMenu;
    @BindView(R.id.account_floating_layout)
    FloatingLayout accountFloatingLayout;
    @BindView(R.id.account_pay_sheet)
    LinearLayout bottomSheetViewgroup;
    @BindView(R.id.account_withdraw_sheet)
    LinearLayout withdrawSheetViewgroup;
    @BindView(R.id.account_contribute_sheet)
    LinearLayout contributeSheetViewgroup;
    @BindView(R.id.account_progress)
    ProgressBar accountProgress;
    @BindView(R.id.account_cash_card)
    MaterialCardView cardCash;
    @BindView(R.id.account_refresh)
    SwipeRefreshLayout accountRefresh;


    Unbinder unbinder;

    private static final String TAG = "AccFrag";

    private View mRootView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    private PayFragment payFragment;
    private WithdrawFragment withdrawFragment;
    private GroupAddfundsFragment contributeFragment;
    private BottomSheetBehavior paySheetBehavior;
    private BottomSheetBehavior withdrawSheetBehavior;
    private BottomSheetBehavior contributeSheetBehavior;

    private AccountViewModel model;
    private NavigationViewModel navModel;
    private FirestoreRecyclerAdapter adapter;

    final List<String> listGroups = new ArrayList<>();
    private ListIterator<String> iterator;
    private String currentGroupId;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_account, container, false);


        unbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        floatLayListener();

        payFragment = new PayFragment();
        withdrawFragment = new WithdrawFragment();
        contributeFragment = new GroupAddfundsFragment();


        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        //read db data
        AccountViewModel.Factory factory = new AccountViewModel.Factory(
                getActivity().getApplication(), mAuth, mFirestore);

        model = ViewModelProviders.of(getActivity(), factory)
                .get(AccountViewModel.class);

        navModel = ViewModelProviders.of(getActivity()).get(NavigationViewModel.class);

        //setup ui observers
        setUpCashUi();
        setUpUI(getString(R.string.personal_account));
        navModelObserver();

        currentGroupId = getString(R.string.personal_account);
        cardCashSwiperListener();


        paySheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        paySheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        withdrawSheetBehavior = BottomSheetBehavior.from(withdrawSheetViewgroup);
        withdrawSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        contributeSheetBehavior = BottomSheetBehavior.from(contributeSheetViewgroup);
        contributeSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        setUpTransactionList();

        //set up navigator
        listGroups.add(getString(R.string.personal_account));
        userGroups();

        //swipe refresh
        int a = getResources().getColor(R.color.payGreen);
        int b = getResources().getColor(R.color.colorAppBarDark);
        int c = getResources().getColor(R.color.colorAccent);
        accountRefresh.setColorSchemeColors(a,b,c);
        accountRefresh.setOnRefreshListener(onRefreshListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void cardCashSwiperListener() {
        cardCash.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {

                navLeft();
            }

            @Override
            public void onSwipeRight() {

                navRight();
            }
        });
    }

    private void navRight() {
        if (!listGroups.isEmpty() && iterator != null) {

            if (iterator.hasNext()) {
                accountImgright.setVisibility(View.VISIBLE);
                String id = iterator.next();
                model.setCurrentGroupIdMediatorLiveData(id);
                currentGroupId = id;
                setUpUI(id);

                if (id.equals(getString(R.string.personal_account))) {
                    changeFabActions(false);
                } else {
                    changeFabActions(true);
                }
            } else {
                accountImgright.setVisibility(View.INVISIBLE);
            }
            navImages();
        }
    }

    private void navLeft() {
        if (!listGroups.isEmpty() && iterator != null) {

            if (iterator.hasPrevious()) {
                accountImgleft.setVisibility(View.VISIBLE);

                String id = iterator.previous();
                model.setCurrentGroupIdMediatorLiveData(id);
                currentGroupId = id;
                setUpUI(id);

                if (id.equals(getString(R.string.personal_account))) {
                    changeFabActions(false);
                } else {
                    changeFabActions(true);
                }
            } else {
                accountImgleft.setVisibility(View.INVISIBLE);
            }
            navImages();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.account_imgleft)
    public void onAccountImgleftClicked() {
        navLeft();
    }


    @OnClick(R.id.account_imgright)
    public void onAccountImgrightClicked() {
        navRight();
    }


    private void navImages() {
        if (iterator.hasNext()) {
            accountImgright.setVisibility(View.VISIBLE);
        } else {
            accountImgright.setVisibility(View.INVISIBLE);
        }

        if (iterator.hasPrevious()) {
            accountImgleft.setVisibility(View.VISIBLE);
        } else {
            accountImgleft.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.account_fab_deposit)
    public void onAccountFabDepositClicked() {
        if (accountFabDeposit.getFabText().equals(getString(R.string.contribute))) {
            contributeFragment.show(getActivity().getSupportFragmentManager(), GroupAddfundsFragment.TAG);
            contributeSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {

            payFragment.show(getActivity().getSupportFragmentManager(), PayFragment.TAG);
            paySheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @OnClick(R.id.account_fab_withdraw)
    public void onAccountFabWithdrawClicked() {
        withdrawFragment.show(getActivity().getSupportFragmentManager(), PayFragment.TAG);
        withdrawSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @OnClick(R.id.account_fab_stats)
    public void onAccountFabStatsClicked() {
        Toast.makeText(getContext(), "stats ", Toast.LENGTH_SHORT).show();

    }

    private void floatLayListener() {
        accountFloatingLayout.setOnMenuExpandedListener(new FloatingLayout.OnMenuExpandedListener() {
            @Override
            public void onMenuExpanded() {
                // Do stuff when expanded...
                accountFloatingLayout.setBackgroundColor(getContext().getResources().getColor(R.color.fablayout));
            }

            @Override
            public void onMenuCollapsed() {
                // Do stuff when collapsed...
                accountFloatingLayout.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
            }
        });
    }

    //to reflect payments to personal accounts
    private void setUpCashUi() {
        MediatorLiveData<UsersAccount> data = model.getUsersAccountMediatorLiveData();

        data.observe(this, new Observer<UsersAccount>() {
            @Override
            public void onChanged(@Nullable UsersAccount usersAccount) {
                if (currentGroupId != null) {
                    if (currentGroupId.equals(getString(R.string.personal_account))) {
                        if (usersAccount != null) {
                            String balance = model.formatMyMoney(usersAccount.getBalance());
                            accountAccountBalance.setText(balance);
                        }
                    } else {
                        accountAccountBalance.setText("Ksh -:-");
                    }
                }
            }
        });
    }

    private void setUpTransactionList() {

        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        accountTranslist.setLayoutManager(linearLayoutManager);
        accountTranslist.setHasFixedSize(true);

        // Create a reference to the members collection
        final CollectionReference transRef = mFirestore.collection(USERSTRANSACTIONCOL);
        final Query query = transRef
                .whereEqualTo("userid", mCurrentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<UsersTransaction> options = new FirestoreRecyclerOptions.Builder<UsersTransaction>()
                .setQuery(query, UsersTransaction.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UsersTransaction, UserTransViewHolder>(options) {
            @NonNull
            @Override
            public UserTransViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item

                /*
                final View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ucell, parent, false);

                */

                LayoutInflater vi = LayoutInflater.from(getContext());
                final FoldingCell cell = (FoldingCell) vi.inflate(R.layout.ucell, parent, false);

                // attach click listener to folding cell
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cell.toggle(false);
                    }
                });

                return new UserTransViewHolder(cell);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserTransViewHolder holder, int position, @NonNull UsersTransaction model) {

                holder.init(getContext(), model.getTransactionid());
                holder.setUpUi(model);

            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                Log.e(TAG, "onError: ", e);
            }


        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        accountTranslist.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    private void changeFabActions(boolean isGroup) {

        if (isGroup) {
            fabMenu.setFabIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_groups));
            fabMenu.setFabIconColor(ContextCompat.getColor(getContext(), android.R.color.white));
            fabMenu.setFabColor(ContextCompat.getColor(getContext(), R.color.fabColor));

            accountFabDeposit.setFabText(getString(R.string.contribute));
            accountFabWithdraw.setFabText("Request Pay");
            accountFabStats.setFabText("Group Stats");
        } else {

            fabMenu.setFabIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_money_action));
            fabMenu.setFabIconColor(ContextCompat.getColor(getContext(), android.R.color.white));
            fabMenu.setFabColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

            accountFabWithdraw.setFabText(getString(R.string.withdraw));
            accountFabDeposit.setFabText(getString(R.string.deposit));
            accountFabStats.setFabText(getString(R.string.stats));
        }
    }

    //get all groups user is in
    private void userGroups() {

        accountProgress.setVisibility(View.VISIBLE);

        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.SERVER;
        DocumentReference profRef = mFirestore.collection(USERSPROFILECOL).document(mCurrentUser.getUid());

        profRef.get(source)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {

                        accountProgress.setVisibility(View.GONE);

                        DefaultExecutorSupplier.getInstance().forMainThreadTasks()
                                .execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        // do some Main Thread work here.
                                        UsersProfile usersProfile = documentSnapshot.toObject(UsersProfile.class);

                                        if (usersProfile != null && usersProfile.getGroups() != null) {
                                            //go ahead for groups

                                            Map<String, Boolean> groups = usersProfile.getGroups();

                                            Set s1 = groups.entrySet();//to get whole entrys
                                            for (Object aS1 : s1) {
                                                Map.Entry m = (Map.Entry) aS1;//to get next entry (and casting required because values are object type)
                                                //Entry is inner interface of Map interface

                                                if (m.getValue().equals(true)) {
                                                    String gId = (String) m.getKey();

                                                    listGroups.add(gId);

                                                }
                                            }
                                            iterator = listGroups.listIterator();
                                            navImages();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        accountProgress.setVisibility(View.GONE);
                    }
                });

    }

    private void setUpUI(String id) {

        Source source = Source.SERVER;
        accountAccountBalance.setText("Ksh -:-");
        accountAccounttype.setText("-:-");

        if (id.equals(getString(R.string.personal_account))) {

            accountAccounttype.setText(getString(R.string.personal_account));
            mFirestore.collection(USERSACCOUNTCOL).document(mCurrentUser.getUid()).get(source)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String balance = model.formatMyMoney(task.getResult().getDouble("balance"));
                                accountAccountBalance.setText(balance);

                            } else {
                                accountAccountBalance.setText("Ksh -:-");
                            }
                        }
                    });

        } else {
            mFirestore.collection(GROUPCOL).document(id).get(source)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String type = task.getResult().getString("displayname");
                                accountAccounttype.setText(type.toUpperCase() + " ACCOUNT");

                            } else {
                                accountAccounttype.setText("-:-");

                            }
                        }
                    });
            mFirestore.collection(GROUPACCOUNTCOL).document(id).get(source)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                String balance = model.formatMyMoney(task.getResult().getDouble("balance"));
                                accountAccountBalance.setText(balance);

                            } else {
                                accountAccountBalance.setText("Ksh -:-");

                            }
                        }
                    });
        }
    }

    private void setAccountRefresh(String id){
        //swipe refresh becomes visible
        if (currentGroupId == null || currentGroupId.isEmpty()){
            accountRefresh.setRefreshing(false);
            return;
        }

        Source source = Source.SERVER;
        accountRefresh.setRefreshing(true);


        if (id.equals(getString(R.string.personal_account))) {

            accountAccounttype.setText(getString(R.string.personal_account));
            mFirestore.collection(USERSACCOUNTCOL).document(mCurrentUser.getUid()).get(source)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String balance = model.formatMyMoney(task.getResult().getDouble("balance"));
                                accountAccountBalance.setText(balance);
                                accountRefresh.setRefreshing(false);

                            } else {
                                accountAccountBalance.setText("Ksh -:-");
                                accountRefresh.setRefreshing(false);
                            }
                        }
                    });

        } else {
            mFirestore.collection(GROUPCOL).document(id).get(source)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String type = task.getResult().getString("displayname");
                                accountAccounttype.setText(type.toUpperCase() + " ACCOUNT");
                                accountRefresh.setRefreshing(false);

                            } else {
                                accountAccounttype.setText("-:-");
                                accountRefresh.setRefreshing(false);

                            }
                        }
                    });
            mFirestore.collection(GROUPACCOUNTCOL).document(id).get(source)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                String balance = model.formatMyMoney(task.getResult().getDouble("balance"));
                                accountAccountBalance.setText(balance);
                                accountRefresh.setRefreshing(false);

                            } else {
                                accountAccountBalance.setText("Ksh -:-");
                                accountRefresh.setRefreshing(false);
                            }
                        }
                    });

        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            setAccountRefresh(currentGroupId);
        }
    };

    private void navModelObserver(){
        navModel.getRefreshData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                setAccountRefresh(currentGroupId);
            }
        });
    }
}
