package com.silent.feelbeat.fragments;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.ListView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.ArtistAdapter;
import com.silent.feelbeat.dataloaders.ArtistLoader;
import com.silent.feelbeat.utils.SilentUtils;

/**
 * Created by silent on 7/17/2017.
 */

public class ArtistsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final int LOADER_ID = 0x00001;

    private ListView artistList;
    private ArtistAdapter adapter;
    private CallbackArtistFragment callback;

    public static ArtistsFragment newInstance(String title, boolean az) {
        ArtistsFragment artistsFragment = new ArtistsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SilentUtils.TITLE_FRAGMENT, title);
        bundle.putBoolean(SilentUtils.EXTRA_ORDER, az);
        artistsFragment.setArguments(bundle);
        return artistsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("Artist Fragment", "onAttach");
        if (context instanceof CallbackArtistFragment) {
            callback = (CallbackArtistFragment) context;
        } else {
            throw new ClassCastException(" must be implemented CallbackArtistFragment");
        }
        adapter = new ArtistAdapter(context, null, 0, getArguments().getBoolean(SilentUtils.EXTRA_ORDER));
    }

    public ArtistsFragment() {
        Log.d("Artist Fragment", "constructor");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Fragment", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment", "onDestroy");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Fragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_listview, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment", "onStart");
        getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Fragment", "onStop");
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artistList = (ListView) view.findViewById(R.id.listView);
        artistList.setEmptyView(view.findViewById(R.id.emptyText));
        artistList.setAdapter(adapter);
        artistList.setOnItemClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("onCreateLoader", args.getBoolean(SilentUtils.EXTRA_ORDER) + "");
        CursorLoader cursorLoader = new ArtistLoader().getCursorLoader(getActivity(), args.getBoolean(SilentUtils.EXTRA_ORDER));
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setAZ(getArguments().getBoolean(SilentUtils.EXTRA_ORDER));
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callback.onItemClick(id, adapter.getStringArtist(position));
    }

    public void reloadData(boolean az) {
        Bundle args = getArguments();
        boolean def = args.getBoolean(SilentUtils.EXTRA_ORDER);
        if (az == def) {
            return;
        }
        args.putBoolean(SilentUtils.EXTRA_ORDER, az);
        getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);

    }

    public interface CallbackArtistFragment {
        void onItemClick(long artistID, String artist);
    }
}
