package com.example.flightgearandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends Activity {
    /**
     * on create activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    /**
     * send params to next activity and start it
     * @param view
     */
    public void sendConnect(View view) {
        Intent intent = new Intent(this, JoystickActivity.class);
        //get params from view
        EditText editIp = (EditText) findViewById(R.id.ipAdress);
        EditText editPort = (EditText) findViewById(R.id.portAdress);
        int port = Integer.parseInt(editPort.getText().toString());

        //put params to next intend dictionary
        intent.putExtra("ip",editIp.getText().toString());
        intent.putExtra("port",port);
        //start joystick
        startActivity(intent);
    }
}
