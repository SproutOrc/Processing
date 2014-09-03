import processing.serial.*;

Serial myPort;

int N = 3;

void setup() {
	size (640, 480);
	background(125);
	myPort = new Serial (this, "COM5", 9600);
}

void draw() {
	if (myPort.available() > 0) {
		println("var: " + myPort.read());
	}
}

byte[] sent = new byte[2];

void keyPressed() {
	if (sent[0] != key && (key == 'w' || key == 's' || key == 'd' || key == 'a' || key == 'p')) {
		sent[0] =byte (key);
		myPort.write(sent);
	}else if (sent[1] != key && (key == 'l' || key == 'm' || key == 'f')) {
		sent[1] = byte (key);
		myPort.write(sent);
	}
	println (sent);
}


