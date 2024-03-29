package com.job.hacelaapp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.job.hacelaapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

/**
 * Created by Job on Thursday : 5/3/2018.
 */
public class ImageProcessor {

    private Context context;

    public ImageProcessor(Context context) {
        this.context = context;
    }


    //set images to CircleImageView
    public void setMyImage(final CircleImageView circleImageView, final String url) {
        if (url.isEmpty()) {
            circleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.avatar_placeholder));
        } else {

            Picasso
                    .get()
                    .load(url)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            //no cache download new image
                            Picasso
                                    .get()
                                    .load(url)
                                    .placeholder(R.drawable.avatar_placeholder)
                                    .error(R.drawable.avatar_placeholder)
                                    .into(circleImageView);
                        }
                    });
        }
    }

    //set images to CircleImageView
    public void setMyImage(final CircleImageView circleImageView, final String url, boolean isGroup) {
        if (url.isEmpty()) {
            circleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.group_placeholder));
        } else {

            Picasso
                    .get()
                    .load(url)
                    .placeholder(R.drawable.group_placeholder)
                    .error(R.drawable.group_placeholder)
                    .into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            //no cache download new image
                            Picasso
                                    .get()
                                    .load(url)
                                    .placeholder(R.drawable.group_placeholder)
                                    .error(R.drawable.group_placeholder)
                                    .into(circleImageView);
                        }
                    });

           /* HacelaApplication.picassoWithCache
                    .load(url)
                    .placeholder(R.drawable.group_placeholder)
                    .error(R.drawable.group_placeholder)
                    .into(circleImageView);*/
        }
    }

    //set images to ImageView
    public void setMyImage(final ImageView imageView, final String url,boolean isGroup) {

        if (url.isEmpty()){
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.group_placeholder));
        }else {
        Picasso
                .get()
                .load(url)
                .placeholder(R.drawable.group_placeholder)
                .error(R.drawable.group_placeholder)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        //no cache download new image
                        Picasso
                                .get()
                                .load(url)
                                .placeholder(R.drawable.group_placeholder)
                                .error(R.drawable.group_placeholder)
                                .into(imageView);
                    }
                });
        }
    }

    //set images to ImageView
    public void setMyImage(final ImageView imageView, final String url) {
        if (url.isEmpty()){
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.avatar_placeholder));
        }else {
            Picasso
                    .get()
                    .load(url)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            //no cache download new image
                            Picasso
                                    .get()
                                    .load(url)
                                    .placeholder(R.drawable.avatar_placeholder)
                                    .error(R.drawable.avatar_placeholder)
                                    .into(imageView);
                        }
                    });
        }
    }

    public Bitmap compressImageBySixty(File imagefile, Context context) {
        Bitmap compressedImage = null;
        try {
            compressedImage = new Compressor(context)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(50)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(imagefile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedImage;
    }

    //TODO: only do thump images
    public Bitmap compressImageToThump(File imagefile, Context context) {
        Bitmap compressedImage = null;
        try {
            compressedImage = new Compressor(context)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(20)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(imagefile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedImage;
    }

    public void ImageCropper(Uri imageuri, Activity activity) {

        CropImage.activity(imageuri)
                .setAspectRatio(1, 1)
                .setMaxCropResultSize(640, 640)
                .setAutoZoomEnabled(true)
                .start(activity);
    }
}
