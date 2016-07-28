package mpogca2;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import static mpogca2.MpogCA2.*;

public class GameClient implements Runnable {

    //update list of players
    //String readInput = "";
    //List<String> namesReceived = new ArrayList<>();
    
    GameNetworkObject gno=new GameNetworkObject();

    @Override
    public void run() {

        if (clientRunning) {
            if (gsocket.isConnected()) {
                try {
                    gdis = new ObjectInputStream(gsocket.getInputStream());
                    gdos = new ObjectOutputStream(gsocket.getOutputStream());

                    gdos.writeObject(gno);
                    gdos.flush();

                    while (clientRunning == true) {

                        if (gsocket.isConnected()) {
                            
                            try {
                                gdis = new ObjectInputStream(gsocket.getInputStream());
                                gno = (GameNetworkObject)gdis.readObject();
                            } catch(IOException e) {
                                break;
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                    }//end of loop
                    gsocket.close();
                    gdis.close();
                    gdos.close();
                    clientRunning = false;
                    clientStarted = false;
                    System.out.println("Disconnected from server");
                    }
                    } catch (IOException ex) {
                    try {
                        gsocket.close();
                        gdis.close();
                        gdos.close();
                        clientRunning = false;
                        clientStarted = false;
                        
                    } catch (IOException ex1) {
                        
                    }
                }//end of big try catch
            }   
            
        }//end of client running block
    }//end of run method

}//end of client thread
