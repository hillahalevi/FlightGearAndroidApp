package com.example.flightgearandroidapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * The class is responsible for the joystick display
 */
public class JoystickView extends View {
    private Paint outerCircle;
    private Paint innerCircle;
    private Paint backgroundColor;
    private int CENTER_X;
    private int CENTER_Y;
    private int currX;
    private int currY;
    private int OUTER_RADIUS;
    private int INNER_RADIUS;

    private int androidStatusBarHeight;

    public JoystickView(Context context) {
        super(context);
        setJoystickColors();
        setJoystickParams();
    }


    /**
     * set joystick colors
     */
    private void setJoystickColors() {
        this.outerCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.outerCircle.setColor(Color.WHITE);
        this.outerCircle.setStyle(Paint.Style.FILL);

        this.innerCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.innerCircle = new Paint(Color.GRAY);
        this.innerCircle.setStyle(Paint.Style.FILL);

        this.backgroundColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.backgroundColor.setColor(Color.rgb(0, 150, 136));
        this.backgroundColor.setStyle(Paint.Style.FILL);
    }

    /**
     * set it nicely in the screen
     */
    private void setJoystickParams() {
        // get the dimensions of the actual screen
        DisplayMetrics dm = getResources().getDisplayMetrics();
        currX = CENTER_X = dm.widthPixels / 2;
        currY = CENTER_Y = dm.heightPixels / 2;
        OUTER_RADIUS = dm.widthPixels / 3;
        INNER_RADIUS = OUTER_RADIUS / 3;
        this.androidStatusBarHeight = this.getAndroidStatusBarHeight() * (int) dm.density;

    }


    /**
     * Get the height of the android status bar
     *
     * @return the height of the status bar
     */
    private int getAndroidStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, CENTER_X * 2, CENTER_Y * 2, backgroundColor);
        canvas.drawCircle(CENTER_X, CENTER_Y - androidStatusBarHeight, OUTER_RADIUS, outerCircle);
        canvas.drawCircle(currX, currY - androidStatusBarHeight, INNER_RADIUS, innerCircle);
    }


    /**
     * A change in orientation has bees made -replace the joystick
     *
     * @param w    - the width of the current orientation
     * @param h    - the height of the current orientation
     * @param oldw - the width of the previous orientation
     * @param oldh - the height of the previous orientation
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        currX = CENTER_X = w / 2;
        currY = CENTER_Y = (h + androidStatusBarHeight) / 2;

        OUTER_RADIUS = Math.min(CENTER_X, CENTER_Y) * 2 / 3;
        INNER_RADIUS = OUTER_RADIUS / 3;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    public int getCurrX() {
        return this.currX;
    }

    public int getCurrY() {
        return this.currY;
    }


    public void setX(int x) {
        this.currX = x;
    }

    public void setY(int y) {
        this.currY = y;
    }


    public int getCenterX() {
        return CENTER_X;
    }

    public int getCenterY() {
        return CENTER_Y;
    }

    public int getOuterRadius() {
        return OUTER_RADIUS;
    }

    public int getInnerRadius() {
        return INNER_RADIUS;
    }
}