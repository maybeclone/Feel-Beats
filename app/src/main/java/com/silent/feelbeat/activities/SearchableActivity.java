package com.silent.feelbeat.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.adapters.SearchAdapter;
import com.silent.feelbeat.database.models.QueryHistory;
import com.silent.feelbeat.database.table.SearchHistory;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.dataloaders.ArtistLoader;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.silent.feelbeat.fragments.DetailAlbumFragment;
import com.silent.feelbeat.fragments.DetailArtistFragment;
import com.silent.feelbeat.models.database.Album;
import com.silent.feelbeat.models.database.Artist;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.musicplayer.RemoteMusic;
import com.silent.feelbeat.utils.NavigationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Created by silent on 7/31/2017.
 */

public class SearchableActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener{

    private ListView listView;
    private Toolbar toolbar;
    private SearchAdapter adapter;
    private AsyncTask searchQuery;
    private String queryString;
    private SearchView searchView;
    private List<Object> queryHistories;
    private int position = 0;

    private final Executor mSearchExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        listView = (ListView) findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        LinearLayout emptySearch = (LinearLayout) findViewById(R.id.emptySearch);
        adapter = new SearchAdapter(this);
        queryHistories = Collections.emptyList();

        listView.setEmptyView(emptySearch);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchable_menu, menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });

        menu.findItem(R.id.search).expandActionView();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        addQueryHistory(query, System.currentTimeMillis());
        return false;
    }

    public void addQueryHistory(String text, long millis){
        if(queryHistories == null){
            queryHistories = new ArrayList<>();
        }
        position++;
        queryHistories.add(0, new QueryHistory(text, millis));
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.equals(queryString)){
            return true;
        }

        queryString = newText;

        if(searchQuery != null){
            searchQuery.cancel(true);
            searchQuery = null;
        }

        if(queryString.equals("")){
            if(thread.getState() == Thread.State.NEW){
                thread.start();
            } else {
                adapter.update(queryHistories);
            }
        } else {
            listView.setAdapter(adapter);
            searchQuery = new SearchAsyncTask().executeOnExecutor(mSearchExecutor, queryString);
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = adapter.getItem(position);

        // load az from Preference

        if(object instanceof Song){
            RemoteMusic remote = RemoteMusic.getInstance();
            ArrayList<Song> list = new ArrayList<>();
            list.add((Song) object);
            remote.play(SearchableActivity.this, list, 0);
        } else if(object instanceof Album){
            Album album = (Album) object;
            Intent intent = new Intent(SearchableActivity.this, MainActivity.class);
            intent.setAction(NavigationUtils.NAVIGATION_TO_ALBUM);
            intent.putExtra(DetailAlbumFragment.EXTRA_ALBUMID, album.id);
            intent.putExtra(DetailAlbumFragment.EXTRA_ARTIST, album.artist);
            intent.putExtra(DetailAlbumFragment.EXTRA_TITLE, album.title);
            intent.putExtra(DetailAlbumFragment.EXTRA_INFO, String.format(getString(R.string.format_time_detail_album), album.numOfSongs, album.year));
            startActivity(intent);
        } else if(object instanceof Artist){
            Artist artist = (Artist) object;
            Intent intent = new Intent(SearchableActivity.this, MainActivity.class);
            intent.setAction(NavigationUtils.NAVIGATION_TO_ARTIST);
            intent.putExtra(DetailArtistFragment.EXTRA_ARTIST_ID, artist.id);
            intent.putExtra(DetailArtistFragment.EXTRA_ARTIST, artist.title);
            startActivity(intent);
        } else if(object instanceof QueryHistory){
            searchView.setQuery(((QueryHistory) object).text, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchHistory.getInstance(SearchableActivity.this).insertDB(queryHistories.subList(0, position));
            }
        }).start();
    }

    private class SearchAsyncTask extends AsyncTask<String,Void, ArrayList<Object>>{

        @Override
        protected ArrayList<Object> doInBackground(String... params) {
            ArrayList<Object> list = new ArrayList<>();

            ArrayList<Song> songs = new SongsLoader(getContentResolver()).getList(params[0], 7);
            if(!songs.isEmpty()){
                list.add("SONGS");
                list.addAll(songs);
            }
            if(isCancelled()){
                return null;
            }

            ArrayList<Artist> artists = new ArtistLoader(getContentResolver()).getList(params[0], 5);
            if(!artists.isEmpty()){
                list.add("ARTISTS");
                list.addAll(artists);
            }
            if(isCancelled()){
                return null;
            }

            ArrayList<Album> albums = new AlbumsLoader(getContentResolver()).getList(params[0], 5);
            if(!albums.isEmpty()){
                list.add("ALBUMS");
                list.addAll(albums);
            }

            if(list.size()==0){
                return null;
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> items) {
            super.onPostExecute(items);
            if(items!=null) {
                adapter.update(items);
            } else {
                adapter.update(Collections.emptyList());
            }
        }
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            queryHistories = SearchHistory.getInstance(SearchableActivity.this).getListHistorySearch();
            runOnUiThread(runnable);
        }
    });

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            adapter.update(queryHistories);
        }
    };
}
