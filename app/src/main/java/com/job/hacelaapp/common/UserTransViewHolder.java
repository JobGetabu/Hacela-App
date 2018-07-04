package com.job.hacelaapp.common;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
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


    @BindView(R.id.title_ex_amount)
    TextView titleExAmount;
    @BindView(R.id.title_ex_time_label)
    TextView titleExTimeLabel;
    @BindView(R.id.title_ex_day_label)
    TextView titleExDayLabel;
    @BindView(R.id.title_ex_kes)
    TextView titleExKes;
    @BindView(R.id.title_ex_trans_type)
    TextView titleExTransType;
    @BindView(R.id.title_ex_status)
    TextView titleExStatus;
    @BindView(R.id.title_ex_long_date)
    TextView titleExLongDate;
    @BindView(R.id.title_ex_details)
    TextView titleExDetails;
    @BindView(R.id.title_ex_rl)
    RelativeLayout titleExRl;


    private static final String WITHDRAW = "withdraw";
    private static final String DEPOSIT = "deposit";

    private Context context;
    private String transId;
    private ImageProcessor imageProcessor;


    public UserTransViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        //View mRootView = inflater.inflate(R.layout.ucell_title_expand, container, false);
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
            titleExTransType.setText("Mpesa withdrawal");

            setmTitleImage(R.drawable.ic_money_withdraw, R.color.colorAccent);
            titleExRl.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));

        } else if (model.getType().equals(DEPOSIT)) {
            mTitleTransType.setText("Mpesa deposit");
            titleExTransType.setText("Mpesa deposit");

            setmTitleImage(R.drawable.ic_money_deposit, R.color.payGreen);
            titleExRl.setBackgroundColor(context.getResources().getColor(R.color.payGreen));
        }

        //set up date - time
        dateFormater(model);

        //set amount
        showAmount(model);

        if (model.getStatus().equals(context.getString(R.string.status_pending))) {
            mTitleStatus.setTextColor(context.getResources().getColor(R.color.bottomtab_item_disabled));
            titleExStatus.setTextColor(context.getResources().getColor(R.color.bottomtab_item_disabled));

            mTitleStatus.setText(context.getString(R.string.status_pending));
            titleExStatus.setText(context.getString(R.string.status_pending));


        } else if (model.getStatus().equals(context.getString(R.string.status_completed))) {
            mTitleStatus.setTextColor(context.getResources().getColor(R.color.payGreen));
            titleExStatus.setTextColor(context.getResources().getColor(R.color.payGreen));

            mTitleStatus.setText(context.getString(R.string.status_completed));
            titleExStatus.setText(context.getString(R.string.status_completed));


        } else if (model.getStatus().equals(context.getString(R.string.status_failed))){
            mTitleStatus.setTextColor(context.getResources().getColor(R.color.googleDarkRed));
            titleExStatus.setTextColor(context.getResources().getColor(R.color.googleDarkRed));

            mTitleStatus.setText(context.getString(R.string.status_failed));
            titleExStatus.setText(context.getString(R.string.status_failed));
        }

        //set up datails
        detailText(model);
    }

    private void dateFormater(UsersTransaction model) {
        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

        String ss = "On " + dateFormat.format(date);
        mTitleDate.setText(ss);

        DateFormat dateFormat2 = new SimpleDateFormat("hh.mm aa");
        titleExTimeLabel.setText(dateFormat2.format(date));

        int intday = c.get(Calendar.DAY_OF_WEEK);

        titleExDayLabel.setText(theDay(intday));

        DateFormat dateFormat3 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        titleExLongDate.setText(dateFormat3.format(date));
    }

    private void showAmount(UsersTransaction model) {

        double amount = model.getAmount();

        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String.format("Ksh %,.2f", amount);

        titleExAmount.setText(String.format("%,.0f", amount));

        //get trans type
        if (model.getType().equals(WITHDRAW)) {
            mTitleAmount.setTextColor(context.getResources().getColor(R.color.colorAccent));
            mTitleAmount.setText("-" + String.format("Ksh %,.2f", amount));

            titleExKes.setText("- KES");

        } else if (model.getType().equals(DEPOSIT)) {
            mTitleAmount.setTextColor(context.getResources().getColor(R.color.payGreen));
            mTitleAmount.setText("+" + String.format("Ksh %,.2f", amount));

            titleExKes.setText("+ KES");
        }
    }

    private void setmTitleImage(@DrawableRes int resourceId, @ColorRes int color) {

        mTitleImage.setImageResource(resourceId);
    }

    private void detailText(UsersTransaction model) {

        Timestamp timestamp = model.getTimestamp();
        Date date = timestamp.toDate();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

        String transId = model.getTransactionid();
        String paymentsystem = model.getPaymentsystem();
        String amount = String.format("Ksh %,.2f", model.getAmount());
        String transType = model.getType();

        //Confirmed. Deposit/Withdrawal of Ksh/KES 300.00 from mpesa to your Personal Account.
        // Transaction ID: xxxxxx on 06/14/18 at 10;33. Thank you


        //get trans type
        if (model.getType().equals(WITHDRAW)) {

            titleExDetails.setText(String.format("Confirmed. %s of %s from %s to your Personal Account " +
                            "Transaction ID %s on %s at %s. Thank you", transType, amount,
                    paymentsystem, transId, dateFormat.format(date), timeFormat.format(date)));

            //TODO: add details on transaction cost

        } else if (model.getType().equals(DEPOSIT)) {
            titleExDetails.setText(String.format("Confirmed. %s of %s from %s to your Personal Account " +
                            "Transaction ID %s on %s at %s. Thank you", transType, amount,
                    paymentsystem, transId, dateFormat.format(date), timeFormat.format(date)));
        }
    }

    private String theDay(int day) {
        switch (day) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "";
        }
    }
}
