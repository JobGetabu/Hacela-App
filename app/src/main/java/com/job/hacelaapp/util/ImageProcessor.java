package com.job.hacelaapp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.job.hacelaapp.HacelaApplication;
import com.job.hacelaapp.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

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

    public Bitmap compressImageBySixty(File imagefile, Context context){
        Bitmap compressedImage = null;
        try {
            compressedImage = new Compressor(context)
                    .setQuality(60)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(imagefile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedImage;
    }

    public void ImageCropper(Uri imageuri, Activity activity){

        CropImage.activity(imageuri)
                .setAspectRatio(1, 1)

                .start(activity);
    }
}
