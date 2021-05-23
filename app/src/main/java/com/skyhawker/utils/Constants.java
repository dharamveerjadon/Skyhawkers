package com.skyhawker.utils;

import android.graphics.Color;

public class Constants {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    public static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    public static final String serverKey = "key=" + "AAAAc17yDnE:APA91bE8YLe__QPTX3EBrGVSVyMptmNNwhCYvZxdxq-F3MQnNs2DE30RVcwuT_Q4qn4bweu7OlaUfTAltsvXzkAw1OtXgMG5TJ_qgBHpDaMcLU0VZcypkRQ0nROicqzxXEKO4TEk9InZ";
    public static final String contentType = "application/json";
    public static final String TAG = "NOTIFICATION TAG";
    public static final String ACCEPT = "accept";
    public static final String DECLINE = "decline";
    public static final String SAVED_FOR_LATER = "Saved for later";
    public static final String SHOW_ALL = "all";

    //---------------------Date Format-------------------------------------------------------------------------------------------------------------------
    public static final String DATE_FORMAT_EDDMMMYYYY = "EE, dd MMMM yyyy";
    public static final String DATE_FORMAT_yyyyMMDD = "yyyy-MM-dd'T'hh:mm:ss";
    public static final String DATE_FORMAT_EMMMMDDYYYY = "EE, MMMM dd yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_EEEHHMM = "EEEE hh:mm a";

    //----------------------------------------------------------------------------------------------------------------------------------------

    //Social Login Credentials-----------------------------------------------------------------------------------------------------------
    public static final String DATE_FORMAT_MMMMDD = "MMMM dd";
    public static final String DATE_FORMAT_MMMMYYYY = "MMMM yyyy";
    public static final String DATE_FORMAT_DDMMYYYY = "dd MMM yyyy";
    public static final String DATE_FORMAT_MMMDDYYYY_UPLOAD = "MMM dd yyyy";
    public static final String DATE_FORMAT_MMDDYYYYAM = "MM/dd/yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_MM_DD_YYYY_AM = "dd-MM-yyyy hh:mm:ss a";
    public static final String DATE_FORMAT_MMDDYYYY = "MM/dd/yyyy";
    public static final String DATE_FORMAT_DDMMYYYYWithdivider = "dd/MM/yyyy";
    public static final String DATE_FORMAT_MMMDDYYYY = "MMM/dd/yyyy";
    public static final String DATE_FORMAT_YYYYMMDD_UTCT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_YYYYMMDD_UTC = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YYYYMMDD_UTC_SSS = "yyyy-MM-dd HH:mm:ss.SSSSSSS";//2018-01-04 07:15:53.9280806
    public static final String DATE_FORMAT_YYYYMMDD_UTC_SSST = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS";//2018-01-04 07:15:53.9280806
    public static final String DATE_FORMAT_MMMDD = "MMM dd";
    /*Set Webservices Default Time*/
    public static final int DEFAULT_TIMEOUT = 30000;





    public static final String[] DOC_MIME_TYPE = new String[]{"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain",
            "application/rtf", "application/x-rtf", "text/richtext", "text/rtf", "text/x-rtf"};
    public static final String[] IMAGE_MIME_TYPE = new String[]{"image/jpeg", "image/png", "image/gif", "image/bmp"};
    public static final String[] ALL_MIME_TYPE = new String[]{"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain",
            "application/rtf", "application/x-rtf", "text/richtext", "text/rtf", "text/x-rtf", "image/jpeg", "image/png", "image/gif", "image/bmp"};
    public static final String[] IMAGE_VIDEO_TYPE = new String[]{"image/*", "video/*"};



    //NUMBERS
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";

    /*
    These constant used for tagview
     */
    public static final float DEFAULT_LINE_MARGIN = 5;
    public static final float DEFAULT_TAG_MARGIN = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 8;
    public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5;
    public static final float LAYOUT_WIDTH_OFFSET = 2;
    //----------------- separator Tag Item-----------------//
    public static final float DEFAULT_TAG_TEXT_SIZE = 14f;
    public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 14f;
    public static final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 0f;
    public static final float DEFAULT_TAG_RADIUS = 100;
    public static final int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#AED374");
    public static final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#88363636");
    public static final int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#ffffff");
    public static final String DEFAULT_TAG_DELETE_ICON = "×";
    public static final boolean DEFAULT_TAG_IS_DELETABLE = false;
    public static String device = "ANDROID";

}
