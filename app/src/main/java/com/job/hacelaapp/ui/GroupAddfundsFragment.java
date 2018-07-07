package com.job.hacelaapp.ui;


import android.app.Fragment;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.job.hacelaapp.R;
import com.job.hacelaapp.viewmodel.AccountViewModel;
import com.job.hacelaapp.viewmodel.NavigationViewModel;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.job.hacelaapp.util.Constants.GROUPCOL;
import static com.job.hacelaapp.util.Constants.HACELAUTILCOL;

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


    private AccountViewModel model;
    private NavigationViewModel navigationViewModel;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;
    private String currentGroupId;

    //starter progress
    private SweetAlertDialog pDialog;
    private NoInternetDialog noInternetDialog;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        setUpNoNetDialogue();

        navigationViewModel = ViewModelProviders.of(getActivity()).get(NavigationViewModel.class);
        //read db data
        AccountViewModel.Factory factory = new AccountViewModel.Factory(
                this.getActivity().getApplication(), mAuth, mFirestore);

        model = ViewModelProviders.of(getActivity(), factory)
                .get(AccountViewModel.class);

        //setup ui observers
        setUpUi();
        currentGroupIdObserver();
    }

    private void currentGroupIdObserver(){
        MediatorLiveData<String> data = model.getCurrentGroupIdMediatorLiveData();

        data.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(s != null){
                    currentGroupId  = s;
                }
            }
        });
    }

    private void setUpUi() {
        //real time data : current time|day only
        final Source source = Source.SERVER;

        final String key = "time";

        Map<String, Object> timeMap = new HashMap<>();
        timeMap.put("currenttime", FieldValue.serverTimestamp());
        //fetch current time : server
        mFirestore.collection(HACELAUTILCOL).document(key)
                .set(timeMap)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFirestore.collection(HACELAUTILCOL).document(key)
                                .get(source)
                                .addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        Timestamp currentTimestamp = documentSnapshot.getTimestamp("currenttime");
                                        Date date = null;

                                        if (currentTimestamp != null) {
                                            date = currentTimestamp.toDate();
                                            Calendar c = Calendar.getInstance();
                                            c.setTime(date);
                                        }

                                        Log.d(TAG, "onSuccess: "+currentGroupId);
                                        if (currentGroupId != null) {

                                            setUpGroupNameUi(currentGroupId);
                                        }
                                    }
                                });
                    }
                });
    }

    private void setUpGroupNameUi(String currentGroupId) {
        mFirestore.collection(GROUPCOL).document(currentGroupId)
                .get()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String groupname = documentSnapshot.getString("groupname");
                        contrGroupname.setText(groupname);
                    }
                });
    }

    private void setUpNoNetDialogue() {
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setBgGradientOrientation(45)
                .setCancelable(true)
                .setBgGradientStart(getResources().getColor(R.color.app_gradient_start))
                .setBgGradientEnd(getResources().getColor(R.color.app_gradient_end))
                .build();
    }

    private boolean validateOnPay() {

        boolean valid = true;

        String am = contrAmountinput.getEditText().getText().toString();

        if (am.isEmpty() || am.equals("0")) {
            contrAmountinput.setError("Amount is not valid");

            contrTextamount.setVisibility(View.GONE);
            contrEditImg.setVisibility(View.GONE);
            contrAmountinput.setVisibility(View.VISIBLE);

            valid = false;
        } else {
            contrTextamount.setVisibility(View.VISIBLE);
            contrEditImg.setVisibility(View.VISIBLE);
            contrAmountinput.setVisibility(View.GONE);
            contrAmountinput.setError(null);
        }

        if (!am.isEmpty()) {
            if (Double.parseDouble(am) < 10) {
                contrAmountinput.setError("Amount must be greater than 10");

                contrTextamount.setVisibility(View.GONE);
                contrEditImg.setVisibility(View.GONE);
                contrAmountinput.setVisibility(View.VISIBLE);

                valid = false;
            } else {
                contrTextamount.setVisibility(View.VISIBLE);
                contrEditImg.setVisibility(View.VISIBLE);
                contrAmountinput.setVisibility(View.GONE);
                contrAmountinput.setError(null);
            }
        }

        return valid;
    }

    public String formatMyMoney(Double money) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        Log.d(TAG, "formatMyMoney: " + formatter.format(money));
        return String.format("KES %,.0f", money);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {

        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

        if (noInternetDialog != null) {
            noInternetDialog.onDestroy();
        }

        super.onDestroy();
    }


    @OnClick({R.id.contr_editImg, R.id.contr_textamount})
    public void onContrEditIcon() {
        contrAmountinput.setVisibility(View.VISIBLE);
        String am = contrTextamount.getText().toString();
        String newstr = am.replaceAll("KES ", "")
                .replaceAll("/-", "")
                .replaceAll(",", "");
        contrAmountinput.getEditText().setText(newstr);
        contrTextamount.setVisibility(View.GONE);
        contrEditImg.setVisibility(View.GONE);
    }

    @OnClick({R.id.main_kjhgf, R.id.textView3, R.id.contr_type,
            R.id.contr_groupname, R.id.asdsgroup, R.id.sdesgroup,
            R.id.textView6contr})
    public void onHideContrInputField() {
        contrAmountinput.setVisibility(View.GONE);
        contrTextamount.setVisibility(View.VISIBLE);
        contrEditImg.setVisibility(View.VISIBLE);
        String am = contrAmountinput.getEditText().getText().toString();

        double temp = 0;
        try {
            temp = Double.parseDouble(am);
        } catch (Exception e) {
            Log.e(TAG, "onHideInputField: ", e);
        }
        contrTextamount.setText(formatMyMoney(temp) + "/-");
    }

    @OnClick(R.id.contr_contrbtn)
    public void onContrContrbtnClicked() {
    }
}
