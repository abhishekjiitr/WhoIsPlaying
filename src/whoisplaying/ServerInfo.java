/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whoisplaying;

import java.net.*;
import static whoisplaying.Receiver.nextNum1;
import static whoisplaying.Receiver.nextString;
import static whoisplaying.Receiver.nextNum4;
import static whoisplaying.Receiver.nextFloat4;
/**
 *
 * @author tanmay
 */
public class ServerInfo {
    
    public java.net.InetAddress realip;        //this is the ip from which packet was received
    public byte type; // = 109 for old, 79 for new
    public String addr,name,map,folder,game,modlink,moddownlink;
    public byte players,maxplayers,bots,protocol,servertype,environment,visibility,mod,modtype,moddll,vac;
    public int modversion,modsize;
    
    byte[] challengeno;
    PlayerInfo playerinfo;
    
    public void getPlayerInfo(){
        try{
        
            String queryStr="ffffffff55ffffffff";
            byte[] sendData = new byte[queryStr.length()/2];
            for(int i = 0; i< queryStr.length()/2;i++){
                sendData[i]=(byte)Integer.parseInt(queryStr.substring(2*i,2*i+2),16);
            }
            DatagramPacket packet =new DatagramPacket( 
                    sendData, sendData.length, realip, 27015
                    );


            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.send(packet);
            
            byte[] receiveData = new byte[1400];
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                   receiveData.length);

            datagramSocket.setSoTimeout(3000);
            try{
                datagramSocket.receive(receivePacket);
            }catch(java.net.SocketTimeoutException e){
                
            }
            //String sentence = new String(receivePacket.getData(), 0,
             //                receivePacket.getLength());
            
            int [] offset = {0};
        
            if(nextNum4(receiveData,offset) != -1) return;
            if(nextNum1(receiveData,offset) != 65) return;

            challengeno = new byte[4];
            
            challengeno[0]=receiveData[offset[0]+0];
            challengeno[1]=receiveData[offset[0]+1];
            challengeno[2]=receiveData[offset[0]+2];
            challengeno[3]=receiveData[offset[0]+3];

            
            sendData[5]=challengeno[0];
            sendData[6]=challengeno[1];
            sendData[7]=challengeno[2];
            sendData[8]=challengeno[3];
            packet =new DatagramPacket( 
                sendData, sendData.length, realip, 27015
                );
            
            datagramSocket.send(packet);
            
            receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            datagramSocket.setSoTimeout(3000);
            try{
                datagramSocket.receive(receivePacket);
            }catch(java.net.SocketTimeoutException e){
                
            }
            //String sentence = new String(receivePacket.getData(), 0,
             //                receivePacket.getLength());
            
            offset[0] = 0;
            
            if(nextNum4(receiveData,offset) != -1) return;
            if(nextNum1(receiveData,offset) != 68) return;

            
            playerinfo = new PlayerInfo();
            playerinfo.noofplayers = nextNum1(receiveData,offset);
            playerinfo.playerlist = new java.util.Vector<Player>();
            for(int i = playerinfo.noofplayers ;  i>0 ; i--){
                Player p = new Player();
                p.index = nextNum1(receiveData,offset);
                p.name = nextString(receiveData,offset);
                p.score = nextNum4(receiveData,offset);
                p.duration = nextFloat4(receiveData,offset);            //i dont know how to
                playerinfo.playerlist.add(p);
            }
            
        }catch(SocketException e){
            
        }
        catch(java.io.IOException e)
        {
        }
    }
}
