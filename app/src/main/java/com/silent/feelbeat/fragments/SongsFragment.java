package com.silent.feelbeat.fragments;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.SongListAdapter;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.utils.SilentUtils;

import java.io.IOException;

/**
 * Created by silent on 7/17/2017.
 */

public class SongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int LOADER_ID = 0x0001;

    private SongListAdapter adapter;
    private ListView songsList;

    public static SongsFragment newInstance(String title) {
        SongsFragment songsFragment = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SilentUtils.TITLE_FRAGMENT, title);
        songsFragment.setArguments(bundle);
        return songsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        adapter = new SongListAdapter(context, null, 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songsList = (ListView) view.findViewById(R.id.listView);
        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Status", "Playing music");
                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(getContext(), SongsLoader.getSongUri(id));
                    player.prepare();
                    player.start();
                } catch (IOException e) {

                }
            }
        });
        songsList.setAdapter(adapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new SongsLoader().getCursorLoader(getActivity());
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
