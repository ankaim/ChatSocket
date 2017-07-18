package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MyClass {
    static ServerSocket serverSocket = null;
    static Socket socket = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(8189);
            System.out.println("Сервер ждет подключения...");
            socket = serverSocket.accept();
            System.out.println("Клиет подключен!");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            while (true) {
                String str = in.readUTF();
                if (str.equals("end")) break;
                out.writeUTF("echo " + str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
