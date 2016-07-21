/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mpogca2.engine;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author P1431632
 */
public class GameObject {

    public Vector2D position;
    public String playerName, playerColour;
    int playerNum;

    Circle circle;

    public GameObject() {
        position = new Vector2D();
        circle = new Circle(20, Color.web("#3498db"));
        circle.relocate(position.x, position.y);

    }

    //xpos, ypos, radius, player num, player name, player colour 
    public GameObject(float x, float y, float radius, int playerNum, String playerName, String playerColour) {

        this.playerNum = playerNum;
        this.playerName = playerName;
        position = new Vector2D(x, y);
        circle = new Circle(radius, Color.web(playerColour));
        circle.setCenterX(radius / 2);
        circle.setCenterY(radius / 2);
        circle.setTranslateX(position.x);
        circle.setTranslateY(position.y);
    }

    public Circle getCircle() {
        return circle;
    }

    public void move(float x, float y, float speed) {
        Vector2D temp = new Vector2D(x, y);
        temp.normalize();
        temp.multiply(speed);
        position.add(temp);
        //circle.relocate(position.x, position.y);
        circle.setTranslateX(position.x);
        circle.setTranslateY(position.y);
    }

    public boolean isCollided(GameObject temp) {
        float dx = position.x - temp.position.x;
        float dy = position.y - temp.position.y;
        float distance = dx * dx + dy * dy;
        double radiusSum = getCircle().getRadius() + temp.getCircle().getRadius();
        return distance < radiusSum * radiusSum;
    }

}
