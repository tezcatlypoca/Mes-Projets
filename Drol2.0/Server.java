import java.io.*;

public class Server extends Thread{

  PipedInputStream in;
  DataInputStream din;

  PipedOutputStream out;
  DataOutputStream dout;

  PipedOutputStream out2;
  DataOutputStream dout2;

  private boolean isRunning;

  //private Hero h;

  public Server(PipedInputStream pin, PipedOutputStream pout, PipedOutputStream pout2){
    in = pin;
    din = new DataInputStream(in);
    out = pout;
    dout = new DataOutputStream(out);
    out2 = pout2;
    dout2 = new DataOutputStream(out2);
    isRunning = false;
  }

  public void run(){
    isRunning = true;
    while(isRunning){
      try{

        int i = din.readInt();
        dout.writeInt(i);
        if(i == -1){
          arret();
          dout2.writeInt(-1);
        }
        System.out.println(i);
      }catch(IOException e){e.printStackTrace();}
    }
  }

  public void arret(){ isRunning = false;}

}
