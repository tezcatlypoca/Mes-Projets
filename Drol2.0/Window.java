import javax.swing.JFrame;
import javax.swing.*;

import java.awt.*;

import java.io.*;


public class Window extends JFrame{

  Panneau jp;


  public Window(/*PipedInputStream pin,*/ PipedOutputStream pout){
    // config fenetre
    this.setSize(700, 700);
    this.setTitle("Drol");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
    this.setVisible(true);

    // ajout d'un jpanel
    jp = new Panneau(/*pin, */pout);
    this.add(jp);
    this.addKeyListener(jp);
  }

  /*public void paint(Graphics g){
    g.drawImage(jp.getHero().getImage(), jp.getHero().getX()*35, jp.getHero().getY()*35, jp);
  }*/

  public Panneau getPanel(){ return jp;}

  public static void main(String[] args) throws Exception{
    PipedOutputStream outFromPanneau = new PipedOutputStream(); // le panneau ecrit des infos pour le serv

    PipedInputStream inForServer = new PipedInputStream(outFromPanneau); // le serv ecoute les info envoyer par le panneau
    PipedOutputStream outFromServer = new PipedOutputStream(); // le server ecrit des info sur ce piped
    PipedOutputStream outFromServer2 = new PipedOutputStream();


    PipedInputStream inForPE = new PipedInputStream(outFromServer); // PhysicEngine ecoute sur piped les infos envoyer par le serv
    PipedInputStream inForGE = new PipedInputStream(outFromServer2);


    Window w = new Window(/*in, */outFromPanneau);
    Server s = new Server(inForServer, outFromServer, outFromServer2);
    //GraphicEngine ge = new GraphicEngine(w, inForGE);
    PhysicEngine pe = new PhysicEngine(w.getPanel().getHero(), inForPE);
    s.start();
    pe.start();
    //ge.start();
    //try{
    //Thread.sleep(500);
    //}catch(InterruptedExceptions e){e.printStackTrace();}
    //s.arret();

  }


}
