package com.silent.feelbeat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.callback.CallbackService;
import com.silent.feelbeat.models.database.Song;
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

    class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView avatarImageView;
        private TextView titleText, singerText;

        public SongHolder(View itemView) {
            super(itemView);
            avatarImageView = (ImageView) itemView.findViewById(R.id.avaImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            singerText = (TextView) itemView.findViewById(R.id.artistText);
            itemView.setOnClickListener(this);
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
    }
}
