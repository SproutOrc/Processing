import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Coordinate extends PApplet {

public void setup() {
    size(500, 500, P3D);  
    
}

float lineLong = 200;
float x = 0;
final float angle10 = PI / 18;

public void draw() {
    // lights();
    background(153);
    pushMatrix();
    translate(x * 10, 250, 0);
    rotateZ(angle10 * x);
    x = x + 1;
    if (x == 50) {
        x = 0;
    }
    
    
    smooth(8);
    strokeWeight(4);
    
    stroke(255, 0, 0);
    line(0, 0, 0, lineLong, 0, 0);

    stroke(0, 255, 0);
    line(0, 0, 0, 0, lineLong, 0);

    stroke(0, 0, 255);
    line(0, 0, 0, 0, 0, lineLong);

    popMatrix();
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Coordinate" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
