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

public class GamePlayer extends GameObject {

    public String playerName;
    int playerNum;

    boolean isAlive;

    public GamePlayer(float x, float y, float radius, String hexColor, String playerName, int playerNum) {
        super(x, y, radius, hexColor);
        this.playerName = playerName;
        this.playerNum = playerNum;
        isAlive = true;
    }

    public void setIsAlive(boolean temp) {
        isAlive = temp;
    }

    public boolean isAlive() {

        return isAlive;
    }

    public void dead() {
        isAlive = false;
        circle.setRadius(0);
    }

}
