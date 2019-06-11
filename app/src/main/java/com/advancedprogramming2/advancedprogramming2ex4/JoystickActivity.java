package com.advancedprogramming2.advancedprogramming2ex4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class JoystickActivity extends AppCompatActivity {

    private class AsyncSocketHandler extends AsyncTask<String, Void, Socket> {

        private Socket mSocket;

        protected Socket doInBackground(String... params) {
            if (params.length != 2) {
                throw new RuntimeException("Number Of Params Isn't Valid!");
            }
            String ip = params[0];
            if (!isValidIPv4(ip)) {
                throw new RuntimeException("IP parameter isn't a valid IPv4 Address");
            }
            String portStr = params[1];
            int port = 1234;
            try {
                port = Integer.parseInt(portStr);
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            try {
                //here you must put your computer's IP address.
                InetAddress serverAddr = InetAddress.getByName(ip);
                //create a socket to make the connection with the server
                mSocket = new Socket();
//                while (!isCancelled()) {
                mSocket.connect(new InetSocketAddress(serverAddr, port), 30000);
//                }
                if (isCancelled()) {
                    return null;
                }
//                try {
//                    //sends the message to the server
//                    OutputStream output = socket.getOutputStream();
//                    OutputStreamWriter osw = new OutputStreamWriter(output);
//                    //FileInputStream fis = new FileInputStream(pic);
//
//                    osw.write("set /controls/flight/aileron " + String.valueOf(newX));
//                    osw.flush();
//                }
//
//                catch (Exception e) {
//                    Log.e("TCP", "S: Error", e);
//                } finally {
//                    socket.close();
//                }
//                int x = 1;
//                x = x * 4;
                return mSocket;
            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            super.onPostExecute(socket);
            onSocketHandlerAsyncTaskFinish(socket);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            try {
                if (mSocket != null && mSocket.isConnected()) {
                    mSocket.close();
                }
            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
            }
        }

        public void closeSocket() {
            try {
                if (mSocket != null) {
                    mSocket.setSoTimeout(0);
                    mSocket.close();
                }
            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
            }
        }


    }

    private class AsyncSocketWriter extends AsyncTask<String, Void, Void> {

        private Socket mSocket;

        public AsyncSocketWriter(Socket socket) {
            if (socket == null || !socket.isConnected()) {
                throw new RuntimeException("Error socket isn't valid");
            }
            mSocket = socket;
        }

        protected Void doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            if (mSocket == null || !mSocket.isConnected()) {
                throw new RuntimeException("Error socket isn't valid");
            }
            try {
                //sends the message to the server
                OutputStream output = mSocket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(output);
                //FileInputStream fis = new FileInputStream(pic);
                for (String param : params) {
                    osw.write(param +"\r\n");
                }
                osw.flush();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage()); //TODO check what to do here in error.
            }



            return null;
        }

//            @Override
//            protected void onPostExecute(Socket socket) {
//                super.onPostExecute(socket);
//                onSocketHandlerAsyncTaskFinish(socket);
//            }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            try {
                if (mSocket != null && mSocket.isConnected()) {
                    mSocket.close();
                }
            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
            }
        }

        public void closeSocket() {
            try {
                if (mSocket != null) {
                    mSocket.close();
                }
            } catch (Exception e) {
                Log.e("TCP", "C: Error", e);
            }
        }


    }

    private volatile Socket socket;
    private AsyncSocketHandler ash;
//    private String ip;
//    private int port;

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
        ash = new AsyncSocketHandler();
        ash.execute(ip, portStr);
//        try {
//            socket = ash.execute(ip, portStr).get(30, TimeUnit.SECONDS);
//            if (socket == null) {
//                Intent data = new Intent();
//                data.putExtra("Error", "Error Connecting To Server.");
//                data.putExtra("Reason", "Unknown");
//                setResult(1, data);
//                finish();
//                return;
//            }
//        } catch(Exception e) {
//            if (e.getClass().equals(java.util.concurrent.TimeoutException.class)) {
//                ash.cancel(true);
//                try {
//                    socket.setSoTimeout(0);
//                } catch (Exception eSocket) {
//                    Log.e("TCP", "T: Error", eSocket);
//                }
////                ash.onCancelled();
////                if (ash.isCancelled()) {
////                    int x = 4;
////                    x = x * 4;
////                }
////                Intent data = new Intent(this, MainActivity.class);
////                data.putExtra("key1", "value1");
////                data.putExtra("key2", "value2");
////                onBackPressed();
////                startActivity(data);
//                // Activity finished return ok, return the data
////                setResult(RESULT_OK, data);
////                finish();
//                Intent data = new Intent();
//                data.putExtra("Error", "Error Connecting To Server.");
//                String message = e.getMessage();
//                if (message == null) {
//                    message = "Connection Timed Out";
//                }
//                data.putExtra("Reason", message);
//                setResult(1, data);
//                finish();
//                return;
//            }
//            Log.e("AsyncTask", e.getMessage());
//        }
//        port = 1234;
//        try {
//            port = Integer.parseInt(portStr);
//        } catch(NumberFormatException e) {
//            Log.e("Port", "Port Isn't A Number Error");
//        }


    }

    private boolean isValidIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String[] splittedIP = ip.split("\\.");
        if (splittedIP.length != 4) {
            return false;
        }
        for (String str : splittedIP) {
            try {
                int num = Integer.parseInt(str);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return !ip.endsWith(".");
    }

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

    private void updateX() {
        JoystickView view = findViewById(R.id.joystickView);
        float newX = view.getInnerCircleCX();
        newX = normalize(newX, view.getMinX(), view.getMaxX(), -1, 1);
        try {
            //here you must put your computer's IP address.
//            InetAddress serverAddr = InetAddress.getByName(ip);
            //create a socket to make the connection with the server
//            Socket socket = new Socket(serverAddr, port);
            try {
                //sends the message to the server
//                OutputStream output = socket.getOutputStream();
//                OutputStreamWriter osw = new OutputStreamWriter(output);
                //FileInputStream fis = new FileInputStream(pic);

                new AsyncSocketWriter(socket).execute("set /controls/flight/aileron " + String.valueOf(newX));

            }

            catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            }
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

    private void updateY() {
        JoystickView view = findViewById(R.id.joystickView);
        float newY = view.getInnerCircleCY();
        newY = normalize(newY, view.getMinY(), view.getMaxY(), -1, 1);
        newY = -newY;
        try {
            //here you must put your computer's IP address.
//            InetAddress serverAddr = InetAddress.getByName(ip);
            //create a socket to make the connection with the server
//            Socket socket = new Socket(serverAddr, port);
            try {
                //sends the message to the server
//                OutputStream output = socket.getOutputStream();
//                OutputStreamWriter osw = new OutputStreamWriter(output);
                //FileInputStream fis = new FileInputStream(pic);

                new AsyncSocketWriter(socket).execute("set /controls/flight/elevator " + String.valueOf(newY));

            }

            catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            }
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

    private float normalize(float value, float min, float max, float newMin, float newMax) {
        if ((max - min) == 0) {
            return value - min + newMin;
        }
        return (((value - min) / (max - min)) * (newMax - newMin)) + newMin;
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
        if (ash != null) {
            ash.closeSocket();
        }
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (Exception e) {
                String message = e.getMessage();
                if (message == null) {
                    message = "Error Closing Socket";
                }
                Log.e("Closing Socket", message);
            }
        }
    }

    public void onSocketHandlerAsyncTaskFinish(Socket socketReturn) {
        if (socketReturn != null) {
            socket = socketReturn;
            View progressBarLayout = findViewById(R.id.progressBarLayout);
            View joystickViewLayout = findViewById(R.id.joystickViewLayout);
            progressBarLayout.setVisibility(View.INVISIBLE);
            joystickViewLayout.setVisibility(View.VISIBLE);
        } else {
            Intent data = new Intent();
            data.putExtra("Error", "Error Connecting To Server.");
            String message = "Connection Timed Out";
            data.putExtra("Reason", message);
            setResult(1, data);
            finish();
        }
    }

    public void onCancelButtonClick(View view) {
        if (ash != null) {
            ash.cancel(true);
            ash.closeSocket();
        }
        setResult(0);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){
//        Intent myIntent = new Intent(getApplicationContext(), MyActivity.class);
//        startActivityForResult(myIntent, 0);
        onBackPressed();
        return true;
    }
}
