package ua.com.pandasushi.pandacourierv2.connection;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.pandasushi.pandacourierv2.R;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import ua.com.pandasushi.database.common.CourierCommand;

/**
 * Created by User9 on 21.03.2018.
 */

public class SocketAsyncTask extends AsyncTask<CourierCommand, Void, Object> {

//    private final String HOST = "192.168.1.72"; //home
//    private final String HOST = "192.168.1.152"; //Panda
//    private static final String HOST = "192.168.88.254"; //work
    private static final String HOST = "192.168.88.94"; //work my laptop
    private static final int PORT = 29999;
    private Context context;
    private ProgressDialog pDialog;

    public SocketAsyncTask(){
    }

    public SocketAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (context != null){
            setDialog(true);
        }
    }

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

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (context != null){
            setDialog(false);
        }
    }

    private void setDialog(boolean show){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.progress);
        Dialog dialog = builder.create();
        if (show) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}
