package com.turnbased.appwarp.longestline;

import com.turnbased.appwarp.longestline.Utilities.Sequence;

public class Result{
    public int length;
    public int begin;
    public int end;
    public Sequence order;
    public Result(int l, int b, int e, Sequence o){
        length = l;
        begin = b;
        end = e;
        order = o;
    }
}
