import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SelfBlanceRobot extends PApplet {

// \u663e\u793a\u9700\u8981\u663e\u793a\u7684\u6ce2\u5f62

// \u521b\u5efa\u4e00\u4e2a\u6ce2\u5f62\u5bf9\u8c61\uff0c\u5c31\u662f\u4e00\u4e2a\u53d8\u91cf\u540d
// 
Waveform angle;
Waveform angleAx;
Waveform gyro;
Waveform leftSpeed;
Waveform rightSpeed;
///////////////////////////////////

ConnectProtocol connect;

public void setup() {
    size(600, 600);
    // \u663e\u793a\u6240\u6709\u8fde\u63a5\u7684\u4e32\u53e3
    println(Serial.list());

    // \u914d\u7f6e\u5355\u7247\u673a\u6240\u4f7f\u7528\u7684\u4e32\u53e3\u53f7
    // COM4 -> \u6253\u5f00COM4\u53e3
    // 115200 -> \u6ce2\u7279\u7387115200
    connect = new ConnectProtocol("COM4", 115200);

    // \u914d\u7f6e\u6ce2\u5f62\u663e\u793a\u7684\u4f4d\u7f6e\u548c\u8303\u56f4
    // 
    // \u7a97\u53e3\u793a\u610f\u56fe
    // \u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014>x+
    // |
    // |
    // |
    // |
    // |
    // |
    // |
    // |
    // |
    // y+
    // Waveform(0, 0, width, 100)
    // \u5728x = 0, y = 0\u4e3a\u8d77\u70b9\uff0c
    // width\u4e2a\u50cf\u7d20\u4f5c\u4e3a\u6ce2\u5f62\u663e\u793a\u5bbd\u5ea6
    // 100\u4e2a\u50cf\u7d20\u4f5c\u4e3a\u6ce2\u5f62\u663e\u793a\u9ad8\u5ea6
    angle = new Waveform(0, 0, width, 100);

    // \u6ce2\u5f62\u663e\u793a\u7684\u8303\u56f4\uff0c
    // \u4e32\u53e3\u5982\u679c\u53d1\u7684\u6570\u636e\u5927\u4e8e\u7b49\u4e8e40\uff0c\u5219\u6ce2\u5f62\u5728\u518d\u9ad8\u7684\u4f4d\u7f6e
    angle.setRange(20, -20);

    angleAx = new Waveform(0, 100, width, 100);
    angleAx.setRange(20, -20);

    gyro = new Waveform(0, 200, width, 100);
    gyro.setRange(20, -20);

    leftSpeed = new Waveform(0, 300, width, 100);
    leftSpeed.setRange(400, -400);

    rightSpeed = new Waveform(0, 400, width, 100);
    rightSpeed.setRange(400, -400);
}

public void draw() {
    
    if (connect.available() > 0) {
        background(0);

        // \u6d6e\u70b9\u578b\u5b57\u5178
        // \u4e32\u53e3\u53d1\u9001\u8fc7\u6765\u7684\u6570\u636e\u4f1a\u4fdd\u5b58\u5728a\u4e2d\uff0c\u4fdd\u5b58\u5f62\u5f0f
        // \u5982
        // a 0.0
        // b 0.0
        // c 0.0
        FloatDict a = connect.getInFloatDict();

        // \u5411\u6ce2\u5f62\u6dfb\u52a0\u6570\u636e
        // \u83b7\u53d6\u5b57\u7b26\u4e32\u4e3akfAngle\u7684\u503c\u7684\u5927\u5c0f
        // \u8fd9\u4e2akfAngle\u548c\u5355\u7247\u673a\u91cc\u9762\u53d1\u9001\u7684\u503c\u662f\u4e00\u4e00\u5bf9\u5e94\u7684
        angle.add(a.get("x"));
        // \u8bbe\u7f6e\u6ce2\u5f62\u7684x\u8f74
        angle.setZero(0.0f);
        
        angleAx.add(a.get("y"));
        angleAx.setZero(0.0f);

        gyro.add(a.get("z"));
        gyro.setZero(0.0f);

        leftSpeed.add(a.get("gx"));
        leftSpeed.setZero(0.0f);

        rightSpeed.add(a.get("gy"));
        rightSpeed.setZero(0.0f);

        angle.showByLine();
        angleAx.showByLine();
        gyro.showByLine();
        leftSpeed.showByLine();
        rightSpeed.showByLine();

        // println("leftSpeed = " + a.get("leftSpeed"));
        // println("rightSpeed = " + a.get("rightSpeed"));

    }
}

public void stop()
{
    connect.stop();
    super.stop();
}

final int ZERO = 127;
final int WALK = 80;
final int TURN = 80;

int flog = 0;

public void keyPressed() {
    int setPoint = ZERO;
    int walkSpeed = ZERO;
    int turnSpeed = ZERO;
    if (key == CODED) {
        if (keyCode == UP) {
            walkSpeed = ZERO - WALK;
        } else if (keyCode == DOWN) {
            walkSpeed = ZERO + WALK;
        } else if (keyCode == LEFT) {
            turnSpeed = ZERO - TURN;
        } else if (keyCode == RIGHT) {
            turnSpeed = ZERO + TURN;
        } else {
            walkSpeed = setPoint;
            turnSpeed = setPoint;
        }
    } else {
        walkSpeed = setPoint;
        turnSpeed = setPoint;
    }
    if (flog == 0) {
        flog = 1;
        byte[] send = new byte[3];
        send[0] = PApplet.parseByte(setPoint);
        send[1] = PApplet.parseByte(walkSpeed);
        send[2] = PApplet.parseByte(turnSpeed);

        connect.sendByteArray(send);

    }
}

public void keyReleased() {
    flog = 0;
    byte[] send = new byte[3];
    send[0] = PApplet.parseByte(0);
    send[1] = PApplet.parseByte(0);
    send[2] = PApplet.parseByte(0);
    int lag = millis();
    while(millis() < lag + 60);
    connect.sendByteArray(send);
}
public class Button {

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void showEllipseButton() {
        smooth();
        ellipse(this.x, this.y, this.width, this.hight);

    }

    public boolean isPressed(int x, int y) {

        return true;
    }

    private boolean pressed;
    private String title;

    private int x;
    private int y;
    private int width;
    private int hight;
}


public static class ConnectProtocol extends PApplet{

    public ConnectProtocol (String com, int buad) {
        port = new Serial(this, com, buad);
        port.bufferUntil('\n');
        inFloatDict = new FloatDict();
    }

    public static FloatDict stringToFloat(String str) {

        String inStr = str.replaceAll(" ", "");

        String stringSplitByComma[] = inStr.split(",");

        FloatDict stringToFloat = new FloatDict();

        for (String itr : stringSplitByComma) {
            String[] stringSplitByEqual = itr.split("=");
            stringToFloat.set(stringSplitByEqual[0], Float.parseFloat(stringSplitByEqual[1]));
        }
        return stringToFloat;
    }

    public int available() {
        return inFloatDictSize;
    }

    public FloatDict getInFloatDict() {
        inFloatDictSize = 0;
        return inFloatDict;
    }

    public void serialEvent(Serial port) {
        inFloatDictSize = 0;
        inString = port.readString();
        // println(inString);
        if (inFloatDict.size() > 0) inFloatDict.clear();
        inFloatDict = stringToFloat(inString);
        inFloatDictSize = inFloatDict.size();
    }

    public void stop() {
        port.stop();
    }

    public void sendByteArray(byte send[]) {
        port.write(send);
    }

    public Serial port;
    private FloatDict inFloatDict;

    private int inFloatDictSize;
    private String inString;
}
public class Waveform{

    public Waveform (float x, float y, float width, float hight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.hight = hight;

        begin = 0;
        end   = PApplet.parseInt(this.width) - 1 - begin;

        reg = new float[PApplet.parseInt(this.width)];

        INMAX = this.hight / 2.0f;
        INMIN = -INMAX;
    }

    public void add(float inValue) {
        float value = inValue;
        if (value > INMAX) value = INMAX;
        if (value < INMIN) value = INMIN;

        end = begin;
        begin = begin + 1;
        if (begin >= PApplet.parseInt(width)) begin = 0;
        reg[end] = value;
    }

    public void showByPoint() {
        int index = begin;
        showXY();
        noStroke();
        smooth();
        fill(255, 0, 0);
        for (int i = 0; i < PApplet.parseInt(width) - 1; ++i) {
            ellipse(x + i, y + hight / 2 - reg[index] * scale, 2, 2);
            index++;
            if (index >= PApplet.parseInt(width)) index = 0;
        }
        noSmooth();
    }

    public void showByLine() {
        int index = begin;
        int lastIndex = index;
        showXY();
        stroke(255, 0, 0);
        //smooth();
        for (int i = 1; i < PApplet.parseInt(width) - 1; ++i) {
            lastIndex = index;
            index++;
            if (index >= PApplet.parseInt(width)) index = 0;
            line(i - 1, y + hight / 2 - reg[lastIndex] * scale, 
                     i, y + hight / 2 - reg[index] * scale);
        }
    }

    public void showByPoint(float x, float y) {
        setXY(x, y);
        showByPoint();
    }

    public void showByLine(float x, float y) {
        setXY(x, y);
        showByLine();
    }

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setRange(float max, float min) {
        INMAX = max;
        INMIN = min;

        scale = hight / (INMAX - INMIN);
    }

    private void showXY() {
        stroke(255, 255, 0);
        // show x
        line(x, y + hight / 2 - zeroPoint, x + width, y + hight / 2);
        // show y
        // line(x + width / 2, y, x + width / 2, y + hight);
        noStroke();
    }

    public void setZero(float zeroPoint) {
        this.zeroPoint = zeroPoint;
    }

    private float[] reg;
    private float zeroPoint;

    private int begin;
    private int end;
    // coordinates
    private float x;
    private float y;
    // size
    private float width;
    private float hight;
    // waveform range
    private float INMAX;
    private float INMIN;

    private float scale;
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
