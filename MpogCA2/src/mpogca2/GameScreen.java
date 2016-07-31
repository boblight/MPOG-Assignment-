package mpogca2;

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javafx.util.Duration;
import mpogca2.engine.*;

public class GameScreen {

    Stage stage;
    Pane pane;
    Scene scene;
    Circle player;

    GameObject testPlayer, testPlayer2, middleCircle, thisPlayer;
    GamePlayer g;

    ArrayList<GamePlayer> playerList = new ArrayList<GamePlayer>();
    ArrayList<Bullet> bulletList;
    GameNetworkObject gno; //this object is to be used to send data over object 
    int x = 0;
    int y = 0;

    int xDirection = 0;
    int yDirection = 0;
    int xDirection1 = 0;
    int yDirection1 = 0;

    int time = 0;

    boolean isServer = true; //to determine if the player is a server or not

    public GameScreen() {

        //default constructor
    }

    public GameScreen(boolean isServer) {

        //if i am server 
        this.isServer = isServer;

    }

    public GameScreen(Scene scene, GamePlayer g) {

        this.scene = scene;
        this.g = g;
    }

    //this part is where we all play the game
    public void StartGameScreen() {

        //create the gameUI
        stage = new Stage();
        stage.getIcons().add(new Image("logo.png"));
        pane = new Pane();
        scene = new Scene(pane, 1390, 870);
        stage.setResizable(false);

        middleCircle = new GameObject(695, 435, 80, "#8e44ad");
        pane.getChildren().add(middleCircle.getCircle());

        playerList = new ArrayList<GamePlayer>();
        InitPlayers();

        //create the player 
        bulletList = new ArrayList<Bullet>();

        stage.setScene(scene);
        stage.setTitle("Orbs");

        scene.getStylesheets().add("style.css");
        pane.getStyleClass().add("mainbg");

        //create TimeLine
        if (isServer == false) {
            ClientTimeline();
        }

        if (isServer == true) {
            ServerTimeline();
        }

        stage.show();
    }

    public void ClientTimeline() {

        //creates the Timeline that updates the screen 
        Timeline tick = TimelineBuilder.create().keyFrames(
                new KeyFrame(
                        new Duration(10),//This is how often it updates in milliseconds
                        new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        //You put what you want to update here
                        Update();

                    }
                }
                )
        ).cycleCount(Timeline.INDEFINITE).build();

        tick.play();//Starts the timeline

    }

    public void ServerTimeline() {

        //creates the Timeline that updates the screen 
        Timeline tick = TimelineBuilder.create().keyFrames(
                new KeyFrame(
                        new Duration(10),//This is how often it updates in milliseconds
                        new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        //You put what you want to update here
                        Update();
                        SpawnBullets(time);
                    }
                }
                )
        ).cycleCount(Timeline.INDEFINITE).build();

        tick.play();//Starts the timeline

    }

    //to init all the other players that are connected
    public void InitPlayers() {

        //loop through the list and init the players 
        testPlayer2 = new GamePlayer(100, 100, 50, "#e74c3c", "PlayerTwo", 1);
        GamePlayer gamePlayer = new GamePlayer(100, 100, 50, "#3498db", "PlayerOne", 1);
        GamePlayer gamePlayer2 = new GamePlayer(100, 300, 50, "#e74c3c", "PlayerTwo", 2);

        playerList.add(gamePlayer);
        playerList.add(gamePlayer2);

        pane.getChildren().add(gamePlayer.getCircle());
        pane.getChildren().add(gamePlayer2.getCircle());

        //}
    }

    //update needs to be reworked to cater to multiplayer
    public void Update() {

        //timer for the spawnbullet
        time++;
        //this is to move the object

        handleKeyboard();

        playerList.get(0).move(xDirection, yDirection, 3);

        for (int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).bulletMove();
        }

        BulletCollision();

    }

    public void TestUpdate() {
        handleKeyboard();
        playerList.get(0).move(xDirection, yDirection, 3);
    }

    public void UpdateClient() {

        //update for client 
        handleKeyboard();
    }

    public void handleKeyboard() {

        //this is to move the object 
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

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

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
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
    }

    public void BulletCollision() {
        for (int i = 0; i < playerList.size(); i++) {
            for (int j = 0; j < bulletList.size(); j++) {
                if (playerList.get(i).isCollided(bulletList.get(j))) {
                    playerList.get(i).dead();
                }
            }
        }
    }

    public void SpawnBullets(int counterTime) {

        //timer method to spawn the bullets 
        if (counterTime == 100) {

            int u = 320;

            Random x = new Random();
            int randomNumber = x.nextInt(20);

            //spawn bullets 
            for (int i = 0; i < randomNumber; i++) {

                int xPos = x.nextInt(21) - 10;
                int yPos = x.nextInt(21) - 10;
                Bullet bullet = new Bullet(695, 435, 20, 5, "#9b59b6", xPos, yPos);
                pane.getChildren().add(bullet.getCircle());
                bulletList.add(bullet);

                u += 10;
            }

            time = 0;

        }

    }

}
