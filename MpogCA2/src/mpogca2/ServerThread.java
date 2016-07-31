/*
DIT/FT/3A/52
============
(1431632) Tan Yi Kang

DIT/FT/3A/34
============
(1431421) Quek Wen Qian
(1431476) Koh Tong Liang
(1431489) Jeremiah Chan Sheng En
 */
package mpogca2;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerThread implements Runnable {

    public volatile List<String> toSend = new ArrayList<>();//List for storing all player names
    public boolean outComplete = false;

    public final ExecutorService pool;
    private String readInput;
    public List<Handler> hList = new ArrayList<>();
    public String name;
    int id = 1;

    public ServerThread(int port, int poolSize) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        pool = Executors.newFixedThreadPool(poolSize);
    }//end of constructor

    public void ReceivedClientPos(String json) {

        JSONParser jP = new JSONParser();
        JSONObject receivedObj = new JSONObject();
        JSONObject updateClients = new JSONObject();
        JSONArray pArray = new JSONArray();
        JSONArray iArray = new JSONArray();
        int tempID = 0, tempXPos = 0, tempYPos = 0;
        boolean iA = false;

        try {
            //we unpack the object received from the client and update them accoridngly
            receivedObj = (JSONObject) jP.parse(json);

            //set the recevied object into the globalUpdate which will be sent to all clients 
            tempID = ((Long) receivedObj.get("playerID")).intValue(); //playerID
            pArray = (JSONArray) receivedObj.get("player"); //x and y of player
            iA = (boolean) receivedObj.get("alive"); //status of player

            //now we update the player accordingly 
            for (int i = 0; i < pArray.size(); i++) {

                if (i == 0) { //xPos                  

                    playerList.get(tempID - 1).position.x = ((Long) pArray.get(i)).intValue();

                }
                if (i == 1) { //yPos/

                    playerList.get(tempID - 1).position.y = ((Long) pArray.get(i)).intValue();
                }

                playerList.get(tempID - 1).updateLocation();
            }
            //playerList.get(tempID - 1).setIsAlive(iA);
            //System.out.println("playerList" + tempID + " X: " + playerList.get(tempID - 1).position.x);
        } catch (ParseException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //this is to update all clients 
    public void UpdateClients(String update) {

        clientList.forEach((client) -> {

            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(update);
                dos.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

    }

    @Override
    public void run() {
        serverStarted = true;
        if (serverRunning == true) {
            toSend.add(pLocal.getName());
            acceptClients();

//            pool.shutdownNow();
//            serverRunning = false;
//            serverStarted = false;
        }
    }//end of run

    public void acceptClients() {
        try {
            //to keep accepting and updating client lobbies as long as game has not started
            while (gameStarted == false && pCount < 3) {
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

                hList.forEach((r) -> {
                    h.updateClientChat("@" + id);
                });

            }//end of infinite loop
            serverSocket.close();
        } catch (Exception ex) {

        }
    }

    //reupdate all client's lobby whenever a client updates
    public void reUpdateClientLobby(String name) {
        String message = "\n" + name + " disconnected.\n";

        Platform.runLater(() -> {
            synchronized (listData) {
                listData.remove(name);

                listData.forEach((l) -> {

                });
                pLobby.setItems(listData);
                chatArea.appendText(message);
                chatSound.play();
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
            }
        }//end of updateClientChat

        public void SendBullets(String son) {

            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(son);
                dos.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        //remove anyclients that dc at clients' side
        public void removeDc(String remove) {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("-" + remove);
                dos.flush();
            } catch (IOException ex) {
                //System.out.println("Failed to update client's list view:\n " + ex.getMessage());
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
                                //System.out.println("Failed to sleep");
                            }
                        } catch (IOException ex) {
                            //System.out.println("Failed to update client's list view:\n " + ex.getMessage());
                        }
                    });
                }
            } catch (IOException ex) {
                //System.out.println("CLIENT UPDATE PART: " + ex.getMessage());
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
                            chatArea.appendText("\nA player has connected. Total player count: " + pCount + "\n");
                            chatSound.play();
                        });

                        //create a final list of players and send it to clients
                        readInput = dis.readUTF();

                        name = readInput.substring(1);
                        Platform.runLater(() -> {
                            listData.add(name);
                            pLobby.setItems(listData);
                        });

                        dos.writeUTF(pLocal.getName());
                        dos.flush();

                        while (true) {
                            dis = new DataInputStream(socket.getInputStream());

                            String received = dis.readUTF();

                            if (received.substring(0, 1).equals("\\") && received.substring(0, 2).equals("$")) {
                                final String t = received.replace("\\", "").replace("/", "");
                                System.out.println(t);
                                //UpdateClients(received);
                                hList.forEach((h) -> {
                                    h.updateClientChat(t);
                                });
                                System.out.println(received);
                                String x = received.substring(1);
                                ReceivedClientPos(x);
                            }

                            if (received.substring(0, 1).equals("/") && received.substring(0, 2).equals("$")) {
                                final String t = received.replace("/", "").replace("\\", "");
                                System.out.println(t);
                                //UpdateClients(received);
                                hList.forEach((h) -> {
                                    h.updateClientChat(t);
                                });
                                System.out.println(received);
                                String x = received.substring(1);
                                ReceivedClientPos(x);
                            }
                             //String received = dis.readUTF().replace("/", "").replace("\\", "");

//                            if (received.substring(0, 1).equals("\\") && received.substring(0, 2).equals("$")) {
//                                received.replace("\\", "").replace("/", "");
//                                System.out.println(received);
//                                //UpdateClients(received);
//                                hList.forEach((h) -> {
//                                    h.updateClientChat(received);
//                                });
//                                System.out.println(received);
//                                String x = received.substring(1);
//                                ReceivedClientPos(x);
//                            }
//
//                            if (received.substring(0, 1).equals("/") && received.substring(0, 2).equals("$")) {
//                                received.replace("/", "").replace("\\", "");
//                                System.out.println(received);
//                                //UpdateClients(received);
//                                hList.forEach((h) -> {
//                                    h.updateClientChat(received);
//                                });
//                                System.out.println(received);
//                                String x = received.substring(1);
//                                ReceivedClientPos(x);
//                            }

                            if (!received.trim().equals("") && !received.substring(0, 1).equals("$")) {
                                chatArea.appendText("\n" + received.substring(1));

                                chatSound.play();
                                hList.forEach((h) -> {
                                    h.updateClientChat(received);
                                });
                            }

                            //receive the client position 
                            if (received.substring(0, 1).equals("$")) {
                                hList.forEach((h) -> {
                                    h.updateClientChat(received);
                                });
                                System.out.println(received);
                                String x = received.substring(1);
                                ReceivedClientPos(x);
                            }

                        }//end of infinite loop
                    }
                }

            } catch (IOException ex) {
                hList.remove(this);//remove current handler from handler's list
                pCount--;
                synchronized (server) {
                    try {
                        serverSocket = new ServerSocket();
                        serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8000));
                        server.reUpdateClientLobby(name);
                        server.acceptClients();
                    } catch (Exception exc) {
                    }
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
                    //System.err.println("Pool did not terminate");
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
