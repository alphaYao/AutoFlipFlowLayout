package com.github.alphayao.autoflipflowlayoutlib;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * author : alphaYao
 * time : 2020/03/20
 * version: 1.0
 * desc :
 */


public class FlowLayout extends ViewGroup {

    private int mMaxLine = 2;

    private int mIndex = 0;

    private int mOutIndex = 0;

    private ArrayList<Integer> mLinePaddingList = new ArrayList<>();

    public void setmMaxLine(int mMaxLine) {
        this.mMaxLine = mMaxLine;
    }


    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //遍历去调用所有子元素的measure方法（child.getMeasuredHeight()才能获取到值，否则为0）
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = 0, measuredHeight = 0;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widtMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //由于计算子view所占宽度，这里传值需要自身减去PaddingRight宽度，PaddingLeft会在接下来计算子元素位置时加上
        Map<String, Integer> compute = compute(widthSize - getPaddingRight());

        //EXACTLY模式：对应于给定大小或者match_parent情况
        //AT_MOS模式：对应wrap-content（需要手动计算大小，否则相当于match_parent）
        if (widtMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize;
        } else if (widtMode == MeasureSpec.AT_MOST) {
            measuredWidth = compute.get("allChildWidth");
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = compute.get("allChildHeight");
        }

        //设置flow的宽高
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < mIndex; i++) {
            View child = getChildAt(i);
            FlowTagBean tagBean = (FlowTagBean) getChildAt(i).getTag();
            Rect rect = tagBean.getRect();

            if (tagBean.getLine() - 1 < mLinePaddingList.size())
                child.layout(rect.left + mLinePaddingList.get(tagBean.getLine() - 1), rect.top,
                        rect.right + mLinePaddingList.get(tagBean.getLine() - 1), rect.bottom);

            else
                child.layout(rect.left, rect.top,
                        rect.right, rect.bottom);
        }

    }

    /**
     * 测量过程
     *
     * @param flowWidth 该view的宽度
     * @return 返回子元素总所占宽度和高度（用于计算Flowlayout的AT_MOST模式设置宽高）
     */
    public Map<String, Integer> compute(int flowWidth) {

        int line = 1;

        //是否是单行
        boolean aRow = true;

        MarginLayoutParams marginParams;//子元素margin
        int rowsWidth = getPaddingLeft();//当前行已占宽度(注意需要加上paddingLeft)
        int columnHeight = getPaddingTop();//当前行顶部已占高度(注意需要加上paddingTop)
        int rowsMaxHeight = 0;//当前行所有子元素的最大高度（用于换行累加高度）


        int tempIndex;
        for (tempIndex = 0; tempIndex < getChildCount(); tempIndex++) {

            View child = getChildAt(tempIndex);

            //获取元素测量宽度和高度
            int measuredWidth = child.getMeasuredWidth();
            int measuredHeight = child.getMeasuredHeight();

            //获取元素的margin
            marginParams = (MarginLayoutParams) child.getLayoutParams();

            //子元素所占宽度 = MarginLeft+ child.getMeasuredWidth+MarginRight  注意此时不能child.getWidth,
            // 因为界面没有绘制完成，此时wdith为0
            int childWidth = marginParams.leftMargin + marginParams.rightMargin + measuredWidth;
            int childHeight = marginParams.topMargin + marginParams.bottomMargin + measuredHeight;

            rowsMaxHeight = Math.max(rowsMaxHeight, childHeight);

            //判断是否换行： 该行已占大小+该元素大小>父容器宽度  则换行
            if (rowsWidth + childWidth > flowWidth) {

                //重置行宽度
                rowsWidth = getPaddingLeft();

                //累加上该行子元素最大高度
                columnHeight += rowsMaxHeight;

                //重置该行最大高度
                rowsMaxHeight = childHeight;

                aRow = false;

                line++;
                if (line == mMaxLine + 1) {
                    break;
                }
            }

            //累加上该行子元素宽度
            rowsWidth += childWidth;

            //判断时占的宽段时加上margin计算，设置顶点位置时不包括margin位置，不然margin会不起作用，这是给View设置tag,
            // 在onlayout给子元素设置位置再遍历取出
            child.setTag(new FlowTagBean(line,
                    new Rect(rowsWidth - childWidth + marginParams.leftMargin,
                            columnHeight + marginParams.topMargin,
                            rowsWidth - marginParams.rightMargin,
                            columnHeight + childHeight - marginParams.bottomMargin)));

        }

        mIndex = tempIndex;

        //返回子元素总所占宽度和高度（用于计算Flowlayout的AT_MOST模式设置宽高）
        Map<String, Integer> flowMap = new HashMap<>();

        if (aRow) {
            flowMap.put("allChildWidth", rowsWidth);
        } else {
            flowMap.put("allChildWidth", flowWidth);
        }

        //FlowLayout测量高度 = 当前行顶部已占高度 +当前行内子元素最大高度+FlowLayout的PaddingBottom
        flowMap.put("allChildHeight", columnHeight + rowsMaxHeight + getPaddingBottom());

        return flowMap;
    }

    /**
     * 计算行数
     *
     * @param flowWidth 行宽度
     */
    public void computeLine(int flowWidth) {
        int outLine = 1;

        MarginLayoutParams marginParams;

        int rowsWidth = getPaddingLeft();

        int tempIndex;
        for (tempIndex = 0; tempIndex < getChildCount(); tempIndex++) {
            Log.v("flowme", "ff" + rowsWidth);

            View child = getChildAt(tempIndex);
            int measuredWidth = child.getMeasuredWidth();

            marginParams = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = marginParams.leftMargin + marginParams.rightMargin + measuredWidth;

            if (rowsWidth + childWidth > flowWidth) {

                //计算居中的左移值
                int pad = flowWidth - getPaddingLeft() - rowsWidth;
                mLinePaddingList.add(pad / 2);

                rowsWidth = getPaddingLeft();

                outLine++;
                if (outLine == mMaxLine + 1) {
                    break;
                }
            }
            rowsWidth += childWidth;
        }

        if (tempIndex == getChildCount()) {
            int pad = flowWidth - getPaddingLeft() - rowsWidth;
            mLinePaddingList.add(pad / 2);
        }

        mOutIndex = tempIndex;
    }

    public int getmOutIndex() {
        return mOutIndex;
    }
}

