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
            
            byte[] receiveData = new byte[1400];
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                               receiveData.length);

            datagramSocket.setSoTimeout(1500);
            while(true){
                try{
                    datagramSocket.receive(receivePacket);
                }catch(java.net.SocketTimeoutException e){
                    if(status==1)break; else continue;
                }
                parsePacket(receiveData,receivePacket);
            }
            
        }catch(Exception e)
        {
            insta.reportError(e);
        }
        
        insta.progi.setValue(0);
        insta.status=1;
        
        if(count==0)
            insta.reportInfo("No Servers found.",4);
        else
            insta.reportInfo("Double click on entry for more info.",2);
        
        insta.jb.setText("Scan");
    }
    
    public void parsePacket(byte [] receiveData, DatagramPacket receivePacket){
        
        int [] offset = {0};
        
        if(nextNum4(receiveData,offset) != -1) return;
        
        byte header = nextNum1(receiveData,offset);
        


        
        if(header==109){        //m
            addmServer(receiveData,receivePacket,offset);
        } //if ends
        else if(header==73){    //I
            addIServer(receiveData,receivePacket,offset);
        } //if ends
        else{
            System.out.println("Unsupported Packet. Header is" + header + ".");
        }
            
    }
    
    
    public void addmServer(byte [] receiveData, DatagramPacket receivePacket, int [] offset){      //old
        ServerInfo info = new ServerInfo();
        
        info.type=109;
        info.realip = receivePacket.getAddress();
        info.addr = nextString(receiveData,offset);
        info.name =nextString(receiveData,offset);
        info.map=nextString(receiveData,offset);
        
        for(ServerInfo servu : insta.servers){
            if(info.addr.equals(servu.addr) && info.map.equals(servu.map)){
//                System.out.println("Already exists in server list.");
                return;
            }
        }

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

        insta.servers.add(info);
        insta.jtm.fireTableRowsInserted(insta.servers.size()-1, insta.servers.size()-1);
        
        info.getPlayerInfo();
        //insta.jtm.addRow(new String[]{info.realip.getHostAddress(), info.game,info.map,(((256+info.players)%256)-((256+info.bots)%256)) + " P + " + ((256+info.bots)%256) + " Bot",info.name});
        count++;
    }
    
    public void addIServer(byte [] receiveData, DatagramPacket receivePacket, int [] offset){       //new
            ServerInfo info = new ServerInfo();
            
            info.type=73;
            info.realip = receivePacket.getAddress();
            info.addr= info.realip.getHostAddress();

            info.protocol=nextNum1(receiveData,offset);
            //info.addr = nextString(receiveData,offset);
            info.name =nextString(receiveData,offset);
            info.map=nextString(receiveData,offset);

            for(ServerInfo servu : insta.servers){
                if(info.addr.equals(servu.addr) && info.map.equals(servu.map)){
                    System.out.println("Already exists in server list.");
                    return;
                }
            }

            info.folder=nextString(receiveData,offset);
            info.game=nextString(receiveData,offset);
            nextNum2(receiveData,offset);
            info.players=nextNum1(receiveData,offset);
            info.maxplayers=nextNum1(receiveData,offset);
            info.bots=nextNum1(receiveData,offset);
            info.servertype = nextNum1(receiveData,offset);
            info.environment=nextNum1(receiveData,offset);
            info.visibility=nextNum1(receiveData,offset);

            info.vac=nextNum1(receiveData,offset);

            insta.servers.add(info);
            insta.jtm.fireTableRowsInserted(insta.servers.size()-1, insta.servers.size()-1);
            
            info.getPlayerInfo();



            //insta.jtm.addRow(new String[]{receivePacket.getAddress().getHostAddress(), info.game,info.map,(((256+info.players)%256)-((256+info.bots)%256)) + " P + " + ((256+info.bots)%256) + " Bot",info.name});
            count++;
        
    }
    
    public static String nextString(byte [] array, int [] offset) {
        int temp = offset[0];
        while(array[offset[0]++] != 0);
        try{
        return new String(array,temp,offset[0]-temp,"UTF-8");
        }
        catch(Exception e)
        {return null;
        }
        
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
    
    public static float nextFloat4(byte [] array, int [] offset){
        
        int res=((int)array[offset[0]] & 0x000000ff)|
                ((((int)array[offset[0]+1])<<8) & 0x0000ff00)|
                ((((int)array[offset[0]+2])<<16) & 0x00ff0000)|
                ((((int)array[offset[0]+3])<<24) & 0xff000000);
        
        offset[0]+=4;
        return Float.intBitsToFloat(res);
        
    }
    
    public static short nextNum2(byte [] array, int [] offset){
        short res=(short)(((int)array[offset[0]] & 0x00ff)|
                ((((int)array[offset[0]+1])<<8) & 0xff00));
                
        offset[0]+=2;
        return res;
        
    }
    
}
