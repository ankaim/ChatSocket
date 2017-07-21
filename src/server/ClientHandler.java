package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String str = in.readUTF();
                            System.out.println("from client: "+ str);
                            if (str.equals("end")) break;
                            myServer.broadCastMsg(str);
                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                            myServer.unsubscribe(this);
                            try{
                                socket.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();

        }catch (IOException e ){
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
