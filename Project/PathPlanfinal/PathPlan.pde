MapCreate userFace;
ConnectPort port;

int inX = 2;
int inY = 0;

PointMap nextPoint = new PointMap();
PointMap originPoint = new PointMap(2, 0);
PointMap targetPoint = new PointMap(2, 0);

int obstacleNum = 2;
PointMap[] obstacle = new PointMap[2];

void setup(){
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

void draw(){
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

    userFace.showCarStateIsConnect(port.carStateIsConnect());
    userFace.showObstacle1IsConnect(port.obstacle1IsConnect());
    userFace.showObstacle2IsConnect(port.obstacle2IsConnect());
    userFace.showledControlIsConnect(port.ledControlIsConnect());
}


void mousePressed()
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
