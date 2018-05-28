package com.job.hacelaapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.job.hacelaapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.job.hacelaapp.util.Constants.GROUP_UID;

public class GroupInfoEditActivity extends AppCompatActivity {

    @BindView(R.id.groupinfoedit_toolbar)
    Toolbar mToolbar;

    private String mGroupUID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info_edit);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        // Get the ActionBar here to configure the way it behaves.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_custom); // set a custom icon for the default home button

        mGroupUID = getIntent().getStringExtra(GROUP_UID);

        if(mGroupUID.isEmpty()){
            //TODO: finish this activity, safety chack
        }
    }
}
