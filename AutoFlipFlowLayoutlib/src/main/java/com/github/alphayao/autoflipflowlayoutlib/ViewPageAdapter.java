package com.github.alphayao.autoflipflowlayoutlib;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * author : alphaYao
 * time : 2020/03/20
 * version: 1.0
 * desc :
 */


public class ViewPageAdapter extends PagerAdapter {
    private List<View> list_view;

    public ViewPageAdapter(List<View> list_view) {
        this.list_view = list_view;
    }

    @Override
    public int getCount() {
        return list_view.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list_view.get(position % list_view.size()));
        return list_view.get(position % list_view.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list_view.get(position % list_view.size()));
    }
}
