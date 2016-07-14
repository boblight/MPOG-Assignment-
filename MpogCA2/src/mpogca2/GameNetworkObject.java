/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mpogca2;

import java.util.ArrayList;
import mpogca2.engine.Bullet;

/**
 *
 * @author Desti
 */
public class GameNetworkObject {

    //used to package the data to be sent over the network
    ArrayList<Bullet> bulletList;

    public void SetBulletList(ArrayList<Bullet> bulletList) {
        this.bulletList = bulletList;
    }

    public ArrayList GetBulletList() {
        return bulletList;
    }
    
    

}
