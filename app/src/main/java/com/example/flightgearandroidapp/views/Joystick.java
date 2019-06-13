package com.example.flightgearandroidapp.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

public class Joystick extends View {
    private final int OUTER_RADIUS = 350;
    private final int INNER_RADIUS = 140;
    private final int CENTER_X;
    private final int CENTER_Y;

    private Paint outerColor;
    private Paint innerColor;

    private int currX;
    private int currY;

    public Joystick(Context context) {
        super(context);

        this.outerColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.outerColor.setColor(Color.GRAY);
        this.outerColor.setStyle(Paint.Style.FILL);

        this.innerColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.innerColor.setColor(Color.RED);
        this.innerColor.setStyle(Paint.Style.FILL);

//        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        currX = CENTER_X = size.x / 2;
////        currY = CENTER_Y = size.y / 2 - size.y / 10;
//        currY = CENTER_Y = size.y / 2;
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        currX = CENTER_X = metrics.widthPixels / 2;
        currY = CENTER_Y = metrics.heightPixels / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(currX, currY, OUTER_RADIUS, this.outerColor);
        canvas.drawCircle(currX, currY, INNER_RADIUS, this.innerColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
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
