/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whoisplaying;

import java.net.*;
import java.util.Vector;
/**
 *
 * @author tanmay
 */
public class Sender implements Runnable {
    Main insta;
    boolean flushOutput;
    
    ScanRange sr;
    Receiver rec;
    public int status=0; // 0 means running
    
    public static final String queryStr = "ffffffff54536f7572636520456e67696e6520517565727900";
    public int port = 27015;
    /**
     * @param args the command line arguments
     */
    public Sender(Main inst,ScanRange sr,int port){
        insta=inst;
        this.port = port;
        this.flushOutput=flushOutput;
        this.sr=sr;
        insta.progi.setMaximum(sr.total+1 +sr.total/9);
        insta.progi.setValue(0);
    }
    @Override
    public void run() {
        try{
            System.out.println("Starting Sender thread.");
            
            
            byte[] sendData = new byte[queryStr.length()/2];
            for(int i = 0; i< queryStr.length()/2;i++){
                sendData[i]=(byte)Integer.parseInt(queryStr.substring(2*i,2*i+2),16);
            }


            DatagramPacket packet =new DatagramPacket( 
                    sendData, sendData.length, null, port
                    );
            DatagramSocket datagramSocket = new DatagramSocket();
            
            
            if(sr.scanPrev){
                for(Object row : insta.servers){
                    try{
                        ServerInfo s=((ServerInfo)row);
                        
                        packet.setAddress(s.realip);
                        datagramSocket.send(packet);
                    
                    }catch(Exception e){reportError(e);}

                }
            }
            
            insta.servers.clear();
            
            
            
            
            System.out.println("Starting Receiver thread.");

            rec=new Receiver(datagramSocket, insta);
            Thread recTh = new Thread(rec);
            recTh.start();
            
            byte [] ip={-1,-1,-1,-1};
            InetAddress address = InetAddress.getByAddress(ip);
            
            
            
            if(sr.scanBroadcast){
                try{
                    ip[0]=-1;ip[1]=-1;ip[2]=-1;ip[3]=-1;
                    address = InetAddress.getByAddress(ip);
                    packet.setAddress(address);
                    datagramSocket.send(packet);
                }catch(java.io.IOException e)
                {
                    reportError(e);
                }
            }
            
            
            if(sr.scanSelf){
                
                packet.setAddress(InetAddress.getLocalHost());
                try{
                    datagramSocket.send(packet);
                }catch(Exception e)
                {
                    reportError(e);
                }
            }
            
            
            try{
                if(sr.scanC){
                    ip[0]=192-256;
                    ip[1]=168-256;

                    for(ip[2]=(byte)sr.minc3; ;ip[2]++){
                        //System.out.println(InetAddress.getByAddress(ip).getHostAddress());
                        for(ip[3]=0;;ip[3]++){
                            packet.setAddress(InetAddress.getByAddress(ip));
                            datagramSocket.send(packet);


                            if(ip[3]==-1) break;
                        }
                        if(status==1) {
                            stop();
                            return;
                        }
                        insta.progi.setValue(insta.progi.getValue()+1);
                        if(ip[2]==(byte)sr.maxc3) break;
                    }

                }
            }catch(Exception e){
                reportError(e);
            }
            
            try{
                if(sr.scanB){
                    ip[0]=172-256;
                    for(ip[1]=(byte)sr.minb2; ;ip[1]++){
                        for(ip[2]=(byte)sr.minb3; ;ip[2]++){
                            //System.out.println(InetAddress.getByAddress(ip).getHostAddress());
                            for(ip[3]=0;;ip[3]++){
                                packet.setAddress(InetAddress.getByAddress(ip));
                                datagramSocket.send(packet);


                                if(ip[3]==-1) break;
                            }
                            if(status==1) {
                                stop();
                                return;
                            }
                            insta.progi.setValue(insta.progi.getValue()+1);
                            if(ip[2]==(byte)sr.maxb3) break;
                        }
                        if(ip[1]==(byte)sr.maxb2) break;
                    }
                }
            }catch(Exception e){
                
                reportError(e);
            }
            
            try{
                if(sr.scanA){
                    ip[0]=10;
                    for(ip[1]=(byte)sr.mina2; ;ip[1]++){
                        for(ip[2]=(byte)sr.mina3; ;ip[2]++){
                            //System.out.println(InetAddress.getByAddress(ip).getHostAddress());
                            for(ip[3]=0;;ip[3]++){
                                packet.setAddress(InetAddress.getByAddress(ip));
                                datagramSocket.send(packet);


                                if(ip[3]==-1) break;
                            }
                            if(status==1) {
                                stop();
                                return;
                            }
                            insta.progi.setValue(insta.progi.getValue()+1);
                            if(ip[2]==(byte)sr.maxa3) break;
                        }
                        if(ip[1]==(byte)sr.maxa2) break;
                    }
                }
            }catch(Exception e){
                reportError(e);
            }
            
            
        }
        catch(Exception e)
        {
            reportError(e);
        }
        
            insta.status=2;
            status=1;
            stop();
            insta.progi.setValue(insta.progi.getMaximum());
            
            
            System.out.println("Sender thread Ended.");
    }
    
    public void stop(){
        rec.status=1;
        
    }
    public void reportError(Exception e){
        insta.labelStatus.setText("Error: " + e.getMessage());
        e.printStackTrace();
    }
}
 

