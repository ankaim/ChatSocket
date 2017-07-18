import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Sever {
    static ServerSocket serverSocket = null;
    static Socket socket = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(8189);
            System.out.println("Сервер ждет подключения...");
            socket = serverSocket.accept();
            System.out.println("Клиет подключен!");
            Scanner scn = new Scanner(socket.getInputStream());
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            while (true) {
                String str = scn.nextLine();
                if (str.equals("end")) break;
                pw.write("echo " + str);
                pw.flush();
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
