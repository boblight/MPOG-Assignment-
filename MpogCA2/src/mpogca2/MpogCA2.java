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
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import static mpogca2.ClientThread.tempbList;
import mpogca2.engine.Bullet;
import mpogca2.engine.GameObject;
import mpogca2.engine.GamePlayer;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class MpogCA2 extends Application {

    public static int latestId = 1;//store latest id of players

    private Button host, join, help, exit, startGame, confirm, back; //buttons for menu interactions

    public static ListView<String> pLobby;
    public static ObservableList<String> listData = FXCollections.observableArrayList();

    public static TextArea chatArea;
    private static Label nameLbl, ipLbl;
    public static Label error;
    private static TextField chatMsg, inputPName, inputIp;
    public Stage currentStage;

    public static boolean isServer = false, gameStart = false, switchTurn = false, btnDisable = true,
            serverStarted = false, gameServerStarted = false, serverRunning = false, gameServerRunning = false,
            clientStarted = false, gameClientStarted = false, clientRunning = false, gameClientRunning = false,
            gameStarted = false;

    public static Player pLocal;
    public static List<Player> pList = new ArrayList<>(); //store list of players and assign them IDs to distinguish them
    public static int pCount = 1;
    public static InetAddress ipAddress;

    public static List<ServerThread.Handler> clientList = new ArrayList<>();

    public static Socket socket;
    public static ServerSocket serverSocket;
    public static DataOutputStream dos;
    public static DataInputStream dis;

    public static Runnable server, client;

    public static Image title, helpDiagram;
    public static ImageView titleImv, helpDiagramImv;

    final static AudioClip bPush = new AudioClip(new File("src/buttonPush.wav").toURI().toString());
    final static AudioClip chatSound = new AudioClip(new File("src/chat.wav").toURI().toString());
    final static AudioClip pop = new AudioClip(new File("src/pop.wav").toURI().toString());
    final static AudioClip shoot = new AudioClip(new File("src/shoot.wav").toURI().toString());
    final static AudioClip longshoot = new AudioClip(new File("src/oldshoot.wav").toURI().toString());
    final static AudioClip endSound = new AudioClip(new File("src/end.wav").toURI().toString());
    final Media bgm = new Media(new File("src/BGM.mp3").toURI().toString());

    public static Pane gamePane;
    Scene gameScene;

    int bulletSpawn = 0;

    int xDirection = 0;
    int yDirection = 0;
    int xDirection1 = 0;
    int yDirection1 = 0;

    public static int playerID = 1; //this is to help assign the player their numbers 

    public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
    public static ArrayList<GamePlayer> playerList = new ArrayList<GamePlayer>();

    public static GamePlayer player;
    public static GameObject middleObj;
    public static BorderPane root = new BorderPane();
    public static String gameData = "";
    public String gameOver = "Game Over !";
    public String gameDraw = "Draw !";

    public static HBox h;
    public static VBox v;

    @Override
    public void start(Stage primaryStage) {
        Action(primaryStage, createMainMenu(), "Orbs");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("logo.png"));

        MediaPlayer mediaPlay = new MediaPlayer(bgm);
        mediaPlay.setAutoPlay(true);
        mediaPlay.setVolume(0.8);

    }//end of main javafx class

    //create the screen for inputting name (host)
    public Scene hostScreen() {

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 540);
        scene.getStylesheets().add("style.css");
        root.getStyleClass().add("mainbg");

        VBox v = new VBox(15);
        v.setAlignment(Pos.CENTER);
        HBox h = new HBox(10);
        h.setAlignment(Pos.CENTER);
        HBox h2 = new HBox(6);
        h2.setAlignment(Pos.CENTER);

        inputPName = new TextField();
        inputPName.getStyleClass().add("chatbox");
        inputPName.setMaxWidth(720);
        confirm = new Button("Confirm");
        confirm.getStyleClass().add("menubtn");
        back = new Button("Back");
        back.getStyleClass().add("menubtn");
        nameLbl = new Label("Player Name: ");
        nameLbl.getStyleClass().add("labeltextlarge");
        error = new Label();
        error.getStyleClass().add("labeltext");

        root.getStyleClass().add("mainbg");

        h.getChildren().add(back);
        h.getChildren().add(confirm);
        h2.getChildren().add(nameLbl);
        h2.getChildren().add(inputPName);
        v.getChildren().add(h2);
        v.getChildren().add(error);
        v.getChildren().add(h);

        //instantiate listview before networking starts
        pLobby = new ListView<>();

        root.getChildren().add(v);

        back.setOnAction(e -> {
            gameStarted = false;
            bPush.play();
            Action(currentStage, createMainMenu(), "Main Menu");
        });

        confirm.setOnAction(e -> {
            bPush.play();
            if (inputPName.getText().trim().equals("")) {
                error.setText("Please enter player name.");
            } else {
                bPush.play();
                Action(currentStage, createServerLobby(), "Orbs");
                pLocal = new Player(inputPName.getText());

                //adding to listview at lobby screen
                listData.add(pLocal.getName());
                pLobby.setItems(listData);

                //add host to list of palyers
                pList.add(pLocal);

                if (serverRunning == false) {
                    serverRunning = true;
                    if (serverStarted == false) {
                        try {
                            chatArea.setText("Server Started. \nYour IP is: " + InetAddress.getLocalHost().getHostAddress()
                                    + "\nWaiting for other players to connect.");

                            server = new ServerThread(8000, Runtime.getRuntime().availableProcessors() + 1);
                            new Thread(server).start();
                        } catch (IOException ex) {
                        }//end of trycatch
                    }//end of if server start
                }//end of if server running
            }
        });

        return scene;
    }//end of host screen

    //create screen for inputing name + IP (client)
    public Scene joinScreen() {

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 540);
        scene.getStylesheets().add("style.css");
        root.getStyleClass().add("mainbg");

        VBox v = new VBox(15);
        v.setAlignment(Pos.CENTER);
        HBox h = new HBox(10);
        h.setAlignment(Pos.CENTER);
        HBox h2 = new HBox(10);
        h2.setAlignment(Pos.CENTER);
        HBox h3 = new HBox(10);
        h3.setAlignment(Pos.CENTER);

        inputPName = new TextField();
        inputPName.getStyleClass().add("chatbox");
        inputPName.setMaxWidth(700);
        inputIp = new TextField();
        inputIp.getStyleClass().add("chatbox");
        inputIp.setMaxWidth(700);
        confirm = new Button("Confirm");
        confirm.getStyleClass().add("menubtn");
        back = new Button("Back");
        back.getStyleClass().add("menubtn");
        nameLbl = new Label("Player Name: ");
        nameLbl.getStyleClass().add("labeltextlarge");
        ipLbl = new Label("Host IP: ");
        ipLbl.getStyleClass().add("labeltextlarge");
        error = new Label();
        error.getStyleClass().add("labeltext");

        h.getChildren().add(back);
        h.getChildren().add(confirm);
        h2.getChildren().add(nameLbl);
        h2.getChildren().add(inputPName);
        h3.getChildren().add(ipLbl);
        h3.getChildren().add(inputIp);
        v.getChildren().add(h2);
        v.getChildren().add(h3);
        v.getChildren().add(error);
        v.getChildren().add(h);

        //instantiate listview before networking starts
        pLobby = new ListView<>();

        root.getChildren().add(v);

        back.setOnAction(e -> {
            gameStarted = false;
            bPush.play();
            Action(currentStage, createMainMenu(), "Main Menu");
        });

        confirm.setOnAction(e -> {
            bPush.play();
            if (inputPName.getText().trim().equals("")) {
                error.setText("Please enter player name.");
            } else {
                bPush.play();
                pLocal = new Player(inputPName.getText());
                try {
                    ipAddress = InetAddress.getByName(inputIp.getText().trim());
                    socket = new Socket(ipAddress, 8000);

                    socket.setTcpNoDelay(false);
                    if (socket.isConnected()) {
                        Platform.runLater(() -> {
                            chatArea.setText("Connected to Host Player.\n");
                        });
                    }
                    //run client
                    if (clientRunning == false) {
                        clientRunning = true;

                        if (clientStarted == false) {
                            client = new ClientThread(this);
                            new Thread(client).start();

                            Action(currentStage, createClientLobby(), "Orbs");
                        }
                    }
                } catch (IOException ex) {
                    error.setText("Host IP could not be found.");
                }
            }
        });

        return scene;
    }//end of join screen

    //create server lobby
    public Scene createServerLobby() {

        BorderPane root = new BorderPane();
        gameScene = new Scene(root, 1140, 640);
        gameScene.getStylesheets().add("style.css");
        root.getStyleClass().add("mainbg");

        h = new HBox(75);
        h.setAlignment(Pos.CENTER);
        v = new VBox(15);
        v.setAlignment(Pos.CENTER);

        pLobby.setPrefWidth(400);
        pLobby.setPrefHeight(3 * 24 + 2);
        chatMsg = new TextField();
        chatMsg.getStyleClass().add("chatbox");
        chatMsg.setFocusTraversable(false);
        chatArea = new TextArea();
        chatArea.getStyleClass().add("chathistory");
        chatArea.setFocusTraversable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setPrefWidth(300);
        chatArea.setEditable(false);
        back = new Button("Exit");
        back.getStyleClass().add("smallbtn");
        back.setFocusTraversable(false);
        startGame = new Button("Start Game");
        startGame.getStyleClass().add("smallbtn");

        v.getChildren().add(chatArea);
        v.getChildren().add(chatMsg);
        v.getChildren().add(startGame);
        v.getChildren().add(back);

        h.getChildren().add(pLobby);
        h.getChildren().add(v);

        root.setCenter(h);

        back.setOnAction(e -> {
            gameStarted = false;
            bPush.play();

            listData.removeAll(listData);
            pLobby.setItems(listData);

            try {
                if (serverRunning == true) {
                    serverSocket.close();
                    clientList.forEach((s) -> {
                        s.shutdown();
                    });
                    serverRunning = false;
                } else if (clientRunning == true) {
                    socket.close();
                    clientRunning = false;
                }
            } catch (IOException ex) {

            }

            Platform.exit();
            System.exit(0);
        });

        startGame.setOnAction(e -> {
            if (pCount <= 3) {

                bPush.play();

                if (clientList.isEmpty()) {
                    chatArea.appendText("\nYou need more players to start the game.\n");
                } else {
                    //send message to client with command 
                    //when client receive command change their own gameStarted=true
                    gameStarted = true; //change server gameStarted=true, client still not changed
                    startGame.setVisible(false);
                    v.getChildren().remove(startGame);
                    //hide the playerList 
                    pLobby.setVisible(false);

                    //tell all clients that game has started
                    String s = "+" + "changing gameStarted=true on client";
                    String tP = "?" + Integer.toString(pCount);

                    clientList.forEach((client) -> {
                        client.updateClientChat(s);
                        client.updateClientChat(tP);
                    });
                    InitGamePaneServer(h);
                }//end else (when there are players to start)
            } else {
                chatArea.appendText("\nMax player count is 3. Current player count: " + pCount + "\n");
            }
        });

        //when user enter msg
        chatMsg.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {

                    if (gameStarted) {
                        gamePane.requestFocus();
                    }

                    if (!chatMsg.getText().trim().equals("")) {
                        String sendMsg = pLocal.getName() + ": " + chatMsg.getText();//replace statement to prevent confusion
                        //in outputstream logic
                        if (serverRunning == true) {
                            chatArea.appendText("\n" + sendMsg);
                            chatSound.play();
                            clientList.forEach((client) -> {
                                client.updateClientChat("<" + sendMsg);
                            });
                        } else if (clientRunning == true) {
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                dos.writeUTF("<" + sendMsg);
                                dos.flush();
                            } catch (IOException ex) {
                                chatArea.appendText("\nFailed to send message.");
                                chatSound.play();
                            }
                        }
                    }
                    chatMsg.clear();
                }//end of keypressed events
            }
        });
        return gameScene;
    }//end of create server lobby

    //create client lobby
    public Scene createClientLobby() {

        gameScene = new Scene(root, 1140, 640);
        gameScene.getStylesheets().add("style.css");

        root.getStyleClass().add("mainbg");

        h = new HBox(75);
        h.setAlignment(Pos.CENTER);
        v = new VBox(15);
        v.setAlignment(Pos.CENTER);

        pLobby.setPrefWidth(400);
        pLobby.setPrefHeight(100);
        chatMsg = new TextField();
        chatMsg.getStyleClass().add("chatbox");
        chatMsg.setFocusTraversable(false);
        chatArea = new TextArea();
        chatArea.getStyleClass().add("chathistory");
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setPrefWidth(300);
        chatArea.setEditable(false);
        chatArea.setFocusTraversable(false);
        back = new Button("Exit");
        back.getStyleClass().add("smallbtn");

        v.getChildren().add(chatArea);
        v.getChildren().add(chatMsg);

        v.getChildren().add(back);
        h.getChildren().add(pLobby);
        h.getChildren().add(v);

        root.setCenter(h);

        back.setOnAction(e -> {
            gameStarted = false;
            bPush.play();

            listData.removeAll(listData);
            pLobby.setItems(listData);

            try {
                if (serverRunning == true) {
                    serverSocket.close();
                    clientList.forEach((s) -> {
                        s.shutdown();
                    });
                    serverRunning = false;
                } else if (clientRunning == true) {
                    socket.close();
                    clientRunning = false;
                }
            } catch (IOException ex) {

            }
            Platform.exit();
            System.exit(0);
        });

        try {

        } catch (Exception ex) {

        }

        //when user enter msg
        chatMsg.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {

                    if (gameStarted) {
                        gamePane.requestFocus();
                    }

                    if (!chatMsg.getText().trim().equals("")) {
                        String sendMsg = pLocal.getName() + ": " + chatMsg.getText();//replace statement to prevent confusion
                        //in outputstream logic
                        if (serverRunning == true) {
                            chatArea.appendText("\n" + sendMsg);
                            chatSound.play();
                            clientList.forEach((client) -> {
                                client.updateClientChat("<" + sendMsg);
                            });
                        } else if (clientRunning == true) {
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                dos.writeUTF("<" + sendMsg);
                                dos.flush();

                            } catch (IOException ex) {
                                chatArea.appendText("\nFailed to send message.");
                                chatSound.play();
                            }
                        }
                    }
                    chatMsg.clear();
                }//end of keypressed events
            }
        });
        return gameScene;
    }//end of create client lobby

    //help screen at main menu
    public static void createHelpScreen() {
        Stage helpStage = new Stage();
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("style.css");

        VBox vbCenter = new VBox(15);
        vbCenter.setAlignment(Pos.CENTER);

        root.setCenter(vbCenter);
        root.getStyleClass().add("mainbg");

        helpDiagram = new Image("instructions.png");
        helpDiagramImv = new ImageView(helpDiagram);
        helpDiagramImv.setPreserveRatio(true);
        helpDiagramImv.fitWidthProperty().bind(scene.widthProperty());
        helpDiagramImv.fitHeightProperty().bind(scene.heightProperty());

        vbCenter.getChildren().add(helpDiagramImv);

        helpStage.setTitle("How to Play");
        helpStage.setScene(scene);
        helpStage.getIcons().add(new Image("logo.png"));
        helpStage.show();
    }//end of create help screen

    //player colour 
    public static String SwitchColour(int num) {
        String colour = "";

        switch (num) {
            case 1:
                colour = "#2ecc71";
                break;

            case 2:
                colour = "#e74c3c";
                break;

            case 3:
                colour = "#3498db";
                break;
            case 4:
                colour = "#f1c40f";
                break;
        }
        return colour;
    }

    //start game area (server)
    public void InitGamePaneServer(HBox h) {

        longshoot.play(); //play sound

        middleObj = new GameObject(400 - 25, 300 - 25, 50, "#8e44ad");

        gamePane = new Pane();
        gamePane.setMouseTransparent(false);
        gamePane.setMinHeight(600);
        gamePane.setMaxHeight(600);
        gamePane.setMinWidth(800);
        gamePane.setMaxWidth(800);
        gamePane.setStyle("-fx-background-color: #34495e");

        switch (pCount) {
            case 1:
                player = new GamePlayer(100, 100, 25, SwitchColour(1), "player" + 1, 1);
                playerList.add(player);
                break;

            case 2:
                player = new GamePlayer(100, 100, 25, SwitchColour(1), "player" + 1, 1);
                playerList.add(player);
                player = new GamePlayer(500, 100, 25, SwitchColour(2), "player" + 2, 2);
                playerList.add(player);
                break;

            case 3:
                player = new GamePlayer(100, 100, 25, SwitchColour(1), "player" + 1, 1);
                playerList.add(player);
                player = new GamePlayer(500, 100, 25, SwitchColour(2), "player" + 2, 2);
                playerList.add(player);
                player = new GamePlayer(100, 500, 25, SwitchColour(3), "player" + 3, 3);
                playerList.add(player);
                break;

            case 4:
                player = new GamePlayer(100, 100, 25, SwitchColour(1), "player" + 1, 1);
                playerList.add(player);
                player = new GamePlayer(500, 100, 25, SwitchColour(2), "player" + 2, 2);
                playerList.add(player);
                player = new GamePlayer(100, 500, 25, SwitchColour(3), "player" + 3, 3);
                playerList.add(player);
                player = new GamePlayer(500, 500, 25, SwitchColour(4), "player" + 4, 4);
                playerList.add(player);
                break;
        }
        //add current player
        for (int i = 0; i < playerList.size(); i++) {
            gamePane.getChildren().add(playerList.get(i).getCircle());
        }

        //get all the other player
        //the middle circle 
        gamePane.getChildren().add(middleObj.getCircle());

        h.setSpacing(10);
        h.setPadding(new Insets(0, 0, 0, 0));
        h.getChildren().remove(pLobby);
        h.getChildren().add(gamePane);

        //start the animation
        ServerTimeline();

    }

    //start game area (client)
    public void InitGamePaneClient(HBox h) {
        longshoot.play(); //play sound

        middleObj = new GameObject(400 - 25, 300 - 25, 50, "#8e44ad");

        gamePane = new Pane();
        gamePane.setMouseTransparent(false);
        gamePane.setMinHeight(600);
        gamePane.setMaxHeight(600);
        gamePane.setMinWidth(800);
        gamePane.setMaxWidth(800);
        gamePane.setStyle("-fx-background-color: #34495e");

        System.out.println("pCount = " + pCount);

        switch (pCount) {
            case 1:
                player = new GamePlayer(100, 100, 25, SwitchColour(1), "player" + 1, 1);
                playerList.add(player);
                break;

            case 2:
                player = new GamePlayer(100, 100, 25, SwitchColour(1), "player" + 1, 1);
                playerList.add(player);
                player = new GamePlayer(500, 100, 25, SwitchColour(2), "player" + 2, 2);
                playerList.add(player);
                break;

            case 3:
                player = new GamePlayer(100, 100, 25, SwitchColour(1), "player" + 1, 1);
                playerList.add(player);
                player = new GamePlayer(500, 100, 25, SwitchColour(2), "player" + 2, 2);
                playerList.add(player);
                player = new GamePlayer(100, 500, 25, SwitchColour(3), "player" + 3, 3);
                playerList.add(player);
                break;

            case 4:
                player = new GamePlayer(100, 100, 25, SwitchColour(1), "player" + 1, 1);
                playerList.add(player);
                player = new GamePlayer(500, 100, 25, SwitchColour(2), "player" + 2, 2);
                playerList.add(player);
                player = new GamePlayer(100, 500, 25, SwitchColour(3), "player" + 3, 3);
                playerList.add(player);
                player = new GamePlayer(500, 500, 25, SwitchColour(4), "player" + 4, 4);
                playerList.add(player);
                break;

        }
        //add current player
        for (int i = 0; i < playerList.size(); i++) {
            gamePane.getChildren().add(playerList.get(i).getCircle());
        }

        h.setSpacing(10);
        h.setPadding(new Insets(0, 0, 0, 0));
        h.getChildren().remove(pLobby);
        h.getChildren().add(gamePane);

        ClientTimeline();
    }

    public void ServerTimeline() {

        //creates the Timeline that updates the screen 
        Timeline tick = TimelineBuilder.create().keyFrames(
                new KeyFrame(
                        new Duration(40),//This is how often it updates in milliseconds
                        new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {

                        ServerUpdate();
                    }
                }
                )
        ).cycleCount(Timeline.INDEFINITE).build();

        tick.play();//Starts the timeline

    }

    public void ClientTimeline() {

        //creates the Timeline that updates the screen 
        Timeline tick = TimelineBuilder.create().keyFrames(
                new KeyFrame(
                        new Duration(40),//This is how often it updates in milliseconds
                        new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        ClientUpdate();
                    }
                }
                )
        ).cycleCount(Timeline.INDEFINITE).build();

        tick.play();//Starts the timeline

    }

    public void ServerUpdate() {

        //update the positions 
        refreshScreen();
        HandleServerKeyboard();
        bulletSpawn++;
        SpawnBullets(bulletSpawn);

        playerList.get(playerID - 1).move(xDirection, yDirection, 3);

        //send list of bullets to client 
        for (int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).bulletMove();
        }

        try {
            destroyBullets();
        } catch (Exception e) {

        }
        UpdateClientBullets(bulletList);
        UpdatePlayerPos(((int) playerList.get(playerID - 1).position.x), ((int) playerList.get(playerID - 1).position.y), playerList.get(playerID - 1).isAlive());

        if (gameStarted == true) {
            checkWinner();
        }
        bulletCollision();

        //UpdatePlayerPos(((int) playerList.get(playerID - 1).position.x), ((int) playerList.get(playerID - 1).position.y), true);
    }

    public void ClientUpdate() {

        refreshScreen();
        HandleClientKeyboard();
        playerList.get(playerID - 1).move(xDirection, yDirection, 3);
        UpdatePlayerPos(((int) playerList.get(playerID - 1).position.x), ((int) playerList.get(playerID - 1).position.y), playerList.get(playerID - 1).isAlive());

//        if (gameStarted == true) {
//            checkWinner();
//        }

        bulletCollision();
    }

    //package the gamedata of the player and send over network
    public void UpdatePlayerPos(int playerXPos, int playerYPos, boolean isAlive) {

        int x = 0;

        JSONObject playerObj = new JSONObject();
        JSONArray playerPos = new JSONArray();

        playerPos.add(playerXPos);
        playerPos.add(playerYPos);

        if (isAlive == true) {
            x = 1;
        }

        playerObj.put("playerID", playerID);
        playerObj.put("player", playerPos);
        playerObj.put("alive", x);

        String json = playerObj.toString();
        String j = "$" + json;

        if (playerID == 1) {
            clientList.forEach((client) -> {
                client.updateClientChat(j);
            });
        } else {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(j);
                dos.flush();
            } catch (Exception ex) {

            }
        }
    }

    void refreshScreen() {
        gamePane.getChildren().clear();
        gamePane.getChildren().add(middleObj.getCircle());
        bulletList = tempbList;

        for (int t = 0; t < bulletList.size(); t++) {

            try {
                gamePane.getChildren().add(bulletList.get(t).getCircle());
            } catch (NullPointerException e) {
            }

        }

        for (int i = 0; i < playerList.size(); i++) {
            gamePane.getChildren().add(playerList.get(i).getCircle());
        }

        HandleClientKeyboard();
        playerList.get(playerID - 1).move(xDirection, yDirection, 3);

    }

    public void HandleServerKeyboard() {

        //this is to move the object 
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.UP) {
                    yDirection = -1;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yDirection = 1;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xDirection = -1;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xDirection = 1;
                }

            }
        });

        gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.UP) {
                    yDirection = 0;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yDirection = 0;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xDirection = 0;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xDirection = 0;
                }

            }
        });

        // Prevents player from moving out of screen
        if ((0 + playerList.get(playerID - 1).getCircle().getRadius() - 10) > playerList.get(playerID - 1).position.x && xDirection == -1) {

            xDirection = 0;
        } //left wall

        if (playerList.get(playerID - 1).position.x > (800 - playerList.get(playerID - 1).getCircle().getRadius() + 12 - 25) && xDirection == 1) {

            xDirection = 0;
        } //right wall

        if ((0 + playerList.get(playerID - 1).getCircle().getRadius() - 12) > playerList.get(playerID - 1).position.y && yDirection == -1) {
            yDirection = 0;
        } //top wall

        if (playerList.get(playerID - 1).position.y > (600 - playerList.get(playerID - 1).getCircle().getRadius() + 12 - 25) && yDirection == 1) {

            yDirection = 0;
        } //bottom wall

    }

    public void HandleClientKeyboard() {
        //this is to move the object 
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.UP) {
                    yDirection = -1;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yDirection = 1;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xDirection = -1;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xDirection = 1;
                }

            }
        });

        gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.UP) {
                    yDirection = 0;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    yDirection = 0;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xDirection = 0;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xDirection = 0;
                }

            }
        });

        // Prevents player from moving out of screen
        if ((0 + playerList.get(playerID - 1).getCircle().getRadius() - 10) > playerList.get(playerID - 1).position.x && xDirection == -1) {
            xDirection = 0;
        }

        if (playerList.get(playerID - 1).position.x > (800 - playerList.get(playerID - 1).getCircle().getRadius() + 12 - 25) && xDirection == 1) {
            xDirection = 0;
        }

        if ((0 + playerList.get(playerID - 1).getCircle().getRadius() - 12) > playerList.get(playerID - 1).position.y && yDirection == -1) {
            yDirection = 0;
        }

        if (playerList.get(playerID - 1).position.y > (600 - playerList.get(playerID - 1).getCircle().getRadius() - 12) && yDirection == 1) {
            yDirection = 0;
        }
    }

    public void SpawnBullets(int time) {

        if (time == 150) {

            if (gameStarted) {
                shoot.play(); //play sound
            }

            Random x = new Random();
            int randomNumber = x.nextInt(10) + 10;

            //spawn bullets 
            for (int i = 0; i < randomNumber; i++) {

                int xPos = x.nextInt(21) - 10;

                int yPos = x.nextInt(21) - 10;

                Bullet bullet = new Bullet(400 - 10, 300 - 10, 20, 5, "#9b59b6", xPos, yPos);
                gamePane.getChildren().add(bullet.getCircle());
                bulletList.add(bullet);

            }
            bulletSpawn = 0;
        }
    }

    void destroyBullets() {
        for (int i = 0; i < bulletList.size(); i++) {

            if (bulletList.get(i).position.x > 800 - bulletList.get(i).getCircle().getRadius()) {
                gamePane.getChildren().remove(bulletList.get(i).getCircle());
                bulletList.remove(i);
            }

            if (bulletList.get(i).position.y > 600 - bulletList.get(i).getCircle().getRadius()) {
                gamePane.getChildren().remove(bulletList.get(i).getCircle());
                bulletList.remove(i);
            }

            if (bulletList.get(i).position.x < 0) {
                gamePane.getChildren().remove(bulletList.get(i).getCircle());
                bulletList.remove(i);
            }

            if (bulletList.get(i).position.y < 0) {

                gamePane.getChildren().remove(bulletList.get(i).getCircle());

                bulletList.remove(i);
            }
        }

    }

    public void UpdateClientBullets(ArrayList<Bullet> bList) {

        JSONObject r = new JSONObject();
        JSONArray bulletListJ = new JSONArray();

        for (int i = 0; i < bList.size(); i++) {

            JSONArray bulletJL = new JSONArray();
            JSONObject bulletJ = new JSONObject();
            bulletJL.add((int) bList.get(i).position.x);
            bulletJL.add((int) bList.get(i).position.y);
            bulletJ.put("bullet", bulletJL);
            bulletListJ.add(bulletJ);

        }

        r.put("BulletList", bulletListJ);

        String x = r.toString();
        gameData = "#" + x;

        clientList.forEach((client) -> {
            client.updateClientChat(gameData);

        });

    }

    //create main menu ui
    public Scene createMainMenu() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 860, 660);
        scene.getStylesheets().add("style.css");
        root.getStyleClass().add("mainbg");

        VBox vbCenter = new VBox(15);
        vbCenter.setAlignment(Pos.CENTER);

        title = new Image("title.png", 600, 461, true, true);
        titleImv = new ImageView(title);

        host = new Button("Host lobby");
        host.getStyleClass().add("menubtn");
        join = new Button("Join lobby");
        join.getStyleClass().add("menubtn");
        help = new Button("How to play");
        help.getStyleClass().add("menubtn");
        exit = new Button("Exit");
        exit.getStyleClass().add("menubtn");

        vbCenter.getChildren().add(titleImv);
        vbCenter.getChildren().add(host);
        vbCenter.getChildren().add(join);
        vbCenter.getChildren().add(help);
        vbCenter.getChildren().add(exit);

        root.setCenter(vbCenter);

        host.setOnAction(e -> {
            bPush.play();
            Action(currentStage, hostScreen(), "Host Game");
        });

        join.setOnAction(e -> {
            bPush.play();
            Action(currentStage, joinScreen(), "Join Game");
        });

        help.setOnAction(e -> {
            bPush.play();
            createHelpScreen();
        });

        exit.setOnAction(e -> {
            bPush.play();

            try {
                Thread.sleep(80);

            } catch (InterruptedException ex) {
                Logger.getLogger(ClientThread.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

            Platform.exit();
            System.exit(0);
        });

        return scene;
    }//end of createMainMenu

    //check for players colliding with bullets
    public void bulletCollision() {
        for (int i = 0; i < playerList.size(); i++) {
            for (int j = 0; j < bulletList.size(); j++) {
                //try
                //{
                if (playerList.get(i).isCollided(bulletList.get(j))) {

                    if (playerList.get(i).isAlive() == true) {
                        pop.play();
                        playerList.get(i).dead();
                    }
                }
            }
        }
    }

    //check for whos the winner
    public void checkWinner() {

        boolean haveWinner = false;
        int deathCount = 0;
        int lastPlayerAlive = 0;

        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).isAlive() == false) {
                deathCount++;
            } else {
                lastPlayerAlive = i;
            }
        }

        if (deathCount == playerList.size() - 1) {
            gameStarted = false;

            //send over the network to tell everyone that game has ended 
            if (serverRunning == true) {
                String end = "*" + pLocal.getName();
                clientList.forEach((c) -> {
                    c.updateClientChat(end);
                });
                Action(currentStage, endScreen(pLocal.getName()), "Game Over");
            }

        } else if (deathCount == playerList.size()) {

            //send over to say that its a draw
            if (serverRunning == true) {
                String end = "*draw";

                clientList.forEach((c) -> {
                    c.updateClientChat(end);
                });

                gameStarted = false;
                Action(currentStage, endScreen("its_a_draw"), "Game Over");
            }
        }
    }

    //to display at the end of the game
    public Scene endScreen(String gameMsg) {
        endSound.play();

        gameStarted = false;
        gameClientRunning = false;
        gameClientStarted = false;
        gameServerRunning = false;
        gameServerStarted = false;

        BorderPane root = new BorderPane();
        VBox vbox = new VBox(35);
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("style.css");

        Label info = new Label();
        if (gameMsg == "its_a_draw") {
            info = new Label("It's a draw!");
        } else {
            info = new Label(gameMsg + " won!");
        }

        info.getStyleClass().add("labeltextextralarge");
        Button exit = new Button();
        exit.setText("Exit");
        exit.getStyleClass().add("menubtn");

        ImageView endImg = new ImageView(new Image("logo.png", 300, 300, true, true));

        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(endImg);
        vbox.getChildren().add(info);
        vbox.getChildren().add(exit);

        root.setCenter(vbox);
        root.getStyleClass().add("mainbg");

        exit.setOnAction(e -> System.exit(0));

        return scene;
    }//end endScreen()

    //change screen
    public void Action(Stage stage, Scene scene, String title) {
        currentStage = stage;

        if (scene == null) {
            bPush.play();
            stage.close();
        }

        currentStage.setScene(scene);
        currentStage.setTitle(title);

        currentStage.show();

        if (currentStage.getTitle().equals("Main Menu")) {
            currentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    bPush.play();

                    Platform.exit();
                    System.exit(0);
                }
            });
        } else {
            currentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {

                    bPush.play();

                    Platform.exit();
                    System.exit(0);
                }
            });
        }
    }//end of generic button listener

    public static void main(String[] args) {
        launch(args);
    }//end of main method

}
