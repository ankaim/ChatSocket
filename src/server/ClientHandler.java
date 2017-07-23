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
    private static String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;

            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String str = in.readUTF();
                            if (str.startsWith("/auth")) {
                                String[] parts = str.split("\\n");
                                String nick = myServer.getAuthServic().getNickLoginPass(parts[1], parts[2]);
                                if(nick!=null){
                                    if(!myServer.isNickBusy(nick)) {
                                        sendMsg("/authok " + nick);
                                        name = nick;
                                        myServer.broadCastMsg(name+" зашел в чат");
                                        myServer.subscribe(this);
                                        break;
                                    }else sendMsg("Учетная запись уже используется.");
                                }else sendMsg("Не верные логин/пароль");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    while (true) {
                        try {
                            String str = in.readUTF();
                            System.out.println("from "+name+": " + str);
                            if (str.equals("end")) break;
                            myServer.broadCastMsg(name + ": "+ str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            myServer.broadCastMsg(name+" покинул чат");
                            myServer.unsubscribe(this);
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            throw new RuntimeException("Проблеммы при создании обработчика");
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
