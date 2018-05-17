package com.silent.feelbeat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
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
import com.silent.feelbeat.fragments.AlbumsFragment;
import com.silent.feelbeat.utils.ColorUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by silent on 7/13/2017.
 */

public class AlbumAdapter extends RecyclerViewCursor<AlbumAdapter.AlbumHolder> {

    private AlbumsFragment.CallbackAlbumsFragment callback;

    public AlbumAdapter(Context context, Cursor cursor, AlbumsFragment.CallbackAlbumsFragment callback) {
        super(context, cursor);
        this.callback = callback;
    }

    @Override
    public AlbumHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albums_list, parent, false);
        final AlbumHolder albumHolder = new AlbumHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(albumHolder.getAdapterPosition());
                callback.onItemClick(mCursor.getLong(0),
                        mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
                        mCursor.getString(1),
                        String.format(parent.getContext().getString(R.string.format_time_detail_album),
                                mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)),
                                mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR))), true);
            }
        });
        return albumHolder;
    }

    @Override
    public void onBindViewHolder(final AlbumHolder holder, Cursor cursor) {
        final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.albumArt.setImageBitmap(bitmap);
                new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        if (swatch != null) {
                            int color = swatch.getRgb();
                            holder.background.setBackgroundColor(color);
                            int textColor = ColorUtils.getBlackWhiteColor(color);
                            holder.albumTitle.setTextColor(textColor);
                            holder.numOfSong.setTextColor(textColor);
                        } else {
                            Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                            if (mutedSwatch != null) {
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
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS))));
    }


    static class AlbumHolder extends RecyclerView.ViewHolder {

        public ImageView albumArt;
        public TextView numOfSong, albumTitle;
        public LinearLayout background;

        public AlbumHolder(View itemView) {
            super(itemView);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            numOfSong = (TextView) itemView.findViewById(R.id.numOfSong);
            albumTitle = (TextView) itemView.findViewById(R.id.albumTitle);
            background = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }

    }


}
