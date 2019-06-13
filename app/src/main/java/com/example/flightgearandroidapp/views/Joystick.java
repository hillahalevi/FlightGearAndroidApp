package com.example.flightgearandroidapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

public class Joystick extends View {
    private int OUTER_RADIUS;
    private int INNER_RADIUS;
    private int CENTER_X;
    private int CENTER_Y;

    private int currX;
    private int currY;

    private Paint outerCircle;
    private Paint innerCircle;
    private Paint backgroundColor;

    public Joystick(Context context) {
        super(context);

        this.outerCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.outerCircle.setColor(Color.WHITE);
        this.outerCircle.setStyle(Paint.Style.FILL);

        this.innerCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.innerCircle.setColor(Color.rgb(244, 163, 0));
        this.innerCircle.setStyle(Paint.Style.FILL);

        this.backgroundColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.backgroundColor.setColor(Color.rgb(0, 150, 136));
        this.backgroundColor.setStyle(Paint.Style.FILL);


        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        currX = CENTER_X = metrics.widthPixels / 2;
        currY = CENTER_Y = metrics.heightPixels / 2;
        OUTER_RADIUS = metrics.widthPixels / 3;
        INNER_RADIUS = OUTER_RADIUS / 3;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, CENTER_X * 2, CENTER_Y * 2, backgroundColor);
        canvas.drawCircle(CENTER_X, CENTER_Y, OUTER_RADIUS, outerCircle);
        canvas.drawCircle(currX, currY, INNER_RADIUS, innerCircle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        currX = CENTER_X = w / 2;
        currY = CENTER_Y = h / 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            OUTER_RADIUS = CENTER_X * 2 / 3;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            OUTER_RADIUS = CENTER_Y * 2 / 3;
        }
        INNER_RADIUS = OUTER_RADIUS / 3;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public int getOuterRadius() {
        return this.OUTER_RADIUS;
    }

    public int getInnerRadius() {
        return this.INNER_RADIUS;
    }

    public int getCenterX() {
        return CENTER_X;
    }

    public int getCenterY() {
        return CENTER_Y;
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
}
