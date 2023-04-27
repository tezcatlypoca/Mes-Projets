import java.io.*;
import java.awt.*;

public class GraphicEngine extends Thread{

  private PipedInputStream in;
  private DataInputStream din; //read on it

  private PipedOutputStream out;
  private DataOutputStream dout; //write on it

  private Window w;
  private boolean isRunning;
  //private Hero h;

  public GraphicEngine(Window w, PipedInputStream pin/*, PipedOutputStream pout*/){
    in = pin;
    din = new DataInputStream(in);
    this.w = w;
    isRunning = false;
    //h = new Hero();
    /*out = pout;
    dout = new DataOutputStream(out);*/
  }

  /*public void paint(Graphics g){
    g.drawImage(h.getImage(), h.getX(), h.getY(), w.getContentPane()  );
  }*/

  public void run(){
    isRunning = true;
    while(isRunning){
      try{
        if(din.readInt() == -1)
          arret();
      }catch(IOException e3){ e3.printStackTrace();}

    }
  }

  public void arret(){ isRunning = false;}

  //public Hero getHero(){ return h;}

}
