/**
 * ControlP5 Slider. Horizontal and vertical sliders, 
 * with and without tick marks and snap-to-tick behavior.
 * by andreas schlegel, 2010
 */

/**
* ControlP5 Slider
*
* Horizontal and vertical sliders, 
* With and without tick marks and snap-to-tick behavior.
*
* find a list of public methods available for the Slider Controller
* at the bottom of this sketch.
*
* by Andreas Schlegel, 2012
* www.sojamo.de/libraries/controlp5
*
*/

import controlP5.*;
import processing.serial.*;

Serial port;

ControlP5 cp5;

String send = "hello";
String lastSend;

void setup() {
    size(700,400);
    noStroke();
    cp5 = new ControlP5(this);
    port = new Serial(this, "COM15", 115200);

    cp5.addSlider("angleKp")
       .setPosition(30,100)
       .setSize(20, 200)
       .setRange(0,80)
       .setValue(0)
    ;

    cp5.addSlider("angleKd")
       .setPosition(80,100)
       .setSize(20, 200)
       .setRange(0,5)
       .setValue(0)
    ;

    cp5.addSlider("speedKp")
       .setPosition(130,100)
       .setSize(20, 200)
       .setRange(0,2)
       .setValue(0)
    ;

    cp5.addSlider("speedKd")
       .setPosition(180,100)
       .setSize(20, 200)
       .setRange(0,5)
       .setValue(0)
    ;

    cp5.addSlider("turnKp")
       .setPosition(230,100)
       .setSize(20, 200)
       .setRange(0,.5)
       .setValue(0)
    ;

    cp5.addSlider("turnKd")
       .setPosition(280,100)
       .setSize(20, 200)
       .setRange(0,.5)
       .setValue(0)
    ;

    cp5.addSlider("offset")
       .setPosition(330,100)
       .setSize(20, 200)
       .setRange(0,30)
       .setValue(0)
    ;


    time = millis();
    lastSend = send;
}

int time;
void draw() {
    background(100);
    if (millis() > time + 200) {
        time = millis();
        if (lastSend != send) {
            lastSend = send;
            println(send);
            port.write(send + '\0');
        }
    }
}

void angleKp(float value) {
    send = "angleKp = " + (float)(Math.round(value*1000))/1000;
}

void angleKd(float value) {
    send = "angleKd = " + (float)(Math.round(value*1000))/1000;
}

void speedKp(float value) {
    send = "speedKp = " + (float)(Math.round(value*1000))/1000;
}

void speedKd(float value) {
    send = "speedKd = " + (float)(Math.round(value*1000))/1000;
}

void turnKp(float value) {
    send = "turnKp = " + (float)(Math.round(value*1000))/1000;
}

void turnKd(float value) {
    send = "turnKd = " + (float)(Math.round(value*1000))/1000;
}

void offset(float value) {
    send = "offset = " + (float)(Math.round(value*1000))/1000;
}