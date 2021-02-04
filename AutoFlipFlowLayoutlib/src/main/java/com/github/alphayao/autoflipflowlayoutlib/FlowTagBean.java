package com.github.alphayao.autoflipflowlayoutlib;

import android.graphics.Rect;

/**
 * author : alphaYao
 * time : 2020/03/22
 * version: 1.0
 * desc :
 */


public class FlowTagBean {
    private int line;
    private Rect rect;

    public FlowTagBean(int line, Rect rect) {
        this.line = line;
        this.rect = rect;
    }

    public int getLine() {
        return line;
    }

    public Rect getRect() {
        return rect;
    }
}
