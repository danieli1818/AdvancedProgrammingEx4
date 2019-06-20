package com.advancedprogramming2.advancedprogramming2ex4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

/**
 * The MainActivity class of the main activity which is the view of the first activity of the app.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The onCreate function which creates the main activity.
     * @param savedInstanceState save instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * The connectButtonClick function is the function which handles the event
     * of the click on the connect button.
     * It checks the parameters of IP and Port which the user entered
     * if they are valid it sends these parameters to the JoystickActivity
     * else it sends an alert of the error of the validation.
     * @param view The View
     */
    public void connectButtonClick(android.view.View view) {
        EditText ip = findViewById(R.id.ipPlainText);
        EditText port = findViewById(R.id.portPlainText);

        if (!Utils.isValidIPv4(ip.getText().toString())) {
            alarm("Parameters Aren't Valid Error", "IP: \"" + ip.getText().toString()
                    + "\" isn't a valid IPv4 address");
            return;
        }

        try {
            Integer.parseInt(port.getText().toString());
        } catch (NumberFormatException e) {
            alarm("Parameters Aren't Valid Error", "Port: \""
                    + port.getText().toString()
                    + "\" isn't valid");
            return;
        }

        Intent intent = new Intent(this, JoystickActivity.class);
        String ipText = ip.getText().toString();
        String portText = port.getText().toString();
        intent.putExtra("ip", ipText);
        intent.putExtra("port", portText);
        startActivityForResult(intent, 0);
    }

    /**
     * The function onActivityResult gets as parameters the int requestCode, int resultCode
     * and an Intent data.
     * According to the int resultCode it gets from the parameters
     * it checks if there was an error if the resultCode != 0
     * and if there was it sends an alarm with the data of the error from the Intent data
     * it got from the parameters.
     * @param data The Intent to pass the data from the result.
     * @param requestCode The request code
     * @param resultCode The result code
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            String errorMessage = "Error";
            String reason = "Unknown";
            if (data != null) {
                errorMessage = data.getStringExtra("Error");
                reason = data.getStringExtra("Reason");
                if (errorMessage == null) {
                    errorMessage = "Error";
                }
                if (reason == null) {
                    reason = "Unknown";
                }
            }

            alarm("Connection Error", errorMessage + "\r\n" + "Reason: " + reason);
        }
    }

    /**
     * The alarm function gets as parameters a String title and a String message
     * it sends an alarm with the title, title and the message, message and an Ok button.
     * @param message The message of the alert
     * @param title The title of the alert
     */
    private void alarm(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }


}
