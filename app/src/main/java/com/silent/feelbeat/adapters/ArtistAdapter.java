package com.silent.feelbeat.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.silent.feelbeat.R;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.models.Artist;
import com.silent.feelbeat.utils.ColorUtils;
import com.silent.feelbeat.utils.SilentUtils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by silent on 7/14/2017.
 */

public class ArtistAdapter extends CursorAdapter {


    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_COUNT = 2;

    // <section, position>
    private Map<Integer, Integer> positionHeader;
    // <section, num of section in front of this section>
    private Map<Integer, Integer> sectionToOffset;
    private int[] usedSection;
    private AlphabetIndexer alphabetIndexer;
    private Cursor cursor;


    public ArtistAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursor = c;
        if(c == null){
            positionHeader = null;
            sectionToOffset = null;
            usedSection = null;
            alphabetIndexer = null;
            return;
        }
        countHeader();
    }

    private void countHeader(){
        if(alphabetIndexer == null){
            alphabetIndexer = new AlphabetIndexer(cursor, 1, "%ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        } else {
            alphabetIndexer.setCursor(cursor);
        }
        if(positionHeader == null){
            positionHeader = new TreeMap<>();
        } else {
            positionHeader.clear();
        }
        if(sectionToOffset == null){
            sectionToOffset = new HashMap<>();
        } else {
            sectionToOffset.clear();
        }
        int count = cursor.getCount();
        int i;
        for(i = count-1; i>=0; i--){
            positionHeader.put(alphabetIndexer.getSectionForPosition(i), i);
        }
        i=0;
        usedSection = new int[positionHeader.keySet().size()];
        for(Integer section : positionHeader.keySet()){
            sectionToOffset.put(section, i);
            usedSection[i] = section;
            i++;
        }

        for(Integer section : positionHeader.keySet()){
            positionHeader.put(section, positionHeader.get(section)+sectionToOffset.get(section));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        switch (type){
            case TYPE_HEADER:
                HeaderHolder headerHolder;
                if(convertView==null){
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs_list_header, parent, false);
                    headerHolder = new HeaderHolder(convertView);
                    convertView.setTag(headerHolder);
                    convertView.setOnClickListener(null);
                } else{
                    headerHolder = (HeaderHolder) convertView.getTag();
                }
                headerHolder.headerText.setText((CharSequence) alphabetIndexer.getSections()[getSectionToPosition(position)]);
                break;
            case TYPE_NORMAL:
                ArtistHolder artistHolder;
                if(convertView==null){
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs_list, parent, false);
                    artistHolder = new ArtistHolder(convertView);
                    convertView.setTag(artistHolder);
                } else{
                    artistHolder = (ArtistHolder) convertView.getTag();
                }
                if(!cursor.moveToPosition(position - sectionToOffset.get(getSectionToPosition(position))-1)){
                    throw new ArrayIndexOutOfBoundsException("can't move to "+position);
                }
                String artist = cursor.getString(1);
                TextDrawable textDrawable = TextDrawable.builder()
                        .buildRect(SilentUtils.getSubString(artist), ColorUtils.getColorStringLength(artist.length()));
                artistHolder.albumArt.setImageDrawable(textDrawable);
                artistHolder.artistText.setText(artist);
                artistHolder.infoText.setText(String.format(parent.getContext().getString(R.string.format_info_artist),
                                                    cursor.getInt(3), cursor.getInt(4)));
                break;
        }

        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }



    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    public int getSectionToPosition(int position){
        int maxLength = usedSection.length;
        int i=0;
        while( i<maxLength && position >= positionHeader.get(usedSection[i])){
            i++;
        }
        return usedSection[i-1];
    }

    @Override
    public int getItemViewType(int position) {
        for(Integer section : usedSection){
            if(position == positionHeader.get(section)){
                return TYPE_HEADER;
            }
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getCount() {
        if(cursor == null)
            return 0;
        if(super.getCount()!=0){
            return super.getCount()+usedSection.length;
        }
        return super.getCount();
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        cursor = newCursor;
        if(newCursor!=null) {
            countHeader();
        }
        return super.swapCursor(newCursor);
    }

    static class ArtistHolder{
        public ImageView albumArt;
        public TextView artistText, infoText;

        public ArtistHolder(View itemView){
            albumArt = (ImageView) itemView.findViewById(R.id.avaImage);
            artistText = (TextView) itemView.findViewById(R.id.titleText);
            infoText = (TextView) itemView.findViewById(R.id.artistText);
        }
    }

    static class HeaderHolder{
        public TextView headerText;

        public HeaderHolder(View itemView){
            headerText = (TextView) itemView.findViewById(R.id.headerText);
        }
    }
}
