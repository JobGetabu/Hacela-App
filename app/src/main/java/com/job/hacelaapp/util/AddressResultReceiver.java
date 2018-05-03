package com.job.hacelaapp.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by Job on Thursday : 5/3/2018.
 */
public class AddressResultReceiver extends ResultReceiver {
    public String mAddressOutput;

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (resultData == null) {
            return;
        }

        // Display the address string
        // or an error message sent from the intent service.
        mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
        if (mAddressOutput == null) {
            mAddressOutput = "";
        }
        //displayAddressOutput();

        // Show a toast message if an address was found.
        if (resultCode == Constants.SUCCESS_RESULT) {
            //showToast(getString(R.string.address_found));
            Log.d("EditActivity", "onReceiveResult: Address found");
        }

    }
}
