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
import com.silent.feelbeat.adapters.ArtistAdapter;
import com.silent.feelbeat.dataloaders.ArtistLoader;
import com.silent.feelbeat.utils.SilentUtils;

/**
 * Created by silent on 7/17/2017.
 */

public class ArtistsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 0x00001;

    private ListView artistList;
    private ArtistAdapter adapter;

    public static ArtistsFragment newInstance(String title){
        ArtistsFragment artistsFragment = new ArtistsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SilentUtils.TITLE_FRAGMENT, title);
        artistsFragment.setArguments(bundle);
        return artistsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new ArtistAdapter(context, null, 0);
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
        artistList = (ListView) view.findViewById(R.id.listView);
        artistList.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new ArtistLoader().getCursorLoader(getActivity());
        return  cursorLoader;
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
