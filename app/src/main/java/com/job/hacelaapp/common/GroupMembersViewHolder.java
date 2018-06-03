package com.job.hacelaapp.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.job.hacelaapp.R;
import com.job.hacelaapp.util.ImageProcessor;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Job on Sunday : 6/3/2018.
 */
public class GroupMembersViewHolder extends RecyclerView.ViewHolder {


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
}
