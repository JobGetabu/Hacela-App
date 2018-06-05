package com.job.hacelaapp.ui;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.job.hacelaapp.R;
import com.job.hacelaapp.common.GroupMembersViewHolder;
import com.job.hacelaapp.dataSource.GroupMembers;
import com.job.hacelaapp.dataSource.Groups;
import com.job.hacelaapp.dataSource.UsersProfile;
import com.job.hacelaapp.util.ImageProcessor;
import com.job.hacelaapp.viewmodel.GroupControlViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.job.hacelaapp.util.Constants.GROUP_UID;

public class GroupControlActivity extends AppCompatActivity {

    @BindView(R.id.groupcontrol_group_name)
    TextView mGroupFulName;
    @BindView(R.id.groupcontrol_group_disname)
    TextView mGroupDisName;
    @BindView(R.id.groupcontrol_group_date)
    TextView mGroupDateInfo;
    @BindView(R.id.groupcontrol_header_image)
    ImageView headerImage;
    @BindView(R.id.groupcontrol_circular_header)
    CircleImageView circleImage;
    @BindView(R.id.groupcontrol_memberlist)
    RecyclerView mMemberList;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    private GroupControlViewModel model;
    private ImageProcessor imageProcessor;
    private FirestoreRecyclerAdapter adapter;

    public static final String TAG = "GroupControl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_group_control);

        //set up butterknife
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.groupcontrol_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            final View headerImage = findViewById(R.id.groupcontrol_header_image);
            final View headerInfo = findViewById(R.id.groupcontrol_header_info);
            final View avatar = findViewById(R.id.avatar_border);
            final LinearLayout texts = (LinearLayout) findViewById(R.id.texts);

            final int avatarHOffset = getResources().getDimensionPixelSize(R.dimen.profile_avatar_h_offset);
            final int avatarVOffset = getResources().getDimensionPixelSize(R.dimen.profile_avatar_v_offset);
            final int avatarSize = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);
            final int textHOffset = getResources().getDimensionPixelSize(R.dimen.profile_texts_h_offset);
            final int textVMinOffset = getResources().getDimensionPixelSize(R.dimen.profile_texts_v_min_offset);
            final int textVMaxOffset = getResources().getDimensionPixelSize(R.dimen.profile_texts_v_max_offset);
            final int textVDiff = textVMaxOffset - textVMinOffset;
            final int header160 = getResources().getDimensionPixelSize(R.dimen.dp160);
            final int toolBarHeight;

            {
                final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                        new int[]{android.R.attr.actionBarSize});
                toolBarHeight = (int) styledAttributes.getDimension(0, 0) + getStatusBarHeight();
                styledAttributes.recycle();

                avatar.setPivotX(0);
                avatar.setPivotY(0);
                texts.setPivotX(0);
                texts.setPivotY(0);
            }

            final ArrayList<Float> textStart = new ArrayList<>();

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                final int diff = toolBarHeight + verticalOffset;
                final int y = diff < 0 ? header160 - diff : header160;
                headerInfo.setTop(y);


                //solve the warnings
                GroupControlActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) headerImage.getLayoutParams();
                        lp.height = y;
                        headerImage.setLayoutParams(lp);


                        final int totalScrollRange = appBarLayout.getTotalScrollRange();
                        final float ratio = ((float) totalScrollRange + verticalOffset) / totalScrollRange;

                        final int avatarHalf = avatar.getMeasuredHeight() / 2;
                        final int avatarRightest = appBarLayout.getMeasuredWidth() / 2 - avatarHalf - avatarHOffset;
                        final int avatarTopest = avatarHalf + avatarVOffset;

                        avatar.setX(avatarHOffset + avatarRightest * ratio);
                        avatar.setY(avatarVOffset - avatarTopest * ratio);
                        avatar.setScaleX(0.5f + 0.5f * ratio);
                        avatar.setScaleY(0.5f + 0.5f * ratio);

                        if (textStart.isEmpty() && verticalOffset == 0) {
                            for (int i = 0; i < texts.getChildCount(); i++) {
                                textStart.add(texts.getChildAt(i).getX());
                            }
                        }

                        texts.setX(textHOffset + (avatarSize * 0.5f - avatarVOffset) * (1f - ratio));
                        texts.setY(textVMinOffset + textVDiff * ratio);
                        texts.setScaleX(0.8f + 0.2f * ratio);
                        texts.setScaleY(0.8f + 0.2f * ratio);
                        for (int i = 0; i < textStart.size(); i++) {
                            texts.getChildAt(i).setX(textStart.get(i) * ratio);
                        }
                    }
                });


            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {

                boolean isStarting = true;

                @Override
                public void onTransitionStart(Transition transition) {
                    if (isStarting) {
                        isStarting = false;

                        ViewCompat.setTransitionName(findViewById(R.id.groupcontrol_header_image), null);
                        ViewCompat.setTransitionName(findViewById(R.id.groupcontrol_nest), null);
                    }
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        }

        //End of layout animating

        //init firebase
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        imageProcessor = new ImageProcessor(this);

        //init view-model
        DocumentReference USERSPROFILE = mFirestore.collection("UsersProfile")
                .document(mCurrentUser.getUid());

        USERSPROFILE
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UsersProfile usersProfile = documentSnapshot.toObject(UsersProfile.class);
                        //we need the group id
                        if (usersProfile != null && usersProfile.getGroups() != null) {
                            Map<String, Boolean> groups = usersProfile.getGroups();


                            Set s1 = groups.entrySet();//to get whole entrys
                            for (Object aS1 : s1) {
                                Map.Entry m = (Map.Entry) aS1;//to get next entry (and casting required because values are object type)
                                //Entry is inner interface of Map interface

                                System.out.println(m.getKey() + "..." + m.getValue());
                                if (m.getValue().equals(true)) {
                                    String gId = (String) m.getKey();

                                    //read db data
                                    GroupControlViewModel.Factory factory = new GroupControlViewModel.Factory(
                                            getApplication(), mAuth, mFirestore, gId);

                                    model = ViewModelProviders.of(GroupControlActivity.this, factory)
                                            .get(GroupControlViewModel.class);

                                    //UI observers
                                    setUpGroupBasic(model);
                                    setUpMemberList(gId);
                                    testQuery(gId);

                                    break;
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error getting documents: ", e.getCause());
            }
        });
    }

    private void testQuery(String gId) {
        // Create a reference to the cities collection
        final CollectionReference memberRef = mFirestore.collection("GroupMembers");
        final Query query = memberRef
                .whereEqualTo("groupid", gId)
                .whereEqualTo("ismember",true);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("MYQUERY", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d("MYQUERY", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void setUpGroupBasic(GroupControlViewModel model) {
        MediatorLiveData<Groups> data = model.getGroupsMediatorLiveData();

        data.observe(this, new Observer<Groups>() {
            @Override
            public void onChanged(@Nullable Groups groups) {
                if (groups != null) {
                    mGroupFulName.setText(groups.getGroupname());
                    mGroupDisName.setText(groups.getDisplayname());

                    // Create date formats
                    DateFormat dateFormatLong = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.ENGLISH);  // Sun Dec 31, 2017
                    //dateFormatShort = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);  // 31-12-2017
                    SimpleDateFormat dFmatShrt = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);  // 31/12/2017
                    String textDate = dFmatShrt.format(groups.getDescription().getCreatedate());

                    mGroupDateInfo.setText("Created by " + groups.getDescription().getCreatedby() + ", " + textDate);

                    imageProcessor.setMyImage(circleImage, groups.getPhotourl(), true);
                    imageProcessor.setMyImage(headerImage, groups.getPhotourl(), true);
                }
            }
        });
    }

    private void setUpMemberList(String gId) {

        //set smooth scroll list
        ViewCompat.setNestedScrollingEnabled(mMemberList, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mMemberList.setLayoutManager(linearLayoutManager);

        // Create a reference to the cities collection
        final CollectionReference memberRef = mFirestore.collection("GroupMembers");
        final Query query = memberRef
                .whereEqualTo("groupid", gId)
                .whereEqualTo("ismember",true);

        // Configure recycler adapter options:
        //  * query is the Query object defined above.
        //  * GroupMembers.class instructs the adapter to convert each DocumentSnapshot to a Chat object
        FirestoreRecyclerOptions<GroupMembers> options = new FirestoreRecyclerOptions.Builder<GroupMembers>()
                .setQuery(query, GroupMembers.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<GroupMembers, GroupMembersViewHolder>(options) {
            @Override
            public void onBindViewHolder(@NonNull GroupMembersViewHolder holder, int position, @NonNull final GroupMembers model) {

                holder.setUserrole(model.getUserrole());
                holder.setDisName(model.getUsername());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Snackbar.make(GroupControlActivity.this.findViewById(android.R.id.content),model.getGroupid(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/

                        showSnackbar(model.getGroupid(),R.string.dialog_ok, null);
                    }
                });

                // load the userinfo
                holder.loadListIamges(GroupControlActivity.this,mFirestore, model.getUserid());
            }

            @Override
            public GroupMembersViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_group_member_item, group, false);

                return new GroupMembersViewHolder(view);
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                Log.e(TAG, "onError: ", e);
            }

        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        mMemberList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
          adapter.stopListening();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //start first cycle click
    @OnClick(R.id.groupcontrol_startfirstcycle)
    public void startCycleClick() {

        Toast.makeText(this, "TODO: starting cycle", Toast.LENGTH_SHORT).show();
    }

    //start friends invite actions
    @OnClick(R.id.groupcontrol_invite)
    public void startInviteClick() {

        Toast.makeText(this, "TODO: friend invite", Toast.LENGTH_SHORT).show();
    }


    //start togroup info edit
    @OnClick(R.id.groupcontrol_togroup_info)
    public void startToGroupEditClick() {

        groupInfoIntent();
    }

    //start togroup admin info edit
    @OnClick(R.id.groupcontrol_admin_infoedit)
    public void startToAdminGroupEditClick() {

        groupAdminInfoIntent();
    }

    //start group exit actions
    @OnClick(R.id.groupcontrol_exit_group)
    public void exitGroupClick() {

        Toast.makeText(this, "TODO: start exiting process", Toast.LENGTH_SHORT).show();
    }

    //start group delete actions
    @OnClick(R.id.groupcontrol_admin_deletegroup)
    public void deleteGroupClick() {

        Toast.makeText(this, "TODO: start deleting process", Toast.LENGTH_SHORT).show();
    }

    //start view more payout info
    @OnClick(R.id.groupcontrol_topayout_more)
    public void morePayoutInfoClick() {

        Toast.makeText(this, "TODO: to payout info", Toast.LENGTH_SHORT).show();
    }

    //start view more payout info
    @OnClick(R.id.groupcontrol_moretrans)
    public void moreTransInfoClick() {

        Toast.makeText(this, "TODO: to trans info", Toast.LENGTH_SHORT).show();
    }

    private void groupAdminInfoIntent() {
        Intent groupintent = new Intent(this, GroupContributionEditActivity.class);

        //TODO: Pass in the group UID
        groupintent.putExtra(GROUP_UID, "");
        startActivity(groupintent);
    }

    private void groupInfoIntent() {
        Intent groupintent = new Intent(this, GroupInfoEditActivity.class);

        //TODO: Pass in the group UID
        groupintent.putExtra(GROUP_UID, "");
        startActivity(groupintent);
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param message The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    public void showSnackbar(final String message, final int actionStringId,
                             View.OnClickListener listener) {
        Snackbar.make(
                this.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(this.getString(actionStringId), listener).show();
    }
}
