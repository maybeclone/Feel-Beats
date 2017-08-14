package com.silent.feelbeat.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.AlbumAdapter;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.utils.SilentUtils;

/**
 * Created by silent on 7/17/2017.
 */

public class AlbumsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0x00001;
    private static final int COUNT_COL = 2;

    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private CallbackAlbumsFragment callback;

    public static AlbumsFragment newInstance(String title, boolean az) {
        AlbumsFragment albumsFragment = new AlbumsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SilentUtils.TITLE_FRAGMENT, title);
        bundle.putBoolean(SilentUtils.EXTRA_ORDER, az);
        albumsFragment.setArguments(bundle);
        return albumsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallbackAlbumsFragment) {
            callback = (CallbackAlbumsFragment) context;
        }
        adapter = new AlbumAdapter(context, null, callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COUNT_COL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new AlbumsLoader().getCursorLoader(getActivity(), getArguments().getBoolean(SilentUtils.EXTRA_ORDER));
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

    public interface CallbackAlbumsFragment {
        void onItemClick(long id, String artist, String title, String info, boolean az);
    }

    public void reloadData(boolean az) {
        boolean def = getArguments().getBoolean(SilentUtils.EXTRA_ORDER);
        if (az == def) {
            return;
        }
        getArguments().putBoolean(SilentUtils.EXTRA_ORDER, az);
        getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);

    }
}
