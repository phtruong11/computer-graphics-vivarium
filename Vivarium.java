/** 
 * Phuong Truong
 * November 13, 2018
 * Vivarium.java
 * Vivarium contains fish and predator.  
 */

import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.util.*;

public class Vivarium
{
  private Tank tank;
  public Predator pred;
  public Fish fish;
  public boolean eaten;

  public Vivarium()
  {
    tank = new Tank( 4.0f, 4.0f, 4.0f );
    Coord fish_coord = new Coord(0.5f,0.5f,0.5f);
    Coord pred_coord = new Coord(1.5f, 0 ,1.5f); 
    fish = new Fish(fish_coord, this);
    pred = new Predator(pred_coord, this);
    eaten = false;
  }

  public void init( GL2 gl )
  {
    tank.init( gl ); 
    fish.init(gl);
    pred.init(gl);
  }

  public void update( GL2 gl )
  {
    tank.update( gl );
    if (!eaten) {
        fish.update(gl);
    }   
    pred.update(gl);
  }

  public void draw( GL2 gl )
  {
    tank.draw( gl );
    if(!eaten) {
        fish.draw(gl);
    }
    pred.draw(gl);

  }
}
