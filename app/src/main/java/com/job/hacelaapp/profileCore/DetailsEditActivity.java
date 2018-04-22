package com.job.hacelaapp.profileCore;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.job.hacelaapp.R;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsEditActivity extends AppCompatActivity {

    @BindView(R.id.detailsedit_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.detailsedit_profile_image)
    CircleImageView mProfPic;
    @BindView(R.id.detailsedit_nice_spinner)
    NiceSpinner mSpinner;
    @BindView(R.id.detailsedit_profession)
    AutoCompleteTextView et_profession;
    @BindView(R.id.detailsedit_typeofbusiness)
    AutoCompleteTextView et_typeofbusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_edit);
        ButterKnife.bind(this);

        //TODO: Design the details screen'

        setSupportActionBar(mToolbar);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_left_custom); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(false); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)

        mSpinner.attachDataSource(spinnerListInit());
        autoTextListInit();

    }

    private List<String> spinnerListInit(){
        List<String> dataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.income_range)));
        return dataset;
    }
    private void autoTextListInit(){
        List<String> professionDataSet = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.profession_categories)));
        List<String> businessDataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.business_categories)));

        ArrayAdapter<String> professionadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, professionDataSet);

        ArrayAdapter<String> businessadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, businessDataset);

       et_profession.setAdapter(professionadapter);
       et_typeofbusiness.setAdapter(businessadapter);


    }


    private Drawable fetchDrawable(@DrawableRes int mdrawable) {
        // Facade Design Pattern
        return ContextCompat.getDrawable(this, mdrawable);
    }
}
