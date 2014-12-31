void setup() {
    size(500, 500, P3D);  
}

float lineLong = 200;

void draw() {
    background(153);
    pushMatrix();

    translate(width / 2, height / 2);

    smooth(8);
    strokeWeight(4);

    rotateX(PI / 2);
    rotateZ(PI / 6);

    
    stroke(255, 0, 0);
    line(0, 0, 0, lineLong, 0, 0);

    stroke(0, 255, 0);
    line(0, 0, 0, 0, lineLong, 0);

    stroke(0, 0, 255);
    line(0, 0, 0, 0, 0, lineLong);

    popMatrix();
}