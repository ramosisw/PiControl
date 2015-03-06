/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.itson.picontrol.threads;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Stack;

/**
 * Created by ADMIN on 18/05/2014.
 */
public class ClientThread extends Thread {

    Socket socket;
    String HOST;
    int PORT;
    boolean _READ = false;
    boolean _CONNECT = true;
    String _TO_READ = null;
    boolean _ERR = false;
    Listener mListener = null;
    Stack<String> commands;
    Activity context;

    public ClientThread(String host, int port) {
        HOST = host;
        PORT = port;
        commands = new Stack<String>();
    }

    private static String readMessageFromClientLockingThread(InputStream is, int lockSeconds) throws IOException {
        lockSeconds *= 100;//Convert to ms...
        /**
         * This code locks the thread until there's information available in the
         * client's output buffer OR it's been lockSeconds with no info...
         */
        long lockThreadCheckpoint = System.currentTimeMillis();
        int availableBytes = is.available();
        while (availableBytes < 1 && (System.currentTimeMillis() < lockThreadCheckpoint + lockSeconds)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            availableBytes = is.available();
        }

        /**
         * Create a byte array of the size of the data available in the client
         * buffer. As good practice, big data is supposed to be chopped in
         * smaller parts, so for this example we will not assume any buffer size
         * and we will, make a buffer of the size of the actual data.(Maximum
         * socket buffer size is OS dependent).
         */
        byte[] buffer = new byte[availableBytes];
        is.read(buffer, 0, availableBytes);
        return new String(buffer);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(HOST);
            if (serverAddr.isReachable(3000)) {
                socket = new Socket(serverAddr, PORT);
                if (socket.isConnected()) {
                    OutputStream out = socket.getOutputStream();
                    InputStream in = socket.getInputStream();
                    mListener.onConnected();
                    out.write("C".getBytes());
                    while (this._CONNECT && socket.isConnected() && !socket.isClosed()) {
                        if (!this.commands.isEmpty()) {
                            out.write(this.commands.pop().getBytes());
                        } else if (this._READ) {
                            this._READ = false;
                            this._TO_READ = readMessageFromClientLockingThread(in, 1);
                            mListener.onReceiveMessage(_TO_READ);
                        } else {
                            //out.write("TRUE".getBytes());
                        }
                        this._TO_READ = readMessageFromClientLockingThread(in, 1);
                        if (!_TO_READ.isEmpty())
                            mListener.onReceiveMessage(_TO_READ);
                    }
                    socket.close();
                    out.close();
                    in.close();
                    if(this._CONNECT)
                        mListener.onDisconnected("Disconnected");
                } else {
                    mListener.onDisconnected("Can't Connect");
                }
            } else {
                mListener.onDisconnected("Can't Connect, Isn't Reachable RPi");
            }
        } catch (UnknownHostException e1) {
            //e1.printStackTrace();
            mListener.onDisconnected("Can't Connect! \n UnknownHostException \n" + e1.getMessage());
        } catch (IOException e1) {
            //e1.printStackTrace();
            mListener.onDisconnected("Can't Connect! \n IOException \n" + e1.getMessage());
        } catch (Exception e1) {
            //e1.printStackTrace();
            //mListener.onDisconnected("Can't Connect! \n Exception \n" + e1.getMessage());
        }
    }

    public void sendCommand(String command) {
        this.commands.push(command);
        //System.out.println("Send: " + command);
    }

    public void closeConnection() {
        this._CONNECT = false;
    }

    public interface Listener {

        public void onReceiveMessage(String message);

        public void onDisconnected(String message);

        public void onConnected();

        public void onSendMessage();
    }

}
