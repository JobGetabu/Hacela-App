package com.job.hacelaapp.groupCore;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.GroupDescription;
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
        createGroupViewModel.setPageNumber(4);
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

                            //todo register changes
                        }else if(selectedItem.toString().equals("Does not repeat")) {
                            rlRecurrence.setVisibility(View.GONE);
                            tvRecurOption.setText("Recurrence Option: ");
                              tvRecurRule.setText("Recurrence Rule  : ");
                        }
                    }
                })
                .show();
    }
}
