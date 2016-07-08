/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mpogca2;

import javafx.animation.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javafx.util.Duration;

/**
 *
 * @author Desti
 */
public class GameScreen {

    Stage stage;
    Pane pane;
    Scene scene;
    Circle player;
    int x = 0;
    int y = 0;
    int xSpeed = 0, ySpeed = 0;

    //this part is where we all play the game 
    public void StartGameScreen() {

        //create the gameUI 
        stage = new Stage();
        pane = new Pane();
        scene = new Scene(pane, 1200, 1080);

        //create player(s) 
        player = new Circle(100);
        player.setFill(Color.BLUE);

        pane.getChildren().add(player);

        stage.setScene(scene);
        stage.setTitle("Orbs");

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

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                System.out.println("Up");

                if (event.getCode() == KeyCode.UP) {
                    ySpeed = -10;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    ySpeed = 10;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xSpeed = -10;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xSpeed = 10;
                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.UP) {
                    ySpeed = 0;
                }
                if (event.getCode() == KeyCode.DOWN) {
                    ySpeed = 0;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    xSpeed = 0;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    xSpeed = 0;
                }
            }
        });

        x += xSpeed;
        y += ySpeed;

        player.relocate(x, y);

    }

}
