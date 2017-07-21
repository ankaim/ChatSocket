package client;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

// Добавлю действие на закрытие окна
// Добавление изменение размера техтАреа

public class Controller implements Initializable {
    public TextArea textArea;
    public TextField textField;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if(str.endsWith("end"))break;
                        textArea.appendText(str + "\n");
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
            });
            t.start();
        } catch (IOException e) {
            Platform.exit();
        }
    }

    public void onSendMsg() {
        try {
            out.writeUTF(textField.getText());
            if(textField.getText().equals("end"))socket.close();
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}