public class Servo
{
	/**
	 * angleUp is up servo angle
	 * range 45 ~ 127
	 * middle angle is 80
	 * angleDo is down servo angle
	 * range 39 ~ 127
	 * middle angle is 88
	 */
	private int _angleUp;
	private int _angleDo;

	public Servo () {
		//make servo go to middle place
		_angleUp = 80;
		_angleDo = 88;
	}

	public void checkRange (int angleUp, int angleDo) {
		_angleUp = angleUp;
		_angleDo = angleDo;
	}

	public void name () {
		
	}
}