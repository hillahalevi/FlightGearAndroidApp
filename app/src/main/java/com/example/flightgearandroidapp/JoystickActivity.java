package com.example.flightgearandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.flightgearandroidapp.services.ClientSide;
import com.example.flightgearandroidapp.views.JoystickView;

//TODO normalize the values to -1 --> 1
public class JoystickActivity extends Activity {
    private ClientSide client;
    private JoystickView joystickView;
    private boolean isInJoystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.joystickView = new JoystickView(this);
        setContentView(this.joystickView);
        this.isInJoystick = false;

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        int port = intent.getIntExtra("port", 5400);

        this.client = new ClientSide();
        this.client.connect(ip, port);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int touchX = (int) event.getRawX();
        int touchY = (int) event.getRawY();

        Toast.makeText(JoystickActivity.this, "(x:" + touchX + ", y:" + touchY + ")", Toast.LENGTH_SHORT).show();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                // if the touch happened outside the joystick then ignore it
                if (!this.isInsideJoystick(touchX, touchY)) {
                    return false;
                }
                // otherwise, update the flag for upcoming move actions
                this.isInJoystick = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!this.isInJoystick) {
                    return false;
                }
                // get the values to send to the client
                double distance = this.distance(touchX, touchY, this.joystickView.getCenterX(), this.joystickView.getCenterY());
                double magnitude = distance / this.joystickView.getOuterRadius();
                if (magnitude >= 1) {
                    magnitude = 1;
                }
                double angle = this.getAngle(touchX - this.joystickView.getCenterX(), touchY - this.joystickView.getCenterY());
//                this.textView.setText(String.valueOf((int)angle));

                double elevator = Math.sin(Math.toRadians(angle)) * magnitude * -1;
                double aileron = Math.cos(Math.toRadians(angle)) * magnitude;

                this.client.sendCommand("elevator", String.valueOf(elevator));
                this.client.sendCommand("aileron", String.valueOf(aileron));

                // draw the new position
                int[] newPos = this.getAdjustedPosition(touchX, touchY, angle, distance);
                this.updateJoystickPosition(newPos[0], newPos[1]);
                break;
            }
            case MotionEvent.ACTION_UP:         // fallthrough
            case MotionEvent.ACTION_CANCEL: {
                // place the joystick in its original position
                this.updateJoystickPosition(this.joystickView.getCenterX(), this.joystickView.getCenterY());
                this.isInJoystick = false;
                break;
            }
        }
        return true;
    }

    private boolean isInsideJoystick(int touchX, int touchY) {
        return this.distance(touchX, touchY, this.joystickView.getCurrX(), this.joystickView.getCurrY()) <=
                this.joystickView.getInnerRadius();
    }

    private double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private double getAngle(float dx, float dy) {
        double angle = 0;
        if (dx >= 0 && dy >= 0) {
            angle = Math.toDegrees(Math.atan(dy / dx));
        } else if ((dx < 0 && dy >= 0) ||(dx < 0 && dy < 0)) {
            angle = Math.toDegrees(Math.atan(dy / dx)) + 180;
        } else if (dx >= 0 && dy < 0) {
            angle = Math.toDegrees(Math.atan(dy / dx)) + 360;
        }
        return angle;
    }

    private int[] getAdjustedPosition(int touchX, int touchY, double angle, double distanceFromCenter) {
        int outerRadius = this.joystickView.getOuterRadius();
        if (distanceFromCenter <= outerRadius) {
            //irrelevant touch
            return new int[]{touchX, touchY};
        }
        // placing the joystick  relatively
        int newX = this.joystickView.getCenterX() + (int) (Math.cos(Math.toRadians(angle)) * outerRadius);
        int newY = this.joystickView.getCenterY() + (int) (Math.sin(Math.toRadians(angle)) * outerRadius);
        return new int[]{newX, newY};
    }

    private void updateJoystickPosition(int newX, int newY) {
        this.joystickView.setX(newX);
        this.joystickView.setY(newY);
        this.joystickView.postInvalidate();
    }

    @Override
    protected void onDestroy() {
        this.client.disconnect();
        super.onDestroy();
    }
}
