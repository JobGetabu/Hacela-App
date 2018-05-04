package com.job.hacelaapp.profileCore;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.job.hacelaapp.BuildConfig;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UserAuthInfo;
import com.job.hacelaapp.dataSource.UserBasicInfo;
import com.job.hacelaapp.dataSource.UsersProfile;
import com.job.hacelaapp.manageUsers.LoginActivity;
import com.job.hacelaapp.service.LocationMonitoringService;
import com.job.hacelaapp.util.ImageProcessor;
import com.job.hacelaapp.util.PermissionProvider;
import com.job.hacelaapp.viewmodel.DetailsEditActivityViewModel;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

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
    TextView mPhoneNumber;
    @BindView(R.id.details_phonenumber_line)
    View mPhoneNumberLine;
    @BindView(R.id.details_location)
    TextInputLayout location;
    @BindView(R.id.details_idnumber)
    TextInputLayout mIdNum;

    @BindView(R.id.details_tv_income)
    TextView mIncome;
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

    public static final String TAG = "EditActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private String photoFile = "";

    //few db references
    private DocumentReference USERSREF;
    private DocumentReference USERSAUTHREF;
    private DocumentReference USERSPROFILE;


    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String currentUserId;

    private boolean mAlreadyStartedService = false;
    private NoInternetDialog noInternetDialog;
    private PermissionProvider permissionProvider;

    private DetailsEditActivityViewModel model;
    private ImageProcessor imageProcessor;



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        } else {
            //tests
            Log.d(TAG, "onStart: logged in as :" + currentUser.getEmail());

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
        mFirestore = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();


        //util
        permissionProvider = new PermissionProvider(this, DetailsEditActivity.this);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_left_custom); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(false); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)


        imageProcessor = new ImageProcessor();


        //location service registered
        handleLocation();
        handleLastKnownLocation();

        //build no net dialogue
        setUpNoNetDialogue();

        //read db data
        DetailsEditActivityViewModel.Factory factory = new DetailsEditActivityViewModel.Factory(
                getApplication(), mAuth, mFirestore);

        model = ViewModelProviders.of(this, factory)
                .get(DetailsEditActivityViewModel.class);

        //UI observers
        setUpBasicInfo(model);
        setUpAuthInfo(model);
        setUpProfileInfo(model);

    }

    private void setUpNoNetDialogue() {
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setBgGradientOrientation(45)
                .setCancelable(true)
                .setBgGradientStart(getResources().getColor(R.color.app_gradient_start))
                .setBgGradientEnd(getResources().getColor(R.color.app_gradient_end))
                .build();
    }

    @OnClick({R.id.details_tv_income, R.id.details_tv_income_line})
    public void setmIncomeClick() {
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(R.array.income_range, 0, null)
                .setPositiveButton(R.string.choose, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        Object selectedItem = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition);
                        // Do something useful with the position of the selected radio button

                        mIncome.setText(selectedItem.toString());
                    }
                })
                .show();
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
                        // Do something useful with the position of the selected radio button
                        mProfession.setText(selectedItem.toString());

                        if (selectedItem.toString().equals("Business")) {
                            mTypeOfBiz.setVisibility(View.VISIBLE);
                            mTypeOfBizLine.setVisibility(View.VISIBLE);
                        } else {
                            mTypeOfBiz.setVisibility(View.GONE);
                            mTypeOfBizLine.setVisibility(View.GONE);
                        }
                    }
                })
                .show();
    }

    @OnClick({R.id.details_tv_typeofbiz, R.id.details_tv_typeofbiz_line})
    public void setmTypeOfBizClick() {

        new AlertDialog.Builder(this)
                .setSingleChoiceItems(R.array.business_categories, 0, null)
                .setPositiveButton(R.string.choose, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        Object selectedItem = ((AlertDialog) dialog).getListView().getItemAtPosition(selectedPosition);
                        // Do something useful withe the position of the selected radio button
                        mTypeOfBiz.setText(selectedItem.toString());

                    }
                })
                .show();
    }


    @OnClick({R.id.details_phonenumber, R.id.details_phonenumber_line})
    public void setmPhoneNumberClick() {

        LayoutInflater inflater = this.getLayoutInflater();
        View lView = inflater.inflate(R.layout.dialogue_phoneauth, null);
        final TextInputLayout inputLayout = (TextInputLayout) lView.findViewById(R.id.dialogue_phoneauth_phonenum);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(lView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (inputLayout.getVisibility() == View.GONE) {
                            inputLayout.setVisibility(View.VISIBLE);
                        }
                        if (inputLayout.getVisibility() == View.VISIBLE) {
                            if (validateOnclick(inputLayout)) {
                                String phoneNumber = inputLayout.getEditText().getText().toString();
                                makeToast(phoneNumber);
                                dialog.dismiss();
                            }
                        }
                    }
                });



        builder.create();
        builder.show();

    }

    private boolean validateOnclick(final TextInputLayout inputLayout) {

        PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.createInstance(this);
        boolean valid = true;

        String phonenum = inputLayout.getEditText().getText().toString();

        Phonenumber.PhoneNumber kenyaNumberProto = null;
        try {
            kenyaNumberProto = mPhoneNumberUtil.parse(phonenum, "KE");
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        if (phonenum.isEmpty() || !mPhoneNumberUtil.isValidNumber(kenyaNumberProto)) {
            inputLayout.setError("enter a valid phone number");
            valid = false;
        } else {
            inputLayout.setError(null);
        }
        return valid;
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
        if (validate()) {
            writeUpdateToDb();
        }
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

    private void selectGender(String genderStr) {
        if (genderStr.equals("Male"))
            radioSexGroup.check(R.id.details_radiomale);
        else
            radioSexGroup.check(R.id.details_radiofemale);
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(DetailsEditActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    //get the last known location of a user's device
    private void handleLocation() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {

                            makeToast("lat :" + latitude + " log: " + longitude);
                            Log.d(TAG, "Location found: " + "lat :" + latitude + " log: " + longitude);

                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );
    }

    //we've already check permission
    @SuppressLint("MissingPermission")
    private void handleLastKnownLocation() {

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Now make sure about location permission.
        if (permissionProvider.checkPermissions()) {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object

                                String latitude = Double.toString(location.getLatitude());
                                String longitude = Double.toString(location.getLongitude());

                                //check for last known location instead and if available use deregister location monitor
                                Log.d(TAG, "LAST KNOWN LOCATION: Location found: " + "lat :" + latitude + " log: " + longitude);
                                deRegisterLocationMonitor();
                            } else {
                                Log.d(TAG, "onSuccess: No last location cached");
                            }

                            //TODO:handling address


                        }
                    });
        } else if (!permissionProvider.checkPermissions()) {
            permissionProvider.requestPermissions();
        }
    }

    //Start the Location Monitor Service
    private void startLocationMonitor() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService) {

            Log.d(TAG, "startLocationMonitor: Starting the Location Monitor Service");
            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    private void promptLocationMonitor() {

        //Now make sure about location permission.
        if (permissionProvider.checkPermissions()) {

            //Step 2: Start the Location Monitor Service
            //Everything is there to start the service.
            //called from onResume
            startLocationMonitor();
        } else if (!permissionProvider.checkPermissions()) {
            permissionProvider.requestPermissions();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // is executing for the first time.
        promptLocationMonitor();
    }

    //Callback received when a permissions request has been completed.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startLocationMonitor();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                permissionProvider.showSnackbar(R.string.permission_location_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    //set ui with Users data
    private void setUpBasicInfo(DetailsEditActivityViewModel model) {
        MediatorLiveData<UserBasicInfo> data = model.getUsersLiveData();

        data.observe(this, new Observer<UserBasicInfo>() {
            @Override
            public void onChanged(@Nullable UserBasicInfo userBasicInfo) {

                if (userBasicInfo != null) {

                    mUsername.getEditText().setText(userBasicInfo.getUsername());
                    imageProcessor.setMyImage(mProfPic, userBasicInfo.getPhotourl());
                }
            }
        });
    }

    //set ui with UsersAuth data
    private void setUpAuthInfo(DetailsEditActivityViewModel model) {

        MediatorLiveData<UserAuthInfo> data = model.getUserAuthInfoMediatorLiveData();

        data.observe(this, new Observer<UserAuthInfo>() {
            @Override
            public void onChanged(@Nullable UserAuthInfo userAuthInfo) {
                if (userAuthInfo != null) {
                    if (!userAuthInfo.getPhonenumber().isEmpty())
                        mPhoneNumber.setText(userAuthInfo.getPhonenumber());

                }
            }
        });
    }

    //set ui with UsersProfile data
    private void setUpProfileInfo(DetailsEditActivityViewModel model) {

        MediatorLiveData<UsersProfile> data = model.getUsersProfileMediatorLiveData();

        data.observe(this, new Observer<UsersProfile>() {
            @Override
            public void onChanged(@Nullable UsersProfile usersProfile) {

                if (usersProfile != null) {

                    mFullName.getEditText().setText(usersProfile.getFullname());
                    selectGender(usersProfile.getGender());
                    mIdNum.getEditText().setText(usersProfile.getIdnumber());
                    mIncome.setText(usersProfile.getIncome());
                    mProfession.setText(usersProfile.getProfession());
                    if (usersProfile.getProfession().equals("Business")) {
                        mTypeOfBiz.setVisibility(View.VISIBLE);
                        mTypeOfBizLine.setVisibility(View.VISIBLE);
                        mTypeOfBiz.setText(usersProfile.getTypeOfBusiness());
                    } else {
                        mTypeOfBiz.setVisibility(View.GONE);
                        mTypeOfBizLine.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    //writing updates to db
    private void writeUpdateToDb() {

        noInternetDialog.showDialog();

        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f9ab60"));
        pDialog.setTitleText("Saving Changes...");
        pDialog.setCancelable(false);
        pDialog.show();

        //init
        USERSREF = mFirestore.collection("Users").document(currentUserId);
        USERSAUTHREF = mFirestore.collection("UsersAuth").document(currentUserId);
        USERSPROFILE = mFirestore.collection("UsersProfile").document(currentUserId);

        //set up our pojos
        UserAuthInfo userAuthInfo = new UserAuthInfo();
        userAuthInfo.setPhonenumber(mPhoneNumber.getText().toString());

        UserBasicInfo userBasicInfo = new UserBasicInfo();
        userBasicInfo.setUsername(mUsername.getEditText().getText().toString());
        if (!photoFile.isEmpty()) userBasicInfo.setPhotourl(photoFile);
        else userBasicInfo.setPhotourl(mAuth.getCurrentUser().getPhotoUrl().toString());

        //location  and be updated, get from SharedPrefs
        //and groups not set here
        UsersProfile usersProfile = new UsersProfile();
        usersProfile.setFullname(mFullName.getEditText().getText().toString());
        usersProfile.setIncome(mIncome.getText().toString());
        usersProfile.setGender(selectedGender());
        usersProfile.setProfession(mProfession.getText().toString());
        usersProfile.setTypeOfBusiness(mTypeOfBiz.getText().toString());
        usersProfile.setIdnumber(mIdNum.getEditText().getText().toString());

        // Get a new write batch
        WriteBatch batch = mFirestore.batch();
        batch.set(USERSAUTHREF, userAuthInfo, SetOptions.mergeFields("phonenumber"));
        batch.set(USERSREF, userBasicInfo, SetOptions.mergeFields("username", "photourl"));
        batch.set(USERSPROFILE, usersProfile, SetOptions.merge());

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> dbtask) {
                if (dbtask.isSuccessful()) {
                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.dismissWithAnimation();
                    finish();
                } else {
                    pDialog.dismiss();
                    Log.d(TAG, "onComplete: error" + dbtask.getException().toString());
                    errorPrompt();
                }
            }
        });
    }

    private void errorPrompt() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    public boolean validate() {
        boolean valid = true;

        String displayname = mUsername.getEditText().getText().toString();
        String fullname = mFullName.getEditText().getText().toString();


        if (displayname.isEmpty()) {
            mUsername.setError("enter a valid username");
            valid = false;
        } else {
            mUsername.setError(null);
        }
        if (fullname.isEmpty()) {
            mFullName.setError("enter a name");
            valid = false;
        } else {
            mFullName.setError(null);
        }

        return valid;
    }

    private void deRegisterLocationMonitor() {
        //Stop location sharing service to app server.........
        stopService(new Intent(this, LocationMonitoringService.class));
        mAlreadyStartedService = false;
        //Ends................................................
    }

    @Override
    public void onDestroy() {

        //Stop location sharing service to app server.........
        deRegisterLocationMonitor();

        if (noInternetDialog != null)
            noInternetDialog.onDestroy();

        super.onDestroy();
    }
}

//TODO
//calculate profile completion
//Handle location stuff in settings rather than profile settings