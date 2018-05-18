package com.silent.feelbeat.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.silent.feelbeat.Instance;
import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.Playlist;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.servers.song.AddSongPlaylistPostAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by silent on 5/17/2018.
 */
public class SongServerAdapter extends RecyclerView.Adapter<SongServerAdapter.SongHolder> {

    private Context context;
    private ArrayList<Song> songList;

    public SongServerAdapter(Context context){
        this.context = context;
        this.songList = new ArrayList<>();
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs_list, parent, false);
        SongHolder songHolder = new SongHolder(view);
        return songHolder;
    }

    @Override
    public void onBindViewHolder(SongHolder holder, int position) {
        Song song = songList.get(position);
        holder.bindData(song);
    }

    @Override
    public int getItemCount() {
        return (songList == null) ? 0 : songList.size();
    }

    public void swapAdapter(ArrayList<Song> songList){
        this.songList = songList;
        notifyDataSetChanged();
    }

    class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener,  View.OnCreateContextMenuListener{

        private ImageView avatarImageView;
        private TextView titleText, singerText;

        public SongHolder(View itemView) {
            super(itemView);
            avatarImageView = (ImageView) itemView.findViewById(R.id.avaImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            singerText = (TextView) itemView.findViewById(R.id.artistText);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bindData(Song song){
            Picasso.get().load(song.linkImage).config(Bitmap.Config.ARGB_4444).into(avatarImageView);
            titleText.setText(song.title);
            singerText.setText(song.artist);
        }

        @Override
        public void onClick(View v) {
            ((CallbackService) context).playMusic(getAdapterPosition(), songList);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem add = menu.add(Menu.NONE, 1, 1, "Add to playlist");
            add.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.fragment_listview, null);
                        final ListView listView = (ListView) view.findViewById(R.id.listView);
                        List<String> strings = new ArrayList<>();
                        for(Playlist p : Instance.myPlaylits){
                            strings.add(p.name);
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, strings);
                        listView.setAdapter(arrayAdapter);
                        builder.setView(view);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                new AddSongPlaylistPostAsyncTask(context, position, getAdapterPosition(), SongServerAdapter.this).execute(ConfigServer.SONG_PLAYLIST_URL);
                            }
                        });
                        builder.show();
                        break;

                }
                return true;
            }
        };
    }
}
