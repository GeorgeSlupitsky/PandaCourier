package ua.com.pandasushi.database;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    private static final int PORT = 29999;
    private static ServerSocket serverSocket;

    public static void main(String... args) {
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("New client!!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new CourierThread(socket).start();
        }
    }
}
