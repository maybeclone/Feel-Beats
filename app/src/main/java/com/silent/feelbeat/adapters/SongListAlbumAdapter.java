package com.silent.feelbeat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.silent.feelbeat.R;
import com.silent.feelbeat.utils.ColorUtils;
import com.silent.feelbeat.utils.SilentUtils;

/**
 * Created by silent on 7/29/2017.
 */

public class SongListAlbumAdapter extends CursorAdapter {


    public SongListAlbumAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_songs_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(cursor.getString(1));
        viewHolder.time.setText(SilentUtils.getStringTimeFromDuration(context, cursor.getLong(4)));
        TextDrawable drawable = TextDrawable.builder().buildRect(cursor.getPosition()+1+"", ColorUtils.COLOR_GREY);
        viewHolder.img.setImageDrawable(drawable);
    }


    static class ViewHolder{

        public ImageView img;
        public TextView title, time;

        public ViewHolder(View itemView) {
            img = (ImageView) itemView.findViewById(R.id.avaImage);
            title = (TextView) itemView.findViewById(R.id.titleText);
            time = (TextView) itemView.findViewById(R.id.artistText);
        }
    }
}
