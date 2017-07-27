package com.silent.feelbeat.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.SongListArtistAdapter;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.utils.ColorUtils;
import com.silent.feelbeat.utils.SilentUtils;

/**
 * Created by silent on 7/27/2017.
 */

public class DetailArtistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_ARTIST_ID = "artistid";
    public static final String EXTRA_ARTIST = "artist";

    private Toolbar toolbar;
    private ImageView imageView;
    private ListView listView;
    private SongListArtistAdapter adapter;

    private static final int LOADER_ID = 1;

    public static DetailArtistFragment newInstance(long artistID, String artist){
        DetailArtistFragment detailArtistFragment = new DetailArtistFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_ARTIST_ID, artistID);
        args.putString(EXTRA_ARTIST, artist);
        detailArtistFragment.setArguments(args);
        return detailArtistFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new SongListArtistAdapter(getContext(), null, 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            getLoaderManager().initLoader(LOADER_ID, args, this);
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

        listView.setAdapter(adapter);

        Bundle args = getArguments();
        String artist = args.getString(EXTRA_ARTIST);
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRect(SilentUtils.getSubString(artist), ColorUtils.getColorStringLength(artist.length()));
        imageView.setImageDrawable(textDrawable);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(args == null){
            return null;
        }
        return new SongsLoader(getContext().getContentResolver()).getCursorLoader(getContext(), args.getLong(EXTRA_ARTIST_ID));
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
