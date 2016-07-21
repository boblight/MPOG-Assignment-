/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//test
package mpogca2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mpogca2.ServerThread.*;

/**
 *
 * @author tongliang
 */
public class MpogCA2 extends Application {

    public static int latestId = 1;//store latest id of players

    private Button host, join, help, exit, startGame, testGame, confirm, back; //buttons for menu interactions

    public static ListView<String> pLobby;
    public static ObservableList<String> listData = FXCollections.observableArrayList();

    public static TextArea chatArea;
    private static Label nameLbl, ipLbl;
    public static Label error;
    private static TextField chatMsg, inputPName, inputIp;
    private Label mainTitle;
    private Stage currentStage;

    public static boolean isServer, gameStart = false, switchTurn = false, btnDisable = true,
            serverStarted = false, gameServerStarted = false, serverRunning = false, gameServerRunning = false,
            clientStarted = false, gameClientStarted = false, clientRunning = false, gameClientRunning = false,
            gameStarted = false;

    public static Player pLocal;
    public static List<Player> pList = new ArrayList<>(); //store list of players and assign them IDs to distinguish them
    public static int pCount = 0;
    public static InetAddress ipAddress;

    public static List<Handler> clientList = new ArrayList<>();

    public static Socket socket;
    public static ServerSocket serverSocket;
    public static DataOutputStream dos;
    public static DataInputStream dis;

    public static Runnable server, client;

    public static Image title;
    public static ImageView titleImv;

    final static AudioClip bPush = new AudioClip(new File("src/buttonPush.wav").toURI().toString());

    @Override
    public void start(Stage primaryStage) {
        Action(primaryStage, createMainMenu(), "Orbs");
        primaryStage.getIcons().add(new Image("logo.png"));

    }//end of main javafx class

    public Scene hostScreen() {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 200);
        scene.getStylesheets().add("style.css");

        VBox v = new VBox(15);
        v.setAlignment(Pos.CENTER);
        HBox h = new HBox(10);
        h.setAlignment(Pos.CENTER);
        HBox h2 = new HBox(6);
        h2.setAlignment(Pos.CENTER);

        inputPName = new TextField();
        confirm = new Button("Confirm");
        back = new Button("Back");
        nameLbl = new Label("Input Name: ");
        error = new Label();

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
            bPush.play();
            Action(currentStage, createMainMenu(), "Main Menu");
        });

        confirm.setOnAction(e -> {
            if (inputPName.getText().trim().equals("")) {
                error.setText("Please input player name.");
            } else {
                bPush.play();
                Action(currentStage, createServerLobby(), "Lobby");
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
                            serverSocket = new ServerSocket();

                            chatArea.setText("Server Started. \nYour IP is: " + InetAddress.getLocalHost().getHostAddress()
                                    + "\nWaiting for other players to connect.");

                            server = new ServerThread(8000, Runtime.getRuntime().availableProcessors() + 1);
                            new Thread(server).start();
                        } catch (IOException ex) {
                            System.out.println("SERVER RUNNING EXCEPTION: \n" + ex.getMessage());
                        }//end of trycatch
                    }//end of if server start
                }//end of if server running
            }
        });

        return scene;
    }//end of host screen

    public Scene joinScreen() {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 500, 400);

        VBox v = new VBox(15);
        v.setAlignment(Pos.CENTER);
        HBox h = new HBox(10);
        h.setAlignment(Pos.CENTER);
        HBox h2 = new HBox(10);
        h2.setAlignment(Pos.CENTER);
        HBox h3 = new HBox(10);
        h3.setAlignment(Pos.CENTER);

        inputPName = new TextField();
        inputIp = new TextField();
        confirm = new Button("Confirm");
        back = new Button("Back");
        nameLbl = new Label("Input Name: ");
        ipLbl = new Label("Input Host IP: ");
        error = new Label();

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
            bPush.play();
            Action(currentStage, createMainMenu(), "Main Menu");
        });

        confirm.setOnAction(e -> {
            if (inputPName.getText().trim().equals("")) {
                error.setText("Please input player name.");
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
                            client = new ClientThread();
                            new Thread(client).start();

                            Action(currentStage, createClientLobby(), "Lobby");
                        }
                    }
                } catch (IOException ex) {
                    error.setText("Host IP could not be found. Please enter a proper IP address");
                }
            }
        });

        return scene;
    }//end of join screen

    //create server lobby
    public Scene createServerLobby() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add("style.css");

        HBox h = new HBox(75);
        h.setAlignment(Pos.CENTER);
        VBox v = new VBox(15);
        v.setAlignment(Pos.CENTER);

        pLobby.setPrefWidth(400);
        pLobby.setPrefHeight(100);
        chatMsg = new TextField();
        chatArea = new TextArea();
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setPrefWidth(300);
        chatArea.setEditable(false);
        back = new Button("Exit");
        
        startGame=new Button("Start Game");

        v.getChildren().add(chatArea);
        v.getChildren().add(chatMsg);
        
        v.getChildren().add(startGame);
        
        v.getChildren().add(back);
        h.getChildren().add(pLobby);
        h.getChildren().add(v);

        root.setCenter(h);

        back.setOnAction(e -> {
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
                System.out.println("Failed to close socket");
            }

            Action(currentStage, createMainMenu(), "Main Menu");
        });
        
        startGame.setOnAction(e -> {
            bPush.play();
            
            //send message to client with command 
            //when client receive command change their own gameStarted=true
            
            gameStarted=true; //change server gameStarted=true, client still not changed
        });

        //when user enter msg
        chatMsg.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    if (!chatMsg.getText().trim().equals("")) {
                        String sendMsg = pLocal.getName() + ": " + chatMsg.getText();//replace statement to prevent confusion
                        //in outputstream logic
                        if (serverRunning == true) {
                            chatArea.appendText("\n" + sendMsg);
                            clientList.forEach((client) -> {
                                client.updateClientChat("<" + sendMsg);
                            });
                        } else if (clientRunning == true) {
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                System.out.println(sendMsg);
                                dos.writeUTF("<" + sendMsg);
                                dos.flush();
                            } catch (IOException ex) {
                                chatArea.appendText("\nFailed to send message.");
                            }
                        }
                    }
                    chatMsg.clear();
                }//end of keypressed events
            }
        });
        return scene;
    }//end of create server lobby
    
        //create server lobby
    public Scene createClientLobby() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add("style.css");

        HBox h = new HBox(75);
        h.setAlignment(Pos.CENTER);
        VBox v = new VBox(15);
        v.setAlignment(Pos.CENTER);

        pLobby.setPrefWidth(400);
        pLobby.setPrefHeight(100);
        chatMsg = new TextField();
        chatArea = new TextArea();
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setPrefWidth(300);
        chatArea.setEditable(false);
        back = new Button("Exit");

        v.getChildren().add(chatArea);
        v.getChildren().add(chatMsg);
        
        
        v.getChildren().add(back);
        h.getChildren().add(pLobby);
        h.getChildren().add(v);

        root.setCenter(h);

        back.setOnAction(e -> {
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
                System.out.println("Failed to close socket");
            }

            Action(currentStage, createMainMenu(), "Main Menu");
        });

        //when user enter msg
        chatMsg.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    if (!chatMsg.getText().trim().equals("")) {
                        String sendMsg = pLocal.getName() + ": " + chatMsg.getText();//replace statement to prevent confusion
                        //in outputstream logic
                        if (serverRunning == true) {
                            chatArea.appendText("\n" + sendMsg);
                            clientList.forEach((client) -> {
                                client.updateClientChat("<" + sendMsg);
                            });
                        } else if (clientRunning == true) {
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                System.out.println(sendMsg);
                                dos.writeUTF("<" + sendMsg);
                                dos.flush();
                            } catch (IOException ex) {
                                chatArea.appendText("\nFailed to send message.");
                            }
                        }
                    }
                    chatMsg.clear();
                }//end of keypressed events
            }
        });
        return scene;
    }//end of create client lobby

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
        testGame = new Button("TestGame");
        testGame.getStyleClass().add("menubtn");

        vbCenter.getChildren().add(titleImv);
        vbCenter.getChildren().add(host);
        vbCenter.getChildren().add(join);
        vbCenter.getChildren().add(help);
        vbCenter.getChildren().add(exit);
        vbCenter.getChildren().add(testGame);

        root.setCenter(vbCenter);

        host.setOnAction(e -> {
            bPush.play();
            Action(currentStage, hostScreen(), "Host Game");
        });

        join.setOnAction(e -> {
            bPush.play();
            Action(currentStage, joinScreen(), "Join Screen");
        });

        exit.setOnAction(e -> {
            bPush.play();
            Platform.exit();
            System.exit(0);
        });

        //guys please add comment for this kind of thing
        //test game to be deleted most likely
        testGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GameScreen gs = new GameScreen();
                gs.StartGameScreen();
            }

        });

        return scene;
    }//end of createMainMenu

//change screen
    public void Action(Stage stage, Scene scene, String title) {
        currentStage = stage;

        if (scene == null) {
            bPush.play();
            stage.close();
        }

        currentStage.setScene(scene);
        currentStage.setTitle(title);

        //currentStage.getIcons().add(new Image("logo.png"));
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
                    try {
                        if (serverRunning == true) {
                            serverSocket.close();
                            socket.close();
                            serverRunning = false;
                        } else if (clientRunning == true) {
                            socket.close();
                            clientRunning = false;
                        }
                    } catch (IOException ex) {
                        System.out.println("Failed to close socket");
                    }
                    bPush.play();

                    t.consume();
                    Action(currentStage, createMainMenu(), "Main Menu");
                }
            });
        }
    }//end of generic button listener

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }//end of main method

}
