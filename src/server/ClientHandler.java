package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/auth")) {
                            String[] parts = str.split("\\s");
                            String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                            if (nick != null) {
                                if (!myServer.isNickBusy(nick)) {
                                    sendMsg("/authok " + nick);
                                    name = nick;
                                    myServer.broadcastMsg(name + " зашел в чат");
                                    myServer.subscribe(this);
                                    break;
                                } else sendMsg("Учетная запись уже используется");
                            } else {
                                sendMsg("Неверные логин/пароль");
                            }
                        }
                    }
                    while (true) {
                        String str = in.readUTF();
                        myServer.hist(name + ": " + str);
                        // System.out.println("from " + name + ": " + str);
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) break;
                            if (str.startsWith("/w ")) {
                                String[] tokens = str.split("\\s");
                                String nick = tokens[1];
                                String msg = str.substring(4 + nick.length());
                                myServer.sendMsgToClient(this, nick, msg);
                            }
                            if (str.startsWith("/changenick ")) {
                                String newNick = str.split("\\s")[1];
                                if (myServer.getAuthService().changeNick(this, newNick)) {
                                    changeNick(newNick);
                                } else {
                                    sendMsg("Указанный ник уже кем-то занят");
                                }
                            }
                        } else {
                            myServer.broadcastMsg(name + ": " + str);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    myServer.unsubscribe(this);
                    myServer.broadcastMsg(name + " вышел из чата");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public void changeNick(String newNick) {
        myServer.broadcastMsg(name + " сменил ник на " + newNick);
        name = newNick;
        sendMsg("/yournickis " + newNick);
        myServer.broadcastClientList();
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
