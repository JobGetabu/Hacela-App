package com.job.hacelaapp.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.job.hacelaapp.R;
import com.job.hacelaapp.dataSource.UsersTransaction;
import com.job.hacelaapp.util.ImageProcessor;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Job on Sunday : 6/24/2018.
 */
public class UserTransViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title_image)
    CircleImageView mTitleImage;
    @BindView(R.id.title_trans_type)
    TextView mTitleTransType;
    @BindView(R.id.title_date)
    TextView mTitleDate;
    @BindView(R.id.title_amount)
    TextView mTitleAmount;
    @BindView(R.id.title_status)
    TextView mTitleStatus;

    private static final String WITHDRAW = "withdraw";
    private static final String DEPOSIT = "deposit";

    private Context context;
    private String transId;
    private ImageProcessor imageProcessor;


    public UserTransViewHolder(@NonNull View itemView) {
        super(itemView);

        //View mRootView = inflater.inflate(R.layout.ucell_title, container, false);
    }

    public void init(Context context, String transId) {
        this.context = context;
        this.transId = transId;
        imageProcessor = new ImageProcessor(context);
    }

    public void setUpUi(UsersTransaction model) {
        //get trans type
        if (model.getType().equals(WITHDRAW)) {
            mTitleTransType.setText("Mpesa withdrawal");

        } else if (model.getType().equals(DEPOSIT)) {
            mTitleTransType.setText("Mpesa deposit");

        }

        //set up date - time
        mTitleDate.setText(dateFormater(model));

        //set amount
        showAmount(model);
    }

    private String dateFormater(UsersTransaction model) {
        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.ENGLISH);

        return "On " + dateFormat.format(date);
    }

    private void showAmount(UsersTransaction model){

        double amount = model.getAmount();

        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String.format("Ksh %,.2f", amount);

        //get trans type
        if (model.getType().equals(WITHDRAW)) {
            mTitleAmount.setTextColor(context.getResources().getColor(R.color.colorAccent));
            mTitleAmount.setText("-Ksh "+String.format("Ksh %,.2f", amount));

        } else if (model.getType().equals(DEPOSIT)) {
            mTitleAmount.setTextColor(context.getResources().getColor(R.color.payGreen));
            mTitleAmount.setText("-Ksh "+String.format("Ksh %,.2f", amount));

        }
    }
}
