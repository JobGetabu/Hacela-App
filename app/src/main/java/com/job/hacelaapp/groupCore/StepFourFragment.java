package com.job.hacelaapp.groupCore;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.job.hacelaapp.R;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;
import com.maltaisn.recurpicker.Recurrence;
import com.maltaisn.recurpicker.RecurrencePickerDialog;

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

    private View mRootView;
    private Recurrence recurrence;
    private DateFormat dateFormatShort;

    private Activity activity;
    private RecurrencePickerDialog picker;

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

        // Create date formats
        final DateFormat dateFormatLong = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.ENGLISH);  // Sun Dec 31, 2017
        dateFormatShort = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);  // 31-12-2017

        picker = new RecurrencePickerDialog();
        picker.setDateFormat(dateFormatShort, dateFormatLong);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView =  inflater.inflate(R.layout.fragment_step_four, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @OnClick(R.id.stepfour_fab)
    public void toStepFive(){
        createGroupViewModel.setPageNumber(4);
    }

    @OnClick({R.id.stepfour_continterval,R.id.stepfour_continterval_line})
    public void intervalPromptClick(){

        picker.setRecurrence(recurrence, System.currentTimeMillis());
        picker.show(activity.getFragmentManager(), "recur_picker");
    }
}
