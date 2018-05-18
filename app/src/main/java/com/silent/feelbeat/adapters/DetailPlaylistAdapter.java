package com.silent.feelbeat.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.servers.song.RemoveSongPlaylistDeleteAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 5/18/2018.
 */
public class DetailPlaylistAdapter extends RecyclerView.Adapter<DetailPlaylistAdapter.DetailPlaylistHolder>{

    private Context context;
    private ArrayList<Song> songList;
    private int albumPosition;

    public DetailPlaylistAdapter(Context context, int albumPosition, ArrayList<Song> songList){
        this.context = context;
        this.songList = songList;
        this.albumPosition = albumPosition;
    }

    @Override
    public DetailPlaylistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs_list, parent, false);
        DetailPlaylistHolder detailPlaylistHolder = new DetailPlaylistHolder(view);
        return detailPlaylistHolder;
    }

    @Override
    public void onBindViewHolder(DetailPlaylistHolder holder, int position) {
        Song song = songList.get(position);
        holder.bindData(song);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void swapAdapter(ArrayList<Song> songList){
        this.songList = songList;
        notifyDataSetChanged()
;    }

    class DetailPlaylistHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

        private ImageView avatarImageView;
        private TextView titleText, singerText;

        public DetailPlaylistHolder(View itemView) {
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
            MenuItem remove = menu.add(Menu.NONE, 2, 2, "Remove");
            remove.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 2:
                        new RemoveSongPlaylistDeleteAsyncTask(context, albumPosition, getAdapterPosition(), DetailPlaylistAdapter.this).execute(ConfigServer.SONG_PLAYLIST_URL);
                        break;

                }
                return true;
            }
        };
    }
}
