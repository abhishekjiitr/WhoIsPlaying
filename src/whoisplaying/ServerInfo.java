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
public class ServerInfo {
    byte type; // = 109 for old, 79 for new
    String addr,name,map,folder,game,modlink,moddownlink;
    byte players,maxplayers,bots,protocol,servertype,environment,visibility,mod,modtype,moddll,vac;
    int modversion,modsize;
}
