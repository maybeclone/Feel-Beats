package com.silent.feelbeat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Target;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.abstraction.RecyclerViewCursor;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.models.Album;
import com.silent.feelbeat.utils.ColorUtils;
import com.silent.feelbeat.utils.SilentUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by silent on 7/13/2017.
 */

public class AlbumAdapter extends RecyclerViewCursor<AlbumAdapter.AlbumHolder> {


    public AlbumAdapter(Context context, Cursor cursor){
        super(context, cursor);
    }

    @Override
    public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albums_list, parent, false);
        AlbumHolder albumHolder = new AlbumHolder(view);
        return albumHolder;
    }

    @Override
    public void onBindViewHolder(final AlbumHolder holder, Cursor cursor) {
        // set image

        final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.albumArt.setImageBitmap(bitmap);
                new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        if(swatch != null){
                            int color = swatch.getRgb();
                            holder.background.setBackgroundColor(color);
                            int textColor = ColorUtils.getBlackWhiteColor(color);
                            holder.albumTitle.setTextColor(textColor);
                            holder.numOfSong.setTextColor(textColor);
                        } else {
                            Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                            if(mutedSwatch != null){
                                int color = mutedSwatch.getRgb();
                                holder.background.setBackgroundColor(color);
                                int textColor = ColorUtils.getBlackWhiteColor(color);
                                holder.albumTitle.setTextColor(textColor);
                                holder.numOfSong.setTextColor(textColor);
                            }
                        }
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                holder.albumArt.setImageDrawable(placeHolderDrawable);
            }
        };

        /*Target in Picasso is weak preference so setTag method will help us has strong preference */
        holder.albumArt.setTag(target);

        Picasso.with(holder.albumArt.getContext())
                .load(AlbumsLoader.getUriAlbumArt(cursor.getLong(0)))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(target);

        holder.albumTitle.setText(cursor.getString(1));
        holder.numOfSong.setText(String.format(holder.albumArt.getContext().getString(R.string.format_num_of_song),
                                    cursor.getInt(4)));
    }

    static class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView albumArt;
        public TextView numOfSong, albumTitle;
        public LinearLayout background;


        public AlbumHolder(View itemView) {
            super(itemView);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            numOfSong = (TextView) itemView.findViewById(R.id.numOfSong);
            albumTitle = (TextView) itemView.findViewById(R.id.albumTitle);
            background = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }


}
