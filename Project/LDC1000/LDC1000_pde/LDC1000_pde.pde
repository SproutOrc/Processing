import processing.serial.*;

Serial port;

byte[] inBuffer;
int flag = 0;
int[] myarray;

void setup() {
    size(512, 512);
    background(255);
    port = new Serial(this, "COM15", 9600);
    port.buffer(2);
    inBuffer = new byte[2];
    myarray = new int[200];
}

void draw() {
  if (flag == 0) {
     fill(255, 0, 0);
    noStroke();
    smooth();
    for (int i = 0; i < 200; ++i)
    ellipse(2.5 * i, myarray[i] / 10 - 600, 3, 3);
    flag = 0;
  }

    
}
int i = 0;
void serialEvent(Serial port) {
        inBuffer = port.readBytes();

        int pro = 0;
        pro += inBuffer[1];
        pro <<= 8;
        pro += inBuffer[0];
        myarray[i] = pro;
   
        if (i == 200) {
          flag= 1;  
        }
        
        println(pro + ",");
}

