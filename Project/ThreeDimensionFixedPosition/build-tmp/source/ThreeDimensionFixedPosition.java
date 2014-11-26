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
    lights();
    background(153);
    pushMatrix();
    camera(220, mouseY * 2, mouseX * 2, // eyeX, eyeY, eyeZ
         0.0f, 0.0f, 0.0f, // centerX, centerY, centerZ
         0.0f, 1.0f, 0.0f); // upX, upY, upZ
    //translate(width / 2, height / 2);
    rotateZ(-PI/2.0f);
    rotateY(PI/2.0f);
    rotateY(PI/4.0f);
    rotateZ(-PI/4.0f);

    smooth(8);
    strokeWeight(4);
    tint(255, 126); 
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
