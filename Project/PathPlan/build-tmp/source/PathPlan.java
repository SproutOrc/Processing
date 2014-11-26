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

public class PathPlan extends PApplet {

MapCreate userFace;
ConnectPort port;

int inX = 2;
int inY = 0;

PointMap nextPoint = new PointMap();
PointMap originPoint = new PointMap(2, 0);
PointMap targetPoint = new PointMap(2, 0);

int obstacleNum = 2;
PointMap[] obstacle = new PointMap[2];

public void setup(){
    size(650, 520);
    userFace = new MapCreate();
    port = new ConnectPort();

    obstacle[0] = new PointMap();
    obstacle[1] = new PointMap();
    originPoint.dir = PointMap.xNegative;
    nextPoint.dir = PointMap.xNegative;
    obstacle[0].x = 7;
    obstacle[0].y = 7;
    obstacle[1].x = 7;
    obstacle[1].y = 7;
    
    //userFace.setSize();
}

boolean isSend = false;
int delayTime;
PointMap lastSendPoint = new PointMap();
boolean mouseIsPressed = false;

public void draw(){
    background(255);
    if (port.getValue()) {
        delayTime = second();
        isSend = true;

        port.getOriginPoint(originPoint);
        port.getObstacle1Point(obstacle[0]);
        port.getObstacle2Point(obstacle[1]);
        
        originPoint.dir = nextPoint.dir;
        switch (originPoint.dir) {
                case PointMap.xNegative:
                    originPoint.x += 1;
                    break;
                    
                case PointMap.xPositive:
                    originPoint.x -= 1;
                    break;
                    
                case PointMap.yNegative:
                    originPoint.y += 1;
                    break;
                    
                case PointMap.yPositive:
                    originPoint.y -= 1;
                    break;
                    
                default:
                    break;
            }
    }

    if (mouseIsPressed) {
        mouseIsPressed = false;
        delayTime = second();
        isSend = true;

        targetPoint.x = inX;
        targetPoint.y = inY;
    }

    if (true == isSend && second() != delayTime) {

        isSend = false;
        boolean canPath = PathCreate.pathPlan(nextPoint, 
                                            originPoint, 
                                            targetPoint, 
                                            obstacleNum, 
                                            obstacle);
        if (canPath) {
            println("NEXTPOINT (" + nextPoint.x + ", " + nextPoint.y + ")"); 
            lastSendPoint.setPoint(nextPoint);
            port.controlLED(nextPoint);
        } else {
            println("pathPlan fail");
            PointMap temp = new PointMap(7, 7);
            port.controlLED(temp);
        }
        
    }
    
    userFace.showMap();
    userFace.showCardPlaceAndCardNum(port.getCardNum());
    userFace.showTargetNode(targetPoint);
    userFace.showObstacle(2, obstacle);

    //userFace.showCarStateIsConnect(port.carStateIsConnect());
    //userFace.showObstacle1IsConnect(port.obstacle1IsConnect());
    //userFace.showObstacle2IsConnect(port.obstacle2IsConnect());
    //userFace.showledControlIsConnect(port.ledControlIsConnect());
}


public void mousePressed()
{
    if (mouseX < 500) 
    {
        mouseIsPressed = true;
        inX = (mouseX + 60) / 160;
        inY = (mouseY + 60) / 160;

        inX = inX * 2;
        inY = inY * 2;
    }

//   println("x = " + mouseX);
//   println("y = " + mouseY);
}


public class ConnectPort extends PApplet{

    public ConnectPort () {
        carStateSerial = new Serial(this, "COM5", 57600);
        obstacle1Serial = new Serial(this, "COM4", 4800);
        obstacle2Serial = new Serial(this, "COM1", 57600);
        ledControlSerial = new Serial(this, "COM6", 9600);
        
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
            
            high = PApplet.parseChar(carStateSerial.read());
            low = PApplet.parseChar(carStateSerial.read());
            
            MHCDirX = PApplet.parseChar(high << 8 + low);
            
            high = PApplet.parseChar(carStateSerial.read());
            low = PApplet.parseChar(carStateSerial.read());
            
            MHCDirY = PApplet.parseChar(high << 8 + low);
            
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
        byte temp = PApplet.parseByte(calculateLEDNum(thisPoint));
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
// import processing.serial.*;

// Serial carState;
// Serial LEDControl;
public class MapCreate {

    public MapCreate () {
        
    }

    public void setSize() {
        size(650, 520);
    }
    
    public void showMap() {
        line(20, 20, 20, 500);
        line(180, 20, 180, 500);
        line(340, 20, 340, 500);
        line(500, 20, 500, 500);
        
        line(20, 20, 500, 20);
        line(20, 180, 500, 180);
        line(20, 340, 500, 340);
        line(20, 500, 500, 500);
    }

    public void showCardPlaceAndCardNum(int cardNum) {
        String showNum = "Card Num : " + cardNum;
        fill(0, 255, 0);
        int x = derect[cardNum][0] - 10;
        int y = derect[cardNum][1] - 10;
        rect(x, y, 20, 20);
        textSize(12);
        textAlign(LEFT, TOP);
        fill(255, 0, 0);
        text(showNum, 520, 20);
    }

    public void showTargetNode(PointMap targetNode) {
        fill(255, 0, 0);
        int x = 20 + targetNode.x * 80 - 10;
        int y = 20 + targetNode.y * 80 - 10;
        rect(x, y, 20, 20);
    }

    public void showObstacle(int obstacleNum, PointMap obstacle[]) {
        PImage img;
        img = loadImage("warning.jpg");
        imageMode(CENTER);
        int x;
        int y;
        for (int i = 0; i < obstacleNum; ++i) {
            x = 20 + obstacle[i].x * 80;
            y = 20 + obstacle[i].y * 80;
            image(img, x, y, 20, 20);
        }
    }

    public void showCarStateIsConnect(boolean carState) {
        if (true == carState) {
            fill(0, 255, 0);
        } else {
            fill(255, 0, 0);
        }

        ellipse(575, 180 + 64, 20, 20);
        noFill();
    }

    public void showObstacle1IsConnect(boolean obstacle1) {
        if (true == obstacle1) {
            fill(0, 255, 0);
        } else {
            fill(255, 0, 0);
        }

        ellipse(575, 180 + 64 + 64, 20, 20);
        noFill();
    }

    public void showObstacle2IsConnect(boolean obstacle2) {
        if (true == obstacle2) {
            fill(0, 255, 0);
        } else {
            fill(255, 0, 0);
        }

        ellipse(575, 180 + 64 + 64 + 64, 20, 20);
        noFill();
    }

    public void showledControlIsConnect(boolean ledControl) {
        if (true == ledControl) {
            fill(0, 255, 0);
        } else {
            fill(255, 0, 0);
        }

        ellipse(575, 180 + 64 + 64 + 64 + 64, 20, 20);
        noFill();
    }

    private final int derect[][] = {
        {100, 20},
        {260, 20},
        {420, 20},

        {20, 100},
        {180, 100},
        {340, 100},
        {500, 100},

        {100, 180},
        {260, 180},
        {420, 180},

        {20, 260},
        {180, 260},
        {340, 260},
        {500, 260},

        {100, 340},
        {260, 340},
        {420, 340},

        {20, 420},
        {180, 420},
        {340, 420},
        {500, 420},

        {100, 500},
        {260, 500},
        {420, 500},
    };
}
public static class Node {
    public Node (PointMap nodePoint) {
        coord.setPoint(nodePoint);
        pathNode.add(nodePoint);
        nextPathNum = 4;
        rootNode = null;
        nextNode = null;
        allNode[0] = new PointMap();
        allNode[1] = new PointMap();
        allNode[2] = new PointMap();
        allNode[3] = new PointMap();
        allNode[0].setToNil();
        allNode[1].setToNil();
        allNode[2].setToNil();
        allNode[3].setToNil();
    }
    
    // \u751f\u6210\u4e0b\u4e00\u4e2a\u8282\u70b9
    public boolean create () {
        //deleteNextNode();
        if (4 > nextPathNum) {
            allNode[nextPathNum].setToNil();
        }
        
        int i;
        int min = 4;
        for (i = 0; i < 4; ++i) {
            if ((min > nextNodePriority[i]) && (!allNode[i].isNil())) {
                min = nextNodePriority[i];
                nextPathNum = i;
            }
        }
        //nextPathNum = i;
        if (4 == min) {
            return false;
        } else {
            nextNode = new Node(allNode[nextPathNum]);
            nextNode.rootNode = this;
            return true;
        }
    }
    
    // \u91ca\u653e\u4e0b\u4e00\u4e2a\u8282\u70b9
//    public void deleteNextNode () {
//        delete nextNode;
//        nextNode = NULL;
//    }
    // \u83b7\u53d6\u5f53\u524d\u8282\u70b9\u9644\u8fd1\u7684\u8282\u70b9
    public void getAllRoundNode (int num, PointMap obstacle[]) {
        PointMap nextPoint = new PointMap(0, 0);
        nextPathNum = 4;
        
        // y\u8f74\u8d1f\u65b9\u5411
        nextPoint.x = coord.x;
        nextPoint.y = coord.y - 1;
        if (canToNextNode(nextPoint, num, obstacle)) {
            allNode[0].setPoint(coord.x, coord.y - 2, PointMap.yPositive);
            if (isRootNode(allNode[0]) || isInPathNode(allNode[0])) {
                allNode[0].setToNil();
            }
        } else {
            allNode[0].setToNil();
        }
        
        // x\u8f74\u6b63\u65b9\u5411
        nextPoint.x = coord.x + 1;
        nextPoint.y = coord.y;
        if (canToNextNode(nextPoint, num, obstacle)) {
            allNode[1].setPoint(coord.x + 2, coord.y, PointMap.xNegative);
            if (isRootNode(allNode[1]) || isInPathNode(allNode[1])) {
                allNode[1].setToNil();
            }
        } else {
            allNode[1].setToNil();
        }

        // y\u8f74\u6b63\u65b9\u5411
        nextPoint.x = coord.x;
        nextPoint.y = coord.y + 1;
        if (canToNextNode(nextPoint, num, obstacle)) {
            allNode[2].setPoint(coord.x, coord.y + 2, PointMap.yNegative);
            if (isRootNode(allNode[2]) || isInPathNode(allNode[2])) {
                allNode[2].setToNil();
            }
        } else {
            allNode[2].setToNil();
        }

        // x\u8f74\u8d1f\u65b9\u5411
        nextPoint.x = coord.x - 1;
        nextPoint.y = coord.y;
        if (canToNextNode(nextPoint, num, obstacle)) {
            allNode[3].setPoint(coord.x - 2, coord.y, PointMap.xPositive);
            if (isRootNode(allNode[3]) || isInPathNode(allNode[3])) {
                allNode[3].setToNil();
            }
        } else {
            allNode[3].setToNil();
        }
        if (!haveRootNode) {
            haveRootNode = true;
            switch (coord.dir) {
                case PointMap.xNegative:
                    allNode[3].setToNil();
                    break;
                    
                case PointMap.xPositive:
                    allNode[1].setToNil();
                    break;
                    
                case PointMap.yNegative:
                    allNode[0].setToNil();
                    break;
                    
                case PointMap.yPositive:
                    allNode[2].setToNil();
                    break;
                    
                default:
                    break;
            }
        }
    }

    // \u8bbe\u7f6e\u4e0b\u4e00\u4e2a\u8282\u70b9\u7684\u4f18\u5148\u7ea7
    public void setNextNodePriority (PointMap targetPoint) {
        int xDir;
        int yDir;
        
        if (targetPoint.x > coord.x) {
            xDir = PointMap.xNegative;
        } else if (targetPoint.x < coord.x) {
            xDir = PointMap.xPositive;
        } else {
            xDir = PointMap.noDir;
        }
        
        if (targetPoint.y > coord.y) {
            yDir = PointMap.yNegative;
        } else if (targetPoint.y < coord.y) {
            yDir = PointMap.yPositive;
        } else {
            yDir = PointMap.noDir;
        }
        
        for (int i = 0; i < 4; ++i) {
            if (!allNode[i].isNil()) {
                if ((allNode[i].dir == coord.dir || allNode[i].dir == coord.dir) 
                    && (xDir == allNode[i].dir || yDir == allNode[i].dir)) 
                {
                    nextNodePriority[i] = 0;
                } else if (xDir == allNode[i].dir || yDir == allNode[i].dir) {
                    nextNodePriority[i] = 1;
                } else if ((allNode[i].dir == coord.dir) || (allNode[i].dir == coord.dir)) {
                    nextNodePriority[i] = 2;
                } else {
                    nextNodePriority[i] = 3;
                }
            } else {
                nextNodePriority[i] = 4;
            }
        }
    }
    
    // \u5b58\u50a8\u6240\u6709\u7ecf\u8fc7\u7684\u8282\u70b9
    public static ArrayList<PointMap> pathNode = new ArrayList<PointMap>();
    // \u5f53\u524d\u8282\u70b9\u5750\u6807
    public PointMap coord = new PointMap();
    // \u7236\u8282\u70b9
    public Node rootNode;
    // \u4e0b\u4e00\u4e2a\u8282\u70b9
    public Node nextNode;
    
    // \u4e0a\u4e00\u4e2a\u8def\u5f84\u7684\u5e8f\u53f7
    private int nextPathNum;
    
    // \u5f53\u524d\u8282\u70b9\u9644\u8fd1\u7684\u6240\u6709\u7684\u8282\u70b9
    private PointMap[] allNode = new PointMap[4];
    
    // \u8bbe\u7f6e\u4f18\u5148\u7ea7
    private int[] nextNodePriority = new int[4];
    
    // \u5f53\u524d\u8282\u70b9\u662f\u5426\u6709\u7236\u8282\u70b9
    private static boolean haveRootNode;

    // \u8bbe\u7f6e\u5730\u56fe\u5927\u5c0f
    private final static int XMAX = 6;
    private final static int YMAX = 6;
    
    // \u5224\u65ad\u4e24\u4e2a\u8282\u70b9\u4e4b\u95f4\u662f\u5426\u6709\u969c\u788d\u7269\u548c\u8282\u70b9\u662f\u5426\u51fa\u4e86\u8fb9\u754c
    private boolean canToNextNode (PointMap pathPoint, int num, PointMap[] obstacle) {
        int i;
        for (i = 0; i < num; ++i) {
            if ((pathPoint.x == obstacle[i].x && pathPoint.y == obstacle[i].y)
                || pathPoint.x < 0 || pathPoint.x > 6
                || pathPoint.y < 0 || pathPoint.y > 6) {
                return false;
            }
        }
        return true;
    }
    
    //  \u5224\u65ad\u4e0b\u4e00\u4e2a\u8282\u70b9\u662f\u5426\u662f\u7236\u8282\u70b9
    private boolean isRootNode (PointMap thisNode) {
        if (this.rootNode != null) {
            if (this.rootNode.coord.isEqual(thisNode)){
                return true;
            }
        }
        return false;
    }
    
    // \u5224\u65ad\u8282\u70b9\u662f\u5426\u5728pathNode\u4e2d
    private boolean isInPathNode (PointMap thisPoint) {
        for (int i = pathNode.size()-1; i >= 0; i--)
        {
            if (pathNode.get(i).isEqual(thisPoint)) {
                return true;
            }
        }
        return false;
    }
}
public static class PathCreate {

    public PathCreate () {
        
    }

    private static boolean notToNext(PointMap thisPoint, PointMap nextPoint) {
        if (nextPoint.x > thisPoint.x && thisPoint.dir == PointMap.xPositive) {
            return true;
        }
        
        if (nextPoint.x < thisPoint.x && thisPoint.dir == PointMap.xNegative) {
            return true;
        }
        
        if (nextPoint.y > thisPoint.y && thisPoint.dir == PointMap.yPositive) {
            return true;
        }
        
        if (nextPoint.y < thisPoint.y && thisPoint.dir == PointMap.yNegative) {
            return true;
        }

        return false;
    }

    public static boolean pathPlan (PointMap nextPoint, PointMap originNode, PointMap targetNode, int obstacleNum, PointMap obstacle[]) {
        
        if (originNode.isEqual(targetNode)) {
            //fatherNode.pathNode.clear();
            println("IsEqual!");
            return false;
        }
        PointMap[] pathPoint = new PointMap[16];
        for (int i = 0; i < 16; i++)
          pathPoint[i] = new PointMap();
        int num = 0;
        
        Node fatherNode = new Node(originNode);
        Node.haveRootNode = false;
        fatherNode.getAllRoundNode(obstacleNum, obstacle);
        fatherNode.setNextNodePriority(targetNode);
        Node next;
        next = fatherNode;
        while (true) {
            boolean canCreate = next.create();
            if (!canCreate) {
                if (next.rootNode == null) {
                    println("NO NODE!");
                    println("ORIGIN x = " + originNode.x + ", y = " + originNode.y + ", dir =" + originNode.dir);
                    fatherNode.pathNode.clear();
                    return false;
                } else {
                    next = next.rootNode;
                    //next->nextNode->~Node();
                    
                }
            } else {
                next = next.nextNode;
                next.getAllRoundNode(obstacleNum, obstacle);
                next.setNextNodePriority(targetNode);
            }
            
            if (next.coord.isEqual(targetNode)) {
                next = fatherNode;
                for (int i = 0;; ++i) {
                    pathPoint[i].setPoint(next.coord);
                    if (next.nextNode == null) {
                        num = i + 1;
                        break;
                    } else {
                        next = next.nextNode;
                    }
                }
                if (num > 3) {
                    for (int i = 0; i < num - 3; ++i) {
                        for (int j = i + 3; j < num; ++j) {
                            if (((pathPoint[i].x + 2 == pathPoint[j].x) && (pathPoint[i].y == pathPoint[j].y))
                                || ((pathPoint[i].x - 2 == pathPoint[j].x) && (pathPoint[i].y == pathPoint[j].y))
                                || ((pathPoint[i].y + 2 == pathPoint[j].y) && (pathPoint[i].x == pathPoint[j].x))
                                || ((pathPoint[i].y - 2 == pathPoint[j].y) && (pathPoint[i].x == pathPoint[j].x))) {
                                int a;
                                for (a = 0; a < obstacleNum; a++) {
                                    if (((pathPoint[i].x + pathPoint[j].x) / 2 == obstacle[a].x
                                        && (pathPoint[i].y + pathPoint[j].y) / 2 == obstacle[a].y)
                                        || notToNext(pathPoint[i], pathPoint[j])
                                    ) {
                                        break;
                                    }
                                }
                                
                                if (a == obstacleNum) {
                                    for (int z = 0; z + j < num; ++z) {
                                        pathPoint[z + 1 + i].setPoint(pathPoint[j + z]);
                                    }
                                    num = num - (j - i) + 1;
                                }
                                
                            }
                        }
                    }
                }
                for (int i = 0; i < num; ++i) {
                    println(i + " (" + pathPoint[i].x + ", " + pathPoint[i].y + ")");
                }
                nextPoint.x = PApplet.parseInt((pathPoint[0].x + pathPoint[1].x) / 2);
                nextPoint.y = PApplet.parseInt((pathPoint[0].y + pathPoint[1].y) / 2);
                if (pathPoint[1].x > pathPoint[0].x) {
                    nextPoint.dir = PointMap.xNegative;
                } else if (pathPoint[1].x < pathPoint[0].x) {
                    nextPoint.dir = PointMap.xPositive;
                } else if (pathPoint[1].y > pathPoint[0].y) {
                    nextPoint.dir = PointMap.yNegative;
                } else if (pathPoint[1].y < pathPoint[0].y) {
                    nextPoint.dir = PointMap.yPositive;
                }
                break;
            }
        }
        next = null;
        fatherNode.pathNode.clear();
        return true;
    }
}
public static class PointMap{

    public int x;
    public int y;
    
    public final static int xNegative = 1;
    public final static int xPositive = 2;
    public final static int yNegative = 3;
    public final static int yPositive = 4;
    public final static int noDir = 5;
   
    
    public int dir;
    public PointMap(PointMap setPoint) {
        x = setPoint.x;
        y = setPoint.y;
        dir = setPoint.dir;
    }
    
    public PointMap(int inX, int inY) {
        x = inX;
        y = inY;
    }
    
    public PointMap() {
        x = 0;
        y = 0;
    }
    
    public boolean isEqual (PointMap thisPoint) {
        if (this.x == thisPoint.x && this.y == thisPoint.y) {
            return true;
        }
        return false;
    }
    
    public void setToNil () {
        x = -1;
        y = -1;
        dir = noDir;
    }
    
    public boolean isNil() {
        if (x == -1 && y == -1) {
            return true;
        }
        return false;
    }
    
    public void setPoint (int inX, int inY, int inDir) {
        x = inX;
        y = inY;
        dir = inDir;
    }
    
    public void setPoint (PointMap thisPoint) {
        x = thisPoint.x;
        y = thisPoint.y;
        dir = thisPoint.dir;
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PathPlan" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
