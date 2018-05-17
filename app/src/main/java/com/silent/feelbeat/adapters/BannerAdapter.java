package com.silent.feelbeat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.silent.feelbeat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 5/17/2018.
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {

    List<Integer> bitmapIds;

    public BannerAdapter(){
        bitmapIds = new ArrayList<>();
        bitmapIds.add(R.drawable.banner_two);
        bitmapIds.add(R.drawable.banner_one);
        bitmapIds.add(R.drawable.banner_three);
    }

    @Override
    public BannerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        BannerHolder bannerHolder = new BannerHolder(view);
        return bannerHolder;
    }

    @Override
    public void onBindViewHolder(BannerHolder holder, int position) {
        holder.bindData(bitmapIds.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmapIds.size();
    }

    class BannerHolder extends RecyclerView.ViewHolder{

        private ImageView bannerImageView;

        public BannerHolder(View itemView) {
            super(itemView);
            bannerImageView = (ImageView) itemView.findViewById(R.id.bannerImageView);
        }

        public void bindData(int id){
            bannerImageView.setImageResource(id);
        }
    }
}
