void setup() {
    size(500, 500, P3D);  
}

float lineLong = 200;

void draw() {
    lights();
    background(153);
    pushMatrix();
    camera(220, mouseY * 2, mouseX * 2, // eyeX, eyeY, eyeZ
         0.0, 0.0, 0.0, // centerX, centerY, centerZ
         0.0, 1.0, 0.0); // upX, upY, upZ
    //translate(width / 2, height / 2);
    rotateZ(-PI/2.0);
    rotateY(PI/2.0);
    rotateY(PI/4.0);
    rotateZ(-PI/4.0);

    smooth(8);
    strokeWeight(4);
    
    stroke(255, 0, 0);
    line(0, 0, 0, lineLong, 0, 0);

    stroke(0, 255, 0);
    line(0, 0, 0, 0, lineLong, 0);

    stroke(0, 0, 255);
    line(0, 0, 0, 0, 0, lineLong);

    popMatrix();
}