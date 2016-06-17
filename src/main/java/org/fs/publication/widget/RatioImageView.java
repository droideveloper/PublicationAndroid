package org.fs.publication.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.fs.publication.R;

/**
 * Created by Fatih on 07/06/16.
 * as org.fs.publication.widget.RatioImageView
 */
public class RatioImageView extends ImageView implements IRatioImageView {

    private final static float       DEFAULT_RW     = 3.0f;
    private final static float       DEFAULT_RH     = 4.0f;
    private final static boolean     DEFAULT_MIN    = true;

    private boolean      min;
    private float        rw;
    private float        rh;

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        this.rw = a.getFloat(R.styleable.RatioImageView_widthRatio,     DEFAULT_RW);
        this.rh = a.getFloat(R.styleable.RatioImageView_heightRatio,    DEFAULT_RH);
        this.min = a.getBoolean(R.styleable.RatioImageView_useMin,      DEFAULT_MIN);
        a.recycle();
    }

    @Override
    public void widthRatio(float withRatio) {
        //if value is already same we do not care about newValue
        if(rw != withRatio) {
            this.rw = withRatio;
            requestLayout(); //make view call @see #onMeasure(int w, int h)
        }
    }

    @Override public void heightRatio(float heightRatio) {
        //if value is already same we do not care about newValue
        if(rh != heightRatio) {
            this.rh = heightRatio;
            requestLayout();
        }
    }

    @Override public void hasMinimumUsed(boolean minUsed) {
        if(min != minUsed) {
            this.min = minUsed;
            requestLayout();
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = min ? Math.min(width, height) : Math.max(width, height);
        if(size == width) {
            if(min) {
                height = Math.round(width / rw * rh);
            } else {
                width = Math.round(height / rh * rw);
            }
        } else {
            if(min) {
                width = Math.round(height / rh * rw);
            } else {
                height = Math.round(width / rw * rh);
            }
        }
        setMeasuredDimension(width, height);
    }
}
