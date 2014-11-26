// MotioninJoy Virtual Game Controller
// 
import procontroll.*;
import java.io.*;
import processing.serial.*;

final int setPoint = 30;

ControllIO controll;
ControllDevice device;
ControllSlider sliderX;
ControllSlider sliderY;
ControllButton button;

Serial PS3Controler;

void setup(){
    size(400,400);
    
    PS3Controler = new Serial(this, "COM15", 115200);

    controll = ControllIO.getInstance(this);
    controll.printDevices();

    device = controll.getDevice("MotioninJoy Virtual Game Controller");
    device.setTolerance(0.05f);
    
    sliderX = device.getSlider("X axis");
    sliderY = device.getSlider("Y axis");
    
    button = device.getButton("L1");
    
    fill(0);
    rectMode(CENTER);
}

int m = 0;

void draw(){
    if (millis() > m + 100) {
        m = millis();
        background(255); 
    
        if(button.pressed()){
            fill(255,0,0);
        }else{
            fill(0);
        }
        
        float sliderXValue = sliderX.getValue();
        float sliderYValue = sliderY.getValue();

        PS3Controler.write(byte(setPoint));
        PS3Controler.write(byte(sliderYValue * setPoint + setPoint));
        PS3Controler.write(byte(-sliderXValue * setPoint + setPoint));
        

        float x = sliderXValue * setPoint+ width/2;
        float y = sliderYValue * setPoint+ height/2;

        println("sliderX: " + x + ",\tsliderY:" + y);
        
        if(x > width + 20 || x < - 20 || y > height + 20 || y < - 20){
            sliderX.reset();
            sliderY.reset();
        }
        
        rect(x,y,20,20);
    }
    
}