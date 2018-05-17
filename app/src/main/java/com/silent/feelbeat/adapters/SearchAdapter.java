package com.silent.feelbeat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.silent.feelbeat.R;
import com.silent.feelbeat.abstraction.Item;
import com.silent.feelbeat.database.models.QueryHistory;
import com.silent.feelbeat.dataloaders.AlbumsLoader;
import com.silent.feelbeat.models.database.Album;
import com.silent.feelbeat.models.database.Artist;
import com.silent.feelbeat.models.database.Song;
import com.silent.feelbeat.utils.ColorUtils;
import com.silent.feelbeat.utils.SilentUtils;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by silent on 8/1/2017.
 */

public class SearchAdapter extends BaseAdapter {

    private final static int TYPE_COUNT = 5;
    private final static int HISTORY = 4;
    private final static int SONG = 1;
    private final static int ARTIST = 2;
    private final static int ALBUM = 3;
    private final static int HEADER = 0;

    private List<Object> list;
    private Context context;

    public SearchAdapter(Context context) {
        this.context = context;
        list = Collections.emptyList();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        int type = getItemViewType(position);
        if (type != HEADER && type !=HISTORY) {
            Item item = (Item) list.get(position);
            return item.id;
        }
        return list.get(position).hashCode();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = list.get(position);
        if (item instanceof Song) {
            return SONG;
        } else if (item instanceof Album) {
            return ALBUM;
        } else if (item instanceof Artist) {
            return ARTIST;
        } else if(item instanceof QueryHistory){
            return HISTORY;
        } else{
            return HEADER;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ItemHolder holder ;
        HeaderHolder headerHolder;
        SearchHolder searchHolder;
        if (convertView == null) {
            switch (type) {
                case HEADER:
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_search_header, parent, false);
                    headerHolder = new HeaderHolder(convertView);
                    convertView.setTag(headerHolder);
                    convertView.setOnClickListener(null);
                    break;
                case HISTORY:
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_search_history, parent, false);
                    searchHolder = new SearchHolder(convertView);
                    convertView.setTag(searchHolder);
                    break;
                default:
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_songs_list, parent, false);
                    holder = new ItemHolder(convertView);
                    convertView.setTag(holder);
                    break;
            }
        }
        switch (type) {
            case SONG:
                holder = (ItemHolder) convertView.getTag();
                Song song = (Song) list.get(position);
                holder.title.setText(song.title);
                holder.info.setText(song.artist);
                Picasso.with(context).load(AlbumsLoader.getUriAlbumArt(song.albumId)).into(holder.imageView);
                break;
            case ARTIST:
                holder = (ItemHolder) convertView.getTag();
                Artist artist = (Artist) list.get(position);
                holder.title.setText(artist.title);
                holder.info.setText(String.format(context.getString(R.string.format_info_artist),
                        artist.numOfArtist, artist.numOfAlbum));
                TextDrawable artistName = TextDrawable.builder()
                        .buildRect(SilentUtils.getSubString(artist.title), ColorUtils.getColorStringLength(artist.title.length()));
                holder.imageView.setImageDrawable(artistName);
                break;
            case ALBUM:
                holder = (ItemHolder) convertView.getTag();
                Album album = (Album) list.get(position);
                holder.title.setText(album.title);
                holder.info.setText(String.format(context.getString(R.string.format_time_detail_album),
                        album.numOfSongs, album.year));
                Picasso.with(context).load(AlbumsLoader.getUriAlbumArt(album.id)).into(holder.imageView);
                break;
            case HEADER:
                headerHolder = (HeaderHolder) convertView.getTag();
                String title = (String) list.get(position);
                headerHolder.textView.setText(title);
                break;
            case HISTORY:
                searchHolder = (SearchHolder) convertView.getTag();
                QueryHistory query = (QueryHistory) list.get(position);
                searchHolder.text.setText(query.text);
                break;
        }

        return convertView;
    }

    public void update(List<Object> newList) {
        this.list = newList;
        this.notifyDataSetChanged();
    }

    private static class ItemHolder {
        public ImageView imageView;
        public TextView title, info;

        public ItemHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.avaImage);
            title = (TextView) view.findViewById(R.id.titleText);
            info = (TextView) view.findViewById(R.id.artistText);
        }
    }

    private static class HeaderHolder {
        public TextView textView;

        public HeaderHolder(View view) {
            textView = (TextView) view.findViewById(R.id.text);
        }
    }

    private static class SearchHolder{
        public TextView text;

        public SearchHolder(View view){
            text = (TextView) view.findViewById(R.id.text);
        }
    }

}
