package com.example.liuyh73.glory;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.ScrollView;

import java.util.List;

public class DetailPagerAdapter extends FragmentPagerAdapter {
    private List<Skin> list;
    private ScrollView scrollView;
    public DetailPagerAdapter(FragmentManager fm, List<Skin> _list, ScrollView scrollView) {
        super(fm);
        list = _list;
        this.scrollView = scrollView;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).getName();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int i) {
        return SuperAwesomeCardFragment.newInstance(list.get(i).getImg_url(), list.get(i).getAvatar_url(), new SuperAwesomeCardFragment.SetBitmapAtCardFragment() {
            @Override
            public void set(Bitmap bitmap) {
                scrollView.setBackground(new BitmapDrawable(scrollView.getResources(), bitmap));
            }
        });
    }
}
