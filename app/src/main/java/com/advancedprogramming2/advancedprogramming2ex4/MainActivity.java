package com.advancedprogramming2.advancedprogramming2ex4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

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
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            String errorMessage = data.getStringExtra("Error");
            String reason = data.getStringExtra("Reason");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setCancelable(false);

            builder.setTitle("Connection Error");
            builder.setMessage(errorMessage + "\r\n" + "Reason: " + reason);

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.show();
        }
    }
}
