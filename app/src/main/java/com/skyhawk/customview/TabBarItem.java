package com.skyhawk.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.skyhawk.R;
import com.skyhawk.models.MenuItem;

/**
 * Created on 7/12/17.
 */
public class TabBarItem extends LinearLayout {

    /**
     * Initialize TabBarItem
     *
     * @param context  Context
     * @param listener TabBarItemInteraction
     * @param menuItem MenuItem
     */

    public TabBarItem(@NonNull Context context, final TabBarItemInteraction listener, final MenuItem menuItem) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if (inflater != null)
            view = inflater.inflate(R.layout.tab_bar_item, this);

        if (view != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            TextView textView = (TextView) view.findViewById(R.id.textView);

            //set the image
            imageView.setImageResource(menuItem.imageResId);

            //set the text
            textView.setText(menuItem.textResId);
        }

        //set the listener
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTabClick(TabBarItem.this, isSelected());
                }
            }
        });
    }

    public TabBarItem(@NonNull Context context) {
        super(context);
    }

    public TabBarItem(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public TabBarItem(@NonNull Context context, @NonNull AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public interface TabBarItemInteraction {
        void onTabClick(TabBarItem tabBarItem, boolean isSelected);
    }
}