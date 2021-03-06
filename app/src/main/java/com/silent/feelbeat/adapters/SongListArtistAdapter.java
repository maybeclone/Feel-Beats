package com.silent.feelbeat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.squareup.picasso.Picasso;

/**
 * Created by silent on 7/27/2017.
 */

public class SongListArtistAdapter extends CursorAdapter {

    public SongListArtistAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_songs_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Picasso.with(context).load(AlbumsLoader.getUriAlbumArt(cursor.getLong(7))).into(viewHolder.art);
        viewHolder.titleText.setText(cursor.getString(1));
        viewHolder.albumText.setText(cursor.getString(5));
    }

    private static class ViewHolder{
        public ImageView art;
        public TextView titleText;
        public TextView albumText;

        public ViewHolder(View view){
            art = (ImageView) view.findViewById(R.id.avaImage);
            titleText = (TextView) view.findViewById(R.id.titleText);
            albumText = (TextView) view.findViewById(R.id.artistText);
        }

    }
}
