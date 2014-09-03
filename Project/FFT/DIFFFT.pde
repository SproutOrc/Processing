/**
 * Operating environment Processing
 * the FFT code 
 * wirte time : 2013.5.23
 */

public static class DIFFFT
{
	//base 2
	private int series;
	private int numData;
	private int halfNumData;

	public  COMPLEX[] inArray = new COMPLEX[1];
	private COMPLEX[] wArray  = new COMPLEX[1];

	//Constructor
	public DIFFFT (int inArrayN) {
		series = int (log (inArrayN) / log (2));
		numData = inArrayN;
		halfNumData = inArrayN / 2;
		//Extended array
		inArray = (COMPLEX[]) expand (inArray, inArrayN);
		wArray  = (COMPLEX[]) expand (wArray , inArrayN / 2);
		//Create object
		for (int i = 0; i < inArrayN; i++) {
			inArray[i] = new COMPLEX();
		}

		for (int i = 0; i < inArrayN / 2; i++) {
			wArray [i] = new COMPLEX();
		}
		// set up twiddle constants in wArray 
		for (int i = 0; i < inArrayN / 2; i++) {
			//Re component of twiddle constants
			wArray[i].real = cos (2.0 * PI * i / inArrayN);
			//Im component of twiddle constants  
			wArray[i].imag =-sin (2.0 * PI * i / inArrayN);
		}
	}

	//The complex's imaginary part is set to zero
	private void imagZero () {
		for (int i =  0; i < numData; i++) {
			inArray[i].imag = 0.0;
		}
	}

	//input sample array, # of points
	private void processFFT () {
		//temporary storage variables 
		COMPLEX topData;
		COMPLEX lowData;
		//difference between top/lower leg
		int leg_diff;
		int lower_leg;
		//index/step through twiddle constant 
		int index = 0;
		//step between values
		int step = 1;
		//difference between upper & lower legs
		leg_diff = halfNumData;
		
		//for N-point FFT
		for (int i = 0; i < series; i++)                   
		{
			index = 0;
			for (int j = 0; j < leg_diff; j++) {
				for (int upper_leg = j; upper_leg < numData; upper_leg += (2 * leg_diff)) {
					lower_leg = upper_leg+leg_diff;

					topData = COMPLEX.add (inArray[upper_leg], inArray[lower_leg]);
					lowData = COMPLEX.sub (inArray[upper_leg], inArray[lower_leg]);
					inArray[lower_leg] = COMPLEX.mul (lowData, wArray[index]);
					inArray[upper_leg] = topData;
				}
				index += step;
			}

			leg_diff >>= 1;
			step *= 2;
		}
	}

	//bit reversal for resequencing data
	//Rader algorithm
	//Input reverse
	private void inReverse () {	
		COMPLEX Temp;	
		int j = 0;
		int k = 0;
		
		for (int i = 1; i < (numData - 1); i++) {
			k = numData / 2;

			while (k <= j) {
				j = j - k;
				k = k / 2;
			}
			j = j + k;
			if (i < j) {
				Temp = inArray[j];
				inArray[j] = inArray[i];
				inArray[i] = Temp;
			}
		}
	}

	public void FFT () {
		imagZero ();
		processFFT ();
		inReverse ();
	}
}
