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

public class ThreeDimensionFixedPosition extends PApplet {

public void setup() {
    size(500, 500, P3D);  
}

float lineLong = 200;

public void draw() {
    background(153);
    pushMatrix();

    translate(width / 2, height / 2);

    smooth(8);
    strokeWeight(4);

    rotateX(PI / 2);
    rotateZ(PI / 6);

    
    stroke(255, 0, 0);
    line(0, 0, 0, lineLong, 0, 0);

    stroke(0, 255, 0);
    line(0, 0, 0, 0, lineLong, 0);

    stroke(0, 0, 255);
    line(0, 0, 0, 0, 0, lineLong);

    popMatrix();
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ThreeDimensionFixedPosition" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
