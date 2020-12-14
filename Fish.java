/** 
 * Phuong Truong
 * November 13, 2018
 * Fish.java
 * Fish moves randomly inside the tank and avoids predator. 
 */

import javax.media.opengl.GL2;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import java.util.*;

public class Fish {
    
  private Random rand = new Random ();
  private int fish_object, tail_object, body_object; 
  private float speed_x, speed_y, speed_z;
  private float dir_x, dir_y, dir_z; 
  private float tail_angle, tail_speed, tail_dir;
  public Coord coord;
  public Coord predator_coord; 
  public Vivarium vivarium; 

  public Fish(Coord c, Vivarium v) {
    vivarium = v;
    coord = c;
    fish_object = body_object = tail_object = 0;
    
    speed_x = speed_y = speed_z = 0.005f;
    dir_x = dir_y = dir_z = -1;
    
    tail_angle = 0;
    tail_speed = 1.5f; 
    tail_dir = 1; 
  }

  public void init(GL2 gl) {
    body(gl);
    tail(gl);
    fish_object = gl.glGenLists(1); 
    gl.glNewList(fish_object, GL2.GL_COMPILE );
    fish(gl);
    gl.glEndList();
  }
  
  public void update(GL2 gl) {
    moving_tail();
    moving_fish();
    gl.glNewList( fish_object, GL2.GL_COMPILE );
    fish( gl ); 
    gl.glEndList();   
  }
  
  public void draw(GL2 gl) {
    gl.glPushMatrix();    
    gl.glColor3f( 0.85f, 0.55f, 0.20f);
    gl.glCallList( fish_object );
    gl.glPopMatrix();
  }
  
  //Draw fish body
  public void body(GL2 gl){
    body_object = gl.glGenLists(1);
    gl.glNewList(body_object, GL2.GL_COMPILE);
    GLUT glut = new GLUT();
    gl.glPushMatrix();
    gl.glScalef(0.2f, 0.1f, 0.1f);
    glut.glutSolidSphere(1,  40,  50);  
    gl.glPopMatrix();
    gl.glEndList();
  }

  //Draw fish tail 
  public void tail(GL2 gl) {  
    tail_object = gl.glGenLists(1);
    gl.glNewList( tail_object, GL2.GL_COMPILE);
    GLUT glut = new GLUT();
    gl.glPushMatrix();
    gl.glTranslatef(0.27f, 0.0f, 0.0f);
    gl.glRotatef(270.0f, 0.0f, 1.0f,  0.0f);
    gl.glScalef(0.1f, 0.1f, 0.1f);
    glut.glutSolidCone(1.0, 1.0, 30, 40); 
    gl.glPopMatrix();
    gl.glEndList();
  } 
  
  //Draw fish using display list
  public void fish(GL2 gl) { 
    gl.glPushMatrix();
    gl.glTranslated(coord.x,coord.y,coord.z); 
    
    //If fish comes to the edge of the tank, 
    //turn fish around to stay within the tank
    if (dir_x > 1) {
        gl.glRotatef(-180,0,1,0);     
    } 
    if (dir_y > 1) {
        gl.glRotatef(-180,1,0,0);
    } 
    //fish body
    gl.glCallList( body_object );

    //fish tail 
    gl.glPushMatrix();
    gl.glRotatef(tail_angle,0,1,0);
    gl.glCallList( tail_object );
    gl.glPopMatrix();
    
    gl.glPopMatrix();
  }

  //Moving tail
  public void moving_tail() {   
    tail_angle += tail_speed * tail_dir;
    if (tail_angle > 20 || tail_angle <-20) {
        tail_dir = -tail_dir;
    } 
  }

  //Calculate potential function
  public Coord potential_function(Coord p, Coord q, double weight) {
    
    double f = Math.pow(p.x-q.x,2) + Math.pow(p.y-q.y,2) + Math.pow(p.z-q.z,2); 

    double dx = 2*(q.x - p.x)*Math.exp(-f);
    double dy = 2*(q.y - p.y)*Math.exp(-f);
    double dz = 2*(q.z - p.z)*Math.exp(-f); 
    
    Coord potential = new Coord(weight*dx, weight*dy, weight*dz);
    
    return potential; 
  }

  //Check if collision
  public boolean collision(Coord a, Coord b) {
    if (Math.abs(a.x - b.x) < 0.3 & Math.abs(a.y - b.y) < 0.3 & Math.abs(a.z - b.z) < 0.3) {
        return true;
    }

    return false; 
  } 

  //Moving fish in the tank
  public void moving_fish()  {
    
    //Create random destination for fish
    //Create a random number between -2 and 2
    
    double x = (-2)+rand.nextFloat()*4;
    double y = (-2)+rand.nextFloat()*4;
    double z = (-2)+rand.nextFloat()*4; 
    
    Coord rand_dest = new Coord(x, y, z); 
    

    //Use potential function for random destination
    Coord p1 = potential_function(coord, rand_dest, 0.2); 
    
    predator_coord = vivarium.pred.coord; 
    
    //Use potential function for avoiding predator
    Coord p2 = potential_function(coord, predator_coord, 0.5); 
    
    //Check for collision 
    boolean dead = collision(vivarium.fish.coord, vivarium.pred.coord);
    if(dead) {
        vivarium.eaten = true; 
    } 
    
    //Gradient of sum of potential
    double sum_x = p1.x + (-p2.x);
    double sum_y = p1.y + (-p2.y);
    double sum_z = p1.z + (-p2.z);  

    dir_x += sum_x;
    dir_y += sum_y;
    dir_z += sum_z; 
    
    coord.x +=  speed_x*dir_x;
    if (coord.x > 1.9 || coord.x < -1.9) {
          dir_x = -dir_x;
      }
    
    coord.y += speed_y*dir_y;
    if (coord.y > 1.9 || coord.y < -1.9) {
          dir_y = -dir_y;
      }
    
    coord.z += speed_z*dir_z;
    if (coord.z > 1.9 || coord.z < -1.9) {
          dir_z = -dir_z;
      }
  }

}