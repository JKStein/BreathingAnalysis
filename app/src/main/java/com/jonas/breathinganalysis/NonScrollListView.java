package com.jonas.breathinganalysis;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Non scrolling list view.
 * Needed to use non scrolling ListViews inside a ScrollView.
 * Source: https://stackoverflow.com/questions/18813296/non-scrollable-listview-inside-scrollview
 * @author Dedaniya HirenKumar, comments: Jonas Stein
 */

public class NonScrollListView extends ListView {
    //Call super constructor on constructor called.
    public NonScrollListView(Context context) {
        super(context);
    }
    public NonScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NonScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Measure the view and its content to determine the measured width and the measured height.
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Set height attribute to the highest possible value.
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 1, MeasureSpec.AT_MOST);
        //Calling the superclass' onMeasure to store the measurements of this view.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        //Tell parent the set height.
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}