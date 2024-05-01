package lk.ijse.dep12.client1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClinetApp1Initializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/InventoryView.fxml"))));
        primaryStage.setTitle("Client App1 : Inventory Control System");
        primaryStage.centerOnScreen();
        primaryStage.show();
        primaryStage.setResizable(false);

    }
}
