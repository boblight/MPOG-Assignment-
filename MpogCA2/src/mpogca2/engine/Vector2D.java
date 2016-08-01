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
