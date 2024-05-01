package lk.ijse.dep12.client1.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lk.ijse.dep12.shared.to.Item;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class InventoryViewController {
    public Button btnDelete;

    public Button btnNewItem;

    public Button btnSave;

    public TableView<Item> tblDisplay;

    public TextField txtBarcode;

    public TextField txtDescription;

    public TextField txtPrice;

    public TextField txtQuantity;
    private Socket remoteSocket;
    private ObjectOutputStream oos;

    public void initialize() throws IOException, ClassNotFoundException {
        remoteSocket = new Socket("localhost", 5050);
        oos = new ObjectOutputStream(remoteSocket.getOutputStream());
        loadStock();

        tblDisplay.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("barcode"));
        tblDisplay.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblDisplay.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblDisplay.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));

        tblDisplay.getSelectionModel().selectedItemProperty().addListener((observable, previous, current) -> {
            btnSave.setDisable(current != null);
            btnDelete.setDisable(current == null);
            if (current != null) {
                txtBarcode.setText(current.getBarcode());
                txtDescription.setText(current.getDescription());
                txtQuantity.setText(String.valueOf(current.getQty()));
                txtPrice.setText(current.getPrice().toString());
            }
        });
        btnDelete.setDisable(true);
    }

    private void loadStock() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(remoteSocket.getInputStream());
        List<Item> itemList = (List<Item>) ois.readObject();
       tblDisplay.getItems().addAll(itemList);
    }

    public void btnDeleteOnaction(ActionEvent event) throws IOException {
        Item selectedItem = tblDisplay.getSelectionModel().getSelectedItem();
        ObservableList<Item> itemList = tblDisplay.getItems();
        itemList.remove(selectedItem);

        oos.writeObject(new ArrayList<>(itemList));
        if (itemList.isEmpty()) btnNewItem.fire();

    }

    public void btnNewItemOnAction(ActionEvent event) {
        for (TextField textField : new TextField[]{txtBarcode, txtDescription, txtQuantity, txtPrice})
            textField.clear();
        btnSave.setDisable(false);
        tblDisplay.getSelectionModel().clearSelection();
        txtBarcode.requestFocus();

    }

    public void btnSaveOnAction(ActionEvent event) throws IOException {
        // data validation
        TextField textField;
        if ((textField = validateData()) != null) {
            textField.requestFocus();
            textField.selectAll();
            return;
        }

        ObservableList<Item> itemList = tblDisplay.getItems();
        String barcode = txtBarcode.getText().strip();
// Business validation
        for (Item item : itemList) {
            if (item.getBarcode().equals(barcode)) {
                txtBarcode.requestFocus();
                txtBarcode.selectAll();
                new Alert(Alert.AlertType.ERROR, "Barcode is already exist.").show();
                return;
            }
        }

        Item newItem = new Item(txtBarcode.getText().strip(),
                txtDescription.getText().strip(),
                Integer.parseInt(txtQuantity.getText().strip()),
                new BigDecimal(txtPrice.getText()));
        itemList.add(newItem);

        oos.writeObject(new ArrayList<>(itemList));
        btnNewItem.fire();

    }

    public void tblOnKeyPressed(KeyEvent event) {

    }

    private TextField validateData() {
        String barCode = txtBarcode.getText();
        String description = txtDescription.getText();
        String qty = txtQuantity.getText();
        String price = txtPrice.getText();

        if (barCode.isBlank() || !isNumber(barCode.strip())) return txtBarcode;
        else if (description.isBlank() || description.length() < 3) return txtDescription;
        else if (qty.isBlank() || !isNumber(qty.strip()) || Integer.parseInt(qty.strip()) <= 0) return txtQuantity;
        else if (price.isBlank() || !isPrice(price)) return txtPrice;

        return null;
    }

    private boolean isNumber(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    private boolean isPrice(String input) {
        try {
            BigDecimal price = new BigDecimal(input);
            return price.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }

    }
}
