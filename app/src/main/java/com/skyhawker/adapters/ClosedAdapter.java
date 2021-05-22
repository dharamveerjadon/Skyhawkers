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
import com.skyhawker.models.MyJobsModel;

import java.util.ArrayList;
import java.util.List;

public class ClosedAdapter extends BaseAdapter {

    //corporate list view
    private static final int TYPE_ITEM = 0;

    //show footer view
    private static final int TYPE_EMPTY = 1;

    private final OnItemClickListener mOnItemClickListener;
    @SuppressWarnings("CanBeFinal")
    private final LayoutInflater mInflater;
    private final Context context;
    private List<MyJobsModel> mItems;
    private long mItemCountOnServer;

    public ClosedAdapter(Context context, ClosedAdapter.OnItemClickListener onItemClickListener) {
        this.context = context;
        mOnItemClickListener = onItemClickListener;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public interface OnItemClickListener {
        void onItemClick(MyJobsModel item);

        void onDeveloperAppliedUser(MyJobsModel item);
    }

    /**
     * set the article items
     *
     * @param items             article items
     * @param itemCountOnServer total item count on the server
     */
    public void setItems(final List<MyJobsModel> items, final long itemCountOnServer) {
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
    public void addItems(int start, final List<MyJobsModel> items, final long itemCountOnServer) {
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
    public List<MyJobsModel> getItems() {
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
                final MyJobsModel item = mItems.get(position);
                ItemViewHolder itemViewHolder;
                if (v == null) {
                    v = mInflater.inflate(R.layout.fragment_closed_info_list_item, parent, false);
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
        private final ClosedAdapter.OnItemClickListener mOnItemClickListener;
        private final TextView mTitle;
        private final TextView mTxtDescription;
        private final TextView mTxtDate;
        private final TextView mTxtJobType;
        private final TagView tagGroup;
        private final TextView mTxtYearOfExperience;
        private final TextView mBudget;
        private final ImageView mImgStatus;
        private final ImageView mAppliedDeveloper;
        private final View viewBar;
        // current bind to view holder
        private MyJobsModel mCurrentItem;

        ItemViewHolder(@NonNull View view, final ClosedAdapter.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            view.setOnClickListener(this);
            mTitle = view.findViewById(R.id.txt_title);
            mTxtDescription = view.findViewById(R.id.txt_description);
            mTxtDate = view.findViewById(R.id.txt_date);
            mTxtJobType = view.findViewById(R.id.txt_job_type);
            tagGroup = view.findViewById(R.id.tag_group);
            mTxtYearOfExperience = view.findViewById(R.id.txt_year_of_experience);
            mBudget = view.findViewById(R.id.txt_tentative_budget);
            mImgStatus = view.findViewById(R.id.img_status);
            viewBar = view.findViewById(R.id.view_bar);
            mAppliedDeveloper = view.findViewById(R.id.view_applied_developer);
            mAppliedDeveloper.setOnClickListener(v -> {
                if (mCurrentItem != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onDeveloperAppliedUser(mCurrentItem);
                }
            });

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
        void bind(Context context, final MyJobsModel item) {
            mCurrentItem = item;

            mTitle.setText(item.getTitle());
            mTxtDescription.setText(item.getDescription());
            mTxtDate.setText(item.getDate());
            mTxtJobType.setText(item.getJobType());
            setTags(context, item.getSkills());
            mTxtYearOfExperience.setText(item.getYearOfExperience() + " Yrs experience");
            mBudget.setText("₹ " +item.getBudgets());

            if ("Accepted".equalsIgnoreCase(item.getStatus())) {
                viewBar.setBackgroundColor(context.getResources().getColor(R.color.light_green));
                mImgStatus.setImageResource(R.drawable.ic_accept);
            } else if ("Declined".equalsIgnoreCase(item.getStatus())) {
                viewBar.setBackgroundColor(context.getResources().getColor(R.color.red));
                mImgStatus.setImageResource(R.drawable.ic_decline);
            } else {
                mImgStatus.setImageResource(R.drawable.ic_saved_later);
                viewBar.setBackgroundColor(context.getResources().getColor(R.color.blue));
            }
        }

        private void setTags(Context context, String skills) {
            List<Tag> tagList = new ArrayList<>();

            String[] strSkills = skills.split(",");
            for (String value : strSkills) {
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
