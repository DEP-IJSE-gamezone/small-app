package lk.ijse.dep12.server;

import lk.ijse.dep12.shared.to.Item;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppInitializer {
    private static final File DB_FILE = new File("Inventory.db");
    private static final List<Socket> CLIENT_LIST = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5050)) {
            System.out.println("Server started");
            while (true) {
                Socket localSocket = serverSocket.accept();
                CLIENT_LIST.add(localSocket);
                sendCurrentItemList(localSocket);
                new Thread(() -> {
                    try (InputStream is = localSocket.getInputStream();
                         ObjectInputStream ois = new ObjectInputStream(is)) {

                        while (true) {
                            List<Item> list = (List<Item>) ois.readObject();
                            try (FileOutputStream fos = new FileOutputStream(DB_FILE);
                                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                                oos.writeObject(new ArrayList<>(list));
                            }
                            broadcastItemList();
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendCurrentItemList(Socket localSocket) throws IOException {
        if(DB_FILE.length()==0){
            new ObjectOutputStream(new FileOutputStream(DB_FILE)).writeObject(new ArrayList<>());
            return;
        }
        try (var ois=new ObjectInputStream(new FileInputStream(DB_FILE))){
            List<Item> itemList = (List<Item>) ois.readObject();
            new ObjectOutputStream(localSocket.getOutputStream()).writeObject(itemList);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void broadcastItemList() {
        new Thread(() -> {
            for (Socket client : CLIENT_LIST) {
                if (client.isConnected()) {
                    // file input strean chained with the object input stream to identify the object
                    try (FileInputStream fis = new FileInputStream(DB_FILE);
                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                        var oos = new ObjectOutputStream(client.getOutputStream());
                        List<Item> itemList = (List<Item>) ois.readObject();
                        oos.writeObject(itemList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

    }
}
