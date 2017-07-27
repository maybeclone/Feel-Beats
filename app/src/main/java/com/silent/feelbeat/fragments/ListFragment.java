package com.silent.feelbeat.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.TabAdapter;
import com.silent.feelbeat.callback.CallbackControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 7/27/2017.
 */

public class ListFragment extends Fragment{

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> list;
    private TabAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) view.findViewById(R.id.toolBar);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        list = new ArrayList<>();
        list.add(ArtistsFragment.newInstance("Artists"));
        list.add(SongsFragment.newInstance("Songs"));
        list.add(AlbumsFragment.newInstance("Albums"));
        list.add(PlaylistFragment.newInstance("Playlist"));

        tabLayout.setupWithViewPager(viewPager);
        adapter = new TabAdapter(getChildFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    public Fragment getFragment(int position){
        return adapter.getItem(position);
    }
}
