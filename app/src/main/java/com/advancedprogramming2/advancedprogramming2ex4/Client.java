package com.advancedprogramming2.advancedprogramming2ex4;

import android.os.AsyncTask;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The Client class of the communication with the server.
 */
 class Client {

    /**
     * The AsyncSocketHandler Class extends AsyncTask<String, Void, Socket>
     * is the inner class of the Client class which handles the connection to the server.
     */
    private static class AsyncSocketHandler extends AsyncTask<String, Void, Socket> {

        private Socket mSocket;
        private RuntimeException e;
        private List<Function<Pair<Socket, RuntimeException>, Void>> finishTaskFunctions;

        /**
         * The Constructor of the AsyncSocketHandler.
         */
        AsyncSocketHandler() {
            finishTaskFunctions = new ArrayList<>();
        }

//        /**
//         * The AsyncSocketHandler Constructor which gets as parameters
//         * a List<Function<Pair<Socket, RuntimeException>, Void>> mFinishTaskFunctions
//         * and adds all the Functions in it to the finishTaskFunctions.
//         * @param mFinishTaskFunctions - List<Function<Pair<Socket, RuntimeException>, Void>>
//         *                             of the Functions to add to the finishTaskFunctions.
//         */
//        public AsyncSocketHandler(List<Function<Pair<Socket, RuntimeException>, Void>>
//                                          mFinishTaskFunctions) {
//            finishTaskFunctions = new ArrayList<>();
//            finishTaskFunctions.addAll(mFinishTaskFunctions);
//        }

        /**
         * The addFinishTaskFunction gets as a parameter a Function<Pair<Socket, RuntimeException>,
         *  Void> finishTaskFunction and adds it to the finishTaskFunctions.
         * @param finishTaskFunction The Function<Pair<Socket, RuntimeException>, Void>
         *                           to the finishTaskFunctions.
         */
        void addFinishTaskFunction(Function<Pair<Socket, RuntimeException>
                , Void> finishTaskFunction) {
            finishTaskFunctions.add(finishTaskFunction);
        }

//        /**
//         * The removeFinishTaskFunction function gets as a parameter a
//         * Function<Pair<Socket, RuntimeException>, Void>
//         * finishTaskFunction of the function to remove from the finishTaskFunctions if it is there.
//         * @param finishTaskFunction
//         * @return
//         */
//        public boolean removeFinishTaskFunction(Function<Pair<Socket, RuntimeException>, Void>
//                                                     finishTaskFunction) {
//            return finishTaskFunctions.remove(finishTaskFunction);
//        }

        /**
         * The doInBackground function gets in parameters String... params and connects to the server
         * from the params which holds the IP Address of it in the first argument and the port of
         * the server in the second argument of it.
         * @param params String... params of the function which holds in the first argument
         *               the IP Address of the server to connect to and in the second argument
         *               the port of the server to connect to.
         * @return Socket of the connection between the client to the server.
         */
        protected Socket doInBackground(String... params) {
            e = null;
            if (params.length != 2) {
                e = new RuntimeException("Number Of Params Isn't Valid!");
                throw e;
            }
            String ip = params[0];
            if (!Utils.isValidIPv4(ip)) {
                e = new RuntimeException("IP parameter isn't a valid IPv4 Address");
                throw e;
            }
            String portStr = params[1];
            int port;
            try {
                port = Integer.parseInt(portStr);
            } catch(Exception e1) {
                e = new RuntimeException(e1);
                throw e;
            }

            try {
                //here you must put your computer's IP address.
                InetAddress serverAddr = InetAddress.getByName(ip);
                //create a socket to make the connection with the server
                mSocket = new Socket();
                mSocket.connect(new InetSocketAddress(serverAddr, port), 30000);
                if (isCancelled()) {
                    return null;
                }
                return mSocket;
            } catch (Exception e1) {
                e = new RuntimeException(e1);
                Log.e("TCP", "C: Error", e1);
            }


            return null;
        }

        /**
         * The onPostExecute function gets as parameters a Socket socket
         * of the return value of the AsyncTask and runs the calls to all the functions
         * of finishTaskFunctions.
         * @param socket Socket of the return value from the AsyncTask.
         */
        @Override
        protected void onPostExecute(Socket socket) {
            super.onPostExecute(socket);
            Pair<Socket, RuntimeException> data = new Pair<>(socket, e);
            for (Function<Pair<Socket, RuntimeException>, Void> finishTaskFunction :
                    finishTaskFunctions) {
                finishTaskFunction.apply(data);
            }
        }

        /**
         * The onCancelled function runs the cancel code.
         */
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

        /**
         * The closeSocket function closes the socket.
         */
        void closeSocket() {
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

    /**
     * The AsyncSocketWriter Class extends AsyncTask<String, Void, Void>
     * it is the inner class of the Client which handles the sending data to the server
     * from the client.
     */
    private static class AsyncSocketWriter extends AsyncTask<String, Void, Void> {

        private Socket mSocket;
        private Consumer<RuntimeException> mErrorHandler;

        /**
         * The AsyncSocketWriter Constructor which gets as parameters a Socket socket
         * to use it to send data from the client to the server.
         * @param socket Socket socket of the connection between the server and the client.
         */
        private AsyncSocketWriter(Socket socket, Consumer<RuntimeException> errorHandler) {
            if (socket == null || !socket.isConnected()) {
                throw new RuntimeException("Error socket isn't valid");
            }
            mSocket = socket;
            mErrorHandler = errorHandler;
        }

        /**
         * The doInBackground function gets as parameters String... params
         * of the String messages to send from the client to the server.
         * @param params String messages to send from the client to the server.
         * @return Void.
         */
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
                Log.e("TCP", "W: Error", e);
                RuntimeException re = new RuntimeException(e.getMessage());
                if (mErrorHandler != null) {
                    mErrorHandler.accept(re);
                }
//                throw re;
            }



            return null;
        }

        /**
         * The onCancelled function which runs the cancellation code.
         */
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


    }

    private Socket socket;
    private AsyncSocketHandler ash;
    private List<Function<Pair<String, RuntimeException>, Void>> updateFunctions;
    private List<Consumer<RuntimeException>> errorHandlingFunctions;

    /**
     * The addFinishTaskFunction gets as a parameter a Consumer<RuntimeException> errorHandlingFunction
     * and adds it to the errorHandlingFunctions.
     * @param errorHandlingFunction The Consumer<RuntimeException>
     *                           to the errorHandlingFunctions.
     */
    void addErrorHandlingFunction(Consumer<RuntimeException> errorHandlingFunction) {
        errorHandlingFunctions.add(errorHandlingFunction);
    }

//    /**
//     * The removeFinishTaskFunction function gets as a parameter a
//     * Consumer<RuntimeException>
//     * errorHandlingFunction
//     * of the function to remove from the errorHandlingFunctions if it is there.
//     * @param errorHandlingFunction Consumer<RuntimeException> errorHandlingFunction
//     *                              to remove from the errorHandlingFunctions if it is there.
//     * @return true if the errorHandlingFunction was in the errorHandlingFunctions
//     * and was removed else false.
//     */
//    public boolean removeErrorHandlingFunction(Consumer<RuntimeException> errorHandlingFunction) {
//        return errorHandlingFunctions.remove(errorHandlingFunction);
//    }

    /**
     * The Client Class Constructor.
     */
    Client() {
        updateFunctions = new ArrayList<>();
        errorHandlingFunctions = new ArrayList<>();
    }

    /**
     * The connect function gets as parameters a String ip and an int port.
     * It connects to the server with the IP Address, ip and port, port.
     * @param ip The IP Address of the server to connect to.
     * @param port The port of the server to connect to.
     */
    void connect(String ip, int port) {
        if (socket != null && socket.isConnected()) {
            close();
        }
        ash = new AsyncSocketHandler();
        ash.addFinishTaskFunction(new Function<Pair<Socket, RuntimeException>, Void>() {
            public Void apply(Pair<Socket, RuntimeException> data) {
                onSocketHandlerAsyncTaskFinish(data);
                return null;
            }
        });
        ash.execute(ip, Integer.toString(port));
    }

    /**
     * The onSocketHandlerAsyncTaskFinish function gets as a parameter
     * a Pair<Socket, RuntimeException> data and runs when the Connection SocketHandlerAsyncTask
     * finishes its run.
     * It calls the updateFunctions with the data.
     * @param data Data we get from the SocketHandlerAsyncTask.
     */
    private void onSocketHandlerAsyncTaskFinish(Pair<Socket, RuntimeException> data) {
        socket = data.first;
        for (Function<Pair<String, RuntimeException>, Void> updateFunction : updateFunctions) {
            updateFunction.apply(new Pair<>("socket", data.second));
        }
    }

    /**
     * The close function closes the client by closing its socket.
     */
    void close() {
        if (ash != null) {
            ash.cancel(true);
            ash.closeSocket();
        }
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch(Exception e) {
                Log.e("TCP", "C: Error", e);
            }
        }
    }

//    /**
//     * The function isConnected returns true if the client is connected to the server
//     * else it returns false.
//     * @return true if the client is connected to the server else false.
//     */
//    public boolean isConnected() {
//        return (socket != null && socket.isConnected());
//    }

    /**
     * The sendMessage function gets as parameters a String message.
     * It sends the message to the server.
     * @param message The String message to send to the server.
     */
    void sendMessage(String message) {
        if (socket != null && socket.isConnected()) {
            new AsyncSocketWriter(socket, new Consumer<RuntimeException>() {
                @Override
                public void accept(RuntimeException e) {
                    runErrorHandlingFunctions(e);
                }
            }).execute(message);
        }
    }

    /**
     * The addUpdateFunction function gets as parameters a
     * Function<Pair<String, RuntimeException>, Void>
     * updateFunction and adds it to the updateFunctions.
     * @param updateFunction The Function<Pair<String, RuntimeException>, Void>
     *                       to add to the updateFunctions.
     */
    void addUpdateFunction(Function<Pair<String, RuntimeException>, Void> updateFunction) {
        updateFunctions.add(updateFunction);
    }

//    /**
//     * The removeUpdateFunction function gets as parameters a
//     * Function<Pair<String, RuntimeException>, Void>
//     * updateFunction and removes it from the updateFunctions if it is there.
//     * @param updateFunction The Function<Pair<String, RuntimeException>, Void>
//     * updateFunction to remove from the updateFunctions.
//     */
//    public void removeUpdateFunction(Function<Pair<String, RuntimeException>, Void> updateFunction) {
//        updateFunctions.remove(updateFunction);
//    }

    /**
     * The runErrorHandlingFunction gets as parameters a RuntimeException
     * and runs the errorHandlingFunction with it.
     * @param e RuntimeException e that has occured.
     */
    private void runErrorHandlingFunctions(RuntimeException e) {
        for (Consumer<RuntimeException> errorHandlingFunction : errorHandlingFunctions) {
            errorHandlingFunction.accept(e);
        }
    }




}
