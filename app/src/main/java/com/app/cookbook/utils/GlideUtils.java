package com.app.cookbook.utils;

import android.widget.ImageView;

import com.app.cookbook.R;
import com.bumptech.glide.Glide;

public class GlideUtils {

    public static void loadUrlBanner(String url, ImageView imageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_banner_no_image);
            return;
        }
        Glide.with(imageView.getContext())
                .load(url)
                .error(R.drawable.img_banner_no_image)
                .dontAnimate()
                .into(imageView);
    }

    public static void loadUrl(String url, ImageView imageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_no_image);
            return;
        }
        Glide.with(imageView.getContext())
                .load(url)
                .error(R.drawable.img_no_image)
                .dontAnimate()
                .into(imageView);
    }
}