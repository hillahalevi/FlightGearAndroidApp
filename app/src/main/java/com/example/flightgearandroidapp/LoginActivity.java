package com.example.flightgearandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void sendConnect(View view) {
        Intent intent = new Intent(this, JoystickActivity.class);
        EditText editIp = (EditText) findViewById(R.id.ipAdress);
        EditText editPort = (EditText) findViewById(R.id.portAdress);

        intent.putExtra("ip",editIp.getText().toString());
        intent.putExtra("port",editPort.getText().toString());
        startActivity(intent);
    }
}
