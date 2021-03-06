package com.jojo.metroapp.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.jojo.metroapp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM_GENERAL_SETTINGS_TOTAL_FORM;

@GlideModule
public class utils extends AppGlideModule {

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static Map<String, Object> setUserAccountInformationMap(String username, String email, String password, String Uid, String urlProfileImage) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("email", email);
        map.put("password", password);
        map.put("Uid", Uid);
        map.put("profileImage", urlProfileImage);
        return map;
    }

    public static Map<String, Object> setPublicFormMap(String formNumber, String email, String username, String Uid, String datePublished, String titleForm, String alasan, String dateDari, String dateSampai, String imageUrl, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("formNumber", formNumber);
        map.put("email", email);
        map.put("username", username);
        map.put("Uid", Uid);
        map.put("datePublished", datePublished);
        map.put("titleForm", titleForm);
        map.put("deskripsi", alasan);
        map.put("dateDari", dateDari);
        map.put("dateSampai", dateSampai);
        map.put("imageUrl", imageUrl);
        map.put("status", status);
        return map;
    }

    public static Map<String, Object> setFormMap(String formNumber, String email, String username, String Uid, String datePublished, String titleForm, String alasan, String dateDari, String dateSampai, String imageUrl, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("formNumber", formNumber);
        map.put("email", email);
        map.put("username", username);
        map.put("Uid", Uid);
        map.put("datePublished", datePublished);
        map.put("titleForm", titleForm);
        map.put("deskripsi", alasan);
        map.put("dateDari", dateDari);
        map.put("dateSampai", dateSampai);
        map.put("imageUrl", imageUrl);
        map.put("status", status);
        return map;
    }

    public static Map<String, Object> setPublicFormGeneralSettings(String totalForm) {
        Map<String, Object> map = new HashMap<>();
        map.put(DB_PUBLIC_FORM_GENERAL_SETTINGS_TOTAL_FORM, totalForm);
        return map;
    }

    public static Map<String, Object> setCancelForm(String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        return map;
    }

    public static void setImageWithGlide(Context context, ImageView imageView, String url, int type) {
        switch (type) {
            // regular
            case 0:
                GlideApp.with(context)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
                break;
            // center crop
            case 1:
                GlideApp.with(context)
                        .load(url)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
                break;
            // fit center
            case 2:
                GlideApp.with(context)
                        .load(url)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
                break;
            // center crop w/ placeholder
            case 3:
                GlideApp.with(context)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(imageView);
                break;
            // circle crop w/ placeholder user
            case 4:
                GlideApp.with(context)
                        .load(url)
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(imageView);
                break;
        }
    }

    public static void setImageWithGlideFromLocal(Context context, ImageView imageView, int img, int type) {
        switch (type) {
            // regular
            case 0:
                GlideApp.with(context)
                        .load(img)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
                break;
            // center crop
            case 1:
                GlideApp.with(context)
                        .load(img)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
                break;
            // fit center
            case 2:
                GlideApp.with(context)
                        .load(img)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
                break;
        }
    }

    public static void removeImageWithGlide(Context context, View v) {
        GlideApp.with(context)
                .clear(v);
    }

    public static String idFactory() {
        return UUID.randomUUID().toString();
    }
}
