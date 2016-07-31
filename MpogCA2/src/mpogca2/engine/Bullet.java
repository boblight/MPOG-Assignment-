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

public class Bullet extends GameObject {

    float xDirection;
    float yDirection;
    float speed;

    public Bullet(float x, float y, float radius, float speed, String hexColor, float xDirection, float yDirection) {
        super(x, y, radius, hexColor);
        this.xDirection = xDirection;
        this.yDirection = yDirection;
        this.speed = speed;
    }

    public void bulletMove() {
        move(xDirection, yDirection, speed);
    }

}
