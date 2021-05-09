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
import com.skyhawker.customview.Tag;
import com.skyhawker.customview.TagView;
import com.skyhawker.models.ApplyJob;
import com.skyhawker.models.Session;

import java.util.ArrayList;
import java.util.List;

public class TimeLineDeveloperListAdapter extends BaseAdapter {

    //corporate list view
    private static final int TYPE_ITEM = 0;

    //show footer view
    private static final int TYPE_EMPTY = 1;

    private Context context;

    private final OnItemClickListener mOnItemClickListener;
    @SuppressWarnings("CanBeFinal")
    private LayoutInflater mInflater;
    private List<ApplyJob> mItems;
    private long mItemCountOnServer;

    public TimeLineDeveloperListAdapter(Context context, TimeLineDeveloperListAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        this.context = context;
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
    public void setItems(final List<ApplyJob> items, final long itemCountOnServer) {
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
    public void addItems(int start, final List<ApplyJob> items, final long itemCountOnServer) {
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
    public List<ApplyJob> getItems() {
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
                final ApplyJob item = mItems.get(position);
                ItemViewHolder itemViewHolder;
                if (v == null) {
                    v = mInflater.inflate(R.layout.fragment_timeline_developer_info_list_item, parent, false);
                    itemViewHolder = new ItemViewHolder(v, mOnItemClickListener);
                    v.setTag(itemViewHolder);
                } else {
                    itemViewHolder = (ItemViewHolder) v.getTag();
                }
                itemViewHolder.bind(context, item);
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
        private final TimeLineDeveloperListAdapter.OnItemClickListener mOnItemClickListener;
        private final TextView mUsername;
        private final TextView mMobileNumber;
        private final TagView tagGroup;
        private final TextView mLocation;
        private final TextView mEmailId;
        private final TextView mYearOfExperience;
        private final TextView mPricePerHour;
        private final TextView txtExpectedCtc;
        // current bind to view holder
        private Session mCurrentItem;

        ItemViewHolder(@NonNull View view, final TimeLineDeveloperListAdapter.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            mUsername = view.findViewById(R.id.txt_user_name);
            mMobileNumber = view.findViewById(R.id.txt_mobile_number);
            tagGroup = view.findViewById(R.id.tag_group);
            mLocation = view.findViewById(R.id.txt_location);
            mEmailId = view.findViewById(R.id.txt_email_id);
            mYearOfExperience = view.findViewById(R.id.txt_year_of_experience);
            mPricePerHour = view.findViewById(R.id.txt_price_per_hour);
            txtExpectedCtc = view.findViewById(R.id.txt_expected_ctc);

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
        void bind(Context context, final ApplyJob item) {
            mCurrentItem = item.getSession();
            mUsername.setText(item.getSession().getUserModel().getFirstName() +" "+ item.getSession().getUserModel().getLastName());
            mMobileNumber.setText(item.getSession().getMobileNumber());
            setTags(context, item.getSession().getUserModel().getSkills());
            mLocation.setText(item.getSession().getUserModel().getLocation());
            mEmailId.setText(item.getSession().getEmailId());
            mYearOfExperience.setText(item.getSession().getUserModel().getYearOfExperience());
            mPricePerHour.setText(item.getSession().getUserModel().getPricePerHour());
            txtExpectedCtc.setText(item.getSession().getUserModel().getExpectedCtc());
        }
        private void setTags(Context context, String skills) {
            List<Tag> tagList = new ArrayList<>();

            String[] strSkills = skills.split(",");
            for(String value: strSkills) {
                Tag tag;
                tag = new Tag(context, value);
                tag.radius = 10f;
                tag.layoutColor = tag.layoutBorderColor;
                tag.isDeletable = false;
                tagList.add(tag);
            }
            tagGroup.addTags(tagList);
        }
    }
}
