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

public class InterFace extends PApplet {

// ellipse is x y Coordinate
float x = 200;
float y = 200;

int flag = 0;
public void setup(){
    size (800, 600);
    background(125);
    frameRate (30);
}

public void draw(){
    background(125);
    fill (255);
    ellipse (200, 200, 400, 400);
    for (int i = 0; i < 400; i += 4){
        point (i, 200);
        point (200, i);
    }
    pushStyle ();
    fill (33, 153, 255);
    noStroke();
    ellipse (x, y, 20, 20);
    noFill ();
    popStyle ();
    line (200, 200, x, y);
    if (flag == 1 && abs(x - 200) > abs(y - 200)) {
        float k = (y - 200.0f) / (x - 200.0f);
        float b = 200 - 200 * k;
        if (x > 200) {
            x -= 1;
        } else {
            x += 1;
        }
        y = k * x + b;
    } else if (flag == 1 && abs(x - 200) < abs(y - 200)) {
        float k = (y - 200.0f) / (x - 200.0f);
        float b = 200 - 200 * k;
        if (y > 200) {
            y -= 1;
        } else {
            y += 1;
        }
        x = (y - b) / k;
    }
    if (PApplet.parseInt (x) == 200.0f && PApplet.parseInt (y) == 200.0f) {
        x = 200;
        y = 200;
    }
    println("x: " + x + "y: " + y);
}

public void mouseDragged(){
    float l = sqrt((mouseX - 200) * (mouseX - 200) + (mouseY - 200) * (mouseY - 200));
    if (l < 200) {
        x = mouseX;
        y = mouseY;
    }
    flag = 0;
}

public void mouseReleased(){
    flag = 1;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "InterFace" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
