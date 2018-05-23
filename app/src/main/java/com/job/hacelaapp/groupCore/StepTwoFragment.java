package com.job.hacelaapp.groupCore;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.job.hacelaapp.R;
import com.job.hacelaapp.viewmodel.CreateGroupViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepTwoFragment extends Fragment {

    private static final int MAX_WORDS = 2;
    @BindView(R.id.steptwo_fulname)
    TextInputLayout mGrpfulName;
    @BindView(R.id.steptwo_displayname)
    TextInputLayout mGrpDisName;

    private View mRootView;
    private CreateGroupViewModel createGroupViewModel;


    public StepTwoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createGroupViewModel = ViewModelProviders.of(getActivity()).get(CreateGroupViewModel.class);


        mGrpDisName.getEditText().addTextChangedListener(disNameTextWatcher);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView= inflater.inflate(R.layout.fragment_step_two, container, false);
        ButterKnife.bind(this,mRootView);

        return mRootView;
    }

    @OnClick(R.id.steptwo_fab)
    public void toStepThree(){
        if (validate()){

            createGroupViewModel.setGroupFullName(mGrpfulName.getEditText().getText().toString().trim());
            createGroupViewModel.setGroupDisplayName(mGrpDisName.getEditText().getText().toString().trim());
            createGroupViewModel.setPageNumber(2);
        }
    }

    public boolean validate() {
        boolean valid = true;

        String fulname = mGrpfulName.getEditText().getText().toString();
        String disname = mGrpDisName.getEditText().getText().toString();

        if (fulname.isEmpty()) {
            mGrpfulName.setError("enter a valid group name");
            valid = false;
        } else {
            mGrpfulName.setError(null);
        }

        if (disname.isEmpty()) {
            mGrpDisName.setError("enter a valid group display name");
            valid = false;
        } else {
            mGrpDisName.setError(null);
        }
        return valid;
    }

    TextWatcher disNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            int wordsLength = countWords(charSequence.toString());// words.length;
            // count == 0 means a new word is going to start
            if (i1 == 0 && wordsLength >= MAX_WORDS) {
                setCharLimit(mGrpDisName.getEditText(), mGrpDisName.getEditText().getText().length());
            } else {
                removeFilter(mGrpDisName.getEditText());
            }

            //tvWordCount.setText(String.valueOf(wordsLength) + "/" + MAX_WORDS);

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private InputFilter filter;

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[] { filter });
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }
}
