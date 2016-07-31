package mpogca2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import static mpogca2.MpogCA2.latestId;

public class Player {

    private StringProperty playerName = new SimpleStringProperty();
    private IntegerProperty playerId = new SimpleIntegerProperty();
    private BooleanProperty alive = new SimpleBooleanProperty();
    private IntegerProperty id = new SimpleIntegerProperty();

    public Player(String pName) {
        playerName.setValue(pName);
        id.setValue(latestId);
        latestId++;
    }//end of constructor

    public String getName() {
        return playerName.getValue();
    }

    public int getId() {
        return id.getValue();
    }

}//end of class
