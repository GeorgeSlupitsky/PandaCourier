package ua.com.pandasushi.pandacourierv2.connection;

import android.os.AsyncTask;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;

/**
 * Created by User9 on 21.03.2018.
 */

public class SocketAsyncTask extends AsyncTask<CourierCommand, Void, Object> {

    //    private final String HOST = "192.168.0.105"; //home
    private static final String HOST = "192.168.88.254"; //work
    private static final int PORT = 29999;

    @Override
    protected Object doInBackground(CourierCommand... commands) {
        Object response = null;

        try {
            InetAddress ipAddress = InetAddress.getByName(HOST);
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, PORT), 1000);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(commands[0]);
            oos.flush();
            try {
                response = ois.readObject();
            } catch (EOFException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
