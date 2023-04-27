import java.io.*;

public class PhysicEngine extends Thread{

  private PipedInputStream in;
  private DataInputStream din;

  private boolean isRunning;

  private Hero h;

  private final int LEFT = 0;
  private final int UP = 1;
  private final int RIGHT = 2;
  private final int DOWN = 3;
  private final int SIZE = 700;

  public PhysicEngine(Hero h, PipedInputStream pin){
    in = pin;
    din = new DataInputStream(in);
    isRunning = false;
    this.h = h;
  }

  public void run(){
    isRunning = true;
    while(isRunning){
      try{
        if(din.readInt() == LEFT && h.getX()-35 >= 0)
          h.setX(h.getX()-35);
          //System.out.println("left");
        else if(din.readInt() == RIGHT && h.getX()+35 <= SIZE)
          h.setX(h.getX()+35);
          //System.out.println("right");
        else if(din.readInt() == -1)
          arret();
        /*int i = din.readInt();
        System.out.println(i);*/

      }catch(IOException e){e.printStackTrace();}
    }
  }



  public void arret(){ isRunning = false;}

}
