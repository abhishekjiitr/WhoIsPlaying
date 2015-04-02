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
    
    public static final String queryStr="ffffffff54536f7572636520456e67696e6520517565727900";
    /**
     * @param args the command line arguments
     */
    public Sender(Main inst,boolean flushOutput, ScanRange sr){
        insta=inst;
        
        this.flushOutput=flushOutput;
        this.sr=sr;
        insta.progi.setMaximum(sr.total+1 +sr.total/9);
        insta.progi.setValue(0);
    }
    @Override
    public void run() {
        try{
            System.out.println("Starting Sender thread.");
            
            if(flushOutput)
                while(insta.jtm.getRowCount()>0)
                    insta.jtm.removeRow(0);
            else
                insta.jtm.addRow(new String[] {"----"});

            byte[] sendData = new byte[queryStr.length()/2];
            for(int i = 0; i< queryStr.length()/2;i++){
                sendData[i]=(byte)Integer.parseInt(queryStr.substring(2*i,2*i+2),16);
            }


            byte [] ip={-1,-1,-1,-1};
            InetAddress address = InetAddress.getByAddress(ip);

            DatagramPacket packet =new DatagramPacket( 
                    sendData, sendData.length, address, 27015
                    );


            DatagramSocket datagramSocket = new DatagramSocket();
            System.out.println("Starting Receiver thread.");
            
            rec=new Receiver(datagramSocket, insta);
            Thread recTh = new Thread(rec);
            recTh.start();
            for(int port=27015; port<=27020; port++){
                packet.setPort(port);
                datagramSocket.send(packet);
            }
            
            
            
            packet.setPort(27015);
            for(Object row : insta.jtm.getDataVector()){
                try{
                    String s=(String)((Vector)row).elementAt(0);
                    if(s.startsWith("1")){
                        packet.setAddress(InetAddress.getByName(s));
                        datagramSocket.send(packet);
                    }
                }catch(Exception e){}
                
            }
            
            
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
          
            System.out.println("Sleeping.");
            //Thread.sleep(800);
            rec.status=1;
            //recTh.join();
            insta.progi.setValue(insta.progi.getMaximum());
            
            
            System.out.println("Sender thread joined Receiver both ended.");
        }catch(java.io.IOException e){
            insta.jtm.addRow(new String[] {"Network error."});
            insta.status=2;
            status=1;
            stop();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }   
    public void stop(){
        rec.status=1;
        
    }
}
 

