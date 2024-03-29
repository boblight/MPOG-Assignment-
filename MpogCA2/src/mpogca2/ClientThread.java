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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class ClientThread implements Runnable {

    //update list of players
    String readInput = "";
    List<String> namesReceived = new ArrayList<>();
    public static ArrayList<Bullet> tempbList = new ArrayList<Bullet>();
    MpogCA2 main;

    //SYMBOLS WIKI:
    //$ : player data 
    //< : chat messages 
    //- : to handle disconnect
    //+ : to start game 
    //@ : player ID 
    //# : bullet data
    //? : pCount
    //* : end game
    
    public ClientThread(MpogCA2 main2) {
        main = main2;
    }

    //unpack the bullet data received and update accordingly 
    public void UnpackBullets(String bulletString) {

        int bulSize = 0;
        JSONParser jParser = new JSONParser();
        JSONObject outerObj = new JSONObject(); //entire object
        JSONObject innerObj = new JSONObject(); //one bullet
        JSONArray outerArray = new JSONArray(); //list of bullets 
        JSONArray innerArray = new JSONArray(); //access the coordinates inside the bullets 
        JSONArray posArray = new JSONArray();

        try {
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
                    }
                }
                Bullet b = new Bullet(xPos, yPos, 20, 5, "#9b59b6", xPos, yPos);
                tempbList.add(b);
            }
        } catch (ParseException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //unapack client data received and update accordingly
    public void UnpackPlayer(String player) {

        JSONParser p = new JSONParser();
        JSONObject pObject = new JSONObject();
        JSONArray innerArray = new JSONArray();
        int tempXPos = 0, tempYPos = 0, tempID = 0;
        int isAlive = 0;

        try {
            //convert string to JSON
            try {
                player = player.replace("$", "");
                pObject = (JSONObject) p.parse(player);
            } catch (Exception e) {
                pObject = (JSONObject) p.parse("{" + player);
            }
            //get player details
            tempID = ((Long) pObject.get("playerID")).intValue();

            if (tempID == playerID) {

                isAlive = ((Long) pObject.get("alive")).intValue();

            } else {

                isAlive = ((Long) pObject.get("alive")).intValue();

                innerArray = (JSONArray) pObject.get("player"); //get the x and y pos
                for (int t = 0; t < innerArray.size(); t++) {

                    if (t == 0) {
                        playerList.get(tempID - 1).position.x = ((Long) innerArray.get(t)).intValue();
                    }
                    if (t == 1) {
                        playerList.get(tempID - 1).position.y = ((Long) innerArray.get(t)).intValue();
                    }
                }
            }
            playerList.get(tempID - 1).updateLocation();

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

                    dos.writeUTF("!" + pLocal.getName());
                    dos.flush();

                    while (clientRunning == true) {

                        if (socket.isConnected()) {
                            dis = new DataInputStream(socket.getInputStream());

                            readInput = dis.readUTF().replace("/", "").replace("\\", "");

                            if (readInput.substring(0, 1).equals("$")) {

                                UnpackPlayer(readInput);
                            }

                            //for CHAT
                            if (readInput.substring(0, 1).equals("<")) {
                                String received = readInput.substring(1);
                                chatArea.appendText("\n" + received);
                                chatSound.play();

                                //for DC
                            } else if (readInput.substring(0, 1).equals("-")) {
                                String received = readInput.substring(1);
                                namesReceived.remove(received);
                                Platform.runLater(() -> {
                                    listData.remove(received);
                                    pLobby.setItems(listData);
                                });

                            } //this is for lobby NAME
                            else if (!namesReceived.contains(readInput) && !readInput.substring(0, 1).equals("+") && !readInput.substring(0, 1).equals("#") && !readInput.substring(0, 1).equals("@")) { //+ for startgame, # for gamedata
                                Platform.runLater(() -> {
                                    listData.add(readInput);
                                    pLobby.setItems(listData);
                                });
                                namesReceived.add(readInput);

                            } //for GAME START        
                            else if (readInput.substring(0, 1).equals("+")) { //create server lobby gamestart button pressed, changing gamestarted boolean

                                gameStarted = true;

                                Platform.runLater(() -> {

                                    if (gameStarted == true && clientStarted == true) {

                                        pLobby.setVisible(false);
                                        pCount = listData.size();
                                        main.InitGamePaneClient(h);
                                    }
                                });
                            }

                            //for ID
                            if (readInput.substring(0, 1).equals("@")) {
                                String re = readInput.substring(1);
                                playerID = Integer.parseInt(re);
                            }

                            //for BULLETS 
                            if (readInput.substring(0, 1).equals("#")) {
                                String re = readInput.substring(1);
                                UnpackBullets(re);
                            }

                            //for the total player count -> used to generate players 
                            if (readInput.substring(0, 1).equals("?")) {
                                pCount = Integer.parseInt(readInput.substring(1));
                            }

                            //this is to receive the inputs from the server and update alllll players on the screen
                            if (readInput.substring(0, 1).equals("$")) {
                                String s = readInput.substring(1);
                                UnpackPlayer(s);
                            } //end game condition
                            else if (readInput.substring(0, 1).equals("*")) {

                                String s = readInput.substring(1);
                                System.out.println(s);

                                if (s.equals("draw")) {

                                    Platform.runLater(() -> {
                                        gameStarted = false;
                                        main.Action(main.currentStage, main.endScreen("its_a_draw"), "Game Over");
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        gameStarted = false;
                                        main.Action(main.currentStage, main.endScreen(s), "Game Over");
                                    });
                                }
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

                } catch (IOException ex) {
                    try {
                        socket.close();
                        dis.close();
                        dos.close();
                        clientRunning = false;
                        clientStarted = false;
                        Platform.runLater(() -> {
                            chatArea.appendText("\nDisconnected from host. You may now exit.");
                            chatSound.play();
                        });
                    } catch (IOException ex1) {
                    }
                }  //end of big try catch
            }
        }//end of client running block
    }//end of run method

}//end of client thread
