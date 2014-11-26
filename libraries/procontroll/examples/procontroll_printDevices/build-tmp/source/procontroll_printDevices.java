import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import procontroll.*; 
import java.io.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class procontroll_printDevices extends PApplet {




ControllIO controll;

public void setup(){
  size(400,400);
  
  controll = ControllIO.getInstance(this);
  controll.printDevices();
  
  for(int i = 0; i < controll.getNumberOfDevices(); i++){
    ControllDevice device = controll.getDevice(i);
    
    println(device.getName()+" has:");
    println(" " + device.getNumberOfSliders() + " sliders");
    println(" " + device.getNumberOfButtons() + " buttons");
    println(" " + device.getNumberOfSticks() + " sticks");
    
    device.printSliders();
    device.printButtons();
    device.printSticks();
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "procontroll_printDevices" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
