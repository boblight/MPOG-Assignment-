/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mpogca2;

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author tongliang
 */
public class MpogCA2 extends Application {

    private Button host, join, help, exit;
    private Label mainTitle;
    private Stage currentStage;

    final static AudioClip bPush = new AudioClip(new File("src/buttonPush.wav").toURI().toString());

    @Override
    public void start(Stage primaryStage) {
        Action(primaryStage, createMainMenu(), "Main Menu");
        
        exit.setOnAction(e -> {
           bPush.play();
           System.exit(0);
        });
        
    }//end of main javafx class

    //create main menu
    public Scene createMainMenu() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 860, 660);

        VBox vbCenter = new VBox(15);
        vbCenter.setAlignment(Pos.CENTER);

        mainTitle = new Label("Ooorrbbs");
        host = new Button("Host lobby");
        join = new Button("Join lobby");
        help = new Button("How to play");
        exit = new Button("Exit");
        
        vbCenter.getChildren().add(mainTitle);
        vbCenter.getChildren().add(host);
        vbCenter.getChildren().add(join);
        vbCenter.getChildren().add(help);
        vbCenter.getChildren().add(exit);
        
        root.setCenter(vbCenter);
        
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

        currentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {

                t.consume();
                //Action(currentStage, createMainMenu(), "Main Menu");
            }
        });
    }//end of generic button listener

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }//end of main method

}
