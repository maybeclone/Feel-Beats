package com.silent.feelbeat.activities;

import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.AlbumAdapter;
import com.silent.feelbeat.adapters.ArtistAdapter;
import com.silent.feelbeat.adapters.SongListAdapter;
import com.silent.feelbeat.adapters.TabAdapter;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.dataloaders.ArtistLoader;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.fragments.AlbumsFragment;
import com.silent.feelbeat.fragments.ArtistsFragment;
import com.silent.feelbeat.fragments.PlaylistFragment;
import com.silent.feelbeat.fragments.QuickControlFragment;
import com.silent.feelbeat.fragments.SongsFragment;
import com.silent.feelbeat.models.Artist;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> list;
    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        testQuickFragment();
    }
    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        list = new ArrayList<>();
        list.add(ArtistsFragment.newInstance("Artists"));
        list.add(SongsFragment.newInstance("Songs"));
        list.add(AlbumsFragment.newInstance("Albums"));
        list.add(PlaylistFragment.newInstance("Playlist"));

        tabLayout.setupWithViewPager(viewPager);
        adapter = new TabAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    void testQuickFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.quickControlContainer, new QuickControlFragment())
                .commit();
    }


}
