package com.job.hacelaapp.profileCore;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.job.hacelaapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsEditActivity extends AppCompatActivity {

    @BindView(R.id.detailsedit_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.detailsedit_profile_image)
    CircleImageView mProfPic;

    @BindView(R.id.detailsedit_btn_changeimg)
    ImageButton mChangeImage;
    @BindView(R.id.detailsedit_tv_changeimg)
    TextView mChangeImageTv;

    @BindView(R.id.details_username)
    TextInputLayout mUsername;
    @BindView(R.id.details_fullname)
    TextInputLayout mFullName;
    @BindView(R.id.details_phonenumber)
    TextInputLayout mPhoneNumber;
    @BindView(R.id.details_location)
    TextInputLayout location;
    @BindView(R.id.details_idnumber)
    AutoCompleteTextView mIdNum;

    @BindView(R.id.details_tv_profession)
    TextView mProfession;
    @BindView(R.id.details_tv_typeofbiz)
    TextView mTypeOfBiz;
    @BindView(R.id.details_tv_typeofbiz_line)
    View mTypeOfBizLine;
    @BindView(R.id.details_tv_profession_line)
    View mProfessionLine;

    @BindView(R.id.detailsedit_radioSex)
    RadioGroup radioSexGroup;

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


    }

    /*
    private List<String> spinnerListInit(){
        List<String> dataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.income_range)));
        return dataset;
    }
    private void autoTextListInit(){
        List<String> professionDataSet = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.profession_categories)));
        List<String> businessDataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.business_categories)));

        final ArrayAdapter<String> professionadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, professionDataSet);

        ArrayAdapter<String> businessadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, businessDataset);

       et_profession.setAdapter(professionadapter);
       et_typeofbusiness.setAdapter(businessadapter);

       final String str = et_profession.getText().toString();

        et_profession.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b) {
                    // on focus off
                    ListAdapter listAdapter = et_profession.getAdapter();
                    for(int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if(str.equals(temp)) {
                            return;
                        }
                    }

                    et_profession.setText("");

                }
            }
        });
    }
    */

    @OnClick(R.id.details_tv_income)
    public void setmIncomeClick(){
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(DetailsEditActivity.this, mProfessionLine);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.income_menu, popup.getMenu());

        popup.setGravity(Gravity.END);
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(
                        DetailsEditActivity.this,
                        "You Clicked : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    private Drawable fetchDrawable(@DrawableRes int mdrawable) {
        // Facade Design Pattern
        return ContextCompat.getDrawable(this, mdrawable);
    }

    private void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @OnClick({R.id.detailsedit_btn_changeimg,R.id.detailsedit_tv_changeimg})
    public void changeProfileImageClick(){
        //TODO:
        makeToast("TODO: Change prof pic");
    }


    @OnClick(R.id.details_btn_cancel)
    public void cancelEditClick(){
        finish();
    }

    @OnClick(R.id.details_btn_save)
    public void saveProfileChanges(){
        //TODO:
        makeToast("TODO: Save changes gender:" + selectedGender());
    }

    private String selectedGender(){
        int selectedId = radioSexGroup.getCheckedRadioButtonId();

        switch (selectedId){
            case R.id.details_radiomale:
                return "Male";
            case R.id.details_radiofemale:
                return "Female";
                default:
                    return "";
        }
    }
}
