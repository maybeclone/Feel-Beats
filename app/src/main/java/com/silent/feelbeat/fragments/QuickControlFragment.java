package com.silent.feelbeat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.silent.feelbeat.R;

/**
 * Created by silent on 7/20/2017.
 */

public class QuickControlFragment extends Fragment implements View.OnClickListener {

    private ImageButton play, next, previous;

    public static QuickControlFragment newInstance(){
        return new QuickControlFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quick_control, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        play = (ImageButton) view.findViewById(R.id.playIB);
        next = (ImageButton) view.findViewById(R.id.nextIB);
        previous = (ImageButton) view.findViewById(R.id.previousIB);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previousIB:

                break;
            case R.id.nextIB:

                break;
            case R.id.playIB:

                break;
        }
    }
}
