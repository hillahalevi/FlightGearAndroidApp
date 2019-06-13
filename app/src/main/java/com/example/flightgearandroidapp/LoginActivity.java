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
        EditText editIp = (EditText) findViewById(R.id.editTextIP);
        EditText editPort = (EditText) findViewById(R.id.editTextPort);

//        intent.putExtra("ip", editIp.getText().toString());
//        intent.putExtra("port", Integer.parseInt(editPort.getText().toString()));
        intent.putExtra("ip","123");
        intent.putExtra("port",122);
        startActivity(intent);
    }
}
