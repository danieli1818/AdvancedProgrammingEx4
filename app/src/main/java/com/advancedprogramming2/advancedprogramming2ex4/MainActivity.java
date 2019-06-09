package com.advancedprogramming2.advancedprogramming2ex4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connectButtonClick(android.view.View view) {
        EditText ip = (EditText)findViewById(R.id.ipPlainText);
        EditText port = (EditText)findViewById(R.id.portPlainText);

        Intent intent = new Intent(this, JoystickActivity.class);
        String ipText = ip.getText().toString();
        String portText = port.getText().toString();
        intent.putExtra("ip", ipText);
        intent.putExtra("port", portText);
        startActivity(intent);
    }
}
