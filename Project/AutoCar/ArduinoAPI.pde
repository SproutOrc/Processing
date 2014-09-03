import processing.serial.*;

public class ArduinoAPI
{
	private Serial myPort;
	
	public ArduinoAPI (String port) {
		println (Serial.list());
		myPort = new Serial (this, port, 9600);
	}
}