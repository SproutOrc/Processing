import processing.serial.*;

static final int WINDOWS_SIZE = 800;

static int ACC_begin = 0;
static int ACC_end   = WINDOWS_SIZE - 1;

static int KF_begin  = 0;
static int KF_end    = WINDOWS_SIZE - 1;

Serial port;

byte[] inBuffer;
float[] ACC_angle;
float[] KF_angle;

void setup() {
  size(WINDOWS_SIZE, 400);
  background(255);

  port = new Serial(this, "COM20", 115200);
  port.buffer(10);

  inBuffer  = new byte[10];

  ACC_angle = new float[WINDOWS_SIZE];
  KF_angle  = new float[WINDOWS_SIZE];

  for(int i = 0; i < WINDOWS_SIZE; i++) {
    ACC_angle[i] = sin(4 * PI * i / WINDOWS_SIZE);
    KF_angle[i]  = cos(4 * PI * i / WINDOWS_SIZE);
  }  

  frameRate(30);
  noStroke();
  smooth();
}

void draw() {
  background(255);
  int ACC_index = ACC_begin;
  int KF_index = KF_begin;
  for (int i = 0; i < WINDOWS_SIZE; ++i) {
    fill(255, 0, 0);
    ellipse(i, ACC_angle[ACC_index] * 100 + 200, 3, 3);
    ACC_index++;
    if (ACC_index >= WINDOWS_SIZE) ACC_index = 0;

    fill(0, 255, 0);
    ellipse(i, KF_angle[KF_index] * 100 + 200, 3, 3); 
    KF_index++;
    if (KF_index >= WINDOWS_SIZE) KF_index = 0;
  } 
}

float makeFloat(byte[] buffer, int begin) {
  int value = 0;
  for (int i = 0; i < 4; i++) {
    value = (value << 8) | buffer[3 - i + begin];
  }
  return Float.intBitsToFloat(value);
}

void serialEvent(Serial port) {
  inBuffer = port.readBytes();
  if(inBuffer[0] == 0xa5 && inBuffer[1] == 0x5a) {
    print("resive:");
    KF_begin += 1;
    if (KF_begin >= WINDOWS_SIZE) KF_begin = 0;
    KF_end = WINDOWS_SIZE - 1 - KF_begin;

    KF_angle[KF_end] = makeFloat(inBuffer, 2);
    print("KF_angle = " + KF_angle[KF_end] + "\t");

    ACC_begin += 1;
    if (ACC_begin >= WINDOWS_SIZE) ACC_begin = 0;
    ACC_end = WINDOWS_SIZE - 1 - ACC_begin;

    ACC_angle[ACC_end] = makeFloat(inBuffer, 6);
    print("ACC_angle = " + ACC_angle[ACC_end] + "\t");

    println(";");
  } 
}
