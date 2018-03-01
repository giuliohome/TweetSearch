package org.new_year;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;

public class WrappingSlidingPaneLayout extends SlidingPaneLayout {

	public WrappingSlidingPaneLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

	    int height = 0;
	    for(int i = 0; i < getChildCount(); i++) {
	        View child = getChildAt(i);
	        child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	        int h = child.getMeasuredHeight();
	        if(h > height) height = h;
	    }

	    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
