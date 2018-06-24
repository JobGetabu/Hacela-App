package com.job.hacelaapp.hacelaCore;


import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.job.hacelaapp.R;
import com.job.hacelaapp.common.UserTransViewHolder;
import com.job.hacelaapp.dataSource.UsersAccount;
import com.job.hacelaapp.dataSource.UsersTransaction;
import com.job.hacelaapp.ui.PayFragment;
import com.job.hacelaapp.ui.WithdrawFragment;
import com.job.hacelaapp.viewmodel.AccountViewModel;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.job.hacelaapp.util.Constants.USERSTRANSACTIONCOL;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.account_accounttype)
    TextView accountAccounttype;
    @BindView(R.id.account_account_balance)
    TextView accountAccountBalance;
    @BindView(R.id.account_imgleft)
    ImageButton accountImgleft;
    @BindView(R.id.account_imgright)
    ImageButton accountImgright;
    @BindView(R.id.account_translist)
    RecyclerView accountTranslist;
    @BindView(R.id.account_swiperefresh)
    SwipeRefreshLayout accountSwiperefresh;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.account_fab_deposit)
    FloatingActionButton accountFabDeposit;
    @BindView(R.id.account_fab_withdraw)
    FloatingActionButton accountFabWithdraw;
    @BindView(R.id.account_fab_stats)
    FloatingActionButton accountFabStats;
    @BindView(R.id.account_floating_layout)
    FloatingLayout accountFloatingLayout;
    @BindView(R.id.account_pay_sheet)
    LinearLayout bottomSheetViewgroup;
    @BindView(R.id.account_withdraw_sheet)
    LinearLayout withdrawSheetViewgroup;

    Unbinder unbinder;

    private static final String TAG = "AccFrag";

    private View mRootView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    private PayFragment payFragment;
    private WithdrawFragment withdrawFragment;
    private BottomSheetBehavior paySheetBehavior;
    private BottomSheetBehavior withdrawSheetBehavior;
    private AccountViewModel model;
    private FirestoreRecyclerAdapter adapter;

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



        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        //read db data
        AccountViewModel.Factory factory = new AccountViewModel.Factory(
                getActivity().getApplication(), mAuth, mFirestore);

        model = ViewModelProviders.of(this, factory)
                .get(AccountViewModel.class);

        //setup ui observers
        setUpCashUi();

        paySheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        paySheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        withdrawSheetBehavior = BottomSheetBehavior.from(withdrawSheetViewgroup);
        withdrawSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        setUpTransactionList();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.account_imgleft)
    public void onAccountImgleftClicked() {
    }

    @OnClick(R.id.account_imgright)
    public void onAccountImgrightClicked() {
    }

    @OnClick(R.id.account_fab_deposit)
    public void onAccountFabDepositClicked() {
        payFragment.show(getActivity().getSupportFragmentManager(), PayFragment.TAG);
        paySheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick(R.id.account_fab_withdraw)
    public void onAccountFabWithdrawClicked() {
        withdrawFragment.show(getActivity().getSupportFragmentManager(), PayFragment.TAG);
        withdrawSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);    }

    @OnClick(R.id.account_fab_stats)
    public void onAccountFabStatsClicked() {
        Toast.makeText(getContext(), "stats ", Toast.LENGTH_SHORT).show();

    }

    private void floatLayListener() {
        accountFloatingLayout.setOnMenuExpandedListener(new FloatingLayout.OnMenuExpandedListener() {
            @Override
            public void onMenuExpanded() {
                // Do stuff when expanded...
            }

            @Override
            public void onMenuCollapsed() {
                // Do stuff when collapsed...
            }
        });
    }

    private void setUpCashUi() {
        MediatorLiveData<UsersAccount> data = model.getUsersAccountMediatorLiveData();

        data.observe(this, new Observer<UsersAccount>() {
            @Override
            public void onChanged(@Nullable UsersAccount usersAccount) {
                if (usersAccount != null) {

                    String balance = model.formatMyMoney(usersAccount.getBalance());
                    accountAccountBalance.setText(balance);
                } else {
                    accountAccountBalance.setText("Ksh 0.00");
                }
            }
        });
    }

    private void setUpTransactionList(){

        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        accountTranslist.setLayoutManager(linearLayoutManager);

        // Create a reference to the members collection
        final CollectionReference transRef = mFirestore.collection(USERSTRANSACTIONCOL);
        final Query query = transRef
                .whereEqualTo("userid",mCurrentUser.getUid())
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<UsersTransaction> options = new FirestoreRecyclerOptions.Builder<UsersTransaction>()
                .setQuery(query, UsersTransaction.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UsersTransaction,UserTransViewHolder>(options) {
            @NonNull
            @Override
            public UserTransViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ucell, parent, false);

                return new UserTransViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserTransViewHolder holder, int position, @NonNull UsersTransaction model) {
                //for todo
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

}
