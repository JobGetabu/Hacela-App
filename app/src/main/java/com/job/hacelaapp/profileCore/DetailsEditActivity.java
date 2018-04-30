package com.job.hacelaapp.profileCore;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.job.hacelaapp.R;
import com.job.hacelaapp.manageUsers.LoginActivity;

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

    public static final String TAG = "DetailsEditActivity";

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }else {
            //tests
            Log.d(TAG, "onStart: logged in as :"+currentUser.getEmail());

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_edit);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        //firebase
        mAuth = FirebaseAuth.getInstance();


        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_left_custom); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(false); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)


    }

    @OnClick({R.id.details_tv_income, R.id.details_tv_income_line})
    public void setmIncomeClick() {
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

    @OnClick({R.id.details_tv_profession, R.id.details_tv_profession_line})
    public void setmProfessionClick() {

        new AlertDialog.Builder(this)
                .setSingleChoiceItems(R.array.profession_categories, 0, null)
                .setPositiveButton(R.string.choose, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        Object selectedItem = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition);
                        // Do something useful withe the position of the selected radio button
                        makeToast(selectedItem.toString() + "int id: " + selectedPosition);
                    }
                })
                .show();
    }

    private Drawable fetchDrawable(@DrawableRes int mdrawable) {
        // Facade Design Pattern
        return ContextCompat.getDrawable(this, mdrawable);
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.detailsedit_btn_changeimg, R.id.detailsedit_tv_changeimg})
    public void changeProfileImageClick() {
        //TODO:
        makeToast("TODO: Change prof pic");
    }


    @OnClick(R.id.details_btn_cancel)
    public void cancelEditClick() {
        finish();
    }

    @OnClick(R.id.details_btn_save)
    public void saveProfileChanges() {
        //TODO:
        makeToast("TODO: Save changes gender:" + selectedGender());
    }

    @NonNull
    private String selectedGender() {
        int selectedId = radioSexGroup.getCheckedRadioButtonId();

        switch (selectedId) {
            case R.id.details_radiomale:
                return "Male";
            case R.id.details_radiofemale:
                return "Female";
            default:
                return "";
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(DetailsEditActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
