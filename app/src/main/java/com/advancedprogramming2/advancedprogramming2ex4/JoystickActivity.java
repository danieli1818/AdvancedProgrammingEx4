package com.advancedprogramming2.advancedprogramming2ex4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

/**
 * The JoystickActivity class extends AppCompatActivity is the class of the Joystick Activity.
 */
public class JoystickActivity extends AppCompatActivity {

    /**
     * The Client client of the joystick which is/will be connected to the server.
     */
    Client client;

    /**
     * The onCreate Function which runs on the creation of the activity.
     * @param savedInstanceState The Bundle os saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        JoystickView joystickView = findViewById(R.id.joystickView);
        joystickView.addUpdateFunction(new Function<String, Void>() {
            public Void apply(String data) {
                onChangeData(data);
                return null;
            }
        });

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
//        if (!isValidIPv4(ip)) {
//            Log.e("IP", "IP: " + ip + " Is Not Valid"
//                    , new RuntimeException("IP Is Not Valid"));
//        }
        String portStr = intent.getStringExtra("port");
        try {
            int port = Integer.parseInt(portStr);
            client = new Client();
            client.addUpdateFunction(new Function<Pair<String, RuntimeException>, Void>() {
                public Void apply(Pair<String, RuntimeException> data) {
                    onConnected(data.second);
                    return null;
                }
            });
            client.addErrorHandlingFunction(new Consumer<RuntimeException>() {
                @Override
                public void accept(RuntimeException e) {
                    errorHandlingFunction(e);
                }
            });
            client.connect(ip, port);
        } catch (Exception e) {
            Log.e("Client", "Error", e);
            if (e.getClass().equals(NumberFormatException.class)) {
                Intent data = new Intent();
                data.putExtra("Error", "Error Connecting To Server.");
                String message = "Invalid Port";
                data.putExtra("Reason", message);
                setResult(1, data);
                finish();
                return;
            }
            Intent data = new Intent();
            data.putExtra("Error", "Error Connecting To Server.");
            String message = "Unknown";
            data.putExtra("Reason", message);
            setResult(1, data);
            finish();
        }

    }

    /**
     * The onChangeData function gets as a parameter a String data.
     * The data is the data which changed.
     * It updates according to the data.
     * @param data String data which got changed.
     */
    private void onChangeData(String data) {
        if (data == null) {
            return;
        }
        if (data.toUpperCase().equals("X")) {
            updateX();
        } else {
            if (data.toUpperCase().equals("Y")) {
                updateY();
            }
        }
    }

    /**
     * The updateX function updates the x value of the joystick.
     */
    private void updateX() {
        JoystickView view = findViewById(R.id.joystickView);
        float newX = 0;
        if (!view.isInCenter()) {
            newX = view.getInnerCircleCX();
            newX = normalize(newX, view.getMinX(), view.getMaxX());
        }
        try {
            try {

                client.sendMessage("set /controls/flight/aileron " + newX);

            }

            catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            }
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

    /**
     * The updateY function updates the y value of the joystick.
     */
    private void updateY() {
        JoystickView view = findViewById(R.id.joystickView);
        float newY = 0;
        if (!view.isInCenter()) {
            newY = view.getInnerCircleCY();
            newY = normalize(newY, view.getMinY(), view.getMaxY());
            newY = -newY;
        }
        try {
            try {

                client.sendMessage("set /controls/flight/elevator " + newY);

            }

            catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            }
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

    /**
     * The normalize function which gets as parameters a float value, a float min and a float max.
     * It normalizes the value min max normalization according to the parameters
     * to the domain between -1 to 1.
     * @param value The value to normalize.
     * @param min The min value possible for the value.
     * @param max The max value possible for the value.
     * @return normalized value of the value from the parameters, min max normalization.
     */
    private float normalize(float value, float min, float max) {
        float newMin = -1;
        float newMax = 1;
        if ((max - min) == 0) {
            return value - min + newMin;
        }
        return (((value - min) / (max - min)) * (newMax - newMin)) + newMin;
    }

    /**
     * The onBackPressed function is the function which runs when back button is pressed.
     */
    @Override
    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
        closeSocket();
    }

    /**
     * The onConnected function runs when the AsyncTask Of The Connection finishes.
     * It gets in parameters a RuntimeException e of the RuntimeException which has occured
     * in the connection if it is Null there hasn't been an Exception.
     * @param e RuntimeException which has occured in the connection can be Null
     *          if there hasn't been an Exception.
     */
    public void onConnected(@Nullable RuntimeException e) {
        if (e == null) {
            View progressBarLayout = findViewById(R.id.progressBarLayout);
            View joystickViewLayout = findViewById(R.id.joystickViewLayout);
            progressBarLayout.setVisibility(View.INVISIBLE);
            joystickViewLayout.setVisibility(View.VISIBLE);
        } else {
            Intent data = new Intent();
            data.putExtra("Error", "Error Connecting To Server.");
            String message = e.getMessage();
            data.putExtra("Reason", message);
            setResult(1, data);
            finish();
        }
    }

    /**
     * The onCancelButtonClick function is the function which handles the click on the cancel button
     * it gets as parameters a View view.
     * @param view The view.
     */
    public void onCancelButtonClick(View view) {
        closeSocket();
        setResult(0);
        finish();
    }

    /**
     * The onOptionsItemSelected function is the function which runs when the back top arrow button
     * is being clicked. It gets as parameters a MenuItem item.
     * @param item MenuItem item.
     * @return true.
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        onBackPressed();
        return true;
    }

    /**
     * The onDestroy function handles the destroy of the activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    /**
     * The closeSocket function closes the client.
     */
    private void closeSocket() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * The errorHandlingFunction gets as parameters a RuntimeException e
     * and returns the error to the Main Activity with the data of it.
     * @param e RuntimeExceptione that has occured.
     */
    private void errorHandlingFunction(RuntimeException e) {
        Intent data = new Intent();
        data.putExtra("Error", "Error With The Connection With The Server.");
        String message = e.getMessage();
        data.putExtra("Reason", message);
        setResult(1, data);
        client.close();
        finish();

    }
}
