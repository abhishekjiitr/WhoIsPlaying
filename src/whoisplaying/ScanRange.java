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
public class ScanRange {
    public boolean scanC,scanB,scanA;
    int minc3,  maxc3,  minb2,  maxb2, minb3,  maxb3,  mina2,  maxa2,  mina3,  maxa3;
    int total;
    public ScanRange(boolean c, boolean b, boolean a,int minc3,int  maxc3,int  minb2,  int maxb2, int minb3,int   maxb3,  int mina2, int maxa2,int  mina3,int  maxa3){
        scanC=c;
        scanB=b;
        scanA=a;
        this.minc3=minc3;
        this.minb3=minb3;
        this.mina3=mina3;
        this.mina2=mina2;
        this.minb2=minb2;
        
        this.maxc3=maxc3;
        this.maxb3=maxb3;
        this.maxa3=maxa3;
        this.maxa2=maxa2;
        this.maxb2=maxb2;
        
        total =(maxb2-minb2+1)*(maxb3-minb3+1) + (maxa2-mina2+1)*(maxa3-mina3+1) + (maxc3-minc3+1);
    }
}
