import processing.serial.*;

Serial myPort;

int[] a = new int[2];

int flag = 0;

void setup() {
	size (600, 600);
	background(255);
	println(Serial.list());
	myPort = new Serial (this, "COM6", 9600);
	myPort.buffer(1);
	frameRate(4);
}

void draw() {
	for (int i = 0; i < 600; i += 4) {
		point(i, height / 2);
	}
	for (int i = 0; i < 600; i += 4) {
		point(width / 2, i);
	}
	if (mouseX > 0 && mouseY > 0 
		&& mouseX < 600 && mouseY < 600 && flag == 1){
		fill(35, 145, 216);
		cursor(CROSS);
		noFill();
		a[0] = mouseY * 80 / 600 + 45;
		a[1] = mouseX * 88 / 600 + 39;
		myPort.write (a[0]);
		myPort.write (a[1]);
	}
}

void serialEvent(Serial myPort) {
	int var = myPort.read ();
	println(var);
}

void mousePressed() {
	if (flag == 0)
		flag = 1;
	else
		flag = 0;
}
