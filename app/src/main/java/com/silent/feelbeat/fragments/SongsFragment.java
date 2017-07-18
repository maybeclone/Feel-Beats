package com.silent.feelbeat.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.SongListAdapter;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.utils.SilentUtils;

/**
 * Created by silent on 7/17/2017.
 */

public class SongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static int LOADER_ID = 0x0001;

    private SongListAdapter adapter;
    private ListView songsList;

    public static SongsFragment newInstance(String title){
        SongsFragment songsFragment = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SilentUtils.TITLE_FRAGMENT, title);
        songsFragment.setArguments(bundle);
        return songsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        adapter = new SongListAdapter(context, null, 0);
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
