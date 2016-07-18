/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mpogca2;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import static mpogca2.MpogCA2.*;


/**
 *
 * @author tongliang
 */
public class ServerThread implements Runnable {

    public volatile List<String> toSend = new ArrayList<>();//List for storing all player names
    public boolean outComplete = false;
    public final ExecutorService pool;
    private String readInput;
    public List<Handler> hList = new ArrayList<>();
    public String name;
    int id;

    public ServerThread(int port, int poolSize) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        pool = Executors.newFixedThreadPool(poolSize);
    }//end of constructor

    @Override
    public void run() {
        try {
            serverStarted = true;

            if (serverRunning == true) {
                toSend.add(pLocal.getName());

                //to keep accepting and updating client lobbies as long as game has not started
                while (gameStarted == false) {
                    socket = serverSocket.accept();//accept client
                    id++; //identify handlers
                    Handler h = new Handler(socket, this);//create new handler class each time client joins
                    h.id = id; //set ids to handlers
                    hList.add(h);//add handler to list of handler
                    pool.execute(h);//run handler class in new Thread

                    clientList.add(h);

                    //buffer time for network
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    hList.forEach((o) -> {
                        o.updateClientLobby();
                    });
                }//end of infinite loop
                pool.shutdownNow();
                serverRunning = false;
                serverStarted = false;
            }//end of server running
        } catch (IOException ex) {
            pool.shutdownNow();
            serverRunning = false;
            serverStarted = false;
        }
    }//end of run

    //reupdate all client's lobby whenever a client updates
    public void reUpdateClientLobby(String name) {
        String message = "\n" + name + " dced";
        //toSend.remove(name);
        Platform.runLater(() -> {
            synchronized (listData) {
                listData.remove(name);
                System.out.println("Iterating over listData:");
                listData.forEach((l) -> {
                    System.out.println(l);
                });
                pLobby.setItems(listData);
                chatArea.appendText(message);
            }
        });

        hList.forEach((h) -> {
            h.removeDc(name);
            h.updateClientChat("<" + message);
        });
    }//end of reUpdateClientLobby

    class Handler implements Runnable {

        private Socket socket;
        boolean running = true;
        int id;
        String name;
        ServerThread server;

        Handler(Socket socket, ServerThread server) {
            this.socket = socket;
            this.server = server;
        }//end of constructor

        //message to all client
        public void updateClientChat(String msg) {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException ex) {
                System.out.println("Failed to send messages to client");
            }
        }//end of updateClientChat

        //remove anyclients that dc at clients' side
        public void removeDc(String remove) {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("-" + remove);
                dos.flush();
            } catch (IOException ex) {
                System.out.println("Failed to update client's list view:\n " + ex.getMessage());
            }
        }//end of removeDc

        public void shutdown() {
            try {
                pool.shutdown();
                socket.close();
            } catch (Exception ex) {
                
            }
        }
        
        //to be called when needed to update client
        public void updateClientLobby() {
            try {
                dos = new DataOutputStream(socket.getOutputStream());

                synchronized (listData) {
                    //send each name to all clients
                    listData.forEach((s) -> {
                        try {
                            dos.writeUTF(s);
                            dos.flush();
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException ex) {
                                System.out.println("Failed to sleep");
                            }
                        } catch (IOException ex) {
                            System.out.println("Failed to update client's list view:\n " + ex.getMessage());
                        }
                    });
                }
            } catch (IOException ex) {
                System.out.println("CLIENT UPDATE PART: " + ex.getMessage());
            }
        }//end of lient update

        //initialize each handler
        @Override
        public void run() {
            // read and service request on socket
            try {
                if (gameStarted == false) {
                    //let server accept clients only while in lobby
                    if (socket.isConnected()) {
                        dis = new DataInputStream(socket.getInputStream());
                        dos = new DataOutputStream(socket.getOutputStream());

                        pCount++;
                        Platform.runLater(() -> {
                            chatArea.appendText("\nA player has connected. Total player count: " + pCount +"\n");
                        });

                        //create a final list of players and send it to clients
                        readInput = dis.readUTF();
                        if (!readInput.substring(0, 1).equals("<")) {
                            name = readInput;
                            Platform.runLater(() -> {
                                listData.add(name);
                                pLobby.setItems(listData);
                            });
                        }

                        dos.writeUTF(pLocal.getName());
                        dos.flush();

                        while (true) {
                            dis = new DataInputStream(socket.getInputStream());
                            String received = dis.readUTF();

                            if (!received.trim().equals("")) {
                                System.out.println("Clients message: " + received);
                                chatArea.appendText("\n" + received.substring(1));
                                hList.forEach((h) -> {
                                    h.updateClientChat(received);
                                });
                            }
                        }//end of infinite loop
                    }
                }
            } catch (IOException ex) {
                hList.remove(this);//remove current handler from handler's list
                synchronized (server) {
                    server.reUpdateClientLobby(name);
                }
            }
        }//end of run method
    }//end of handler class

    static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }//shutdown ExecutorService

}//end of server thread
