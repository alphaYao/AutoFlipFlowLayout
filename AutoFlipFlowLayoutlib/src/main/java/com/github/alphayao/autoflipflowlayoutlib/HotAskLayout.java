package com.github.alphayao.autoflipflowlayoutlib;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * author : alphaYao
 * time : 2020/03/19
 * version: 1.0
 * desc :
 */


public class HotAskLayout extends LinearLayout {
    private ViewPager mVp;

    private int[] mDrawbleList;

    private int mMaxLine = 2;

    private OnTagClickListener mOnTagClickListener;

    public interface OnTagClickListener {
        void onClick(String tagContent);
    }

    public void setOnTagClickListener(OnTagClickListener mOnTagClickListener) {
        this.mOnTagClickListener = mOnTagClickListener;
    }

    public HotAskLayout(Context context) {
        super(context);
        init();
    }

    public HotAskLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public HotAskLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = inflate(getContext(), R.layout.layout_hotask, this);
        mVp = rootView.findViewById(R.id.vpAsk);
        mDrawbleList = new int[]{R.drawable.tag_bg_deep_blue, R.drawable.tag_bg_green,
                R.drawable.tag_bg_yellow, R.drawable.tag_bg};
    }

    public void initData(ArrayList<String> data) {

        int size = data.size();

        List<String> temp = data;

        ArrayList<View> list_view = new ArrayList<>();

        //根据数据量进行分页
        while (true) {
            View view = inflate(getContext(), R.layout.fragment_page, null);
            FlowLayout flowLayout = view.findViewById(R.id.flow);
            flowLayout.setmMaxLine(mMaxLine);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 5, 10, 5);
            if (flowLayout != null) {
                flowLayout.removeAllViews();
            }
            for (int j = 0; j < temp.size(); j++) {
                final TextView tv = new TextView(getContext());
                tv.setPadding(28, 10, 28, 10);
                tv.setText(temp.get(j));

                if (null != mOnTagClickListener) {
                    tv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnTagClickListener.onClick(tv.getText().toString());
                        }
                    });
                }
                tv.setTextColor(rgb("#ffffff"));
                tv.setSingleLine();
                tv.setBackgroundResource(mDrawbleList[j % (mDrawbleList.length)]);
                tv.setLayoutParams(layoutParams);
                flowLayout.addView(tv, layoutParams);
            }
            measureView(flowLayout);

            int lastIndex = flowLayout.getmOutIndex();

            temp = temp.subList(lastIndex, temp.size());
            size = size - lastIndex;
            list_view.add(view);

            if (size <= 0)
                break;

            if (lastIndex == 0) {
                break;
            }
        }
        ViewPageAdapter adpter = new ViewPageAdapter(list_view);
        mVp.setAdapter(adpter);

    }

    private void measureView(FlowLayout child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        //headerView的宽度信息
        int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int childMeasureHeight;
        if (lp.height > 0) {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(lp.height,
                    View.MeasureSpec.EXACTLY);
            //最后一个参数表示：适合、匹配
        } else {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);//未指定
        }
        //将宽和高设置给child
        child.measure(childMeasureWidth, childMeasureHeight);

        ViewGroup.LayoutParams thislp = mVp.getLayoutParams();
        int thischildMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, thislp.width);

        measure(thischildMeasureWidth, childMeasureHeight);

        Log.v("flowme", "gg" + getMeasuredWidth());
        Log.v("flowme", "gg" + getWidth());
        child.computeLine(getWidth() - getPaddingLeft() - getPaddingRight() - child.getPaddingRight());
    }

    private static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

}
