package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String str = in.readUTF();
                            System.out.println("from client: "+ str);
                            if (str.equals("end")) break;
                            sendMsg("echo " + str);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }catch (IOException e ){
            throw new RuntimeException("Проблеммы при создании обработчика");
        }
    }

    private void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
