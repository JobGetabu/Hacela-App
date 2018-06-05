package com.job.hacelaapp.ui;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.Groups;
import com.job.hacelaapp.util.ImageProcessor;
import com.job.hacelaapp.viewmodel.GroupInfoViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.job.hacelaapp.util.Constants.GROUPCOL;
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
    private DocumentReference GROUPREF;

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
    ByteArrayOutputStream mBaosThump = new ByteArrayOutputStream();

    private static final String TAG = "GroupEdit";
    private static final int PICK_IMAGE_REQUEST = 1001;

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
        setUpGroupUi();
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
                if (validateOnclick()){
                    writeUpdateToDb();
                }
                break;
        }
    }

    @OnClick({R.id.groupinfoedit_btn_changeimg, R.id.groupinfoedit_tv_changeimg})
    public void changeImageClick(){
        //change the group profile image.
        //start image intent
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(imageIntent, "Select Group Profile Picture"), PICK_IMAGE_REQUEST);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case (PICK_IMAGE_REQUEST):
                Uri dataDatauri = data.getData();

                // start cropping activity for pre-acquired image saved on the device
                imageProcessor.ImageCropper(dataDatauri, this);
                break;

            case (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE):
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    //test

                    File imagefile = new File(resultUri.getPath());
                    mResultPhotoFile = resultUri;
                    Bitmap mCompImage = imageProcessor.compressImageBySixty(imagefile, this);

                    Bitmap mCompImageThump = imageProcessor.compressImageToThump(imagefile, this);
                    groupinfoeditProfileImage.setImageBitmap(mCompImage);
                    mCompImage.compress(Bitmap.CompressFormat.JPEG, 50, mBaos);
                    mCompImageThump.compress(Bitmap.CompressFormat.JPEG, 20, mBaosThump);

                    //push

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.e(TAG, "onActivityResult: ",error);
                }
                break;
        }
    }

    private void writeUpdateToDb(){

        noInternetDialog.showDialog();

        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Saving Changes...");
        pDialog.setCancelable(false);
        pDialog.show();

        //get texts
        final String groupFulName = groupinfoeditGroupfullname.getEditText().getText().toString();
        final String groupDisName = groupinfoeditGroupdisname.getEditText().getText().toString();
        final String groupDes = groupinfoeditDescrip.getEditText().getText().toString();

        //init db ref
        GROUPREF = mFirestore.collection(GROUPCOL).document(mGroupUID);

        //update

        if (mResultPhotoFile != null){

            //prepare compressed image

            // Get the data from an ImageView as bytes

            byte[] data = mBaos.toByteArray();

            final StorageReference ref = mProfileImageReference;
            final UploadTask uploadTask = ref.putBytes(data);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {

                        // Handle failures
                        pDialog.dismiss();
                        Log.d(TAG, "onComplete: error" + task.getException().toString());
                        errorPrompt();

                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        String photourl = downloadUri.toString();

                        //upload thump image now
                        byte[] mydata = mBaosThump.toByteArray();
                        storageReference.getReference("images/group/"+mGroupUID+"/thumbnail.jpg").putBytes(mydata);

                        GROUPREF.update("groupname",groupFulName,
                                "displayname",groupDisName,"photourl",photourl,"description.description",groupDes)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> dbtask) {
                                        if (dbtask.isSuccessful()){
                                            pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            pDialog.dismissWithAnimation();

                                            finish();
                                        }else {
                                            pDialog.dismiss();
                                            Log.d(TAG, "onComplete: error" + dbtask.getException().toString());
                                            errorPrompt();
                                        }
                                    }
                                });

                    }else {
                        pDialog.dismiss();
                        Log.d(TAG, "onComplete: error" + task.getException().toString());
                        errorPrompt();
                    }
                }
            });
        }else {

            GROUPREF.update("groupname",groupFulName,
                    "displayname",groupDisName,"description.description",groupDes)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> dbtask) {
                            if (dbtask.isSuccessful()){
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.dismissWithAnimation();

                                finish();
                            }else {
                                pDialog.dismiss();
                                Log.d(TAG, "onComplete: error" + dbtask.getException().toString());
                                errorPrompt();
                            }
                        }
                    });
        }
    }

    private void errorPrompt() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    @Override
    public void onDestroy() {

        if (noInternetDialog != null)
            noInternetDialog.onDestroy();

        super.onDestroy();
    }
}
