/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whoisplaying;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author tanmay
 */
public class Receiver implements Runnable{
    public Main insta;
    public DatagramSocket datagramSocket;
    public int status=0; // 0 means running
    public int count=0;
    
    public Receiver(DatagramSocket ds, Main inst){
        insta=inst;
        datagramSocket=ds;
    }
        @Override
    public void run() {
        try{
            
            byte[] receiveData = new byte[256];
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                               receiveData.length);

            datagramSocket.setSoTimeout(1500);
            while(true){
                try{
                    datagramSocket.receive(receivePacket);
                }catch(java.net.SocketTimeoutException e){if(status==1)break; else continue;}
                //String sentence = new String(receivePacket.getData(), 0,
                 //                receivePacket.getLength());
                addServer(receiveData,receivePacket);
            }
            insta.progi.setValue(0);
            if(count==0)insta.jtm.addRow(new Object[]{"No servers found."});
            insta.status=1;
            insta.jb.setText("Scan");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        

    }
    public void addServer(byte [] receiveData, DatagramPacket receivePacket) throws Exception{
                for(Object row : insta.jtm.getDataVector()){
                    if(receivePacket.getAddress().getHostAddress().equals(((Vector)row).elementAt(0))){
                        System.out.println("Already exists in table.");
                        return;
                    }
                }
                int [] offset = {0};
                
                if(nextNum4(receiveData,offset) != -1) return;
                ServerInfo info = new ServerInfo();
                if(nextNum1(receiveData,offset)==109){
                    info.addr = nextString(receiveData,offset);
                    info.name =nextString(receiveData,offset);
                    info.map=nextString(receiveData,offset);
                    info.folder=nextString(receiveData,offset);
                    info.game=nextString(receiveData,offset);
                    info.players=nextNum1(receiveData,offset);
                    info.maxplayers=nextNum1(receiveData,offset);
                    info.protocol=nextNum1(receiveData,offset);
                    info.servertype = nextNum1(receiveData,offset);
                    info.environment=nextNum1(receiveData,offset);
                    info.visibility=nextNum1(receiveData,offset);
                    info.mod=nextNum1(receiveData,offset);
                    
                    if (info.mod==1) {
                        info.modlink=nextString(receiveData,offset);
                        info.moddownlink=nextString(receiveData,offset);
                        if(nextNum1(receiveData,offset) != 0)
                            System.out.println("Warning: Null bit not Null.");
                        info.modversion=nextNum4(receiveData,offset);
                        info.modsize=nextNum4(receiveData,offset);
                        info.modtype=nextNum1(receiveData,offset);
                        info.moddll=nextNum1(receiveData,offset);
                    }
                    info.vac=nextNum1(receiveData,offset);
                    info.bots=nextNum1(receiveData,offset);

                    
                    insta.jtm.addRow(new String[]{receivePacket.getAddress().getHostAddress(), info.game,info.map,(((256+info.players)%256)-((256+info.bots)%256)) + " P + " + ((256+info.bots)%256) + " Bot",info.name});
                    count++;
                } //if ends
                if(nextNum1(receiveData,offset)==73){
                    info.protocol=nextNum1(receiveData,offset);
                    //info.addr = nextString(receiveData,offset);
                    info.name =nextString(receiveData,offset);
                    info.map=nextString(receiveData,offset);
                    info.folder=nextString(receiveData,offset);
                    nextNum2(receiveData,offset);
                    info.players=nextNum1(receiveData,offset);
                    info.maxplayers=nextNum1(receiveData,offset);
                    info.bots=nextNum1(receiveData,offset);
                    info.servertype = nextNum1(receiveData,offset);
                    info.environment=nextNum1(receiveData,offset);
                    info.visibility=nextNum1(receiveData,offset);
                    
                    info.vac=nextNum1(receiveData,offset);
                                     
                    

                    
                    insta.jtm.addRow(new String[]{receivePacket.getAddress().getHostAddress(), info.game,info.map,(((256+info.players)%256)-((256+info.bots)%256)) + " P + " + ((256+info.bots)%256) + " Bot",info.name});
                    count++;
                } //if ends
            
    }
    public static String nextString(byte [] array, int [] offset) throws Exception{
        int temp = offset[0];
        while(array[offset[0]++] != 0);
        return new String(array,temp,offset[0]-temp,"UTF-8");
        
    }
    public static byte nextNum1(byte [] array, int [] offset){
        offset[0]++;
        return array[offset[0]-1];
    }
    public static int nextNum4(byte [] array, int [] offset){
        int res=((int)array[offset[0]] & 0x000000ff)|
                ((((int)array[offset[0]+1])<<8) & 0x0000ff00)|
                ((((int)array[offset[0]+2])<<16) & 0x00ff0000)|
                ((((int)array[offset[0]+3])<<24) & 0xff000000);
        
        offset[0]+=4;
        return res;
        
    }
    public static short nextNum2(byte [] array, int [] offset){
        short res=(short)(((int)array[offset[0]] & 0x00ff)|
                ((((int)array[offset[0]+1])<<8) & 0xff00));
                
        offset[0]+=2;
        return res;
        
    }
    
}
