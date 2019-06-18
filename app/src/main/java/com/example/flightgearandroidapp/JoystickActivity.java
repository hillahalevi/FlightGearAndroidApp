package com.example.flightgearandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightgearandroidapp.services.ClientSide;
import com.example.flightgearandroidapp.views.JoystickView;


public class JoystickActivity extends AppCompatActivity {

    private ClientSide client;
    private JoystickView joystickView;
    private boolean isTouchingJoystick;
    private boolean exceptionOccured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.joystickView = new JoystickView(this);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run(){
                try{
                    Intent intent = getIntent();
                    String ip = intent.getStringExtra("ip");
                    int port = Integer.parseInt(intent.getStringExtra("port"));
                    client = new ClientSide();
                    client.connect(ip, port);
                }
                catch (Exception e){
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("Uncaught exception: " + ex);
                exceptionOccured = true;
            }
        };
        t.setUncaughtExceptionHandler(h);
        t.start();
        try
        {
            t.join();
            if(!exceptionOccured){
                setContentView(this.joystickView);
                this.isTouchingJoystick = false;
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int touchX = (int) event.getRawX();
        int touchY = (int) event.getRawY();

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
                double distance = this.distance(touchX, touchY, this.joystickView.getCenterX(), this.joystickView.getCenterY());
                double magnitude = distance / this.joystickView.getOuterRadius();
                if (magnitude >= 1) {
                    magnitude = 1;
                }
                double angle = this.getAngle(touchX - this.joystickView.getCenterX(), touchY - this.joystickView.getCenterY());
                float elevator = (float) (Math.sin(Math.toRadians(angle)) * magnitude * -1);
                float aileron = (float) (Math.cos(Math.toRadians(angle)) * magnitude);

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
                this.isTouchingJoystick = false;
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
        if (dx >= 0 && dy >= 0) return Math.toDegrees(Math.atan(dy / dx));
        else if (dx < 0 && dy >= 0) return Math.toDegrees(Math.atan(dy / dx)) + 180;
        else if (dx < 0 && dy < 0) return Math.toDegrees(Math.atan(dy / dx)) + 180;
        else if (dx >= 0 && dy < 0) return Math.toDegrees(Math.atan(dy / dx)) + 360;
        return 0;
    }

    private int[] getAdjustedPosition(int touchX, int touchY, double angle, double distanceFromCenter) {
        int outerRadius = this.joystickView.getOuterRadius();
        // if the position isn't outside the joystick, return the original values
        if (distanceFromCenter <= outerRadius) {
            return new int[]{touchX, touchY};
        }
        // placing the joystick on the edge of the pad according to the relative position to the center
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