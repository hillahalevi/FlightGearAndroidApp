package com.example.flightgearandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.flightgearandroidapp.services.TcpClient;
import com.example.flightgearandroidapp.views.JoystickView;

class Info {
    private float elevator;
    private float aileron;
    private double angle;
    private double distance;

    Info(float elevator, float aileron, double angle, double distance) {
        this.elevator = elevator;
        this.aileron = aileron;
        this.angle = angle;
        this.distance = distance;
    }

    public float getElevator() {
        return elevator;
    }

    public float getAileron() {
        return aileron;
    }

    public double getAngle() {
        return angle;
    }

    public double getDistance() {
        return distance;
    }
}


public class JoystickActivity extends Activity {
    private TcpClient client;
    private JoystickView joystickView;
    private boolean isInJoystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.joystickView = new JoystickView(this);
        setContentView(this.joystickView);
        this.isInJoystick = false;
        connectToServer();

    }


    private void connectToServer() {
        //TODO with jenny
        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        int port = intent.getIntExtra("port", 5400);

        this.client = new TcpClient(ip, port);
    }

    /**
     * Is responsible for the joystick-moving logic-whenever a touch accrues
     *
     * @param event - the gesture the user acted on the screen
     * @return true if a relevant action has performed, false otherwise
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        Point touchPoint = new Point();
        touchPoint.x = (int) event.getRawX();
        touchPoint.y = (int) event.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (!this.isInsideJoystick(touchPoint)) {
                    return false; //irrelevant touch
                }
                this.isInJoystick = true; //update the flag for upcoming  actions
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!this.isInJoystick) {
                    return false; //irrelevant touch
                }
                move(touchPoint);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                // reCenter the joystick
                this.updateJoystickPosition(this.joystickView.getCenterX(), this.joystickView.getCenterY());
                this.isInJoystick = false;
                break;
            }
        }
        return true;
    }

    /**
     * normalize the values of the movement
     * @return
     */
    private Info getNormalizeValues(Point touchPoint) {
        int outerRadius = this.joystickView.getOuterRadius();
        int innerRadius = this.joystickView.getInnerRadius();
        int centerX = this.joystickView.getCenterX();
        int centerY = this.joystickView.getCenterY();
        double distance = this.distance(touchPoint.x, touchPoint.y, centerX, centerY);
        double magnitude = (distance + innerRadius) / outerRadius;
        if (magnitude > 1) {
            magnitude = 1;
        }
        double angle = this.getAngle(touchPoint.x - centerX, touchPoint.y - centerY);
        float elevator = (float) (Math.sin(Math.toRadians(angle)) * magnitude * -1);
        float aileron = (float) (Math.cos(Math.toRadians(angle)) * magnitude);
        return new Info(elevator, aileron, angle, distance);
    }


    private void move(Point touchPoint) {
        Info info = getNormalizeValues(touchPoint);
        //see the values
        Toast.makeText(JoystickActivity.this, "(elevator:" + info.getElevator() + ", aileron:" + info.getAileron() + ")", Toast.LENGTH_SHORT).show();
        //todo check with jenny
        this.client.sendMessage("elevator", String.valueOf(info.getElevator()));
        this.client.sendMessage("aileron", String.valueOf(info.getAileron()));

        // draw the new position
        Point newPos = this.AdjustPosition(touchPoint.x, touchPoint.y, info.getAngle(), info.getDistance());
        this.updateJoystickPosition(newPos.x, newPos.y);

    }


    /**
     * check if the touch is inside the joystick range
     *
     * @return
     */
    private boolean isInsideJoystick(Point touchPoint) {
        return this.distance(touchPoint.x, touchPoint.y, this.joystickView.getCurrX(), this.joystickView.getCurrY()) <=
                this.joystickView.getInnerRadius();
    }

    /**
     * calculate the distance between two points
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * calculate the angle between two points
     *
     * @param dx
     * @param dy
     * @return
     */
    private double getAngle(float dx, float dy) {
        double angle = 0;
        if (dx >= 0 && dy >= 0) {
            angle = Math.toDegrees(Math.atan(dy / dx));
        } else if ((dx < 0 && dy >= 0) || (dx < 0 && dy < 0)) {
            angle = Math.toDegrees(Math.atan(dy / dx)) + 180;
        } else if (dx >= 0 && dy < 0) {
            angle = Math.toDegrees(Math.atan(dy / dx)) + 360;
        }
        return angle;
    }

    /**
     * correcting the touch position if occurred outside the joystick
     *
     * @param touchX
     * @param touchY
     * @param angle
     * @param distanceFromCenter
     * @return
     */
    private Point AdjustPosition(int touchX, int touchY, double angle, double distanceFromCenter) {
        int outerRadius = this.joystickView.getOuterRadius();
        int innerRadius = this.joystickView.getInnerRadius();
        if (distanceFromCenter + innerRadius <= outerRadius) {
            //irrelevant touch
            return new Point(touchX, touchY);
        }

        int distance = outerRadius - innerRadius;
        // placing the joystick  relatively
        int newX = this.joystickView.getCenterX() + (int) (Math.cos(Math.toRadians(angle)) * distance);
        int newY = this.joystickView.getCenterY() + (int) (Math.sin(Math.toRadians(angle)) * distance);
        return new Point(newX, newY);
    }

    /**
     * set the new position of the joystick
     *
     * @param newX
     * @param newY
     */
    private void updateJoystickPosition(int newX, int newY) {
        this.joystickView.setX(newX);
        this.joystickView.setY(newY);
        this.joystickView.postInvalidate();
    }

    @Override
    protected void onDestroy() {
        this.client.stopClient();
        super.onDestroy();
    }
}
