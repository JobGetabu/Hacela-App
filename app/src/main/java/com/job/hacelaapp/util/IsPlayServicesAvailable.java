package com.job.hacelaapp.util;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Job on Wednesday : 5/2/2018.
 */
public class IsPlayServicesAvailable {
    public boolean checksForGoogleServicesAvailability(Context context, Activity activity){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);

        if (status == ConnectionResult.API_VERSION_UPDATE_REQUIRED){
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.showErrorDialogFragment(activity,status,2404);

                return false;
            }
        }
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}
