package com.silent.feelbeat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.abstraction.RecyclerViewCursor;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.squareup.picasso.Picasso;

/**
 * Created by silent on 8/11/2017.
 */

public class AlbumsListAtrtistAdapter extends RecyclerViewCursor<AlbumsListAtrtistAdapter.ViewHolder> {


    public AlbumsListAtrtistAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail_artist, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        Picasso.get().load(AlbumsLoader.getUriAlbumArt(cursor.getLong(0))).into(holder.img);
        holder.title.setText(cursor.getString(1));
        holder.numOfSongs.setText(String.format(mContext.getString(R.string.format_num_of_song),
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS))));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, numOfSongs;
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titleText);
            numOfSongs = (TextView) itemView.findViewById(R.id.numOfSong);
            img = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
