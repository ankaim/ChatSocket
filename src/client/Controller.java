package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

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

    public TextField loginFild;
    public PasswordField passwordFild;

    public HBox upperPanel;
    public HBox bottonPanel;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private boolean authorized;

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        if(!authorized){
            upperPanel.setVisible(true);
            bottonPanel.setVisible(false);
        }else{
            upperPanel.setVisible(false);
            bottonPanel.setVisible(true);
            textArea.clear();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(() -> {
                try {
                    while (true){
                        String str = in.readUTF();
                        if(str.startsWith("/authok")){
                            setAuthorized(true);
                            break;
                        }
                        textArea.appendText(str+"/n");
                    }
                    while (true) {
                        String str = in.readUTF();
                        if(str.endsWith("/end"))break;
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
                    setAuthorized(false);
                }
            });
            t.setDaemon(true);
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

    public void onActionClick() {
        try {
            out.writeUTF("/auth " + loginFild.getText()+ " "+ passwordFild.getText());
            loginFild.clear();
            passwordFild.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}