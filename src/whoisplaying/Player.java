/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whoisplaying;

/**
 *
 * @author tanmay
 */
public class Player {
    byte index;         //Index of player chunk starting from 0.
    String name;
    int score;
    float duration;     //Time (in seconds) player has been connected to the server.
}
