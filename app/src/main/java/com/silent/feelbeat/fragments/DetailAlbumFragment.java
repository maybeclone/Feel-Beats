package com.silent.feelbeat.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.SongListAlbumAdapter;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.utils.SilentUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by silent on 7/29/2017.
 */

public class DetailAlbumFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, View.OnClickListener, Toolbar.OnMenuItemClickListener {

    public final static String EXTRA_ALBUMID = "albumid";
    public final static String EXTRA_TITLE = "title";
    public final static String EXTRA_INFO = "info";
    public final static String EXTRA_ARTIST = "artist";

    private TextView title, info;
    private ImageView backgound;
    private Toolbar toolbar;
    private ListView listView;
    private FloatingActionButton floatBtn;
    private SongListAlbumAdapter adapter;
    private CallbackService callback;

    private final static int LOADER_ID = 1;

    public static DetailAlbumFragment newInstance(long albumID, String artist, String title, String info, boolean az) {
        DetailAlbumFragment detailAlbumFragment = new DetailAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_ALBUMID, albumID);
        bundle.putString(EXTRA_ARTIST, artist);
        bundle.putString(EXTRA_TITLE, title);
        bundle.putString(EXTRA_INFO, info);
        bundle.putBoolean(SilentUtils.EXTRA_ORDER, az);
        detailAlbumFragment.setArguments(bundle);
        return detailAlbumFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new SongListAlbumAdapter(context, null);
        if (context instanceof CallbackService) {
            callback = (CallbackService) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            getLoaderManager().initLoader(LOADER_ID, bundle, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fargment_detail_album, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = (TextView) view.findViewById(R.id.titleTV);
        info = (TextView) view.findViewById(R.id.infoTV);
        floatBtn = (FloatingActionButton) view.findViewById(R.id.floatButton);
        backgound = (ImageView) view.findViewById(R.id.backgroundIV);
        toolbar = (Toolbar) view.findViewById(R.id.toolBar);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        floatBtn.setOnClickListener(this);


        Bundle args = getArguments();
        if (args != null) {
            title.setText(args.getString(EXTRA_TITLE));
            info.setText(args.getString(EXTRA_INFO));
            Picasso.with(getContext())
                    .load(AlbumsLoader.getUriAlbumArt(args.getLong(EXTRA_ALBUMID)))
                    .into(backgound);
            toolbar.setTitle(args.getString(EXTRA_ARTIST));

        }

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.detail_menu);
        toolbar.setOnMenuItemClickListener(this);
        if (getArguments().getBoolean(SilentUtils.EXTRA_ORDER)) {
            toolbar.getMenu().findItem(R.id.az).setChecked(true);
        } else {
            toolbar.getMenu().findItem(R.id.za).setChecked(false);
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SongsLoader(getContext().getContentResolver()).getCursorLoaderAlbum(getContext(),
                args.getLong(EXTRA_ALBUMID), args.getBoolean(SilentUtils.EXTRA_ORDER));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callback.playMusic(position, SongsLoader.getList(adapter.getCursor()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatButton:
                callback.playMusic(0, SongsLoader.getList(adapter.getCursor()));
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.az:
                boolean az = getArguments().getBoolean(SilentUtils.EXTRA_ORDER);
                boolean checked_az = item.isChecked();
                if (checked_az != az) {
                    item.setChecked(true);
                    getArguments().putBoolean(SilentUtils.EXTRA_ORDER, false);
                    getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
                }
                break;
            case R.id.za:
                boolean _az = !getArguments().getBoolean(SilentUtils.EXTRA_ORDER);
                boolean checked_za = item.isChecked();
                if (checked_za != _az) {
                    item.setChecked(true);
                    getArguments().putBoolean(SilentUtils.EXTRA_ORDER, true);
                    getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
                }
                break;
            case R.id.settings:

                break;
        }
        return true;
    }
}
