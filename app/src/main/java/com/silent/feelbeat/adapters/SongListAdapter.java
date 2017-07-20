package com.silent.feelbeat.adapters;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.silent.feelbeat.R;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.dataloaders.SongsLoader;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by silent on 7/7/2017.
 */

public class SongListAdapter extends CursorAdapter{

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_NORMAL = 0;

    private static final int TYPE_COUNT = 2;

    private AlphabetIndexer indexer;

    private int[] usedSectionNumbers;

    private Cursor cursor;

    //map from alphabet section to the number of other sections
    //that appear before it
    private Map<Integer, Integer> sectionToOffset;

    //map from alphabet section to the index it ought
    //to appear in
    private Map<Integer, Integer> sectionToPosition;

    public SongListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursor = c;
        sectionToOffset = null;
        sectionToPosition = null;
        indexer = null;
        usedSectionNumbers = null;
        if(c == null){
            return;
        }
        countHeader();
    }

    private void countHeader(){
        if(indexer == null){
            indexer = new AlphabetIndexer(cursor, cursor.getColumnIndex(MediaStore.Audio.Media.TITLE), "%ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        } else {
            indexer.setCursor(cursor);
        }
        if(sectionToPosition == null){
            sectionToPosition = new TreeMap<>();
        } else {
            sectionToPosition.clear();
        }

        if(sectionToOffset == null){
            sectionToOffset = new HashMap<>();
        } else {
            sectionToOffset.clear();
        }
        final int count = cursor.getCount();
        int i;
        for(i = count-1; i>=0; i--){
            sectionToPosition.put(indexer.getSectionForPosition(i), i);
        }
        i =0;
        usedSectionNumbers = new int[sectionToPosition.keySet().size()];

        for(Integer section : sectionToPosition.keySet()){
            sectionToOffset.put(section, i);
            usedSectionNumbers[i] = section;
            i++;
        }


        for(Integer section : sectionToPosition.keySet()){
            sectionToPosition.put(section, sectionToPosition.get(section)+sectionToOffset.get(section));
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case TYPE_HEADER:
                HeaderItemHolder headerHolder;
                if(convertView==null){
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs_list_header, parent, false);
                    convertView.setOnClickListener(null);
                    headerHolder = new HeaderItemHolder(convertView);
                    convertView.setTag(headerHolder);
                } else{
                    headerHolder = (HeaderItemHolder) convertView.getTag();
                }
                headerHolder.headerText.setText((String)indexer.getSections()[getSectionForPosition(position)]);
                break;
            case TYPE_NORMAL:
                SongItemHolder songHolder;
                if(convertView==null){
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs_list, parent, false);
                    songHolder = new SongItemHolder(convertView);
                    convertView.setTag(songHolder);
                } else{
                    songHolder = (SongItemHolder) convertView.getTag();
                }
                if (!cursor.moveToPosition(position - sectionToOffset.get(getSectionForPosition(position))-1)) {
                    throw new IllegalStateException("couldn't move cursor to position " + position);
                }
                songHolder.titleText.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                songHolder.artistText.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                // set image
                Picasso.with(convertView.getContext())
                        .load(AlbumsLoader.getUriAlbumArt(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))))
                        .placeholder(R.mipmap.ic_launcher)
                        .into(songHolder.avaImage);
                break;
        }
        return convertView;
    }

    public int getSectionForPosition(int position) {
        int i = 0;
        int maxLength = usedSectionNumbers.length;

        //linear scan over the used alphabetical sections' positions
        //to find where the given section fits in
        while (i < maxLength && position >= sectionToPosition.get(usedSectionNumbers[i])){
            i++;
        }
        return usedSectionNumbers[i-1];
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }

    @Override
    public int getCount() {
        if(cursor==null){
            return 0;
        }
        if(super.getCount()!=0){
            return super.getCount()+usedSectionNumbers.length;
        }


        return super.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if(cursor!=null){
            for(Integer section : usedSectionNumbers){
                if(position == sectionToPosition.get(section)){
                    return TYPE_HEADER;
                }
            }
        }
        return TYPE_NORMAL;
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        cursor = newCursor;
        if(cursor!=null){
            countHeader();
        }
        return super.swapCursor(newCursor);
    }

    @Override
    public long getItemId(int position) {
        int type = getItemViewType(position);
        switch (type){
            case TYPE_HEADER:
                return -1;
            case TYPE_NORMAL:
                cursor.moveToPosition(position - sectionToOffset.get(getSectionForPosition(position))-1);
                long id = cursor.getLong(0);
                return id;
        }
        return -1;
    }

    public static class SongItemHolder{

        public ImageView avaImage;
        public TextView titleText, artistText;

        public SongItemHolder(View itemView) {

            avaImage = (ImageView) itemView.findViewById(R.id.avaImage);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
            artistText =(TextView) itemView.findViewById(R.id.artistText);
        }

    }

    static class HeaderItemHolder{
        public TextView headerText;

        public HeaderItemHolder(View itemView){
            headerText = (TextView) itemView.findViewById(R.id.headerText);
        }
    }
}
