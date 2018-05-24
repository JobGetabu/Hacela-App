package com.job.hacelaapp.groupCore;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.GroupDescription;
import com.job.hacelaapp.dataSource.Step4OM;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFourFragment extends Fragment {


    @BindView(R.id.stepfour_continterval)
    AppCompatTextView mContinterval;
    @BindView(R.id.stepfour_continterval_line)
    View mContintervalLine;
    @BindView(R.id.stepfour_layoutsavings)
    Group savingsvisibility;
    @BindView(R.id.stepfour_rlDateTimeRecurrenceInfo)
    RelativeLayout rlRecurrence;
    @BindView(R.id.s4_tvRecurrenceOption)
    TextView tvRecurOption;
    @BindView(R.id.s4_tvRecurrenceRule)
    TextView tvRecurRule;

    @BindView(R.id.stepfour_contamount)
    TextInputLayout contamount;
    @BindView(R.id.stepfour_payout)
    TextInputLayout contPayout;
    @BindView(R.id.stepfour_savings)
    TextInputLayout contSavings;



    private String contInterval="";
    private View mRootView;
    private DateFormat dateFormatShort;
    private Activity activity;


    private static final String TAG = "stepfour";

    private CreateGroupViewModel createGroupViewModel;

    public StepFourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createGroupViewModel = ViewModelProviders.of(getActivity()).get(CreateGroupViewModel.class);

        activity = this.getActivity();

        //savings visibility
        showSavings();

        // Create date formats
        final DateFormat dateFormatLong = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.ENGLISH);  // Sun Dec 31, 2017
        dateFormatShort = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);  // 31-12-2017


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_step_four, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    private void showSavings() {
        createGroupViewModel.getGroupDescriptionMutableLiveData().observe(this, new Observer<GroupDescription>() {
            @Override
            public void onChanged(@Nullable GroupDescription groupDescription) {
                if (groupDescription != null) {
                    String typeofgroup = groupDescription.getTypeofgroup();
                    if (typeofgroup.equals("Merry-go-Round"))
                        savingsvisibility.setVisibility(View.GONE);
                    else
                        savingsvisibility.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @OnClick(R.id.stepfour_fab)
    public void toStepFive() {

        if (validate()){

            //save Step4OM
            Step4OM step4OM = new Step4OM();
            step4OM.setAmount(Double.parseDouble(contamount.getEditText().getText().toString().trim()));
            step4OM.setPayout(Double.parseDouble(contPayout.getEditText().getText().toString().trim()));
            step4OM.setIntervalPeriod(contInterval);
            step4OM.setContPeriod(countDays(contInterval));

            if (contSavings.getVisibility() == View.VISIBLE){
                step4OM.setSavings(Double.parseDouble(contSavings.getEditText().getText().toString()));
            }
            //TODO calculate period interval from choices
            createGroupViewModel.setStep4OMMutableLiveData(step4OM);
            createGroupViewModel.setPageNumber(4);
        }
    }

    @OnClick({R.id.stepfour_continterval, R.id.stepfour_continterval_line})
    public void intervalPromptClick() {

        new AlertDialog.Builder(getContext())
                .setTitle("REPEAT OPTIONS")
                .setSingleChoiceItems(R.array.group_interval, 0, null)
                .setPositiveButton(R.string.choose, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        Object selectedItem = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition);
                        // Do something useful with the position of the selected radio button

                        if (!selectedItem.toString().equals("Does not repeat")){
                            rlRecurrence.setVisibility(View.VISIBLE);
                            tvRecurOption.setText("Recurrence Option: "+selectedItem.toString());
                              tvRecurRule.setText("Recurrence Rule  : "+selectedItem.toString());

                            contInterval = selectedItem.toString();
                            //todo register changes
                        }else if(selectedItem.toString().equals("Does not repeat")) {
                            rlRecurrence.setVisibility(View.GONE);
                            tvRecurOption.setText("Recurrence Option: ");
                              tvRecurRule.setText("Recurrence Rule  : ");

                            contInterval = "";
                        }
                    }
                })
                .show();
    }

    private int countDays(String repeatoptions){

        switch (repeatoptions) {
            case "Does not repeat":
                return 0;
            case "Every day":
                return 1;
            case "Every week":
                return 7;
            case "Every month":
                return 30;
            case "Every year":
                return 365;
            case "Once Every 2 weeks":
                return 14;
            case "Once Every 2 months":
                return 60;
            case "Once Every 3 months":
                return 90;
            case "Once Every 6 months":
                return 180;
        }
        return 0;
    }

    public boolean validate() {
        boolean valid = true;

        String amount = contamount.getEditText().getText().toString();
        String payout = contPayout.getEditText().getText().toString();
        String saving = contSavings.getEditText().getText().toString();
        String interval = contInterval;


        if (amount.isEmpty() | !TextUtils.isDigitsOnly(amount)) {
            contamount.setError("enter a valid number");
            valid = false;
        } else {
            contamount.setError(null);
        }

        if (payout.isEmpty() | !TextUtils.isDigitsOnly(payout)) {
            contPayout.setError("enter a valid number");
            valid = false;
        } else {
            contPayout.setError(null);
        }

       if (contSavings.getVisibility() == View.VISIBLE){
           if (saving.isEmpty() | !TextUtils.isDigitsOnly(saving)) {
               contSavings.setError("enter a valid number");
               valid = false;
           } else {
               contSavings.setError(null);
           }
       }

        if (interval.isEmpty()) {
            Toast.makeText(getContext(), "Select Contribution Interval", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }
}
