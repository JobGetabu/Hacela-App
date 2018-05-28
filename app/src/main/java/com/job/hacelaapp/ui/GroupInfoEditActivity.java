package com.job.hacelaapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.job.hacelaapp.R;

import static com.job.hacelaapp.util.Constants.GROUP_UID;

public class GroupInfoEditActivity extends AppCompatActivity {

    private String mGroupUID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info_edit);

        mGroupUID = getIntent().getStringExtra(GROUP_UID);

        if(mGroupUID.isEmpty()){
            //TODO: finish this activity, safety chack
        }
    }
}
