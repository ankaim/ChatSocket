package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MyServer {
    private static ServerSocket server = null;
    private static Socket socket = null;
    private final static int port = 8189;
    private Vector<ClientHandler>clients;
    private AuthServic authServic;

    public AuthServic getAuthServic() {
        return authServic;
    }

    public MyServer() {
        try {
            server = new ServerSocket(port);
            clients = new Vector<>();
            //authServic = new BaseAuthServis();
            authServic = new DBAuthService();
            authServic.start();
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
            authServic.stop();
        }
    }
    public synchronized boolean isNickBusy(String nick){
        for(ClientHandler as: clients){
            if(nick.equals(as.getName())){
                return true;
            }
        }
        return false;
    }
    public synchronized void broadCastMsg(String msg){
        for(ClientHandler a: clients){
            a.sendMsg(msg);
        }
    }
    public synchronized void unsubscribe(ClientHandler o){
        clients.remove(o);
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }

    public void privatMsg(String s, String s1) {
        for(ClientHandler as:clients){
            if(s.equals(as.getName())){
                as.sendMsg(s1);
            }
        }
    }
}
