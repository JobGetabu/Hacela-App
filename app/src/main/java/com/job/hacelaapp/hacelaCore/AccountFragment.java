package com.job.hacelaapp.hacelaCore;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.job.hacelaapp.R;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
    Unbinder unbinder;
    private View mRootView;

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
        Toast.makeText(getContext(), "deposit ", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.account_fab_withdraw)
    public void onAccountFabWithdrawClicked() {
        Toast.makeText(getContext(), "withdraw ", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.account_fab_stats)
    public void onAccountFabStatsClicked() {
        Toast.makeText(getContext(), "stats ", Toast.LENGTH_SHORT).show();
    }
}
