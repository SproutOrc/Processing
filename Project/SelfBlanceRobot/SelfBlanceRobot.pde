// 显示需要显示的波形

// 创建一个波形对象，就是一个变量名
// 
Waveform angle;
Waveform angleAx;
Waveform gyro;
Waveform leftSpeed;
Waveform rightSpeed;
///////////////////////////////////

ConnectProtocol connect;

void setup() {
    size(600, 600);
    // 显示所有连接的串口
    println(Serial.list());

    // 配置单片机所使用的串口号
    // COM4 -> 打开COM4口
    // 115200 -> 波特率115200
    connect = new ConnectProtocol("COM7", 115200);

    // 配置波形显示的位置和范围
    // 
    // 窗口示意图
    // ——————————————————————>x+
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
    // 在x = 0, y = 0为起点，
    // width个像素作为波形显示宽度
    // 100个像素作为波形显示高度
    angle = new Waveform(0, 0, width, 100);

    // 波形显示的范围，
    // 串口如果发的数据大于等于40，则波形在再高的位置
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

        // 浮点型字典
        // 串口发送过来的数据会保存在a中，保存形式
        // 如
        // a 0.0
        // b 0.0
        // c 0.0
        FloatDict a = connect.getInFloatDict();

        // 向波形添加数据
        // 获取字符串为kfAngle的值的大小
        // 这个kfAngle和单片机里面发送的值是一一对应的
        angle.add(a.get("ax"));
        // 设置波形的x轴
        angle.setZero(0.0);
        
        angleAx.add(a.get("ay"));
        angleAx.setZero(0.0);

        gyro.add(a.get("az"));
        gyro.setZero(0.0);

        leftSpeed.add(a.get("gx"));
        leftSpeed.setZero(0.0);

        rightSpeed.add(a.get("gy"));
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
