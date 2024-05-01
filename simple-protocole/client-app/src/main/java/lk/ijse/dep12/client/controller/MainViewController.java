package lk.ijse.dep12.client.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import lk.ijse.dep12.shared.to.Customer;
import lk.ijse.dep12.shared.to.Request;
import lk.ijse.dep12.shared.to.Response;
import lk.ijse.dep12.shared.util.Type;

import java.io.*;
import java.net.Socket;

public class MainViewController {


    public TextField txtBrowse;

    public TextField txtId;

    public TextField txtName;

    public TextField txtmsg;
    public Button btnSendMsg;
    public Button btnSendCustomer;
    public Button btnBrowse;
    public Button btnSendFile;
    private ObjectOutputStream oos;

    public void initialize() throws IOException {
        Socket remoteSocket = new Socket("localhost", 5050);
        oos = new ObjectOutputStream(remoteSocket.getOutputStream());

        new Thread(() -> {
            try {
                ObjectInputStream ois = new ObjectInputStream(remoteSocket.getInputStream());
                while (true) {
                    Response response = (Response) ois.readObject();
                    System.out.println(response);
                    disabledSendButton(false);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
    public void btnBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");
        File file = fileChooser.showOpenDialog(btnSendFile.getScene().getWindow());
        txtBrowse.setText(file != null ? file.getAbsolutePath() : "");

    }


    public void btnSendFile(ActionEvent event) throws IOException {
        File file = new File(txtBrowse.getText());
        FileInputStream fis = new FileInputStream(file);
        byte[] read = fis.readAllBytes();
        Request request = new Request(Type.FILE, read);
        oos.writeObject(request);
        txtBrowse.clear();
        disabledSendButton(true);
        System.out.println("File object has been sent");
    }

    public void btnSendCustomerOnAction(ActionEvent event) throws IOException {
        Customer customer = new Customer(txtId.getText(), txtName.getText());
        Request request = new Request(Type.CUSTOMER, customer);
        oos.writeObject(request);
        txtId.clear();
        txtName.clear();
       disabledSendButton(true);
        System.out.println("Customer object has been sent");

    }


    public void btnSendmsgOnAction(ActionEvent event) throws IOException {
        Request request = new Request(Type.MESSAGE, txtmsg.getText());
        oos.writeObject(request);
        txtmsg.clear();
        disabledSendButton(true);
        System.out.println("Massage object has been sent");


    }


    private void disabledSendButton(boolean disable) {
        btnSendCustomer.setDisable(disable);
        btnSendMsg.setDisable(disable);
        btnSendFile.setDisable(disable);
        btnBrowse.setDisable(disable);
    }


}
