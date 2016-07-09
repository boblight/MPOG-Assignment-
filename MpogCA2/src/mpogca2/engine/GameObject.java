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
    public String tag;
    
    Circle circle;
    
    public GameObject()
    {
        position = new Vector2D();
        circle = new Circle (20, Color.web("#3498db"));
        
    }
    
    public GameObject(float x, float y, float radius, String hexColor,String tag)
    {
        position = new Vector2D(x, y);
        circle = new Circle (radius, Color.web(hexColor));
        this.tag = tag;
    }
    
    public Circle getCircle()
    {
        return circle;
    }
    
    public void move(float x, float y, float speed)
    {
        Vector2D temp = new Vector2D(x, y);
        temp.normalize();
        temp.multiply(speed);
        position.add(temp);
        circle.relocate(position.x, position.y);
    }
    
//    public boolean isCollided(GameObject temp)
//    {
//        if ()
//        
//        return true;
//    }
    
    public boolean isCollided(GameObject temp)
    {
        float dx = position.x - temp.position.x;
        float dy =  position.y - temp.position.y;
        float distance = dx * dx + dy * dy;
        double radiusSum = getCircle().getRadius() + temp.getCircle().getRadius();
        return distance < radiusSum * radiusSum;
    }
    
}
