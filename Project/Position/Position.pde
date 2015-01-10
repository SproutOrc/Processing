ConnectProtocol connect;
Waveform showX;
Waveform showY;
Waveform showvx;
Waveform showvy;
Waveform showsx;
Waveform showsy;

int len = 6;

double[] an = {1, -0.997548637782850};
double[] bn = {0.998774318891425, -0.998774318891425};

double[] xx = new double[len];
double[] yy = new double[len];

double[] sxx = new double[len];
double[] syy = new double[len];

float range = 5;

PFont font;

void setup() {
    size(800, 800);
    connect = new ConnectProtocol("COM7", 115200); 

    showX = new Waveform(600, 100, 200, 100);
    showX.setRange(10, -10);

    showY = new Waveform(600, 200, 200, 100);
    showY.setRange(10, -10);

    showvx = new Waveform(600, 300, 200, 100);
    showvx.setRange(range, -range);

    showvy = new Waveform(600, 400, 200, 100);
    showvy.setRange(range, -range);

    showsx = new Waveform(600, 500, 200, 100);
    showsx.setRange(range, -range);

    showsy = new Waveform(600, 600, 200, 100);
    showsy.setRange(range, -range);

    font = loadFont("Consolas-Bold-20.vlw"); 

    for (int i = 0; i < len; ++i) {
        xx[i] = 0.0;
        yy[i] = 0.0;
        sxx[i] = 0.0;
        syy[i] = 0.0;
    }
}

double xv = 0.0;
double xs = 0.0;

double yv = 0.0;
double ys = 0.0;

float x = 0.0;
float y = 0.0;

final double G = 9.8;

final double DT = 0.01;


float tmp1 = 0.0;
float tmp2 = 0.0;
float tmp3 = 0.0;
float tmp4 = 0.0;

void draw() {
    if (connect.available() > 0) {
        FloatDict a = connect.getInFloatDict();

        showX.add(a.get("z"));
        showY.add(a.get("y"));
        
        xv += (double)a.get("z") * DT;

        for (int i = 0; i < len - 1; ++i) {
            xx[i] = xx[i + 1];
        }

        xx[len - 1] = xv;

        yy = Filter.filtfilt(bn, an, xx);

        showvx.add((float)yy[len - 1]);

        yv += a.get("y") * DT;


        showvy.add(tmp2);

        xs += yy[len - 1] * DT;

        for (int i = 0; i < len - 1; ++i) {
            sxx[i] = sxx[i + 1];
        }

        sxx[len - 1] = xs;

        syy = Filter.filtfilt(bn, an, sxx);

        showsx.add((float)syy[len - 1]);

        ys += tmp2 * DT;

        showsy.add(tmp4);
        
        if (xs > 300 || xs < -300) {
          xv = 0.0;
          xs = 0.0;
        }
        if (ys > 300 || ys < -300) {
          yv = 0.0;
          ys = 0.0;
        }

        x = (float)xs * 100 + 300;
        y = -(float)ys * 0.0 + 300;

        background(0);
        textFont(font, 20);
        fill(0, 102, 153);
        textAlign(LEFT, TOP);
        text("ACC: \n" 
            + "x: " + a.get("x") + "\n"
            + "y: " + a.get("y") + "\n"
            + "z: " + a.get("z")
            , 20
            , 20
        );

        text("Position: \n" 
            + "x: " + xs + "\n"
            + "y: " + ys + "\n"
            + "z: " + 0.00
            , 300
            , 20
        );
        
        fill(255, 0, 0);
        ellipse(x, y, 10, 10);

        showX.showByLine();
        showY.showByLine();
        showvx.showByLine();
        showvy.showByLine();
        showsx.showByLine();
        showsy.showByLine();
    }
}

void keyPressed() {
    xv = 0.0;
    xs = 0.0;
    yv = 0.0;
    ys = 0.0;
}
