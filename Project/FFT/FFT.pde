import processing.serial.*;

Serial myPort;

DIFFFT myFFT;
IllustrateAPI dis;
byte[] inBuffer;
byte[] wave;
final int N = 128;
void setup() {
    size(1024, 600);
    background(125);
    //显示所有可用串口
    println(Serial.list());
    // 初始化串口
    myPort = new Serial(this, "COM1", 115200);
    myPort.buffer(N);

    myFFT = new DIFFFT(N);
    dis = new IllustrateAPI(N, 1024);
    inBuffer = new byte[N];
    wave = new byte[N];

    frameRate(30);
}
int flag = 0;

void draw() {
    if (flag == 0) {
        
        background(125);
        for (int i = 0; i < N; i++) {
            myFFT.inArray[i].real = inBuffer[i];
            wave[i] = inBuffer[i];
             myFFT.inArray[i].real = (float)(sin(PI_TWO * i / 64));
             //2 + 3 * cos (2 * PI * 50 * i / 256 - PI * 30 / 180) 
                                     //+ 1.5 * cos (2 * PI * 75 * i / 256 + PI * 90 / 180);
        }
        myFFT.FFT();
        println("FFT OK");
        for (int i = 0 ; i < N / 2 ; i++) {
            int a = int(sqrt(myFFT.inArray[i].real * myFFT.inArray[i].real 
                            + myFFT.inArray[i].imag * myFFT.inArray[i].imag));
            //a /= 10;
            if (a >= 596) a = 596;
            dis.motion(a, i);
            dis.show (i);
            fill(255, 0, 0);
            ellipse(i*3, -(wave[i] - 128), 2, 2);
        }
        flag = 0;
    }
}

void serialEvent(Serial myPort) {
    if (flag == 0){
        inBuffer = myPort.readBytes ();
        println("OK");
        flag = 1;
    }
}

