package client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    public static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = (Controller) loader.getController();//запрашиваем у сцены ссылку на контроллер
        primaryStage.setTitle("JavaFX Client");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
        mainStage = primaryStage;
        // primaryStage.setOpacity(0.5);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {//слушатель на закрытие окна
            @Override
            public void handle(WindowEvent event) {
                try {
                    controller.closeConnection();//при закрытии окна мы залезаем в сцену и обрубаем сеть
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
