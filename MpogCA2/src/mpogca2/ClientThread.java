/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mpogca2;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import static mpogca2.MpogCA2.*;

/**
 *
 * @author tongliang
 */
public class ClientThread implements Runnable {

    //update list of players
    String readInput = "";
    List<String> namesReceived = new ArrayList<>();

    @Override
    public void run() {
        clientStarted = true;
        if (clientRunning) {
            if (socket.isConnected()) {
                try {
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());

                    dos.writeUTF(pLocal.getName());
                    dos.flush();

                    while (clientRunning == true) {

                        if (socket.isConnected()) {
                            dis = new DataInputStream(socket.getInputStream());
                            readInput = dis.readUTF();
                            if (readInput.substring(0, 1).equals("<")) {
                                System.out.println("Yes" + readInput);
                                String received = readInput.substring(1);
                                chatArea.appendText("\n" + received);
                            } else if (readInput.substring(0, 1).equals("-")) {
                                String received = readInput.substring(1);
                                namesReceived.remove(received);
                                Platform.runLater(() -> {
                                    listData.remove(received);
                                    pLobby.setItems(listData);
                                });
                            } else if (!namesReceived.contains(readInput) && !readInput.substring(0, 1).equals("+")) {
                                Platform.runLater(() -> {
                                    listData.add(readInput);
                                    pLobby.setItems(listData);
                                });
                                namesReceived.add(readInput);
                            }
                            else if (readInput.substring(0, 1).equals("+")) { //create server lobby gamestart button pressed, changing gamestarted boolean
                                
                                System.out.println("received from network: " + readInput);
                                gameStarted=true;
                                System.out.println("client has changed gamestarted=true");
                            }
                        } else {
                            break;
                        }
                    }//end of loop
                    socket.close();
                    dis.close();
                    dos.close();
                    clientRunning = false;
                    clientStarted = false;
                    System.out.println("Disconnected from server");
                } catch (IOException ex) {
                    try {
                        socket.close();
                        dis.close();
                        dos.close();
                        clientRunning = false;
                        clientStarted = false;
                        Platform.runLater(() -> {
                            chatArea.appendText("\nDisconnected from host. Please exit to main menu.");
                        });
                        System.out.println("Disconnected from server");
                    } catch (IOException ex1) {
                        System.out.println("Cannot close connection.");
                    }
                }//end of big try catch
            }
        }//end of client running block
    }//end of run method

}//end of client thread
