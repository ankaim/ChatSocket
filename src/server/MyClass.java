package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyClass {
    static ServerSocket serverSocket = null;
    static Socket socket = null;

    public static void main(String[] args) {
        try {
            while (true) {
                serverSocket = new ServerSocket(8189);
                System.out.println("Сервер ждет подключения...");
                socket = serverSocket.accept();
                System.out.println("Клиет подключен!");
                new ClientHandler(socket);
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
