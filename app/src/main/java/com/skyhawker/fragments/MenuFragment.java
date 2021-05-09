package com.skyhawker.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.skyhawker.R;
import com.skyhawker.adapters.MenuAdapter;
import com.skyhawker.interfaces.MenuItemInteraction;
import com.skyhawker.models.MenuItem;
import com.skyhawker.models.Session;
import com.skyhawker.models.UserModel;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.Keys;
import com.skyhawker.utils.SkyhawkerApplication;

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
    private CircleImageView mImvMember;


    @SuppressWarnings("CanBeFinal")
    private BroadcastReceiver mProfileImageBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Session session = AppPreferences.getSession();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                session.getCurrentUser().setProfileImageUrl(bundle.getString(Keys.IMAGE_URL));
                AppPreferences.setSession(session);
            }
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

        if(session != null && session.getUserModel() != null) {
            Glide.with(SkyhawkerApplication.sharedInstance())
                    .load(session.getUserModel().getProfileImageUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                                isFirstResource) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable>
                                target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .placeholder(R.drawable.ic_skyhawk_profile_orange)
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mListener != null) {
                    //minus one for header
                    mListener.onMenuClick((MenuItem) menuAdapter.getItem(position - 1));
                }
            }
        });

        listView.setAdapter(menuAdapter);

        final ViewGroup headerView = (ViewGroup) inflater.inflate(R.layout.nav_header_menu, listView, false);
        mProgressBar = (ProgressBar) headerView.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
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
