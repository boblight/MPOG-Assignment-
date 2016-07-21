/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javafx.util.Duration;
import mpogca2.engine.*;

/**
 *
 * @author Desti
 */
public class GameScreen {

    Stage stage;
    Pane pane;
    Scene scene;
    Circle player;

    GameObject testPlayer, testPlayer2, middleCircle, thisPlayer;
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

    //this part is where we all play the game
    public void StartGameScreen() {

        //create the gameUI
        stage = new Stage();
        stage.getIcons().add(new Image("logo.png"));
        pane = new Pane();
        scene = new Scene(pane, 1390, 870);
        stage.setResizable(false);

        middleCircle = new GameObject(695, 435, 80, 0, "middleCircle", "#8e44ad");
        pane.getChildren().add(middleCircle.getCircle());

        //Test GameObject Class
        testPlayer = new GameObject(300, 100, 50, 1, "PlayerOne", "#3498db");
        testPlayer2 = new GameObject(100, 100, 50, 2, "PlayerTwo", "#e74c3c");
        pane.getChildren().add(testPlayer.getCircle());
        pane.getChildren().add(testPlayer2.getCircle());
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
                        System.out.println("time " + time);
                        //get the bullet and set on the client

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
                        System.out.println("time " + time);
                        SpawnBullets(time);
                        //send the bullet over the network
                    }
                }
                )
        ).cycleCount(Timeline.INDEFINITE).build();

        tick.play();//Starts the timeline

    }

    //to init all the other players that are connected
    public void InitPlayers(ArrayList<GameObject> playerList) {

        //initialize players
        for (int i = 0; i < playerList.size(); i++) {

            //loop through the list and init the players 
        }
    }

    //update needs to be reworked to cater to multiplayer
    void Update() {

        //timer for the spawnbullet
        time++;
        //this is to move the object
        //added another comment to test git 
        handleKeyboard();

        testPlayer.move(xDirection, yDirection, 3);
        testPlayer2.move(xDirection1, yDirection1, 3);

        //this is for when collide players
        if (testPlayer.isCollided(testPlayer2)) {
            System.out.println("Collision Success");
        }

        for (int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).bulletMove();
        }

    }

    void UpdateClient() {

        //update for client 
        handleKeyboard();

        //receive the network object and set all the other players 
    }

    void handleKeyboard() {

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

                if (event.getCode() == KeyCode.W) {
                    yDirection1 = -1;
                }
                if (event.getCode() == KeyCode.S) {
                    yDirection1 = 1;
                }
                if (event.getCode() == KeyCode.A) {
                    xDirection1 = -1;
                }
                if (event.getCode() == KeyCode.D) {
                    xDirection1 = 1;
                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                System.out.println("x: " + testPlayer.position.x + " y: " + testPlayer.position.y);

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

                if (event.getCode() == KeyCode.W) {
                    yDirection1 = 0;
                }
                if (event.getCode() == KeyCode.S) {
                    yDirection1 = 0;
                }
                if (event.getCode() == KeyCode.A) {
                    xDirection1 = 0;
                }
                if (event.getCode() == KeyCode.D) {
                    xDirection1 = 0;
                }
            }
        });
    }

    void SpawnBullets(int counterTime) {

        //timer method to spawn the bullets 
        if (counterTime == 100) {

            int u = 320;

            Random x = new Random();
            int randomNumber = x.nextInt(20);
            System.out.println("Math.random is : " + randomNumber);

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
