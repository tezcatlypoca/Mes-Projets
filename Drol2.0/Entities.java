import javax.swing.ImageIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;

public abstract class Entities{

  private int x, y;
  Image i;

  public Entities(){
    x = 0; y = 0;
    i = null;
  }

  public void setX(int x){ this.x = x;}

  public void setY(int y){ this.y = y;}

  public void setImage(Image i){ this.i = i;}

  public int getX(){ return x;}

  public int getY(){ return y;}

  public Image getImage(){ return i;}


}
