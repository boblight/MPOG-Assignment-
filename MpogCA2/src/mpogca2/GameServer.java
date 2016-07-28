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

public class GameServer implements Runnable {

    public final ExecutorService pool;
//    private String readInput;
    public List<Handler> hList = new ArrayList<>();

    public GameServer(int port, int poolSize) throws IOException {
        gserverSocket = new ServerSocket();
        gserverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        pool = Executors.newFixedThreadPool(poolSize);
    }//end of constructor

    @Override
    public void run() {
//        try {
        serverStarted = true;

        if (serverRunning == true) {
            //toSend.add(pLocal.getName());

            //to keep accepting and updating client lobbies as long as game has not started
            while (gameStarted) {
                System.out.println("Hello");
                //socket = serverSocket.accept();//accept client
                //id++; //identify handlers
                // Handler h = new Handler(gsocket, this);//create new handler class each time client joins
                // hList.add(h);//add handler to list of handler
                // pool.execute(h);//run handler class in new Thread

                //gclientList.add(h);
                //buffer time for network
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                    hList.forEach((o) -> {
//                        o.updateClient();
//                    });
                if (gsocket.isConnected()) {

                    try {

                        gdis = new ObjectInputStream(gsocket.getInputStream());
                        gdos = new ObjectOutputStream(gsocket.getOutputStream());

                        gdos.writeObject(gno);

                        System.out.println("Object sent");

                    } catch (Exception ex) {

                    }

                }

            }//end of infinite loop
            pool.shutdownNow();
            serverRunning = false;
            serverStarted = false;
        }//end of server running
//        } 
//        catch (IOException ex) {
//            pool.shutdownNow();
//            serverRunning = false;
//            serverStarted = false;
//        }
    }//end of run

    class Handler implements Runnable {

        private Socket gameSocket;
        boolean running = true;

        GameServer gameServer;

        Handler(Socket socket, GameServer server) {
            this.gameSocket = socket;
            this.gameServer = server;
        }//end of constructor

        //send to all client
        public void sendToClient(GameNetworkObject gno) {
            try {
                gdis = new ObjectInputStream(socket.getInputStream());
                gdos = new ObjectOutputStream(gsocket.getOutputStream());
                gdos.writeObject(gno);
                gdos.flush();
            } catch (IOException ex) {
                System.out.println("Failed to send game object to client");
            }
        }//end of sendToClient

        //receive from client 
        public GameNetworkObject receiveFromClient() {

            GameNetworkObject gno = new GameNetworkObject();

            try {

                gdis = new ObjectInputStream(socket.getInputStream());
                gdos = new ObjectOutputStream(socket.getOutputStream());
                gno = (GameNetworkObject) gdis.readObject();

            } catch (Exception ex) {
                System.out.println("Failed to send game object to client");
            }

            return gno;

        }

//        //remove anyclients that dc at clients' side
//        public void removeDc(String remove) {
//            try {
//                dos = new DataOutputStream(socket.getOutputStream());
//                dos.writeUTF("-" + remove);
//                dos.flush();
//            } catch (IOException ex) {
//                System.out.println("Failed to update client's list view:\n " + ex.getMessage());
//            }
//        }//end of removeDc
        public void shutdown() {
            try {
                pool.shutdown();
                gsocket.close();
            } catch (Exception ex) {

            }
        }

        //initialize each handler
        @Override
        public void run() {

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
