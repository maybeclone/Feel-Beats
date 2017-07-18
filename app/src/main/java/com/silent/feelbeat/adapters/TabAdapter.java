package com.silent.feelbeat.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.silent.feelbeat.utils.SilentUtils;

import java.util.List;

/**
 * Created by silent on 7/18/2017.
 */

public class TabAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;

    public TabAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Bundle args = list.get(position).getArguments();
        return args.getString(SilentUtils.TITLE_FRAGMENT);
    }
}
