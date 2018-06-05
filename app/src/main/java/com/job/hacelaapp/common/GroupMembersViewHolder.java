package com.job.hacelaapp.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.job.hacelaapp.R;
import com.job.hacelaapp.appExecutor.DefaultExecutorSupplier;
import com.job.hacelaapp.dataSource.UserBasicInfo;
import com.job.hacelaapp.util.ImageProcessor;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Job on Sunday : 6/3/2018.
 */
public class GroupMembersViewHolder extends RecyclerView.ViewHolder {


    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    public GroupMembersViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    //init our views
    private CircleImageView profImage = itemView.findViewById(R.id.single_group_profimage);
    private TextView disName = itemView.findViewById(R.id.single_group_username);
    private TextView userrole = itemView.findViewById(R.id.single_group_role);
    private TextView userPayDetails = itemView.findViewById(R.id.single_group_paydetails);
    private ImageView payimageicon = itemView.findViewById(R.id.single_group_payimage);

    public void setProfImage(Context context, String url) {
        new ImageProcessor(context).setMyImage(profImage, url);
    }

    public void setDisName(String disName) {
        this.disName.setText(disName);
    }

    public void setUserrole(String userrole) {
        this.userrole.setText(userrole);
    }

    public void setUserPayDetails(String userPayDetails) {
        this.userPayDetails.setText(userPayDetails);
    }

    public void setPayimageiconVisibility(Boolean payimageiconVisibility) {
        if (payimageiconVisibility)
            this.payimageicon.setVisibility(View.VISIBLE);
        else
            this.payimageicon.setVisibility(View.GONE);
    }

    public void loadListIamges(final Context context, FirebaseFirestore mFirestore, String userid){
        DocumentReference GROUPMEMBERREF = mFirestore.collection("GroupsMembers").document(userid);
        DocumentReference USERSREF = mFirestore.collection("Users").document(userid);

        //do a query to get photourl
        USERSREF.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        //do some work
                        DefaultExecutorSupplier.getInstance().forMainThreadTasks()
                                .execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        // do some Main Thread work here.
                                        UserBasicInfo userBasicInfo = document.toObject(UserBasicInfo.class);
                                        setProfImage(context,userBasicInfo.getPhotourl());
                                    }
                                });

                    } else {
                        Log.d("Groups", "No such document");
                    }
                } else {
                    Log.e("Groups", "get failed with ", task.getException());
                }
            }
        });
    }
}
