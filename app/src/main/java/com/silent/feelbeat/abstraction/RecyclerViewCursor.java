package com.silent.feelbeat.abstraction;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

import com.silent.feelbeat.adapters.AlbumAdapter;

import java.util.List;

/**
 * Created by silent on 7/13/2017.
 */

public abstract class RecyclerViewCursor<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected Cursor mCursor;
    private boolean mDataVaild;
    private int mRowIdColum;
    private DataSetObserver mDataSetObserver;


    public RecyclerViewCursor(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
        mDataVaild = (cursor != null);
        mRowIdColum = mDataVaild ? mCursor.getColumnIndex("_id") : -1;
//        mDataSetObserver = new NotifyingDataSetObserver();
//        if(mCursor != null){
//            mCursor.registerDataSetObserver(mDataSetObserver);
//        }
    }

    public Cursor getCursor(){
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if(mDataVaild && mCursor!=null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if(mDataVaild && mCursor != null && mCursor.moveToPosition(position)){
            return mCursor.getLong(mRowIdColum);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }


    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if(!mDataVaild){
            throw new IllegalStateException("this should only be called when cursor is valid");
        }
        if(!mCursor.moveToPosition(position)){
            throw new IllegalStateException("can't move cursor to position "+position);
        }
        onBindViewHolder(holder, mCursor);
    }

    public void changeCursor(Cursor cursor){
        Cursor old = swapCursor(cursor);
        if(old!=null){
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if(newCursor == mCursor){
            return null;
        }
        final Cursor oldCursor = mCursor;
//        if(oldCursor != null && mDataSetObserver != null){
//            oldCursor.unregisterDataSetObserver(mDataSetObserver);
//        }
        mCursor = newCursor;
        if(mCursor != null){
//            if(mDataSetObserver != null){
//                mCursor.registerDataSetObserver(mDataSetObserver);
//            }
            mRowIdColum = newCursor.getColumnIndexOrThrow("_id");
            mDataVaild = true;
            notifyDataSetChanged();
        } else{
            mRowIdColum = -1;
            mDataVaild = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

//    private class NotifyingDataSetObserver extends DataSetObserver{
//        @Override
//        public void onChanged() {
//            super.onChanged();
//            mDataVaild = true;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public void onInvalidated() {
//            super.onInvalidated();
//            mDataVaild = false;
//            notifyDataSetChanged();
//        }
//    }
}
