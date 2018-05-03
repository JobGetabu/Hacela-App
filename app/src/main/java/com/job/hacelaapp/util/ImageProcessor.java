package com.job.hacelaapp.util;

import android.widget.ImageView;

import com.job.hacelaapp.HacelaApplication;
import com.job.hacelaapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Job on Thursday : 5/3/2018.
 */
public class ImageProcessor {

    public ImageProcessor() { }

    //set images to CircleImageView
    public void setMyImage(final CircleImageView circleImageView, final String url) {
        HacelaApplication.picassoWithCache
                .load(url)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(circleImageView);
    }

    //set images to ImageView
    private void setMyImage(final ImageView imageView, final String url) {
        HacelaApplication.picassoWithCache
                .load(url)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(imageView);
    }
}
