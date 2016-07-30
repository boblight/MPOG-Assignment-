/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mpogca2;

import java.util.ArrayList;
import java.io.Serializable;
import mpogca2.engine.Bullet;
import mpogca2.engine.GameObject;

/**
 *
 * @author Desti
 */
public class GameNetworkObject implements Serializable {

    //used to package the data to be sent over the network
    ArrayList<Bullet> bulletList;
    ArrayList<GameObject> playerList;
    GameObject thisPlayer;
    String x;

    //bullets
    public void SetBulletList(ArrayList<Bullet> bulletList) {
        this.bulletList = bulletList;
    }

    public ArrayList GetBulletList() {
        return bulletList;
    }

    //send the player state to all the clients 
    //server to client
    public void SetPlayerList(ArrayList<GameObject> playerList) {
        this.playerList = playerList;
    }

    public ArrayList GetPlayerList() {
        return playerList;
    }

    //client to server 
    public void SetThisPlayer(GameObject thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    public GameObject GetThisPlayer() {
        return thisPlayer;
    }
}
