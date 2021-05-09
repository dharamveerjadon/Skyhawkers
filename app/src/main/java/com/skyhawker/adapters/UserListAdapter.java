package com.skyhawker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.skyhawker.R;
import com.skyhawker.models.Session;

import java.util.List;

public class UserListAdapter extends BaseAdapter {

    //corporate list view
    private static final int TYPE_ITEM = 0;

    //show footer view
    private static final int TYPE_EMPTY = 1;

    private final OnItemClickListener mOnItemClickListener;
    @SuppressWarnings("CanBeFinal")
    private LayoutInflater mInflater;
    private List<Session> mItems;
    private long mItemCountOnServer;

    public UserListAdapter(Context context, UserListAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public interface OnItemClickListener {
        void onItemClick(Session item);
    }

    /**
     * set the article items
     *
     * @param items             article items
     * @param itemCountOnServer total item count on the server
     */
    public void setItems(final List<Session> items, final long itemCountOnServer) {
        mItems = items;
        mItemCountOnServer = itemCountOnServer;
        notifyDataSetChanged();
    }

    /**
     * add items to list
     *
     * @param start             starting index
     * @param items             items list
     * @param itemCountOnServer item count on server
     */
    public void addItems(int start, final List<Session> items, final long itemCountOnServer) {
        //remove the expire result of this page due to caching
        mItems.subList(start, mItems.size()).clear();

        mItems.addAll(items);
        mItemCountOnServer = itemCountOnServer;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (mItems == null) {
            return 0;
        }
        //plus one for footer
        return mItems.size();
    }

    /**
     * get the article items
     *
     * @return article items
     */
    public List<Session> getItems() {
        return mItems;
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        //if the position is greater or equal to item size then show footer
            final int size = mItems.size();
            if (size == 0) {
                return TYPE_EMPTY;
            }
        return TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = this.getItemViewType(position);
        View v = convertView;
        switch (viewType) {
            case TYPE_ITEM:
                final Session item = mItems.get(position);
                ItemViewHolder itemViewHolder;
                if (v == null) {
                    v = mInflater.inflate(R.layout.fragment_user_info_list_item, parent, false);
                    itemViewHolder = new ItemViewHolder(v, mOnItemClickListener);
                    v.setTag(itemViewHolder);
                } else {
                    itemViewHolder = (ItemViewHolder) v.getTag();
                }
                itemViewHolder.bind(item);
                return v;

            default:
                return mInflater.inflate(R.layout.fragment_job_list_item_empty, parent, false);

        }
    }

    /**
     * Item View Holder
     */
    private static class ItemViewHolder implements View.OnClickListener {

        //view on click listener need to forward click events
        private final UserListAdapter.OnItemClickListener mOnItemClickListener;
        private final TextView mTitle;
        private final TextView mTxtDescription;
        private final ImageView pushNotification;
        // current bind to view holder
        private Session mCurrentItem;

        ItemViewHolder(@NonNull View view, final UserListAdapter.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            mTitle = view.findViewById(R.id.txt_title);
            mTxtDescription = view.findViewById(R.id.txt_description);
            pushNotification = view.findViewById(R.id.push_notification);
            pushNotification.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mCurrentItem != null && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mCurrentItem);
            }
        }

        /**
         * Bind the the values of the view holder
         *
         * @param item article item
         */
        void bind(final Session item) {
            mCurrentItem = item;
            mTitle.setText(item.getUserModel().getFirstName() + item.getUserModel().getLastName());
            mTxtDescription.setText(item.getUserModel().getSkills());
        }

    }
}
