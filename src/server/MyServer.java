package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Vector;

public class MyServer {
    private ServerSocket server;
    private Vector<ClientHandler> clients; //синхронизированная коллекция
    private AuthService authService;
    private PrintWriter out;

    public AuthService getAuthService() {
        return authService;
    }

    private final int PORT = 8189;

    public MyServer() {
        try {
            out = new PrintWriter("localHistory.txt");
            server = new ServerSocket(PORT);
            Socket socket = null;
            authService = new DBAuthService();
            authService.start();
            clients = new Vector<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при работе сервера");
        } finally {
            try {
                server.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            authService.stop();
        }
    }

    public void hist(String msg) {
        out.println(Calendar.getInstance().getTime().toString() + " " + msg);
        out.flush();
    }

    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nickTo)) {
                o.sendMsg("from " + from.getName() + ": " + msg);
                from.sendMsg("to " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Участника с ником " + nickTo + " нет в чат-комнате");
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) return true;
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
        broadcastClientList();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
        broadcastClientList();
    }

    public synchronized void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler o : clients) {
            sb.append(o.getName() + " ");
        }
        String msg = sb.toString();
        broadcastMsg(msg);
    }
}
