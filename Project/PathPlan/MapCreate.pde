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
