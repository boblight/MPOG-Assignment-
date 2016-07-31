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
