public static class COMPLEX
{
	public float real;
	public float imag;

	public COMPLEX () {
	// expression
	}

	public static COMPLEX add (COMPLEX a, COMPLEX b) {
		COMPLEX Temp = new COMPLEX();
		Temp.real = a.real + b.real;
		Temp.imag = a.imag + b.imag;
		return Temp;
	}

	public static COMPLEX sub (COMPLEX a, COMPLEX b) {
		COMPLEX Temp = new COMPLEX();
		Temp.real = a.real - b.real;
		Temp.imag = a.imag - b.imag;
		return Temp;
	}

	public static COMPLEX mul (COMPLEX a, COMPLEX b) {
		COMPLEX Temp = new COMPLEX();
		Temp.real = a.real * b.real - a.imag * b.imag;
		Temp.imag = a.real * b.imag + a.imag * b.real;
		return Temp;
	}
}
