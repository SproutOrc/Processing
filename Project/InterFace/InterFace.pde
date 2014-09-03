// ellipse is x y Coordinate
float x = 200;
float y = 200;

int flag = 0;
void setup(){
    size (800, 600);
    background(125);
    frameRate (30);
}

void draw(){
    background(125);
    fill (255);
    ellipse (200, 200, 400, 400);
    for (int i = 0; i < 400; i += 4){
        point (i, 200);
        point (200, i);
    }
    pushStyle ();
    fill (33, 153, 255);
    noStroke();
    ellipse (x, y, 20, 20);
    noFill ();
    popStyle ();
    line (200, 200, x, y);
    if (flag == 1 && abs(x - 200) > abs(y - 200)) {
        float k = (y - 200.0) / (x - 200.0);
        float b = 200 - 200 * k;
        if (x > 200) {
            x -= 1;
        } else {
            x += 1;
        }
        y = k * x + b;
    } else if (flag == 1 && abs(x - 200) < abs(y - 200)) {
        float k = (y - 200.0) / (x - 200.0);
        float b = 200 - 200 * k;
        if (y > 200) {
            y -= 1;
        } else {
            y += 1;
        }
        x = (y - b) / k;
    }
    if (int (x) == 200.0 && int (y) == 200.0) {
        x = 200;
        y = 200;
    }
    println("x: " + x + "y: " + y);
}

void mouseDragged(){
    float l = sqrt((mouseX - 200) * (mouseX - 200) + (mouseY - 200) * (mouseY - 200));
    if (l < 200) {
        x = mouseX;
        y = mouseY;
    }
    flag = 0;
}

void mouseReleased(){
    flag = 1;
}