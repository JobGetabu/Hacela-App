package com.job.hacelaapp.util;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by Job on Tuesday : 5/1/2018.
 */
public class UsersDataDeserializer implements Observer<DocumentSnapshot> {

    @Override
    public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {

        if (documentSnapshot != null){

        }
    }
}
