Waveform angle;
Waveform angleAx;
Waveform gyro;
Waveform leftSpeed;
Waveform rightSpeed;
ConnectProtocol connect;

void setup() {
    size(600, 600);
    println(Serial.list());
15    angle = new Waveform(0, 0, width, 100);
    angle.setRange(40, -40);

    angleAx = new Waveform(0, 100, width, 100);
    angleAx.setRange(40, -40);

    gyro = new Waveform(0, 200, width, 100);
    gyro.setRange(200, -200);

    leftSpeed = new Waveform(0, 300, width, 100);
    leftSpeed.setRange(400, -400);

    rightSpeed = new Waveform(0, 400, width, 100);
    rightSpeed.setRange(400, -400);
}

void draw() {
    
    if (connect.available() > 0) {
        background(0);
        FloatDict a = connect.getInFloatDict();

        angle.add(a.get("kfAngle"));
        angle.setZero(a.get("Setpoint"));
        //println("setPoint = " + a.get("Setpoint"));
        angleAx.add(a.get("angleAx"));
        angleAx.setZero(a.get("setPoint"));

        gyro.add(a.get("GYRO"));
        gyro.setZero(0.0);

        leftSpeed.add(a.get("leftSpeed"));
        leftSpeed.setZero(0.0);

        rightSpeed.add(a.get("rightSpeed"));
        rightSpeed.setZero(0.0);

        angle.showByLine();
        angleAx.showByLine();
        gyro.showByLine();
        leftSpeed.showByLine();
        rightSpeed.showByLine();

        // println("leftSpeed = " + a.get("leftSpeed"));
        // println("rightSpeed = " + a.get("rightSpeed"));

    }
}

void stop()
{
    connect.stop();
    super.stop();
}

final int ZERO = 127;
final int WALK = 80;
final int TURN = 80;

int flog = 0;

void keyPressed() {
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
        send[0] = byte(setPoint);
        send[1] = byte(walkSpeed);
        send[2] = byte(turnSpeed);

        connect.sendByteArray(send);

    }
}

void keyReleased() {
    flog = 0;
    byte[] send = new byte[3];
    send[0] = byte(0);
    send[1] = byte(0);
    send[2] = byte(0);
    int lag = millis();
    while(millis() < lag + 60);
    connect.sendByteArray(send);
}
