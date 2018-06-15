package com.job.hacelaapp.common;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.job.hacelaapp.R;
import com.job.hacelaapp.ui.GroupGuestViewActivity;
import com.job.hacelaapp.util.ImageProcessor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.job.hacelaapp.util.Constants.GROUP_UID;

/**
 * Created by Job on Friday : 6/15/2018.
 */
public class GroupsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.group_item_image)
    CircleImageView groupItemImage;
    @BindView(R.id.group_item_name)
    TextView groupItemName;
    private Context context;
    private String groupId;

    public GroupsViewHolder(@NonNull View itemView,Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.context = context;
    }

    public void accessGroupId(String groupId){
        this.groupId = groupId;
    }

    public void setProfImage(Context context, String url) {
        new ImageProcessor(context).setMyImage(groupItemImage, url,true);
    }
    public void setDisName(String disName) {
        this.groupItemName.setText(disName);
    }

    @OnClick({R.id.group_item_image, R.id.group_item_name})
    public void viewClicked() {
        sendToGroupGuestView(context, groupId);
    }

    private void sendToGroupGuestView(Context context,String groupId) {
        Intent createIntent = new Intent(context, GroupGuestViewActivity.class);
        createIntent.putExtra(GROUP_UID,groupId);
        context.startActivity(createIntent);
    }
}
