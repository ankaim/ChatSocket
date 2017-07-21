package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MyServer {
    static ServerSocket server = null;
    static Socket socket = null;
    final static int port = 8189;
    Vector<ClientHandler>clients;

    public MyServer() {
        try {
            server = new ServerSocket(port);
            clients = new Vector<>();
            while (true) {
                System.out.println("Сервер ждет подключения...");
                socket = server.accept();
                System.out.println("Клиет подключен!");
                clients.add(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            System.out.println("Ошибка при работе сервера!");
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void broadCastMsg(String msg){
        for(ClientHandler a: clients){
            a.sendMsg(msg);
        }
    }
    public synchronized void unsubscribe(ClientHandler o){
        clients.remove(o);
    }
}
