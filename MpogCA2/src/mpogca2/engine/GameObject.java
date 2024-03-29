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
package mpogca2.engine;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameObject {

    public Vector2D position;
    public String color;

    Circle circle;

    public GameObject() {
        position = new Vector2D();
        circle = new Circle(20, Color.web("#3498db"));
        circle.relocate(position.x, position.y);
    }

    //xpos, ypos, radius, player colour 
    public GameObject(float x, float y, float radius, String hexColor) {

        position = new Vector2D(x, y);
        circle = new Circle(radius, Color.web(hexColor));
        circle.setCenterX(radius / 2);
        circle.setCenterY(radius / 2);
        circle.setTranslateX(position.x);
        circle.setTranslateY(position.y);
    }

    public Circle getCircle() {
        return circle;
    }

    public void updateLocation() {
        circle.setTranslateX(position.x);
        circle.setTranslateY(position.y);
    }

    public void move(float x, float y, float speed) {
        Vector2D temp = new Vector2D(x, y);
        temp.normalize();
        temp.multiply(speed);
        position.add(temp);

        updateLocation();

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
