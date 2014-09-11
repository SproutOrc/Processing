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

public class SelfBlanceRobot extends PApplet {

public void setup() {
    
}

public void draw() {
    
}
public static class Button extends PApplet{

    public Button (int x, int y, int width, int hight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.hight = hight;
        this.pressed = false;  
    }

    public Button() {
        this.pressed = false;
    }

    public void setProperty(int x, int y, int width, int hight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.hight = hight;
        this.pressed = false;
    }

    public void showButton() {
        ellipse(this.x, this.y, this.width, this.hight);
    }

    public boolean isPressed(int x, int y) {

        return true;
    }

    private static boolean pressed;

    private int x;
    private int y;
    private int width;
    private int hight;

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SelfBlanceRobot" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
