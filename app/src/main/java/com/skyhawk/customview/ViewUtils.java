package com.skyhawk.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyhawk.R;
import com.skyhawk.utils.FontCache;

import static com.skyhawk.utils.Constants.ANDROID_SCHEMA;

/**
 * Created on 7/12/17.
 */
class ViewUtils {

    public interface ViewExtension {
        void onInit(Typeface typeface, boolean isScaleTextSize);
    }

    private ViewUtils() {
        /* cannot be instantiated */
    }

    /**
     * init the view attributes
     *
     * @param viewExtension view implementing view extension interface
     * @param context       context
     * @param attrs         view attrs
     */
    public static void init(ViewExtension viewExtension, Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.SkyhawkTextView);

        Typeface typeface = null;
        String fontName = attributeArray.getString(R.styleable.SkyhawkTextView_appFont);
        if (!TextUtils.isEmpty(fontName)) {
            int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
            typeface = selectTypeface(context, fontName, textStyle);
        }

        boolean isScaleTextSize = attributeArray.getBoolean(R.styleable.SkyhawkTextView_isScaleTextSize, false);

        viewExtension.onInit(typeface, isScaleTextSize);

        attributeArray.recycle();
    }

    /**
     * select the typeface
     *
     * @param context   context
     * @param fontName  name of the custom font
     * @param textStyle text style
     * @return typeface
     */
    @Nullable
    private static Typeface selectTypeface(@NonNull Context context, String fontName, int textStyle) {

        switch (textStyle) {
            case Typeface.BOLD: // bold
                return FontCache.getTypeface(fontName + "-Bold.ttf", context);

            case Typeface.ITALIC: // italic
                return FontCache.getTypeface(fontName + "-Italic.ttf", context);

            // bold italic
            case Typeface.BOLD_ITALIC:
                return FontCache.getTypeface(fontName + "-BoldItalic.ttf", context);

            default:
                return FontCache.getTypeface(fontName + ".ttf", context);
        }
    }
}
