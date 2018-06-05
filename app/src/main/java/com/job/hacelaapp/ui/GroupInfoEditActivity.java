package com.job.hacelaapp.ui;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.Groups;
import com.job.hacelaapp.util.ImageProcessor;
import com.job.hacelaapp.viewmodel.GroupInfoViewModel;

import java.io.ByteArrayOutputStream;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.job.hacelaapp.util.Constants.GROUP_UID;

public class GroupInfoEditActivity extends AppCompatActivity {

    @BindView(R.id.groupinfoedit_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.groupinfoedit_profile_image)
    CircleImageView groupinfoeditProfileImage;
    @BindView(R.id.groupinfoedit_groupdisname)
    TextInputLayout groupinfoeditGroupdisname;
    @BindView(R.id.groupinfoedit_groupfullname)
    TextInputLayout groupinfoeditGroupfullname;
    @BindView(R.id.groupinfoedit_descrip)
    TextInputLayout groupinfoeditDescrip;
    @BindView(R.id.groupinfoedit_btn_cancel)
    Button groupinfoeditBtnCancel;
    @BindView(R.id.groupinfoedit_btn_save)
    Button groupinfoeditBtnSave;

    private String mGroupUID = "";

    //few db references
    private DocumentReference USERSREF;
    private DocumentReference USERSAUTHREF;
    private DocumentReference USERSPROFILE;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storageReference;
    private StorageReference mProfileImageReference;

    private GroupInfoViewModel model;
    private ImageProcessor imageProcessor;
    private NoInternetDialog noInternetDialog;

    private Uri mResultPhotoFile = null;
    ByteArrayOutputStream mBaos = new ByteArrayOutputStream();

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

        if (mGroupUID.isEmpty()) {
            //TODO: finish this activity, safety check
            finish();
        }

        //init
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance();
        mProfileImageReference = storageReference.getReference("images/group/"+mGroupUID+"/profile.jpg");

        imageProcessor = new ImageProcessor(this);

        //build no net dialogue
        setUpNoNetDialogue();

        //read db data
        GroupInfoViewModel.Factory factory = new GroupInfoViewModel.Factory(
                getApplication(), mFirestore, mGroupUID);

        model = ViewModelProviders.of(this, factory)
                .get(GroupInfoViewModel.class);

        //UI observers
    }

    private void setUpNoNetDialogue() {
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setBgGradientOrientation(45)
                .setCancelable(true)
                .setBgGradientStart(getResources().getColor(R.color.app_gradient_start))
                .setBgGradientEnd(getResources().getColor(R.color.app_gradient_end))
                .build();
    }

    private void setUpGroupUi(){
        MediatorLiveData<Groups> data = model.getGroupsMediatorLiveData();

        data.observe(this, new Observer<Groups>() {
            @Override
            public void onChanged(@Nullable Groups groups) {

                if (groups != null){
                    //update ui
                    groupinfoeditGroupfullname.getEditText().setText(groups.getGroupname());
                    groupinfoeditGroupdisname.getEditText().setText(groups.getDisplayname());
                    groupinfoeditDescrip.getEditText().setText(groups.getDescription().getDescription());
                    imageProcessor.setMyImage(groupinfoeditProfileImage,groups.getPhotourl(), true);
                }
            }
        });
    }

    @OnClick({R.id.groupinfoedit_btn_cancel, R.id.groupinfoedit_btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.groupinfoedit_btn_cancel:
                finish();
                break;

            case R.id.groupinfoedit_btn_save:
                break;
        }
    }

    @OnClick({R.id.groupinfoedit_btn_changeimg, R.id.groupinfoedit_tv_changeimg})
    public void changeImageClick(){
        //change the group profile image.
    }

    private boolean validateOnclick() {

        boolean valid = true;

        String groupFulName = groupinfoeditGroupfullname.getEditText().getText().toString();
        String groupDisName = groupinfoeditGroupdisname.getEditText().getText().toString();
        String groupDes = groupinfoeditDescrip.getEditText().getText().toString();

        if (groupFulName.isEmpty()) {
            groupinfoeditGroupfullname.setError("Group name is empty");
            valid = false;
        } else {
            groupinfoeditGroupfullname.setError(null);
        }

        if (groupDisName.isEmpty()) {
            groupinfoeditGroupdisname.setError("Group display name is empty");
            valid = false;
        } else {
            groupinfoeditGroupfullname.setError(null);
        }

        if (groupDes.isEmpty()) {
            groupinfoeditDescrip.setError("Group description is empty");
            valid = false;
        } else {
            groupinfoeditDescrip.setError(null);
        }

        return valid;
    }


    @Override
    public void onDestroy() {

        if (noInternetDialog != null)
            noInternetDialog.onDestroy();

        super.onDestroy();
    }
}
