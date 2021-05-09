package com.skyhawker.customview;

import android.content.Context;

import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyhawker.interfaces.MenuItemInteraction;
import com.skyhawker.models.MenuItem;

import java.util.List;

/**
 * Created on 7/12/17.
 */
public class TabBar extends LinearLayout implements TabBarItem.TabBarItemInteraction {

    //ref to menu items
    private List<MenuItem> mItems;

    //current selected index
    private int mSelectedIndex = -1;

    private MenuItemInteraction mClickListener;

    public TabBar(@NonNull Context context) {
        super(context);
    }

    public TabBar(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public TabBar(@NonNull Context context, @NonNull AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set the menu items
     *
     * @param items menu items
     */
    public void setMenuItems(List<MenuItem> items) {
        mItems = items;
        for (MenuItem item : items) {
            addTab(getContext(), item);
        }
    }

    /**
     * Get menu item at index
     *
     * @param index index
     * @return menu item
     */
    public MenuItem getMenuItem(int index) {
        return mItems.get(index);
    }

    /**
     * Add Tab to the Linear Layout
     *
     * @param context  Context
     * @param menuItem MenuItem
     */

    private void addTab(Context context, MenuItem menuItem) {

        LinearLayout.LayoutParams params = new LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        TabBarItem item = new TabBarItem(context, this, menuItem);
        item.setLayoutParams(params);
        addView(item);
    }

    /**
     * Set the item select listener
     *
     * @param listener listener
     */
    public void setOnMenuClickListener(@Nullable MenuItemInteraction listener) {
        mClickListener = listener;
    }

    @Override
    public void onTabClick(TabBarItem tabBarItem, boolean isSelected) {
        if (isSelected) {
            if (mClickListener != null) {
                mClickListener.onPopClick();
            }
        } else {
            tabBarItem.setSelected(true);
            if (mSelectedIndex >= 0) {
                TabBarItem item = (TabBarItem) getChildAt(mSelectedIndex);
                item.setSelected(false);
            }

            mSelectedIndex = indexOfChild(tabBarItem);

            if (mClickListener != null) {
                mClickListener.onMenuClick(mItems.get(mSelectedIndex));
            }
        }
    }

    /**
     * Set the item as selected at selected index
     *
     * @param selectedIndex       selected index
     * @param ignoreClickListener ignore click listener
     */
    public void setSelectedIndex(int selectedIndex, boolean ignoreClickListener) {
        //mark the old item selected as false
        if (mSelectedIndex >= 0) {
            TabBarItem item = (TabBarItem) getChildAt(mSelectedIndex);
            item.setSelected(false);
        }

        //set the new item selected
        mSelectedIndex = selectedIndex;
        if (mSelectedIndex >= 0) {
            TabBarItem item = (TabBarItem) getChildAt(mSelectedIndex);
            item.setSelected(true);
        }

        if (mClickListener != null && !ignoreClickListener) {
            mClickListener.onMenuClick(mItems.get(mSelectedIndex));
        }
    }

    /**
     * Get selected index
     *
     * @return selected index
     */
    public int getSelectedIndex() {
        return mSelectedIndex;
    }
}