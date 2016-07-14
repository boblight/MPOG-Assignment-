/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mpogca2.engine;

/**
 *
 * @author P1431632
 */
public class Bullet extends GameObject{
    
    float xDirection;
    float yDirection;
    float speed;
    
    public Bullet (float x, float y, float radius, float speed, String hexColor, float xDirection, float yDirection)
    {
        super(x, y, radius, hexColor,"bullets");
        this.xDirection = xDirection;
        this.yDirection = yDirection;
        this.speed = speed;
    }
    
    public void bulletMove()
    {
        move(xDirection, yDirection, speed);
    }
    
}
