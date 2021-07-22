package com.skyhawk.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.skyhawk.R;
import com.skyhawk.adapters.MenuAdapter;
import com.skyhawk.interfaces.MenuItemInteraction;
import com.skyhawk.models.MenuItem;
import com.skyhawk.models.Session;
import com.skyhawk.utils.AppPreferences;
import com.skyhawk.utils.Keys;
import com.skyhawk.utils.SkyhawkerApplication;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created on 7/03/17.
 */
public class MenuFragment extends BaseFragment {

    //activity fragment communication
    private MenuItemInteraction mListener;
    private ProgressBar mProgressBar;
    private ImageView mImvMember;


    @SuppressWarnings("CanBeFinal")
    private BroadcastReceiver mProfileImageBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setImageOnView();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProfileImageBroadcast,
                new IntentFilter(Keys.BROADCAST_PROFILE_IMAGE));

    }

    @Override
    public void onResume() {
        super.onResume();
        setImageOnView();
    }

    /**
     * setting image on circle Image View
     */
    private void setImageOnView() {
        Session session = AppPreferences.getSession();

        if (session != null && session.getUserModel() != null && session.getUserModel().getProfileImage() != null && !TextUtils.isEmpty(session.getUserModel().getProfileImage().url)) {

            Glide.with(SkyhawkerApplication.sharedInstance())
                    .load(session.getUserModel().getProfileImage().url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                                isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable>
                                target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .placeholder(R.drawable.ic_avatar)
                    .dontAnimate()
                    .into(mImvMember);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_menu, container, false);

        final ListView listView = (ListView) view.findViewById(R.id.listView);

        final List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem(R.string.string_timeline, R.drawable.ic_timeline_grey));
        items.add(new MenuItem(R.string.string_my_job, R.drawable.ic_job_grey));
        items.add(new MenuItem(R.string.string_logout, R.drawable.ic_logout));

        final MenuAdapter menuAdapter = new MenuAdapter(getActivity(), items);

        listView.setOnItemClickListener((parent, v, position, id) -> {
            if (mListener != null) {
                //minus one for header
                mListener.onMenuClick((MenuItem) menuAdapter.getItem(position - 1));
            }
        });

        listView.setAdapter(menuAdapter);

        final ViewGroup headerView = (ViewGroup) inflater.inflate(R.layout.nav_header_menu, listView, false);
        mProgressBar = (ProgressBar) headerView.findViewById(R.id.progress_bar);
        listView.addHeaderView(headerView, null, false);
        final TextView textView = (TextView) headerView.findViewById(R.id.textView);
        mImvMember = (CircleImageView) headerView.findViewById(R.id.imv_member);

        Session session = AppPreferences.getSession();
        textView.setText(session.getEmailId());

        setImageOnView();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuItemInteraction) {
            mListener = (MenuItemInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProfileImageBroadcast);
    }
}
