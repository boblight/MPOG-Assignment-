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
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import static mpogca2.MpogCA2.*;
import mpogca2.engine.Bullet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author tongliang
 */
public class ClientThread implements Runnable {

    //update list of players
    String readInput = "";
    GameNetworkObject gnr;
    List<String> namesReceived = new ArrayList<>();
    public static ArrayList<Bullet> tempbList = new ArrayList<Bullet>();

    public void UnpackJSON(String bulletString) {

        int bulSize = 0;
        //ArrayList<Bullet> tempbList = new ArrayList<Bullet>();
        JSONParser jParser = new JSONParser();
        JSONObject outerObj = new JSONObject(); //entire object
        JSONObject innerObj = new JSONObject(); //one bullet
        JSONArray outerArray = new JSONArray(); //list of bullets 
        JSONArray innerArray = new JSONArray(); //access the coordinates inside the bullets 
        JSONArray posArray = new JSONArray();

        try {
            // System.out.println(bulletString);
            outerObj = (JSONObject) jParser.parse(bulletString);
            outerArray = (JSONArray) outerObj.get("BulletList");
            bulSize = outerArray.size();

            tempbList.clear();
            for (int i = 0; i < outerArray.size(); i++) {

                innerObj = (JSONObject) outerArray.get(i);
                //loop through the bullets to get the coordinates
                posArray = (JSONArray) innerObj.get("bullet");

                int xPos = 0, yPos = 0;
                for (int q = 0; q < posArray.size(); q++) {

                    String p = posArray.get(q).toString();
                    if (q == 0) {
                        xPos = Integer.parseInt(posArray.get(q).toString());
                    }
                    if (q == 1) {
                        yPos = Integer.parseInt(posArray.get(q).toString());

                        //System.out.println("Position: " + p);
                    }
                }
                System.out.println("xPos: " + xPos);
                System.out.println("yPos: " + yPos);
                Bullet b = new Bullet(xPos, yPos, 20, 5, "#9b59b6", xPos, yPos);

                tempbList.add(b);
                System.out.println("Bullet added");
            }

//            if (tempbList.size() == bulSize) {
//
//                System.out.println("tempbList size is: " + tempbList.size());
//                System.out.println("bulSize is " + bulSize);
//                //bulletList = tempbList;
//                System.out.println("bulletSize is " + bulletList.size());
//            }
        } catch (ParseException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

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

//                            try {
//                                gno = (GameNetworkObject) gdis.readObject();
//                                System.out.println("Object Received");
//                                
//                            } catch (ClassNotFoundException ex) {
//                                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
//                            }
                            readInput = dis.readUTF();
                            System.out.println("B4: " + readInput.substring(0, 1));
                            //readInput = "#{\"BulletList\":[{\"bullet\":[390,290]},{\"bullet\":[390,290]}]";
                            // System.out.println(readInput);
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
                            } else if (!namesReceived.contains(readInput) && !readInput.substring(0, 1).equals("+") && !readInput.substring(0, 1).equals("#")) { //+ for startgame, # for gamedata
                                Platform.runLater(() -> {
                                    listData.add(readInput);
                                    pLobby.setItems(listData);
                                });
                                namesReceived.add(readInput);
                            } else if (readInput.substring(0, 1).equals("+")) { //create server lobby gamestart button pressed, changing gamestarted boolean

                                System.out.println("received from network: " + readInput);
                                gameStarted = true;
                                System.out.println("client has changed gamestarted=true");
                                System.out.println("gameStarted:" + gameStarted);

                                Platform.runLater(() -> {

                                    if (gameStarted == true && clientStarted == true) {

                                        pLobby.setVisible(false);
                                        InitGamePaneClient(h);
                                    }

                                });
                            }
                            if (readInput.substring(0, 1).equals("#")) {

                                System.out.println("Hello its me");
                                String re = readInput.substring(1);
                                System.out.println(re);

                                //uppack the JSON and loop through to create the bulllets 
                                UnpackJSON(re);
                            }

                            System.out.println("After: " + readInput.substring(0, 1));
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
                            chatArea.appendText("\nDisconnected from host. Please exit to the menu.");
                        });
                        System.out.println("Disconnected from server");
                    } catch (IOException ex1) {
                        System.out.println("Cannot close connection.");
                    }
                }  //end of big try catch
            }
        }//end of client running block
    }//end of run method

}//end of client thread
