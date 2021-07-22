package com.skyhawk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.skyhawk.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;


public class Utils {

    private Utils() {
        /* cannot be instantiated */
    }

    /*
       This method used for show tagview when select items
        */
    public static int dipToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    /**
     * Parse the error message
     *
     * @param json json string from the response
     * @return return string message
     */
    private static String trimErrorMessage(String json) {

        String trimmedMessage = null;

        try {
            JSONObject responseObject = new JSONObject(json);
            trimmedMessage = responseObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trimmedMessage;
    }

    /**
     * show internet error based on the network available status
     *
     * @param context context of the activity
     */
    private static void showInternetError(Context context, View view) {
        if (Utils.isNetworkAvailable(context)) {
            showUnknownError(context, view);
        } else {
            showToast(context, view, context.getString(R.string
                    .error_api_no_internet_connection));
        }
    }

    /**
     * Show unKnown error message
     *
     * @param context context of the activity
     */
    public static void showUnknownError(Context context, View view) {
        showToast(context, view, context.getString(R.string
                .error_api_try_again));
    }



    /**
     * Show the message using toast or snack bar based on view
     *
     * @param context context
     * @param view    current display view
     * @param message message to show
     */
    public static void showToast(Context context, View view, String message) {
        //if view is null then use toast otherwise snak bar
        if (view == null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Show alert dialog
     *
     * @param context context
     * @param title   title to be shown
     * @param message message to be shown
     */
    public static void showAlertDialog(@NonNull Context context, String title, String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if (!TextUtils.isEmpty(title)) {
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(android.R.string.ok), new
                OnClickListener() {
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Show confirm alert dialog, it will be dismiss by user action
     *
     * @param context               context
     * @param title                 title to be shown
     * @param message               message to be shown
     * @param positiveTextId        id to the string for positive button
     * @param negativeTextId        id of the string for negative button
     * @param positiveClickListener active to be performed when positive button click
     */
    public static void showConfirmDialog(Context context, String title, String message, int positiveTextId, int
            negativeTextId, OnClickListener positiveClickListener) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(positiveTextId, positiveClickListener);
        if (negativeTextId > 0) {
            alertDialog.setNegativeButton(negativeTextId, new OnClickListener() {
                @Override
                public void onClick(@NonNull DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        alertDialog.show();
    }

    /**
     * Show confirm alert dialog, it will be dismiss by user action
     *
     * @param context               context
     * @param title                 title to be shown
     * @param message               message to be shown
     * @param postiveTextId         id to the string for positive button
     * @param negativeTextId        id of the string for negative button
     * @param positiveClickListener active to be performed when positive button click
     * @param negativeClickListener active to be performed when negative button click
     */
    public static void showConfirmDialog(Context context, String title, String message, @SuppressWarnings("SameParameterValue") int postiveTextId, @SuppressWarnings("SameParameterValue") int
            negativeTextId, OnClickListener positiveClickListener, OnClickListener negativeClickListener) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(postiveTextId, positiveClickListener);
        alertDialog.setNegativeButton(negativeTextId, negativeClickListener);
        alertDialog.show();
    }

    /**
     * Return true if network connection available
     *
     * @param context context
     * @return true if network connection available
     */
    public static boolean isNetworkAvailable(@NonNull Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }

    /**
     * Hide the keyboard
     *
     * @param context context from which keyboard needs to be hide
     */
    public static void hideKeyboard(Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        if (activity != null) {
            View focus = activity.getCurrentFocus();
            if (focus != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            }
        }
    }

    /**
     * Open the keyboard on the give editText
     *
     * @param context  context
     * @param editText editText
     */
    public static void showKeyboard(Context context, EditText editText) {
        AppCompatActivity activity = (AppCompatActivity) context;
        if (activity != null) {
            View focus = activity.getCurrentFocus();
            if (focus != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

            }
        }
    }

    /**
     * Return the readableFileSize format
     *
     * @param size of the file
     * @return readable string format
     */
    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Return the int value after parsing the string
     *
     * @param text         str to be parsed
     * @param defaultValue value to be used if parse unsuccessful
     * @return parse result
     */
    public static int parseInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * Return the long value after parsing the string
     *
     * @param text         str to be parsed
     * @param defaultValue value to be used if parse unsuccessfull
     * @return parse result
     */
    public static long parseLong(String text, long defaultValue) {
        try {
            return Long.parseLong(text);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * Return the double value after parsing the string
     *
     * @param text         str to be parsed
     * @param defaultValue value to be used if parse unsuccessful
     * @return parse result
     */
    public static double parseDouble(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * Return the float value after parsing the string
     *
     * @param text         str to be parsed
     * @param defaultValue value to be used if parse unsuccessful
     * @return parse result
     */
    public static float parseFloat(String text, float defaultValue) {
        try {
            return Float.parseFloat(text);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * Open the dial screen name with number specified
     *
     * @param context activity context
     * @param number  phone number
     * @return whether intent exists for handling phone no
     */
    public static boolean dialNumber(Context context, String number) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            intent.setData(Uri.parse("tel:" + number));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return true;
            } else {
                return false;
            }
        }
    }



    /**
     * Return true if emailId is valid
     *
     * @param emailId the email id to be examined
     * @return true if emailId is valid
     */
    public static boolean isValidEmail(CharSequence emailId) {
        return !TextUtils.isEmpty(emailId) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

    /**
     * @param url url
     * @return whether is this is app website url
     */
    public static boolean isAppUrl(String url) {

        if (TextUtils.isEmpty(url)) {
            return false;
        }

        //remove the www. from the url
        url = url.replace("www.", "");

        return url.startsWith("https://hfma.org.uk");
    }

    /**
     * Open link the intent active view
     *
     * @param activity The host activity.
     * @param uri      url to open
     */
    public static void openWebLink(final Activity activity, final Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }


    /**
     * truncate long string
     *
     * @param string value to convert
     * @param length length of string
     * @return String
     */
    public static String truncate(String string, @SuppressWarnings("SameParameterValue") int length) {
        if (TextUtils.isEmpty(string)) {
            return "";
        } else if (string.length() > length) {
            return string.substring(0, length - 3) + "...";
        } else {
            return string;
        }
    }

    /**
     * method to read text file from Assets
     *
     * @param context  context
     * @param fileName name of the file to be read
     * @return String file text
     */
    public static String readTextFileFromAssets(Context context, String fileName) {

        String data = null;

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(buffer);
            inputStream.close();

            // byte buffer into a string
            data = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }


    /**
     * device name
     *
     * @return device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    /**
     * check is equal
     *
     * @param str1 str1
     * @param str2 str2
     * @return is equal or not
     */
    public static boolean isEqual(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
    }


    /**
     * Html text to plain string
     *
     * @param html html text
     * @return plain text
     */
    public static String htmlToString(String html) {
        if (TextUtils.isEmpty(html)) {
            return html;
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            //noinspection deprecation
            return Html.fromHtml(html).toString();
        }
    }

    /**
     * capitalizing First Letter
     *
     * @param str string
     * @return capitalizing First Letter
     */
    public static String capitalizingFirstLetter(String str) {
        if (str == null) {
            return null;
        } else if (str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return str.toUpperCase();
        }
    }

}