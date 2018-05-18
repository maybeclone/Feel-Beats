package com.silent.feelbeat.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.silent.feelbeat.R;
import com.silent.feelbeat.activities.DetailPlaylistActivity;
import com.silent.feelbeat.configs.ConfigServer;
import com.silent.feelbeat.models.Playlist;
import com.silent.feelbeat.servers.playlist.PlaylistDeleteAsyncTask;
import com.silent.feelbeat.servers.playlist.PlaylistPutAsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by silent on 5/18/2018.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {

    private List<Playlist> playlistList;

    public PlaylistAdapter(){
//        playlistList = Collections.emptyList();
        playlistList = new ArrayList<>();
        playlistList.add(new Playlist());
        playlistList.add(new Playlist());
        playlistList.add(new Playlist());
        playlistList.add(new Playlist());
    }

    @Override
    public PlaylistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        PlaylistHolder holder = new PlaylistHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlaylistHolder holder, int position) {
        Playlist playlist = playlistList.get(position);
        holder.bindData(playlist);
    }

    @Override
    public int getItemCount() {
        return (playlistList == null) ? 0 : playlistList.size();
    }

    public void swapAdapter(List<Playlist> playlists){
        this.playlistList = playlists;
        notifyDataSetChanged();
    }

    public void addPlaylist(Playlist playlist){
        this.playlistList.add(playlist);
        notifyDataSetChanged();
    }

    class PlaylistHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener{

        TextView nameText;
        TextView countSongsText;
        Context context;

        public PlaylistHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            nameText = (TextView) itemView.findViewById(R.id.nameText);
            countSongsText = (TextView) itemView.findViewById(R.id.countSongsText);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }

        public void bindData(Playlist playlist){
            nameText.setText(playlist.name);
            if(playlist.songArrayList != null) {
                countSongsText.setText(playlist.songArrayList.size() + " songs");
            } else {
                countSongsText.setText("0 song");
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem edit = menu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");
            edit.setOnMenuItemClickListener(onEditMenu);
            delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.add_playlist_dialog, null);
                        final EditText editText = (EditText) view.findViewById(R.id.nameText);
                        builder.setView(view);
                        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!editText.getText().toString().isEmpty()){
                                    Playlist playlist = new Playlist();
                                    playlist.name = editText.getText().toString();
                                    playlist.id = playlistList.get(getAdapterPosition()).id;
                                    new PlaylistPutAsyncTask(context, playlist, PlaylistAdapter.this).execute(ConfigServer.PLAYLIST_URL);
                                } else{
                                    Toast.makeText(context, "Please complete all information", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.show();
                        break;

                    case 2:
                        new PlaylistDeleteAsyncTask(context, playlistList.get(getAdapterPosition()), PlaylistAdapter.this).execute(ConfigServer.PLAYLIST_URL);
                        break;
                }
                return true;
            }
        };

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DetailPlaylistActivity.class);
            intent.putExtra("title", playlistList.get(getAdapterPosition()).name);
            intent.putExtra("list", playlistList.get(getAdapterPosition()).songArrayList);
            intent.putExtra("position", getAdapterPosition());
            ((Activity) context).startActivityForResult(intent, 0);
        }
    }

}
