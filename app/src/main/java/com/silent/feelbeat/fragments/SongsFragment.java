package com.silent.feelbeat.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
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
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.SongListAdapter;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.utils.SilentUtils;

import org.w3c.dom.Text;

import java.io.IOException;

/**
 * Created by silent on 7/17/2017.
 */

public class SongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    // Loader
    private final static int LOADER_ID = 0x0001;

    // View
    private SongListAdapter adapter;
    private ListView songsList;

    // Connect Service
    private ServiceConnection mConnection;
    private Messenger mMessenger;
    private boolean mBound = false;

    // Communicate Activity
    private CallbackService callBackService;

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
        if (context instanceof CallbackService) {
            callBackService = (CallbackService) context;
        } else {
            throw new ClassCastException("Must implement CallBackService to handle communication with activity");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMessenger = new Messenger(service);
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
            }
        };
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
        songsList.setEmptyView(view.findViewById(R.id.emptyText));
        songsList.setOnItemClickListener(this);
        songsList.setAdapter(adapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new SongsLoader().getCursorLoader(getActivity());
        return cursorLoader;
    }

    @Override
    public void onStop() {
        super.onStop();
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
        callBackService.playMusic(adapter.getRealPosition(position), adapter.getCursor());
    }

    public SongListAdapter getAdapter(){
        return this.adapter;
    }

}
