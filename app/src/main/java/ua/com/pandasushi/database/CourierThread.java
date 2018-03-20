package ua.com.pandasushi.database;

import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.database.common.CourierOrder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class CourierThread extends Thread {
    protected Socket socket;

    public CourierThread(Socket clientSocket) {
        this.socket = clientSocket;
        System.out.println("thread created");
    }

    @Override
    public void run() {
        System.out.println("thread running");
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Streams initialized");
        CourierCommand command = null;
        System.out.println("Waiting for a command");
        try {
            command = (CourierCommand) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(command.getCommand().name());

        Object response = null;
        if (command != null) {
            switch (command.getCommand()) {
                case CHECK:
                    response = check(command);
                    break;
                case START_CHANGE:
                    response = startChange(command);
                    break;
                case END_CHANGE:
                    response = endChange(command);
                    break;
                case GET_ORDER_LIST:
                    response = getOrderList(command);
                    break;
                case UPDATE_ORDER:
                    response = updateOrder(command);
                    break;
                case SAVE_TRACK:
                    response = saveTrack(command);
                    break;
                default:
                    break;
            }
        }

        if (response != null) {
            try {
                oos.writeObject(response);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private Object check(CourierCommand command) {
        return "OK";
    }

    private Object startChange(CourierCommand command) {
        return "OK";
    }

    private Object endChange(CourierCommand command) {
        return "OK";
    }

    private ArrayList<CourierOrder> getOrderList(CourierCommand command) {
        ArrayList<CourierOrder> result = new ArrayList<>();
        CourierOrder order = new CourierOrder();
        order.setOrderID(1000041241l);
        order.setCharcode("SV467");
        result.add(order);
        return result;
    }

    private Object updateOrder(CourierCommand command) {
        return null;
    }

    private Object saveTrack(CourierCommand command) {
        return null;
    }

}
