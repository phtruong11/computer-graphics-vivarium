/** 
 * Phuong Truong
 * November 13, 2018
 * Predator.java
 * Fish moves inside the tank and chases the fish. If the fish is eaten,
 * predator moves randomly inside the tank.  
 */
import javax.media.opengl.GL2;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import java.lang.Math;
import java.util.*;


public class Predator {
  
  private Random rand = new Random();
  private int predator_object, tail_object, body_object, fin_object;
  private float speed_x, speed_y, speed_z;
  private float dir_x, dir_y, dir_z; 
  private float tail_angle, tail_speed, tail_dir;
  public Coord coord;
  public Coord prey_coord;  
  public Vivarium vivarium;

  public Predator(Coord c, Vivarium v) {
    vivarium = v;
    coord = c;
    prey_coord = vivarium.fish.coord;
    predator_object = body_object = tail_object = fin_object = 0;

    speed_x = speed_y = speed_z =  0.005f;
    dir_x = dir_y = dir_z = -1;

    tail_angle = 0;
    tail_speed = 1.5f; 
    tail_dir = 1; 
  }


  public void init(GL2 gl) {
    body(gl);
    tail(gl);
    fin(gl);
    predator_object = gl.glGenLists(1);
    gl.glNewList(predator_object, GL2.GL_COMPILE );
    predator(gl);
    gl.glEndList();
  }
  
  public void update(GL2 gl) {
    moving_tail();
    moving_pred();
    gl.glNewList( predator_object, GL2.GL_COMPILE );
    predator( gl ); 
    gl.glEndList();
  }
  
  public void draw(GL2 gl) {
    gl.glPushMatrix();
    gl.glColor3f( 0.45f, 0.55f, 0.60f);
    gl.glCallList( predator_object );
    gl.glPopMatrix();
  }
  
  //Draw predator body
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
 
  //Draw predator tail
  public void tail(GL2 gl){
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
  
  //Draw predator fin
  public void fin(GL2 gl){
    fin_object = gl.glGenLists(1);
    gl.glNewList( fin_object, GL2.GL_COMPILE);
    GLUT glut = new GLUT();
    gl.glPushMatrix();
    gl.glScalef(1, 1, 0.5f);
    gl.glRotatef(-75, 1, 0, 0);
    glut.glutSolidCone(0.1, 0.25, 20, 20);
    gl.glPopMatrix();
    gl.glEndList();
  }
  
  //Draw predator using display list
  public void predator(GL2 gl) {
    gl.glPushMatrix();   
    gl.glTranslated(coord.x,coord.y,coord.z); 
    
    //If predator comes to the edge of the tank, 
    //turn predator around to stay within the tank
    if (dir_x > 1) {
        gl.glRotatef(-180,0,1,0);
    }
    
    //predator body
    gl.glCallList( body_object );
    
    //predator fin
    gl.glPushMatrix();
    gl.glCallList(fin_object);
    gl.glPopMatrix();
    
    //predator tail 
    gl.glPushMatrix();
    gl.glRotatef(tail_angle,0,1,0);
    gl.glCallList( tail_object );
    gl.glPopMatrix();
    
    gl.glPopMatrix();
  }
  
  //Moving tail
  public void moving_tail() {
    tail_angle += tail_speed*tail_dir;
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

  //Moving predator in the tank
  public void moving_pred()  {
    
    double potential_pred_x = 0;
    double potential_pred_y = 0;
    double potential_pred_z = 0;
    
    //Use potential function for chasing prey
    Coord p1 = potential_function(coord, prey_coord, 0.4);
    
    double potential_fish_x = p1.x;
    double potential_fish_y = p1.y;
    double potential_fish_z = p1.z;

    //If fish is eaten, predator moves randomly
    if (vivarium.eaten) {
      
        //Create random destination for predator
        //Create a random number between -2 and 2
        double x = (-2)+rand.nextFloat()*4;
        double y = (-2)+rand.nextFloat()*4;
        double z = (-2)+rand.nextFloat()*4;
        Coord rand_dest = new Coord(x, y, z);
        
        //Potential of fish is zero if fish is eaten 
        potential_fish_x = 0;
        potential_fish_y = 0;
        potential_fish_z = 0;
        
        //Use potential function for random destination
        Coord p2 = potential_function(coord, rand_dest, 0.2);
        
        potential_pred_x = p2.x;
        potential_pred_y = p2.y;
        potential_pred_z = p2.z;
    }
    
    //Gradient of sum of potential
    double sum_x = potential_fish_x + potential_pred_x;
    double sum_y = potential_fish_y + potential_pred_y;
    double sum_z = potential_fish_z + potential_pred_z; 
    
    dir_x += sum_x;
    dir_y += sum_y;
    dir_z += sum_z;
    
    coord.x += speed_x*dir_x;
    if (coord.x > 1.9 || coord.x < -1.9) {
          dir_x = -dir_x;
    }
    
    coord.y += speed_y*dir_y;
    if (coord.y > 1.8 || coord.y < -1.9) {
          dir_y = -dir_y;
    }
    
    coord.z += speed_z*dir_z;
    if (coord.z > 1.9 || coord.z < -1.9) {
          dir_z = -dir_z;
    }
  }

}