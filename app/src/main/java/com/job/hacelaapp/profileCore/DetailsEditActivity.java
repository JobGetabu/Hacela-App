package com.job.hacelaapp.profileCore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.job.hacelaapp.BuildConfig;
import com.job.hacelaapp.R;
import com.job.hacelaapp.manageUsers.LoginActivity;
import com.job.hacelaapp.service.LocationMonitoringService;

import am.appwise.components.ni.NoInternetDialog;
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

    public static final String TAG = "EditActivity";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private FirebaseAuth mAuth;

    private boolean mAlreadyStartedService = false;
    private NoInternetDialog noInternetDialog;
    private FusedLocationProviderClient mFusedLocationClient;


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

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_left_custom); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(false); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(true); // disable the default title element here (for centered title)


        //location service registered
        handleLocation();
        handleLastKnownLocation();

        //build no net dialogue
        //dialogue cant saving on no network
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setBgGradientOrientation(45)
                .setCancelable(true)
                .setBgGradientStart(getResources().getColor(R.color.app_gradient_start))
                .setBgGradientEnd(getResources().getColor(R.color.app_gradient_end))
                .build();
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Now make sure about location permission.
        if (checkPermissions()) {

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
                            }else {
                                Log.d(TAG, "onSuccess: No last location cached");
                            }
                        }
                    });
        } else if (!checkPermissions()) {
            requestPermissions();
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

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale_location,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(DetailsEditActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(DetailsEditActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void promptLocationMonitor() {

        //Now make sure about location permission.
        if (checkPermissions()) {

            //Step 2: Start the Location Monitor Service
            //Everything is there to start the service.
            //called from onResume
            startLocationMonitor();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // is executing for the first time.
        promptLocationMonitor();
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
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
                showSnackbar(R.string.permission_location_denied_explanation,
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
//Handle location stuff in settings rather than profile settings