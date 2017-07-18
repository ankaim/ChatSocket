package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MyClass {
    static Socket socket = null;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 8189);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner scn = new Scanner(System.in);

            while(true){
                String str = scn.nextLine();
                if(str.equals("end")) break;
                out.writeUTF(str);
                System.out.println("from server: "+in.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
