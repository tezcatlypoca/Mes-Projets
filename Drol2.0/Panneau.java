import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class Panneau extends JPanel implements KeyListener{

  private PipedInputStream in;

  private DataOutputStream dout;
  private PipedOutputStream out;

  private Hero h;

  public Panneau(/*PipedInputStream pin,*/ PipedOutputStream pout){
      //in = pin;
      out = pout;
      dout = new DataOutputStream(out);
      h = new Hero();
  }

  public void paint(Graphics g){
    g.drawImage(this.getHero().getImage(), this.getHero().getX()*35, this.getHero().getY()*35, this);
  }

  public Hero getHero(){ return h;}

  public void keyPressed(KeyEvent evt){
    //System.out.println(e.getKeyChar());
    try{
      if(evt.getKeyChar() == 'q')
        dout.writeInt(0);
      else if(evt.getKeyChar() == 'z')
        dout.writeInt(1);
      else if(evt.getKeyChar() == 'd')
        dout.writeInt(2);
      else if(evt.getKeyChar() == 's')
        dout.writeInt(3);
      else
        dout.writeInt(-1);
    }catch(IOException e){e.printStackTrace();}
  }

  public void keyReleased(KeyEvent e){

  }

  public void keyTyped(KeyEvent e){

  }

  //public Hero getHero(){ return h;}


}
