package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

// focus to msgField
// 1. JPasswordField.getPassword() в чем передается? как принимать и сверять?
// он не equals ниразу тексту
// 2. как активно добавлять/удалять элементы в ArrayList
// (свинг и фх) с кнопок или событий и добавлять каждому элементу слушателя.
// например button[i] и слушатель к нему, как грамотно перерисовывать панели
// 3. можно ли прицепить слушателя на текст - ник в чате (свинг и фх), чтобы
// автоматически составить строку для привата для отсылки на сервер

public class Controller implements Initializable {
    public TextArea textArea;
    public TextField textField;

    public TextField loginField;
    public PasswordField passField;

    public HBox bottomPanel;
    public HBox upperPanel;

    public VBox clientsPanel;

    public BorderPane mainPanel;

    public ListView clientsList;
    public ObservableList<String> obsClients;

    private boolean authorized;
    private String myNick = "";

    public void setAuthorized(boolean authorized) {

        this.authorized = authorized;
        if (!this.authorized) {
            upperPanel.setManaged(true);
            bottomPanel.setManaged(false);//учитываетли этот элемент при растановке
            upperPanel.setVisible(true);
            bottomPanel.setVisible(false);
            clientsPanel.setManaged(false);
            clientsPanel.setVisible(false);
            Platform.runLater(() -> Main.mainStage.setTitle("JavaFX Client"));
        } else {
            upperPanel.setManaged(false);
            bottomPanel.setManaged(true);//учитываетли этот элемент при растановке
            upperPanel.setVisible(false);
            bottomPanel.setVisible(true);
            clientsPanel.setManaged(true);
            clientsPanel.setVisible(true);
            textArea.clear();
        }
    }

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
    }

    public void start() {
        try {
            setAuthorized(false);
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            obsClients = FXCollections.observableArrayList();
            clientsList.setItems(obsClients);
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok")) {
                            setAuthorized(true);
                            myNick = str.split("\\s")[1];
                            Platform.runLater(() -> Main.mainStage.setTitle("JavaFX Client: " + myNick));
                            break;
                        }
                        textArea.appendText(str + "\n");
                    }
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                break;
                            }
                            if (str.startsWith("/clients ")) {
                                String[] s = str.split("\\s");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        clientsList.getItems().clear();
                                        for (int i = 1; i < s.length; i++) {
                                            clientsList.getItems().add(s[i]);
                                        }
                                    }
                                });
                            }
                            if (str.startsWith("/yournickis")) {
                                myNick = str.split("\\s")[1];
                                Platform.runLater(() -> Main.mainStage.setTitle("JavaFX Client: " + myNick));
                            }
                        } else {
                            textArea.appendText(str + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        setAuthorized(false);
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        } catch (IOException e) {
            showAlert("Не удалось подключиться к серверу");
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSendMsg() {
        try {
            if (textField.getText().equals("/help")) {
                showAlert("HELP:\n\\w - шепнуть\n\\changenick - сменить ник\n\\end - выйти из чата");
                //textArea.appendText("HELP:\n\\w - шепнуть\\changenick - сменить ник\\end - выйти из чата");
                return;
            }
            out.writeUTF(textField.getText());
            if (textField.getText().equals("/end")) {
                setAuthorized(false);
                socket.close();
            }
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            showAlert("Проверьте сетевое соединение, или обратитесь к администратору");
            e.printStackTrace();
        }
    }

    public void onAuthClick() {
        if (socket == null || socket.isClosed())
            start();
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String msg) {//выводим алерт при ошибке
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Возникли проблемы");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();//ждем закрытия
    }

    public void listClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            textField.setText("/w " + clientsList.getSelectionModel().getSelectedItem().toString() + " ");
            textField.requestFocus();
            textField.selectEnd();
        }
    }
}