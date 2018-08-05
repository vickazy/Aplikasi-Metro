package com.jojo.metroapp.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;

import java.util.HashMap;
import java.util.Map;

import static com.jojo.metroapp.config.config.DB_PUBLIC_FORM_GENERAL_SETTINGS_TOTAL_FORM;

@GlideModule
public class utils extends AppGlideModule {

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static Map<String, Object> setUserAccountInformationMap(String username, String email, String password, String Uid) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("email", email);
        map.put("password", password);
        map.put("Uid", Uid);
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
        map.put("alasan", alasan);
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
        }
    }
}
