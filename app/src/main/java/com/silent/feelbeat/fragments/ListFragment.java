package com.silent.feelbeat.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.R;
import com.silent.feelbeat.activities.ChangePasswordActivity;
import com.silent.feelbeat.activities.SearchableActivity;
import com.silent.feelbeat.adapters.TabAdapter;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.servers.account.LogoutGetAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 7/27/2017.
 */

public class ListFragment extends Fragment implements Toolbar.OnMenuItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> list;
    private TextView emailText;
    private TabAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        if(savedInstanceState==null) {
            list.add(ArtistsFragment.newInstance("Artists", true));
            list.add(SongsFragment.newInstance("Songs", true));
            list.add(HomeFragment.newInstance("Home"));
            list.add(AlbumsFragment.newInstance("Albums", true));
            list.add(PlaylistFragment.newInstance("Playlist"));
        }
        adapter = new TabAdapter(getChildFragmentManager(), list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) view.findViewById(R.id.toolBar);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);
        navView = (NavigationView) view.findViewById(R.id.navView);
        emailText = (TextView) navView.getHeaderView(0).findViewById(R.id.emailText);
        // get from Preference
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);

        navView.setSelected(true);

        navView.bringToFront();
        navView.setNavigationItemSelectedListener(this);

        toolbar.setTitle(getContext().getResources().getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // get boolean from preference
        toolbar.getMenu().findItem(R.id.az).setChecked(true);
        emailText.setText(Instance.nowUser.email);
    }

    public Fragment getFragment(int position){
        return adapter.getItem(position);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                Intent intent = new Intent(getActivity(), SearchableActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.az:
                item.setChecked(true);
                ((ArtistsFragment) getFragment(0)).reloadData(true);
                ((SongsFragment) getFragment(1)).reloadData(true);
                ((AlbumsFragment) getFragment(2)).reloadData(true);
                break;
            case R.id.za:
                item.setChecked(true);
                ((ArtistsFragment) getFragment(0)).reloadData(false);
                ((SongsFragment) getFragment(1)).reloadData(false);
                ((AlbumsFragment) getFragment(2)).reloadData(false);
                break;
            case R.id.settings:

                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileNav:
                return true;
            case R.id.passwordNav:
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
                return true;
            case R.id.logoutNav:
                new LogoutGetAsyncTask(getContext()).execute(ConfigServer.LOGOUT_URL);
                return true;
            default:
                return false;
        }
    }
}
