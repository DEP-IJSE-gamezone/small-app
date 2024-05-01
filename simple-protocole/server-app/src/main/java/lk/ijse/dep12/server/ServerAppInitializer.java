package lk.ijse.dep12.server;

import lk.ijse.dep12.shared.to.Customer;
import lk.ijse.dep12.shared.to.Request;
import lk.ijse.dep12.shared.to.Response;
import lk.ijse.dep12.shared.util.Status;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAppInitializer {
    public static void main(String[] args) throws IOException {
        //open server socket at given port
        try (ServerSocket serverSocket = new ServerSocket(5050)) {
            System.out.println("Server Started");

            while (true) {
                //accept the request
                Socket localSocket = serverSocket.accept();
                System.out.println(" New Client :" + localSocket.getRemoteSocketAddress());

                new Thread(() -> {

                    try {
                        ObjectInputStream ois = new ObjectInputStream(localSocket.getInputStream());
                        ObjectOutputStream oos = new ObjectOutputStream(localSocket.getOutputStream());
                       while (true) {
                           try {
                               Request clientRequest = (Request) ois.readObject();

                               switch (clientRequest.getType()) { // get the object type
                                   case CUSTOMER -> {
                                       try {
                                           Customer customer = (Customer) clientRequest.getBody();

                                           if (customer != null) {
                                               oos.writeObject(new Response(Status.SUCCESS, customer.toString()));
                                           } else {
                                               oos.writeObject(new Response(Status.BAD_REQUEST, "Empty customer"));
                                           }
                                       } catch (ClassCastException e) {
                                           oos.writeObject(new Response(Status.BAD_REQUEST, "Invalid Customer object "));
                                       }
                                   }
                                   case FILE -> {
                                       try {
                                           byte[] file = (byte[]) clientRequest.getBody();
                                           if (file != null) {
                                               oos.writeObject(new Response(Status.SUCCESS, "File recieved"));
                                           } else {
                                               oos.writeObject(new Response(Status.BAD_REQUEST, "Empty File"));
                                           }

                                       } catch (ClassCastException e) {
                                           oos.writeObject(new Response(Status.BAD_REQUEST, "Invalid File"));
                                       }
                                   }
                                   default -> {
                                       try {
                                           String message = (String) clientRequest.getBody();
                                           if (message != null) {
                                               oos.writeObject(new Response(Status.SUCCESS, "Message recieved " + message));
                                           } else {
                                               oos.writeObject(new Response(Status.BAD_REQUEST, "Empty massage"));
                                           }
                                       } catch (ClassCastException e) {
                                           oos.writeObject(new Response(Status.BAD_REQUEST, "Invalid Message"));
                                       }
                                   }


                               }
                           } catch (ClassNotFoundException | IOException e) {
                               throw new RuntimeException(e);
                           }
                       }
                        } catch(IOException e){
                            throw new RuntimeException(e);
                        }

                }).start();
            }
        }
    }
}
