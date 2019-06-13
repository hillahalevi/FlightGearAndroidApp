package com.example.flightgearandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.flightgearandroidapp.services.ClientSide;
import com.example.flightgearandroidapp.views.Joystick;

public class JoystickActivity extends Activity {
    private ClientSide client;
    private Joystick joystick;
    private boolean isTouchingJoystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.joystick = new Joystick(this);
        setContentView(this.joystick);

        this.isTouchingJoystick = false;

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        int port = intent.getIntExtra("port", 5400);


//        this.client = new Client();
//        this.client.connect(ip, port);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // if the touch happened outside the joystick then ignore it
                if (!this.isInsideJoystick(touchX, touchY)) {
                    return false;
                }
                // otherwise, update the flag for upcoming move actions
                this.isTouchingJoystick = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!this.isTouchingJoystick) {
                    return false;
                }
                // get the values to send to the client
                double magnitude = this.distance(touchX, touchY, this.joystick.getCenterX(), this.joystick.getCenterY()) /
                        this.joystick.getOuterRadius();
                if (magnitude >= 1) {
                    magnitude = 1;
                }
                double angle = this.getAngle(touchX - this.joystick.getCenterX(), touchY - this.joystick.getCenterY());
                double elevator = Math.sin(Math.toRadians(angle)) * magnitude * -1;
                double aileron = Math.cos(Math.toRadians(angle)) * magnitude;

//                this.client.sendCommand("elevator", String.valueOf(elevator));
//                this.client.sendCommand("aileron", String.valueOf(aileron));

                // draw the new position
                int[] newPos = this.getAdjustedPosition(touchX, touchY, angle, magnitude * this.joystick.getOuterRadius());
                this.updateJoystickPosition(newPos[0], newPos[1]);
                break;
            }
            case MotionEvent.ACTION_UP:         // fallthrough
            case MotionEvent.ACTION_CANCEL: {
                // place the joystick in its original position
                this.updateJoystickPosition(this.joystick.getCenterX(), this.joystick.getCenterY());
                this.isTouchingJoystick = false;
                break;
            }
        }
        return true;
    }

    private boolean isInsideJoystick(int touchX, int touchY) {
        return this.distance(touchX, touchY, this.joystick.getCurrX(), this.joystick.getCurrY()) <=
                this.joystick.getInnerRadius();
    }

    private double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private double getAngle(float dx, float dy) {
        if (dx >= 0 && dy >= 0) return Math.toDegrees(Math.atan(dy / dx));
        else if (dx < 0 && dy >= 0) return Math.toDegrees(Math.atan(dy / dx)) + 180;
        else if (dx < 0 && dy < 0) return Math.toDegrees(Math.atan(dy / dx)) + 180;
        else if (dx >= 0 && dy < 0) return Math.toDegrees(Math.atan(dy / dx)) + 360;
        return 0;
    }

    private int[] getAdjustedPosition(int touchX, int touchY, double angle, double distanceFromCenter) {
        // if the position isn't outside the joystick, return the original values
        if (distanceFromCenter <= this.joystick.getOuterRadius()) {
            return new int[]{touchX, touchY};
        }
        // placing the joystick on the edge of the pad according to the relative position to the center
        int newX = this.joystick.getCenterX() + (int) Math.cos(Math.toRadians(angle)) * this.joystick.getOuterRadius();
        int newY = this.joystick.getCenterY() + (int) Math.sin(Math.toRadians(angle)) * this.joystick.getOuterRadius();
        return new int[]{newX, newY};
    }


    private void updateJoystickPosition(int newX, int newY) {
        this.joystick.setX(newX);
        this.joystick.setY(newY);
        this.joystick.invalidate();
//        this.joystick.postInvalidate();
    }

    @Override
    protected void onDestroy() {
        this.client.disconnect();
        super.onDestroy();
    }
}
