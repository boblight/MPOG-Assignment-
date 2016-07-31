package mpogca2.engine;

public class GamePlayer extends GameObject {

    String playerName;
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
    }

}
