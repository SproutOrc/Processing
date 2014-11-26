import processing.serial.*;

public class ConnectPort extends PApplet{

    public ConnectPort () {
        carStateSerial = new Serial(this, "COM4", 57600);
        obstacle1Serial = new Serial(this, "COM1", 4800);
        obstacle2Serial = new Serial(this, "COM11", 57600);
        ledControlSerial = new Serial(this, "COM15", 9600);
        
        obstacle1Num = 25;
        obstacle2Num = 25;

        carState = false;
        obstacle1= false;
        obstacle2 = false;
        ledControl = false;
    }

    public void testConnect () {
        carStateSerial.write(0xA5);
        obstacle1Serial.write(0xA5);
        obstacle2Serial.write(0xA5);
        ledControlSerial.write(0xA5);
        int time = second();
        while (time == second());
        carState = false;
        if (carStateSerial.available() > 0) {
            if (carStateSerial.read() == 0xA5) {
                carState = true;
            }
        } 

        obstacle1 = false;
        if (obstacle1Serial.available() > 0) {
            if (obstacle1Serial.read() == 0xA5) {
                obstacle1 = true;
            }
        }

        if (obstacle2Serial.available() > 0) {
            if (obstacle2Serial.read() == 0xA5) {
                obstacle2 = true;
            }
        }

        if (ledControlSerial.available() > 0) {
            if (ledControlSerial.read() == 0xA5) {
                ledControl = true;
            }
        }
    }

    public boolean getValue () {
        boolean getNewValue = false;
        if (carStateSerial.available() >= 5) {
            char low, high;
            cardNum = carStateSerial.read();
            println("Card Num = " + cardNum);
            
            high = char(carStateSerial.read());
            low = char(carStateSerial.read());
            
            MHCDirX = char(high << 8 + low);
            
            high = char(carStateSerial.read());
            low = char(carStateSerial.read());
            
            MHCDirY = char(high << 8 + low);
            
            //println(atan2(MHCDirY / MHCDirX));
            carStateSerial.clear();
            getNewValue = true;
        }

        if (obstacle1Serial.available() > 0) {
            obstacle1Num = obstacle1Serial.read();
            getNewValue = true;
        }

        if (obstacle2Serial.available() > 0) {
            obstacle2Num = obstacle2Serial.read();
            getNewValue = true;
        }
        return getNewValue;
    }

    public void controlLED (PointMap thisPoint) {
        byte temp = byte(calculateLEDNum(thisPoint));
        int time = second();
        //while (time == second());
        
        ledControlSerial.write(temp);
    }

    public int getCardNum() {
        return cardNum;
    }

    public int getObstacle1Num() {
        return obstacle1Num;
    }

    public int getObstacle2Num() {
        return obstacle2Num;
    }

    public void getOriginPoint(PointMap originNodePoint) {
        originPoint.x = coord[cardNum][0];
        originPoint.y = coord[cardNum][1];
    }

    public void getObstacle1Point(PointMap obstacle1Point) {
        obstacle1Point.x = coord[obstacle1Num][0];
        obstacle1Point.y = coord[obstacle1Num][1];
    }

    public void getObstacle2Point(PointMap obstacle2Point) {
        obstacle2Point.x = coord[obstacle2Num][0];
        obstacle2Point.y = coord[obstacle2Num][1];
    }

    public boolean carStateIsConnect() {
        return carState;
    }

    public boolean obstacle1IsConnect() {
        return obstacle1;
    }

    public boolean obstacle2IsConnect() {
        return obstacle2;
    }

    public boolean ledControlIsConnect() {
        return ledControl;
    }

    
    private Serial carStateSerial;
    private Serial obstacle1Serial;
    private Serial obstacle2Serial;
    private Serial ledControlSerial;

    private int cardNum;
    private char MHCDirX;
    private char MHCDirY;

    private int obstacle1Num;
    private int obstacle2Num;

    private boolean carState;
    private boolean obstacle1;
    private boolean obstacle2;
    private boolean ledControl;

    private int calculateLEDNum (PointMap thisPoint) {
        for (int i = 0; i < 24; ++i) {
            if (coord[i][0] == thisPoint.x 
                && coord[i][1] == thisPoint.y) {
                return i;
            } 
        }
        return 25;
    }

    private final int coord[][] = {
        {1, 0}, //0
        {3, 0}, //1
        {5, 0}, //2

        {0, 1}, //3
        {2, 1}, //4
        {4, 1}, //5
        {6, 1}, //6

        {1, 2}, //7
        {3, 2}, //8
        {5, 2}, //9

        {0, 3}, //10
        {2, 3}, //11
        {4, 3}, //12
        {6, 3}, //13

        {1, 4}, //14
        {3, 4}, //15
        {5, 4}, //16

        {0, 5}, //17
        {2, 5}, //18
        {4, 5}, //19
        {6, 5}, //20

        {1, 6}, //21
        {3, 6}, //22
        {5, 6}, //23
        {7, 7}, //24
        {7, 7}  //25
    };
}
