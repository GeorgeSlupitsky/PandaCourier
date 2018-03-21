package ua.com.pandasushi.database;

import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.database.common.CourierOrder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

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
                case GET_COURIER_LIST:
                    response = getCourierList(command);
                    break;
                case CHECK_COURIER_PASSWORD:
                    response = checkCourierPassword(command);
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
//        CourierOrder order = new CourierOrder();
//        order.setOrderID(1000041241l);
//        order.setCharcode("SV467");
//        order.setPhone("0631390085");
//        order.setName("Олег");
//        order.setStreet("Личаківська");
//        order.setHouse("10");
//        order.setApartament("2");
//        order.setRegionCharcode("СБ");
//        order.setRegionBackground(-16711936);
//        order.setPreferedTime(new Date());
//        order.setPromiseTime(new Date());
//        order.setFinalCost(100);
//        order.setDishes("ПРНС");
//        order.setCourierId(123);
//
//        CourierOrder order2 = new CourierOrder();
//        order.setOrderID(1000031241l);
//        order.setCharcode("SV437");
//        order.setPhone("0631390085");
//        order.setName("Єгор");
//        order.setStreet("Кавалерідзе");
//        order.setHouse("3");
//        order.setApartament("2");
//        order.setRegionCharcode("НВ");
//        order.setRegionBackground(-6605);
//        order.setPreferedTime(new Date());
//        order.setPromiseTime(new Date());
//        order.setFinalCost(100);
//        order.setDishes("ПР");
//        order.setCourierId(124);
//
//        CourierOrder order3 = new CourierOrder();
//        order.setOrderID(1000031251l);
//        order.setCharcode("SV433");
//        order.setPhone("0631390085");
//        order.setName("Єгор");
//        order.setStreet("Медової Печери");
//        order.setHouse("3");
//        order.setApartament("2");
//        order.setRegionCharcode("ПАС");
//        order.setRegionBackground(-11294689);
//        order.setPreferedTime(new Date());
//        order.setPromiseTime(new Date());
//        order.setFinalCost(100);
//        order.setDishes("ПРН");
//        order.setCourierId(null);
//
//        result.add(order);
//        result.add(order2);
//        result.add(order3);
        return result;
    }

    private Object updateOrder(CourierCommand command) {
        return null;
    }

    private Object saveTrack(CourierCommand command) {
        return null;
    }

    private Object getCourierList(CourierCommand command) {
        return null;
    }

    private Object checkCourierPassword(CourierCommand command) {
        return null;
    }

}
