package lk.ijse.dep12.client2.controller;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep12.shared.to.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class MainViewController {

    public TableView<Item> tbl;
    private Socket remoteSocket;

    public void initialize() throws IOException {
        remoteSocket = new Socket("localhost", 5050);
        loadStock();
        tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("barcode"));
        tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadStock() {
        new Thread(() -> {
            while (true) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(remoteSocket.getInputStream());
                    List<Item> itemList = (List<Item>) ois.readObject();
                    Platform.runLater(()->{
                        tbl.getItems().clear();
                        tbl.getItems().addAll(itemList);
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
