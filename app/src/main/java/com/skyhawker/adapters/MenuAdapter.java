package com.skyhawker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyhawker.R;
import com.skyhawker.models.MenuItem;

import java.util.List;


/**
 * Created on 7/10/17.
 */
public class MenuAdapter extends BaseAdapter {

    @SuppressWarnings("CanBeFinal")
    private List<MenuItem> mItems;
    @SuppressWarnings("CanBeFinal")
    private LayoutInflater mInflater;

    public MenuAdapter(Context context, List<MenuItem> items) {
        mItems = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Return the count
     *
     * @return count
     */
    public int getCount() {
        if (mItems == null) {
            return 0;
        }

        return mItems.size();
    }

    /**
     * Return the Object item
     *
     * @return Object
     * @param position position
     */
    public Object getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Return the Item Id
     *
     * @return Object
     * @param position position
     */

    public long getItemId(int position) {
        return 0;
    }

    /**
     * Return the View
     *
     * @return View
     * @param position position
     * @param convertView convert view
     * @param parent parent
     */

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_menu_item, parent, false);
            viewHolder = new ViewHolder();

            // Initialize different views
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            // Set tag
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set values to the list items
        final MenuItem item = mItems.get(position);
        viewHolder.nameView.setText(item.name);
        viewHolder.imageView.setImageResource(item.imageResId);
        return convertView;
    }

    /**
     * View Holder class
     */
    private static class ViewHolder {
        TextView nameView;
        ImageView imageView;
    }
}
