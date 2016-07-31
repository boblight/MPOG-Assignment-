package mpogca2.engine;

/*reference: http://noobtuts.com/java/vector2-class*/

public class Vector2D {

    public float x;
    public float y;

    public Vector2D() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void normalize() {
        float length = (float) Math.sqrt(x * x + y * y);

        if (length != 0.0) {
            float s = 1.0f / length;
            x = x * s;
            y = y * s;
        }
    }

    public void multiply(float temp) {
        x *= temp;
        y *= temp;
    }

    public void add(Vector2D temp) {
        x += temp.x;
        y += temp.y;
    }

}
