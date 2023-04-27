import javax.swing.*;
import java.awt.*;
import java.awt.Image;

public class Hero extends Entities{

  public Hero(){
    ImageIcon ic = new ImageIcon("Images/hero.png");
    setImage(ic.getImage());
    setX(1); setY(1);
  }

}
