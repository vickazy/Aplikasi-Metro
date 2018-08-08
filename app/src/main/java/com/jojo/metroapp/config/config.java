package com.jojo.metroapp.config;

public class config {

    // DB Path for
    public static final String DB_USER_ACCOUNT_INFORMATION = "userAccountInfo";
    public static final String DB_USER_ACCOUNT_HISTORY = "history";

    // DB Path for Admin
    public static final String DB_ADMIN_ACCOUNT_INFORMATION = "adminAccountInfo";

    // DB Path for public form
    public static final String DB_PUBLIC_FORM = "publicForm";
    public static final String DB_PUBLIC_FORM_SETTINGS = "publicFormSettings";
    public static final String DB_PUBLIC_FORM_GENERAL_SETTINGS = "generalSettings";
    public static final String DB_PUBLIC_FORM_GENERAL_SETTINGS_TOTAL_FORM = "totalForm";
    public static final String DB_PUBLIC_FORM_NUMBER = "formNumber";

    // Activity request code
    public static final int RC_PICK_IMAGE = 1;
    public static final int RC_PICK_IMAGE_PROFILE = 2;
    public static final int RC_ACTIVITYFORM_STATUS = 3;

    // Bundle Key
    public static final String BK_IMAGE_URL_FORM = "imageUrlForm";
    public static final String BK_TITLE_FORM = "titleForm";
    public static final String BK_DESKRIPSI_FORM = "alasanForm";
    public static final String BK_DATE_PUBLISHED_FORM = "publishedDateForm";
    public static final String BK_DATE_DARI_FORM = "dateDari";
    public static final String BK_DATE_SAMPAI_FORM = "dateSampai";
    public static final String BK_USERNAME_FORM = "usernameForm";
    public static final String BK_STATUS_FORM = "statusForm";
    public static final String BK_NUMBER_FORM = "numberForm";
    public static final String BK_STATUS_CONFIRMED_FORM = "confirmed";
    public static final String BK_STATUS_UNCONFIRMED_FORM = "unconfirmed";
    public static final String BK_STATUS_CANCELED_FORM = "canceled";
    public static final String BK_IMAGE_PATH_ACTIVITY_LIHAT_GAMBAR = "imagePathActivityLihatGambar";
    public static final String BK_IMAGE_TYPE_ACTIVITY_LIHAT_GAMBAR = "imageTypeActivityLihatGambar";
    public static final int BK_IMAGE_URI_TYPE_ACTIVITY_LIHAT_GAMBAR = 0;
    public static final int BK_IMAGE_URL_TYPE_ACTIVITY_LIHAT_GAMBAR = 1;

    // Delay in millis
    public static final long DELAY_SPLASHSCREEN = 1500;
}