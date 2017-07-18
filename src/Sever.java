import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Sever {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            System.out.println("Сервер ждет подключения...");
            Socket socket = serverSocket.accept();
            System.out.println("Клиет подключен!");
            Scanner scn = new Scanner(socket.getInputStream());
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            while (true){
                String str = scn.nextLine();
                if(str.equals("end")) break;
                pw.write("echo "+str);
                pw.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
