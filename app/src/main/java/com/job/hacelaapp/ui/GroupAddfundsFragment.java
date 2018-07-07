package com.job.hacelaapp.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.job.hacelaapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupAddfundsFragment extends BottomSheetDialogFragment {


    public static final String TAG = "ContrFrag";
    @BindView(R.id.contr_type)
    TextView contrType;
    @BindView(R.id.contr_groupname)
    TextView contrGroupname;
    @BindView(R.id.contr_textamount)
    TextView contrTextamount;
    @BindView(R.id.contr_editImg)
    ImageButton contrEditImg;
    @BindView(R.id.contr_amountinput)
    TextInputLayout contrAmountinput;
    @BindView(R.id.contr_contrbtn)
    MaterialButton contrContrbtn;
    Unbinder unbinder;

    public GroupAddfundsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_addfunds, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.contr_editImg)
    public void onContrEditImgClicked() {
    }

    @OnClick(R.id.contr_contrbtn)
    public void onContrContrbtnClicked() {
    }
}
