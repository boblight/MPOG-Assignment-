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

    GameObject testPlayer, testPlayer2, middleCircle;
    ArrayList<Bullet> bulletList;
    int x = 0;
    int y = 0;

    int xDirection = 0;
    int yDirection = 0;
    int xDirection1 = 0;
    int yDirection1 = 0;

    int time = 0;

    //this part is where we all play the game
    public void StartGameScreen() {

        //create the gameUI
        stage = new Stage();
        stage.getIcons().add(new Image("logo.png"));
        pane = new Pane();
        scene = new Scene(pane, 1395, 875);
        stage.setResizable(false);

        //create player(s)
        //   player = new Circle();
        // player.setCenterX(400.0);
        //player.setCenterY(300.0);
        //player.setRadius(80.0);
<<<<<<< HEAD
        middleCircle = new GameObject(320, 220, 80, "#FB1616", "middleCircle");

=======
        middleCircle = new GameObject(320, 220, 80, "#9b59b6", "middleCircle");
        
>>>>>>> f0a0bc7dd281bb62275213bda81cb6c2fa32b507
        pane.getChildren().add(middleCircle.getCircle());

        //Test GameObject Class
        testPlayer = new GameObject(300, 100, 50, "#3498db", "player");
        testPlayer2 = new GameObject(100, 100, 50, "#2980b9", "player");
        pane.getChildren().add(testPlayer.getCircle());
        pane.getChildren().add(testPlayer2.getCircle());

        bulletList = new ArrayList<Bullet>();

        stage.setScene(scene);
        stage.setTitle("Orbs");

        scene.getStylesheets().add("style.css");
        pane.getStyleClass().add("mainbg");
        
        //create TimeLine
        Timeline();

        stage.show();

    }

    public void Timeline() {

        //creates the Timeline that updates the screen '
//        AnimationTimer timer;
//        Timeline tl = new Timeline();
//        tl.setCycleCount(Timeline.INDEFINITE);
//        tl.setAutoReverse(true);
//
//        timer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//
//                Update();
//
//            }
//        };
        Timeline tick = TimelineBuilder.create().keyFrames(
                new KeyFrame(
                        new Duration(10),//This is how often it updates in milliseconds
                        new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        //You put what you want to update here
                        Update();

                        System.out.println("time " + time);
                        SpawnBullets(time);

                    }
                }
                )
        ).cycleCount(Timeline.INDEFINITE).build();

        tick.play();//Starts the timeline

    }

    public void InitPlayers() {

        //initialize players
    }

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

//        if (testPlayer.isCollided(middleCircle)) {
//
//            System.out.println("Player 1 collided with middle thing");
//
//        }
//
//        if (testPlayer2.isCollided(middleCircle)) {
//            System.out.println("Player 2 collied with middle thing");
//        }
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
        if (counterTime == 180) {

            int u = 320;

            Random x = new Random();
            int randomNumber = x.nextInt(10);
            System.out.println("Math.random is : " + randomNumber);

            //spawn bullets 
            for (int i = 0; i < randomNumber; i++) {
                
                Bullet bullet = new Bullet(400, 300, 20, 5, "#8646a0", -1, 1);

                pane.getChildren().add(bullet.getCircle());
                bulletList.add(bullet);
                u += 10;
            }

            time = 0;

        }

    }

    void PackagePos() {

        //send postition of players over network 
        GameNetworkObject o = new GameNetworkObject();

    }

}
