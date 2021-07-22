package com.skyhawk.interfaces;

import com.skyhawk.models.MenuItem;

/**
 * Created on 7/12/17.
 */

public interface MenuItemInteraction {

    /**
     * Handles On Menu Item Click
     *
     * @param item item
     */
    void onMenuClick(MenuItem item);

    void onPopClick();
}
