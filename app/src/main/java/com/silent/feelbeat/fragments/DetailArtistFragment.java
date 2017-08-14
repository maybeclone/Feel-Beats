package com.silent.feelbeat.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.AlbumsListAtrtistAdapter;
import com.silent.feelbeat.adapters.SongListArtistAdapter;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.utils.ColorUtils;
import com.silent.feelbeat.utils.SilentUtils;

/**
 * Created by silent on 7/27/2017.
 */

public class DetailArtistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, Toolbar.OnMenuItemClickListener {

    public static final String EXTRA_ARTIST_ID = "artistid";
    public static final String EXTRA_ARTIST = "artist";

    private Toolbar toolbar;
    private ImageView imageView;
    private ListView listView;
    private SongListArtistAdapter songAdapter;
    private AlbumsListAtrtistAdapter albumAdapter;
    private RecyclerView recyclerView;
    private CallbackService callbackService;

    private static final int LOADER_ID_SONG = 1;
    private static final int LOADER_ID_ALBUM = 2;

    public static DetailArtistFragment newInstance(long artistID, String artist, boolean az) {
        DetailArtistFragment detailArtistFragment = new DetailArtistFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_ARTIST_ID, artistID);
        args.putString(EXTRA_ARTIST, artist);
        args.putBoolean(SilentUtils.EXTRA_ORDER, az);
        detailArtistFragment.setArguments(args);
        return detailArtistFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        songAdapter = new SongListArtistAdapter(getContext(), null, 0);
        albumAdapter = new AlbumsListAtrtistAdapter(getContext(), null);
        if (context instanceof CallbackService) {
            callbackService = (CallbackService) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            getLoaderManager().initLoader(LOADER_ID_SONG, args, this);
            getLoaderManager().initLoader(LOADER_ID_ALBUM, args, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_artist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        listView = (ListView) view.findViewById(R.id.listSong);
        toolbar = (Toolbar) view.findViewById(R.id.toolBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        listView.setAdapter(songAdapter);
        listView.setOnItemClickListener(this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(albumAdapter);

        Bundle args = getArguments();
        String artist = args.getString(EXTRA_ARTIST);
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRect(SilentUtils.getSubString(artist), ColorUtils.getColorStringLength(artist.length()));
        imageView.setImageDrawable(textDrawable);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setTitle(getArguments().getString(EXTRA_ARTIST));
        toolbar.inflateMenu(R.menu.detail_menu);
        toolbar.setOnMenuItemClickListener(this);
        if(getArguments().getBoolean(SilentUtils.EXTRA_ORDER)){
            toolbar.getMenu().findItem(R.id.az).setChecked(true);
        } else {
            toolbar.getMenu().findItem(R.id.za).setChecked(true);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_SONG:
                return new SongsLoader(getContext().getContentResolver()).getCursorLoader(getContext(),
                        args.getLong(EXTRA_ARTIST_ID), args.getBoolean(SilentUtils.EXTRA_ORDER));
            case LOADER_ID_ALBUM:
                return new AlbumsLoader(getContext().getContentResolver()).getAlbumArtist(getContext(),
                        args.getLong(EXTRA_ARTIST_ID), args.getBoolean(SilentUtils.EXTRA_ORDER));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID_SONG:
                songAdapter.swapCursor(data);
                break;
            case LOADER_ID_ALBUM:
                albumAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_ID_SONG:
                songAdapter.swapCursor(null);
                break;
            case LOADER_ID_ALBUM:
                albumAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callbackService.playMusic(position, SongsLoader.getList(songAdapter.getCursor()));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.az:
                boolean checked_az = item.isChecked();
                if(!checked_az){
                    item.setChecked(true);
                    getArguments().putBoolean(SilentUtils.EXTRA_ORDER, true);
                    getLoaderManager().restartLoader(LOADER_ID_SONG, getArguments(), this);
                    getLoaderManager().restartLoader(LOADER_ID_ALBUM, getArguments(), this);
                } else {
                    item.setChecked(false);
                }
                break;
            case R.id.za:
                boolean checked_za = item.isChecked();
                if(!checked_za){
                    item.setChecked(true);
                    getArguments().putBoolean(SilentUtils.EXTRA_ORDER, false);
                    getLoaderManager().restartLoader(LOADER_ID_SONG, getArguments(), this);
                    getLoaderManager().restartLoader(LOADER_ID_ALBUM, getArguments(), this);
                } else {
                    item.setChecked(false);
                }
                break;
            case R.id.settings:

                break;

        }

        return true;
    }
}
