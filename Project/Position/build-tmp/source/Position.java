import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Position extends PApplet {

ConnectProtocol connect;
Waveform showX;
Waveform showY;
Waveform showvx;
Waveform showvy;
Waveform showsx;
Waveform showsy;

int len = 6;

double[] an = {1, -0.997548637782850f};
double[] bn = {0.998774318891425f, -0.998774318891425f};

double[] xx = new double[len];
double[] yy = new double[len];

double[] sxx = new double[len];
double[] syy = new double[len];

float range = 5;

PFont font;

public void setup() {
    size(800, 800);
    connect = new ConnectProtocol("COM7", 115200); 

    showX = new Waveform(600, 100, 200, 100);
    showX.setRange(10, -10);

    showY = new Waveform(600, 200, 200, 100);
    showY.setRange(10, -10);

    showvx = new Waveform(600, 300, 200, 100);
    showvx.setRange(range, -range);

    showvy = new Waveform(600, 400, 200, 100);
    showvy.setRange(range, -range);

    showsx = new Waveform(600, 500, 200, 100);
    showsx.setRange(range, -range);

    showsy = new Waveform(600, 600, 200, 100);
    showsy.setRange(range, -range);

    font = loadFont("Consolas-Bold-20.vlw"); 

    for (int i = 0; i < len; ++i) {
        xx[i] = 0.0f;
        yy[i] = 0.0f;
        sxx[i] = 0.0f;
        syy[i] = 0.0f;
    }
}

double xv = 0.0f;
double xs = 0.0f;

double yv = 0.0f;
double ys = 0.0f;

float x = 0.0f;
float y = 0.0f;

final double G = 9.8f;

final double DT = 0.01f;


float tmp1 = 0.0f;
float tmp2 = 0.0f;
float tmp3 = 0.0f;
float tmp4 = 0.0f;

public void draw() {
    if (connect.available() > 0) {
        FloatDict a = connect.getInFloatDict();

        showX.add(a.get("z"));
        showY.add(a.get("y"));
        
        xv += (double)a.get("z") * DT;

        for (int i = 0; i < len - 1; ++i) {
            xx[i] = xx[i + 1];
        }

        xx[len - 1] = xv;

        yy = Filter.filtfilt(bn, an, xx);

        showvx.add((float)yy[len - 1]);

        yv += a.get("y") * DT;


        showvy.add(tmp2);

        xs += yy[len - 1] * DT;

        for (int i = 0; i < len - 1; ++i) {
            sxx[i] = sxx[i + 1];
        }

        sxx[len - 1] = xs;

        syy = Filter.filtfilt(bn, an, sxx);

        showsx.add((float)syy[len - 1]);

        ys += tmp2 * DT;

        showsy.add(tmp4);
        
        if (xs > 300 || xs < -300) {
          xv = 0.0f;
          xs = 0.0f;
        }
        if (ys > 300 || ys < -300) {
          yv = 0.0f;
          ys = 0.0f;
        }

        x = (float)xs * 100 + 300;
        y = -(float)ys * 0.0f + 300;

        background(0);
        textFont(font, 20);
        fill(0, 102, 153);
        textAlign(LEFT, TOP);
        text("ACC: \n" 
            + "x: " + a.get("x") + "\n"
            + "y: " + a.get("y") + "\n"
            + "z: " + a.get("z")
            , 20
            , 20
        );

        text("Position: \n" 
            + "x: " + xs + "\n"
            + "y: " + ys + "\n"
            + "z: " + 0.00f
            , 300
            , 20
        );
        
        fill(255, 0, 0);
        ellipse(x, y, 10, 10);

        showX.showByLine();
        showY.showByLine();
        showvx.showByLine();
        showvy.showByLine();
        showsx.showByLine();
        showsy.showByLine();
    }
}

public void keyPressed() {
    xv = 0.0f;
    xs = 0.0f;
    yv = 0.0f;
    ys = 0.0f;
}


public static class ConnectProtocol extends PApplet{

    public ConnectProtocol (String com, int buad) {
        port = new Serial(this, com, buad);
        port.bufferUntil('\n');
        inFloatDict = new FloatDict();
    }

    public static FloatDict stringToFloat(String str) {

        String inStr = str.replaceAll(" ", "");

        String stringSplitByComma[] = inStr.split(",");

        FloatDict stringToFloat = new FloatDict();

        for (String itr : stringSplitByComma) {
            String[] stringSplitByEqual = itr.split("=");
            stringToFloat.set(stringSplitByEqual[0], Float.parseFloat(stringSplitByEqual[1]));
        }
        return stringToFloat;
    }

    public int available() {
        return inFloatDictSize;
    }

    public FloatDict getInFloatDict() {
        inFloatDictSize = 0;
        return inFloatDict;
    }

    public void serialEvent(Serial port) {
        inFloatDictSize = 0;
        inString = port.readString();
        // println(inString);
        if (inFloatDict.size() > 0) inFloatDict.clear();
        inFloatDict = stringToFloat(inString);
        inFloatDictSize = inFloatDict.size();
    }

    public void stop() {
        port.stop();
    }

    public void sendByteArray(byte send[]) {
        port.write(send);
    }

    public Serial port;
    private FloatDict inFloatDict;

    private int inFloatDictSize;
    private String inString;
}
/*
 *  Filter.java
 *  This file is part of AcMus.
 *  
 *  AcMus: Tools for Measurement, Analysis, and Simulation of Room Acoustics
 *  
 *  Copyright (C) 2006 Leo Ueda, Bruno Masiero
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
/*
 * Filter.java
 * Created on 07/06/2005
 */

// import Jama.Matrix;

/**
 * @author lku
 */
public static  class Filter {

	public Filter() {

	}

	public static double[] filtfilt(double[] b, double[] a, double[] x) {
		// function y = filtfilt(b,a,x)
		// %FILTFILT Zero-phase forward and reverse digital filtering.
		// % Y = FILTFILT(B, A, X) filters the data in vector X with the filter
		// described
		// % by vectors A and B to create the filtered data Y. The filter is
		// described
		// % by the difference equation:
		// %
		// % y(n) = b(1)*x(n) + b(2)*x(n-1) + ... + b(nb+1)*x(n-nb)
		// % - a(2)*y(n-1) - ... - a(na+1)*y(n-na)
		// %
		// %
		// % After filtering in the forward direction, the filtered sequence is
		// then
		// % reversed and run back through the filter; Y is the time reverse of
		// the
		// % output of the second filtering operation. The result has precisely
		// zero
		// % phase distortion and magnitude modified by the square of the
		// filter's
		// % magnitude response. Care is taken to minimize startup and ending
		// % transients by matching initial conditions.
		// %
		// % The length of the input x must be more than three times
		// % the filter order, defined as max(length(b)-1,length(a)-1).
		// %
		// % Note that FILTFILT should not be used with differentiator and
		// Hilbert
		// FIR
		// % filters, since the operation of these filters depends heavily on
		// their
		// % phase response.
		// %
		// % See also FILTER.
		//
		// % References:
		// % [1] Sanjit K. Mitra, Digital Signal Processing, 2nd ed.,
		// McGraw-Hill,
		// 2001
		// % [2] Fredrik Gustafsson, Determining the initial states in
		// forward-backward
		// % filtering, IEEE Transactions on Signal Processing, pp. 988--992,
		// April
		// 1996,
		// % Volume 44, Issue 4
		//
		// % Author(s): L. Shure, 5-17-88
		// % revised by T. Krauss, 1-21-94
		// % Initial Conditions: Fredrik Gustafsson
		// % Copyright 1988-2002 The MathWorks, Inc.
		// % $Revision: 1.7 $ $Date: 2002/03/28 17:27:55 $
		//
		// error(nargchk(3,3,nargin))
		// if (isempty(b)|isempty(a)|isempty(x))
		// y = [];
		// return
		// end
		if (b.length == 0 || a.length == 0 || x.length == 0) {
			return new double[0];
		}

		// [m,n] = size(x);
		// if (n>1)&(m>1)
		// y = x;
		// for i=1:n % loop over columns
		// y(:,i) = filtfilt(b,a,x(:,i));
		// end
		// return
		// % error('Only works for vector input.')
		// end
		// if m==1
		// x = x(:); % convert row to column
		// end
		// len = size(x,1); % length of input

		int len = x.length;

		// b = b(:).';
		// a = a(:).';
		// nb = length(b);
		// na = length(a);
		// nfilt = max(nb,na);
		int nb = b.length;
		int na = a.length;
		int nfilt = na > nb ? na : nb;

		//
		// nfact = 3*(nfilt-1); % length of edge transients
		int nfact = 3 * (nfilt - 1);

		//
		// if (len<=nfact), % input data too short!
		// error('Data must have length more than 3 times filter order.');
		// end
		if (len <= nfact) {
			throw new Error(
					"Data must have length more than 3 times filter order. "
							+ len + " " + nfact);
		}
		//
		// % set up filter's initial conditions to remove dc offset problems at
		// the
		// % beginning and end of the sequence
		// if nb < nfilt, b(nfilt)=0; end % zero-pad if necessary
		// if na < nfilt, a(nfilt)=0; end

		if (nb < nfilt) {
			double[] btmp = b;
			b = new double[nfilt];
			for (int i = 0; i < btmp.length; i++)
				b[i] = btmp[i];
			for (int i = btmp.length; i < nfilt; i++)
				b[i] = 0;
		} else if (na < nfilt) {
			double[] atmp = a;
			a = new double[nfilt];
			for (int i = 0; i < atmp.length; i++)
				a[i] = atmp[i];
			for (int i = atmp.length; i < nfilt; i++)
				a[i] = 0;
		}

		// % use sparse matrix to solve system of linear equations for initial
		// conditions
		// % zi are the steady-state states of the filter b(z)/a(z) in the
		// state-space
		// % implementation of the 'filter' command.
		// rows = [1:nfilt-1 2:nfilt-1 1:nfilt-2];
		// cols = [ones(1,nfilt-1) 2:nfilt-1 2:nfilt-1];
		// data = [1+a(2) a(3:nfilt) ones(1,nfilt-2) -ones(1,nfilt-2)];
		// sp = sparse(rows,cols,data);
		// zi = sp \ ( b(2:nfilt).' - a(2:nfilt).'*b(1) );
		// % non-sparse:
		// % zi = ( eye(nfilt-1) - [-a(2:nfilt).' [eye(nfilt-2);
		// zeros(1,nfilt-2)]]
		// ) \ ...
		// % ( b(2:nfilt).' - a(2:nfilt).'*b(1) );

		double atmp[][] = new double[nfilt - 1][nfilt - 1];
		double btmp[][] = new double[nfilt - 1][1];
		// zi = ( eye(nfilt-1) - [-a(2:nfilt).' [eye(nfilt-2);
		// zeros(1,nfilt-2)]] )
		for (int i = 0; i < nfilt - 1; i++) {
			atmp[i][0] = eye(i, 0) + a[i + 1];
		}
		for (int i = 0; i < nfilt - 2; i++) {
			for (int j = 1; j < nfilt - 1; j++) {
				atmp[i][j] = eye(i, j) - eye(i, j - 1);
			}
		}
		for (int j = 1; j < nfilt - 1; j++) {
			atmp[nfilt - 2][j] = eye(nfilt - 2, j);
		}

		// \ ( b(2:nfilt).' - a(2:nfilt).'*b(1) );
		for (int i = 0; i < nfilt - 1; i++) {
			btmp[i][0] = b[i + 1] - a[i + 1] * b[0];
		}

		Position posi = new Position();

		Matrix A = posi.new Matrix(atmp);
		Matrix B = posi.new Matrix(btmp);
		Matrix X = A.solve(B);
		double zi[] = new double[X.getRowDimension()];
		for (int i = 0; i < zi.length; i++) {
			zi[i] = X.get(i, 0);
		}

		// % Extrapolate beginning and end of data sequence using a "reflection
		// % method". Slopes of original and extrapolated sequences match at
		// % the end points.
		// % This reduces end effects.
		// y = [2*x(1)-x((nfact+1):-1:2);x;2*x(len)-x((len-1):-1:len-nfact)];

		double y[] = new double[nfact + len + nfact];

		// 2*x(1)-x((nfact+1):-1:2)
		// x
		// 2*x(len)-x((len-1):-1:len-nfact)

		int k = 0;
		for (int i = nfact; i >= 1; i--) {
			y[k++] = 2 * x[0] - x[i];
		}
		for (int i = 0; i < x.length; i++) {
			y[k++] = x[i];
		}
		for (int i = len - 2; i >= len - nfact - 1; i--) {
			y[k++] = 2 * x[len - 1] - x[i];
		}

		// Matrix mmm = new Matrix(y);
		// mmm.print(3, 5);
		// for (int i = 0; i < y.length; i++) System.out.println (y[i]);
		// System.out.println();

		//
		// % filter, reverse data, filter again, and reverse data again
		// y = filter(b,a,y,[zi*y(1)]);

		double zitmp[] = new double[zi.length];
		for (int i = 0; i < zi.length; i++) {
			zitmp[i] = zi[i] * y[0];
		}
		y = filterZ(b, a, y, zitmp);

		// y = y(length(y):-1:1);
		reverseLLL(y);

		// y = filter(b,a,y,[zi*y(1)]);

		for (int i = 0; i < zi.length; i++) {
			zitmp[i] = zi[i] * y[0];
		}
		y = filterZ(b, a, y, zitmp);

		// y = y(length(y):-1:1);
		reverseLLL(y);

		// % remove extrapolated pieces of y
		// y([1:nfact len+nfact+(1:nfact)]) = [];

		double[] ytmp = y;
		y = new double[len];
		for (int i = 0; i < y.length; i++) {
			y[i] = ytmp[i + nfact];
		}
		//
		// if m == 1
		// y = y.'; % convert back to row if necessary
		// end

		return y;
	}

	public static final double[] filterZ(double b[], double a[], double x[],
			double zi[]) {
		double y[] = new double[x.length];
		return filterZ(b, a, x, zi, y);
	}

	public static final double[] filterZ(double b[], double a[], double x[],
			double zi[], double y[]) {

		if (b.length < a.length) {
			double btmp[] = b;
			b = new double[a.length];
			for (int i = 0; i < btmp.length; i++) {
				b[i] = btmp[i];
			}
			for (int i = btmp.length; i < b.length; i++) {
				b[i] = 0;
			}
		} else if (b.length > a.length) {
			double atmp[] = a;
			a = new double[b.length];
			for (int i = 0; i < atmp.length; i++) {
				a[i] = atmp[i];
			}
			for (int i = atmp.length; i < a.length; i++) {
				a[i] = 0;
			}
		}

		for (int i = 0; i < b.length; i++) {
			y[i] = 0;
			for (int j = i; j >= 0; j--) {
				y[i] += b[i - j] * x[j];
			}
		}

		for (int i = 0; i < a.length - 1; i++) {
			for (int j = i - 1; j >= 0; j--) {
				y[i] -= a[i - j] * y[j];
			}
			y[i] /= a[0];
			y[i] += zi[i];
		}
		for (int j = a.length - 2; j >= 0; j--) {
			y[a.length - 1] -= a[a.length - 1 - j] * y[j];
		}
		y[a.length - 1] /= a[0];

		for (int i = b.length; i < x.length; i++) {
			y[i] = 0;
			for (int j = i; j > i - b.length; j--) {
				y[i] += b[i - j] * x[j];
			}
		}
		for (int i = a.length; i < x.length; i++) {
			for (int j = i - 1; j > i - a.length; j--) {
				y[i] -= a[i - j] * y[j];
			}
			y[i] /= a[0];
		}
		return y;
	}

	public static int eye(int i, int j) {
		return (i == j) ? 1 : 0;
	}

	public static double[] reverseLLL(double y[]) {
		for (int i = 0; i < y.length / 2; i++) {
			double tmp = y[y.length - 1 - i];
			y[y.length - 1 - i] = y[i];
			y[i] = tmp;
		}
		return y;
	}
}
public class IIR {

    public IIR (int order, float[] an, float[] bn) {
        this.order = order;

        this.an = an;
        this.bn = bn;

        this.xn = new float[order + 1];
        for (int i = 0; i < order + 1; ++i) {
            xn[i] = 0;
        }

        this.yn = new float[order + 1];
        for (int i = 0; i < order + 1; ++i) {
            yn[i] = 0;
        }
    }

    public void clear() {
        for (int i = 0; i < order + 1; ++i) {
            xn[i] = 0;
        }

        this.yn = new float[order + 1];
        for (int i = 0; i < order + 1; ++i) {
            yn[i] = 0;
        }
    }

    public float Filter(float xi) {
        for (int i = 0; i < order; ++i) {
            xn[i + 1] = xn[i];
            yn[i + 1] = yn[i];
        }

        xn[0] = xi;
        yn[0] = 0.0f;

        for (int i = 0; i < order + 1; ++i) {
            yn[0] += xn[i] * bn[i] - yn[i] * an[i];
        }

        return yn[0];
    }

    private int order;
    private float[] an;
    private float[] bn;

    private float[] xn;
    private float[] yn;
}
// package Jama;

   /** LU Decomposition.
   <P>
   For an m-by-n matrix A with m >= n, the LU decomposition is an m-by-n
   unit lower triangular matrix L, an n-by-n upper triangular matrix U,
   and a permutation vector piv of length m so that A(piv,:) = L*U.
   If m < n, then L is m-by-m and U is m-by-n.
   <P>
   The LU decompostion with pivoting always exists, even if the matrix is
   singular, so the constructor will never fail.  The primary use of the
   LU decomposition is in the solution of square systems of simultaneous
   linear equations.  This will fail if isNonsingular() returns false.
   */

public class LUDecomposition implements java.io.Serializable {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of decomposition.
   @serial internal array storage.
   */
   private double[][] LU;

   /** Row and column dimensions, and pivot sign.
   @serial column dimension.
   @serial row dimension.
   @serial pivot sign.
   */
   private int m, n, pivsign; 

   /** Internal storage of pivot vector.
   @serial pivot vector.
   */
   private int[] piv;

/* ------------------------
   Constructor
 * ------------------------ */

   /** LU Decomposition
       Structure to access L, U and piv.
   @param  A Rectangular matrix
   */

   public LUDecomposition (Matrix A) {

   // Use a "left-looking", dot-product, Crout/Doolittle algorithm.

      LU = A.getArrayCopy();
      m = A.getRowDimension();
      n = A.getColumnDimension();
      piv = new int[m];
      for (int i = 0; i < m; i++) {
         piv[i] = i;
      }
      pivsign = 1;
      double[] LUrowi;
      double[] LUcolj = new double[m];

      // Outer loop.

      for (int j = 0; j < n; j++) {

         // Make a copy of the j-th column to localize references.

         for (int i = 0; i < m; i++) {
            LUcolj[i] = LU[i][j];
         }

         // Apply previous transformations.

         for (int i = 0; i < m; i++) {
            LUrowi = LU[i];

            // Most of the time is spent in the following dot product.

            int kmax = Math.min(i,j);
            double s = 0.0f;
            for (int k = 0; k < kmax; k++) {
               s += LUrowi[k]*LUcolj[k];
            }

            LUrowi[j] = LUcolj[i] -= s;
         }
   
         // Find pivot and exchange if necessary.

         int p = j;
         for (int i = j+1; i < m; i++) {
            if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p])) {
               p = i;
            }
         }
         if (p != j) {
            for (int k = 0; k < n; k++) {
               double t = LU[p][k]; LU[p][k] = LU[j][k]; LU[j][k] = t;
            }
            int k = piv[p]; piv[p] = piv[j]; piv[j] = k;
            pivsign = -pivsign;
         }

         // Compute multipliers.
         
         if (j < m & LU[j][j] != 0.0f) {
            for (int i = j+1; i < m; i++) {
               LU[i][j] /= LU[j][j];
            }
         }
      }
   }

/* ------------------------
   Temporary, experimental code.
   ------------------------ *\

   \** LU Decomposition, computed by Gaussian elimination.
   <P>
   This constructor computes L and U with the "daxpy"-based elimination
   algorithm used in LINPACK and MATLAB.  In Java, we suspect the dot-product,
   Crout algorithm will be faster.  We have temporarily included this
   constructor until timing experiments confirm this suspicion.
   <P>
   @param  A             Rectangular matrix
   @param  linpackflag   Use Gaussian elimination.  Actual value ignored.
   @return               Structure to access L, U and piv.
   *\

   public LUDecomposition (Matrix A, int linpackflag) {
      // Initialize.
      LU = A.getArrayCopy();
      m = A.getRowDimension();
      n = A.getColumnDimension();
      piv = new int[m];
      for (int i = 0; i < m; i++) {
         piv[i] = i;
      }
      pivsign = 1;
      // Main loop.
      for (int k = 0; k < n; k++) {
         // Find pivot.
         int p = k;
         for (int i = k+1; i < m; i++) {
            if (Math.abs(LU[i][k]) > Math.abs(LU[p][k])) {
               p = i;
            }
         }
         // Exchange if necessary.
         if (p != k) {
            for (int j = 0; j < n; j++) {
               double t = LU[p][j]; LU[p][j] = LU[k][j]; LU[k][j] = t;
            }
            int t = piv[p]; piv[p] = piv[k]; piv[k] = t;
            pivsign = -pivsign;
         }
         // Compute multipliers and eliminate k-th column.
         if (LU[k][k] != 0.0) {
            for (int i = k+1; i < m; i++) {
               LU[i][k] /= LU[k][k];
               for (int j = k+1; j < n; j++) {
                  LU[i][j] -= LU[i][k]*LU[k][j];
               }
            }
         }
      }
   }

\* ------------------------
   End of temporary code.
 * ------------------------ */

/* ------------------------
   Public Methods
 * ------------------------ */

   /** Is the matrix nonsingular?
   @return     true if U, and hence A, is nonsingular.
   */

   public boolean isNonsingular () {
      for (int j = 0; j < n; j++) {
         if (LU[j][j] == 0)
            return false;
      }
      return true;
   }

   /** Return lower triangular factor
   @return     L
   */

   public Matrix getL () {
      Matrix X = new Matrix(m,n);
      double[][] L = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            if (i > j) {
               L[i][j] = LU[i][j];
            } else if (i == j) {
               L[i][j] = 1.0f;
            } else {
               L[i][j] = 0.0f;
            }
         }
      }
      return X;
   }

   /** Return upper triangular factor
   @return     U
   */

   public Matrix getU () {
      Matrix X = new Matrix(n,n);
      double[][] U = X.getArray();
      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            if (i <= j) {
               U[i][j] = LU[i][j];
            } else {
               U[i][j] = 0.0f;
            }
         }
      }
      return X;
   }

   /** Return pivot permutation vector
   @return     piv
   */

   public int[] getPivot () {
      int[] p = new int[m];
      for (int i = 0; i < m; i++) {
         p[i] = piv[i];
      }
      return p;
   }

   /** Return pivot permutation vector as a one-dimensional double array
   @return     (double) piv
   */

   public double[] getDoublePivot () {
      double[] vals = new double[m];
      for (int i = 0; i < m; i++) {
         vals[i] = (double) piv[i];
      }
      return vals;
   }

   /** Determinant
   @return     det(A)
   @exception  IllegalArgumentException  Matrix must be square
   */

   public double det () {
      if (m != n) {
         throw new IllegalArgumentException("Matrix must be square.");
      }
      double d = (double) pivsign;
      for (int j = 0; j < n; j++) {
         d *= LU[j][j];
      }
      return d;
   }

   /** Solve A*X = B
   @param  B   A Matrix with as many rows as A and any number of columns.
   @return     X so that L*U*X = B(piv,:)
   @exception  IllegalArgumentException Matrix row dimensions must agree.
   @exception  RuntimeException  Matrix is singular.
   */

   public Matrix solve (Matrix B) {
      if (B.getRowDimension() != m) {
         throw new IllegalArgumentException("Matrix row dimensions must agree.");
      }
      if (!this.isNonsingular()) {
         throw new RuntimeException("Matrix is singular.");
      }

      // Copy right hand side with pivoting
      int nx = B.getColumnDimension();
      Matrix Xmat = B.getMatrix(piv,0,nx-1);
      double[][] X = Xmat.getArray();

      // Solve L*Y = B(piv,:)
      for (int k = 0; k < n; k++) {
         for (int i = k+1; i < n; i++) {
            for (int j = 0; j < nx; j++) {
               X[i][j] -= X[k][j]*LU[i][k];
            }
         }
      }
      // Solve U*X = Y;
      for (int k = n-1; k >= 0; k--) {
         for (int j = 0; j < nx; j++) {
            X[k][j] /= LU[k][k];
         }
         for (int i = 0; i < k; i++) {
            for (int j = 0; j < nx; j++) {
               X[i][j] -= X[k][j]*LU[i][k];
            }
         }
      }
      return Xmat;
   }
  private static final long serialVersionUID = 1;
}
// package Jama.util;

public static class Maths {

   /** sqrt(a^2 + b^2) without under/overflow. **/

   public static double hypot(double a, double b) {
      double r;
      if (Math.abs(a) > Math.abs(b)) {
         r = b/a;
         r = Math.abs(a)*Math.sqrt(1+r*r);
      } else if (b != 0) {
         r = a/b;
         r = Math.abs(b)*Math.sqrt(1+r*r);
      } else {
         r = 0.0f;
      }
      return r;
   }
}

/**
   Jama = Java Matrix class.
<P>
   The Java Matrix Class provides the fundamental operations of numerical
   linear algebra.  Various constructors create Matrices from two dimensional
   arrays of double precision floating point numbers.  Various "gets" and
   "sets" provide access to submatrices and matrix elements.  Several methods 
   implement basic matrix arithmetic, including matrix addition and
   multiplication, matrix norms, and element-by-element array operations.
   Methods for reading and printing matrices are also included.  All the
   operations in this version of the Matrix Class involve real matrices.
   Complex matrices may be handled in a future version.
<P>
   Five fundamental matrix decompositions, which consist of pairs or triples
   of matrices, permutation vectors, and the like, produce results in five
   decomposition classes.  These decompositions are accessed by the Matrix
   class to compute solutions of simultaneous linear equations, determinants,
   inverses and other matrix functions.  The five decompositions are:
<P><UL>
   <LI>Cholesky Decomposition of symmetric, positive definite matrices.
   <LI>LU Decomposition of rectangular matrices.
   <LI>QR Decomposition of rectangular matrices.
   <LI>Singular Value Decomposition of rectangular matrices.
   <LI>Eigenvalue Decomposition of both symmetric and nonsymmetric square matrices.
</UL>
<DL>
<DT><B>Example of use:</B></DT>
<P>
<DD>Solve a linear system A x = b and compute the residual norm, ||b - A x||.
<P><PRE>
      double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
      Matrix A = new Matrix(vals);
      Matrix b = Matrix.random(3,1);
      Matrix x = A.solve(b);
      Matrix r = A.times(x).minus(b);
      double rnorm = r.normInf();
</PRE></DD>
</DL>

@author The MathWorks, Inc. and the National Institute of Standards and Technology.
@version 5 August 1998
*/

public class Matrix {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of elements.
   @serial internal array storage.
   */
   private double[][] A;

   /** Row and column dimensions.
   @serial row dimension.
   @serial column dimension.
   */
   private int m, n;

/* ------------------------
   Constructors
 * ------------------------ */

   /** Construct an m-by-n matrix of zeros. 
   @param m    Number of rows.
   @param n    Number of colums.
   */

   public Matrix (int m, int n) {
      this.m = m;
      this.n = n;
      A = new double[m][n];
   }

   /** Construct an m-by-n constant matrix.
   @param m    Number of rows.
   @param n    Number of colums.
   @param s    Fill the matrix with this scalar value.
   */

   public Matrix (int m, int n, double s) {
      this.m = m;
      this.n = n;
      A = new double[m][n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = s;
         }
      }
   }

   /** Construct a matrix from a 2-D array.
   @param A    Two-dimensional array of doubles.
   @exception  IllegalArgumentException All rows must have the same length
   @see        #constructWithCopy
   */

   public Matrix (double[][] A) {
      m = A.length;
      n = A[0].length;
      for (int i = 0; i < m; i++) {
         if (A[i].length != n) {
            throw new IllegalArgumentException("All rows must have the same length.");
         }
      }
      this.A = A;
   }

   /** Construct a matrix quickly without checking arguments.
   @param A    Two-dimensional array of doubles.
   @param m    Number of rows.
   @param n    Number of colums.
   */

   public Matrix (double[][] A, int m, int n) {
      this.A = A;
      this.m = m;
      this.n = n;
   }

   /** Construct a matrix from a one-dimensional packed array
   @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
   @param m    Number of rows.
   @exception  IllegalArgumentException Array length must be a multiple of m.
   */

   public Matrix (double vals[], int m) {
      this.m = m;
      n = (m != 0 ? vals.length/m : 0);
      if (m*n != vals.length) {
         throw new IllegalArgumentException("Array length must be a multiple of m.");
      }
      A = new double[m][n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = vals[i+j*m];
         }
      }
   }

/* ------------------------
   Public Methods
 * ------------------------ */

   /** Construct a matrix from a copy of a 2-D array.
   @param A    Two-dimensional array of doubles.
   @exception  IllegalArgumentException All rows must have the same length
   */

   public Matrix constructWithCopy(double[][] A) {
      int m = A.length;
      int n = A[0].length;
      Matrix X = new Matrix(m,n);
      double[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         if (A[i].length != n) {
            throw new IllegalArgumentException
               ("All rows must have the same length.");
         }
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j];
         }
      }
      return X;
   }

   /** Make a deep copy of a matrix
   */

   public Matrix copy () {
      Matrix X = new Matrix(m,n);
      double[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j];
         }
      }
      return X;
   }

   /** Clone the Matrix object.
   */

   public Object clone () {
      return this.copy();
   }

   /** Access the internal two-dimensional array.
   @return     Pointer to the two-dimensional array of matrix elements.
   */

   public double[][] getArray () {
      return A;
   }

   /** Copy the internal two-dimensional array.
   @return     Two-dimensional array copy of matrix elements.
   */

   public double[][] getArrayCopy () {
      double[][] C = new double[m][n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j];
         }
      }
      return C;
   }

   /** Make a one-dimensional column packed copy of the internal array.
   @return     Matrix elements packed in a one-dimensional array by columns.
   */

   public double[] getColumnPackedCopy () {
      double[] vals = new double[m*n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            vals[i+j*m] = A[i][j];
         }
      }
      return vals;
   }

   /** Make a one-dimensional row packed copy of the internal array.
   @return     Matrix elements packed in a one-dimensional array by rows.
   */

   public double[] getRowPackedCopy () {
      double[] vals = new double[m*n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            vals[i*n+j] = A[i][j];
         }
      }
      return vals;
   }

   /** Get row dimension.
   @return     m, the number of rows.
   */

   public int getRowDimension () {
      return m;
   }

   /** Get column dimension.
   @return     n, the number of columns.
   */

   public int getColumnDimension () {
      return n;
   }

   /** Get a single element.
   @param i    Row index.
   @param j    Column index.
   @return     A(i,j)
   @exception  ArrayIndexOutOfBoundsException
   */

   public double get (int i, int j) {
      return A[i][j];
   }

   /** Get a submatrix.
   @param i0   Initial row index
   @param i1   Final row index
   @param j0   Initial column index
   @param j1   Final column index
   @return     A(i0:i1,j0:j1)
   @exception  ArrayIndexOutOfBoundsException Submatrix indices
   */

   public Matrix getMatrix (int i0, int i1, int j0, int j1) {
      Matrix X = new Matrix(i1-i0+1,j1-j0+1);
      double[][] B = X.getArray();
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = j0; j <= j1; j++) {
               B[i-i0][j-j0] = A[i][j];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }

   /** Get a submatrix.
   @param r    Array of row indices.
   @param c    Array of column indices.
   @return     A(r(:),c(:))
   @exception  ArrayIndexOutOfBoundsException Submatrix indices
   */

   public Matrix getMatrix (int[] r, int[] c) {
      Matrix X = new Matrix(r.length,c.length);
      double[][] B = X.getArray();
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < c.length; j++) {
               B[i][j] = A[r[i]][c[j]];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }

   /** Get a submatrix.
   @param i0   Initial row index
   @param i1   Final row index
   @param c    Array of column indices.
   @return     A(i0:i1,c(:))
   @exception  ArrayIndexOutOfBoundsException Submatrix indices
   */

   public Matrix getMatrix (int i0, int i1, int[] c) {
      Matrix X = new Matrix(i1-i0+1,c.length);
      double[][] B = X.getArray();
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = 0; j < c.length; j++) {
               B[i-i0][j] = A[i][c[j]];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }

   /** Get a submatrix.
   @param r    Array of row indices.
   @param j0   Initial column index
   @param j1   Final column index
   @return     A(r(:),j0:j1)
   @exception  ArrayIndexOutOfBoundsException Submatrix indices
   */

   public Matrix getMatrix (int[] r, int j0, int j1) {
      Matrix X = new Matrix(r.length,j1-j0+1);
      double[][] B = X.getArray();
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = j0; j <= j1; j++) {
               B[i][j-j0] = A[r[i]][j];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }

   /** Set a single element.
   @param i    Row index.
   @param j    Column index.
   @param s    A(i,j).
   @exception  ArrayIndexOutOfBoundsException
   */

   public void set (int i, int j, double s) {
      A[i][j] = s;
   }

   /** Set a submatrix.
   @param i0   Initial row index
   @param i1   Final row index
   @param j0   Initial column index
   @param j1   Final column index
   @param X    A(i0:i1,j0:j1)
   @exception  ArrayIndexOutOfBoundsException Submatrix indices
   */

   public void setMatrix (int i0, int i1, int j0, int j1, Matrix X) {
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = j0; j <= j1; j++) {
               A[i][j] = X.get(i-i0,j-j0);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   /** Set a submatrix.
   @param r    Array of row indices.
   @param c    Array of column indices.
   @param X    A(r(:),c(:))
   @exception  ArrayIndexOutOfBoundsException Submatrix indices
   */

   public void setMatrix (int[] r, int[] c, Matrix X) {
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < c.length; j++) {
               A[r[i]][c[j]] = X.get(i,j);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   /** Set a submatrix.
   @param r    Array of row indices.
   @param j0   Initial column index
   @param j1   Final column index
   @param X    A(r(:),j0:j1)
   @exception  ArrayIndexOutOfBoundsException Submatrix indices
   */

   public void setMatrix (int[] r, int j0, int j1, Matrix X) {
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = j0; j <= j1; j++) {
               A[r[i]][j] = X.get(i,j-j0);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   /** Set a submatrix.
   @param i0   Initial row index
   @param i1   Final row index
   @param c    Array of column indices.
   @param X    A(i0:i1,c(:))
   @exception  ArrayIndexOutOfBoundsException Submatrix indices
   */

   public void setMatrix (int i0, int i1, int[] c, Matrix X) {
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = 0; j < c.length; j++) {
               A[i][c[j]] = X.get(i-i0,j);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   /** Matrix transpose.
   @return    A'
   */

   public Matrix transpose () {
      Matrix X = new Matrix(n,m);
      double[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[j][i] = A[i][j];
         }
      }
      return X;
   }

   /** One norm
   @return    maximum column sum.
   */

   public double norm1 () {
      double f = 0;
      for (int j = 0; j < n; j++) {
         double s = 0;
         for (int i = 0; i < m; i++) {
            s += Math.abs(A[i][j]);
         }
         f = Math.max(f,s);
      }
      return f;
   }

   /** Two norm
   @return    maximum singular value.
   */

   // public double norm2 () {
   //    return (new SingularValueDecomposition(this).norm2());
   // }

   /** Infinity norm
   @return    maximum row sum.
   */

   public double normInf () {
      double f = 0;
      for (int i = 0; i < m; i++) {
         double s = 0;
         for (int j = 0; j < n; j++) {
            s += Math.abs(A[i][j]);
         }
         f = Math.max(f,s);
      }
      return f;
   }

   /** Frobenius norm
   @return    sqrt of sum of squares of all elements.
   */

   // public double normF () {
   //    double f = 0;
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          f = Maths.hypot(f,A[i][j]);
   //       }
   //    }
   //    return f;
   // }

   /**  Unary minus
   @return    -A
   */

   public Matrix uminus () {
      Matrix X = new Matrix(m,n);
      double[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = -A[i][j];
         }
      }
      return X;
   }

   /** C = A + B
   @param B    another matrix
   @return     A + B
   */

   // public Matrix plus (Matrix B) {
   //    checkMatrixDimensions(B);
   //    Matrix X = new Matrix(m,n);
   //    double[][] C = X.getArray();
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          C[i][j] = A[i][j] + B.A[i][j];
   //       }
   //    }
   //    return X;
   // }

   /** A = A + B
   @param B    another matrix
   @return     A + B
   */

   // public Matrix plusEquals (Matrix B) {
   //    checkMatrixDimensions(B);
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          A[i][j] = A[i][j] + B.A[i][j];
   //       }
   //    }
   //    return this;
   // }

   /** C = A - B
   @param B    another matrix
   @return     A - B
   */

   // public Matrix minus (Matrix B) {
   //    checkMatrixDimensions(B);
   //    Matrix X = new Matrix(m,n);
   //    double[][] C = X.getArray();
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          C[i][j] = A[i][j] - B.A[i][j];
   //       }
   //    }
   //    return X;
   // }

   /** A = A - B
   @param B    another matrix
   @return     A - B
   */

   // public Matrix minusEquals (Matrix B) {
   //    checkMatrixDimensions(B);
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          A[i][j] = A[i][j] - B.A[i][j];
   //       }
   //    }
   //    return this;
   // }

   /** Element-by-element multiplication, C = A.*B
   @param B    another matrix
   @return     A.*B
   */

   // public Matrix arrayTimes (Matrix B) {
   //    checkMatrixDimensions(B);
   //    Matrix X = new Matrix(m,n);
   //    double[][] C = X.getArray();
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          C[i][j] = A[i][j] * B.A[i][j];
   //       }
   //    }
   //    return X;
   // }

   /** Element-by-element multiplication in place, A = A.*B
   @param B    another matrix
   @return     A.*B
   */

   // public Matrix arrayTimesEquals (Matrix B) {
   //    checkMatrixDimensions(B);
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          A[i][j] = A[i][j] * B.A[i][j];
   //       }
   //    }
   //    return this;
   // }

   /** Element-by-element right division, C = A./B
   @param B    another matrix
   @return     A./B
   */

   // public Matrix arrayRightDivide (Matrix B) {
   //    checkMatrixDimensions(B);
   //    Matrix X = new Matrix(m,n);
   //    double[][] C = X.getArray();
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          C[i][j] = A[i][j] / B.A[i][j];
   //       }
   //    }
   //    return X;
   // }

   /** Element-by-element right division in place, A = A./B
   @param B    another matrix
   @return     A./B
   */

   // public Matrix arrayRightDivideEquals (Matrix B) {
   //    checkMatrixDimensions(B);
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          A[i][j] = A[i][j] / B.A[i][j];
   //       }
   //    }
   //    return this;
   // }

   /** Element-by-element left division, C = A.\B
   @param B    another matrix
   @return     A.\B
   */

   // public Matrix arrayLeftDivide (Matrix B) {
   //    checkMatrixDimensions(B);
   //    Matrix X = new Matrix(m,n);
   //    double[][] C = X.getArray();
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          C[i][j] = B.A[i][j] / A[i][j];
   //       }
   //    }
   //    return X;
   // }

   /** Element-by-element left division in place, A = A.\B
   @param B    another matrix
   @return     A.\B
   */

   // public Matrix arrayLeftDivideEquals (Matrix B) {
   //    checkMatrixDimensions(B);
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          A[i][j] = B.A[i][j] / A[i][j];
   //       }
   //    }
   //    return this;
   // }

   /** Multiply a matrix by a scalar, C = s*A
   @param s    scalar
   @return     s*A
   */

   public Matrix times (double s) {
      Matrix X = new Matrix(m,n);
      double[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = s*A[i][j];
         }
      }
      return X;
   }

   /** Multiply a matrix by a scalar in place, A = s*A
   @param s    scalar
   @return     replace A by s*A
   */

   public Matrix timesEquals (double s) {
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = s*A[i][j];
         }
      }
      return this;
   }

   /** Linear algebraic matrix multiplication, A * B
   @param B    another matrix
   @return     Matrix product, A * B
   @exception  IllegalArgumentException Matrix inner dimensions must agree.
   */

   public Matrix times (Matrix B) {
      if (B.m != n) {
         throw new IllegalArgumentException("Matrix inner dimensions must agree.");
      }
      Matrix X = new Matrix(m,B.n);
      double[][] C = X.getArray();
      double[] Bcolj = new double[n];
      for (int j = 0; j < B.n; j++) {
         for (int k = 0; k < n; k++) {
            Bcolj[k] = B.A[k][j];
         }
         for (int i = 0; i < m; i++) {
            double[] Arowi = A[i];
            double s = 0;
            for (int k = 0; k < n; k++) {
               s += Arowi[k]*Bcolj[k];
            }
            C[i][j] = s;
         }
      }
      return X;
   }

   /** LU Decomposition
   @return     LUDecomposition
   @see LUDecomposition
   */

   public LUDecomposition lu () {
      return new LUDecomposition(this);
   }

   /** QR Decomposition
   @return     QRDecomposition
   @see QRDecomposition
   */

   public QRDecomposition qr () {
      return new QRDecomposition(this);
   }

   /** Cholesky Decomposition
   @return     CholeskyDecomposition
   @see CholeskyDecomposition
   */

   // public CholeskyDecomposition chol () {
   //    return new CholeskyDecomposition(this);
   // }

   /** Singular Value Decomposition
   @return     SingularValueDecomposition
   @see SingularValueDecomposition
   */

   // public SingularValueDecomposition svd () {
   //    return new SingularValueDecomposition(this);
   // }

   /** Eigenvalue Decomposition
   @return     EigenvalueDecomposition
   @see EigenvalueDecomposition
   */

   // public EigenvalueDecomposition eig () {
   //    return new EigenvalueDecomposition(this);
   // }

   /** Solve A*X = B
   @param B    right hand side
   @return     solution if A is square, least squares solution otherwise
   */

   public Matrix solve (Matrix B) {
      return (m == n ? (new LUDecomposition(this)).solve(B) :
                       (new QRDecomposition(this)).solve(B));
   }

   /** Solve X*A = B, which is also A'*X' = B'
   @param B    right hand side
   @return     solution if A is square, least squares solution otherwise.
   */

   public Matrix solveTranspose (Matrix B) {
      return transpose().solve(B.transpose());
   }

   /** Matrix inverse or pseudoinverse
   @return     inverse(A) if A is square, pseudoinverse otherwise.
   */

   // public Matrix inverse () {
   //    return solve(identity(m,m));
   // }

   /** Matrix determinant
   @return     determinant
   */

   public double det () {
      return new LUDecomposition(this).det();
   }

   /** Matrix rank
   @return     effective numerical rank, obtained from SVD.
   */

   // public int rank () {
   //    return new SingularValueDecomposition(this).rank();
   // }

   /** Matrix condition (2 norm)
   @return     ratio of largest to smallest singular value.
   */

   // public double cond () {
   //    return new SingularValueDecomposition(this).cond();
   // }

   /** Matrix trace.
   @return     sum of the diagonal elements.
   */

   public double trace () {
      double t = 0;
      for (int i = 0; i < Math.min(m,n); i++) {
         t += A[i][i];
      }
      return t;
   }

   /** Generate matrix with random elements
   @param m    Number of rows.
   @param n    Number of colums.
   @return     An m-by-n matrix with uniformly distributed random elements.
   */

   // public static Matrix random (int m, int n) {
   //    Matrix A = new Matrix(m,n);
   //    double[][] X = A.getArray();
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          X[i][j] = Math.random();
   //       }
   //    }
   //    return A;
   // }

   /** Generate identity matrix
   @param m    Number of rows.
   @param n    Number of colums.
   @return     An m-by-n matrix with ones on the diagonal and zeros elsewhere.
   */

   // public static Matrix identity (int m, int n) {
   //    Matrix A = new Matrix(m,n);
   //    double[][] X = A.getArray();
   //    for (int i = 0; i < m; i++) {
   //       for (int j = 0; j < n; j++) {
   //          X[i][j] = (i == j ? 1.0 : 0.0);
   //       }
   //    }
   //    return A;
   // }
}
// package Jama;
// import Jama.util.*;

/** QR Decomposition.
<P>
   For an m-by-n matrix A with m >= n, the QR decomposition is an m-by-n
   orthogonal matrix Q and an n-by-n upper triangular matrix R so that
   A = Q*R.
<P>
   The QR decompostion always exists, even if the matrix does not have
   full rank, so the constructor will never fail.  The primary use of the
   QR decomposition is in the least squares solution of nonsquare systems
   of simultaneous linear equations.  This will fail if isFullRank()
   returns false.
*/

public class QRDecomposition implements java.io.Serializable {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of decomposition.
   @serial internal array storage.
   */
   private double[][] QR;

   /** Row and column dimensions.
   @serial column dimension.
   @serial row dimension.
   */
   private int m, n;

   /** Array for internal storage of diagonal of R.
   @serial diagonal of R.
   */
   private double[] Rdiag;

/* ------------------------
   Constructor
 * ------------------------ */

   /** QR Decomposition, computed by Householder reflections.
       Structure to access R and the Householder vectors and compute Q.
   @param A    Rectangular matrix
   */

   public QRDecomposition (Matrix A) {
      // Initialize.
      QR = A.getArrayCopy();
      m = A.getRowDimension();
      n = A.getColumnDimension();
      Rdiag = new double[n];

      // Main loop.
      for (int k = 0; k < n; k++) {
         // Compute 2-norm of k-th column without under/overflow.
         double nrm = 0;
         for (int i = k; i < m; i++) {
            nrm = Maths.hypot(nrm,QR[i][k]);
         }

         if (nrm != 0.0f) {
            // Form k-th Householder vector.
            if (QR[k][k] < 0) {
               nrm = -nrm;
            }
            for (int i = k; i < m; i++) {
               QR[i][k] /= nrm;
            }
            QR[k][k] += 1.0f;

            // Apply transformation to remaining columns.
            for (int j = k+1; j < n; j++) {
               double s = 0.0f; 
               for (int i = k; i < m; i++) {
                  s += QR[i][k]*QR[i][j];
               }
               s = -s/QR[k][k];
               for (int i = k; i < m; i++) {
                  QR[i][j] += s*QR[i][k];
               }
            }
         }
         Rdiag[k] = -nrm;
      }
   }

/* ------------------------
   Public Methods
 * ------------------------ */

   /** Is the matrix full rank?
   @return     true if R, and hence A, has full rank.
   */

   public boolean isFullRank () {
      for (int j = 0; j < n; j++) {
         if (Rdiag[j] == 0)
            return false;
      }
      return true;
   }

   /** Return the Householder vectors
   @return     Lower trapezoidal matrix whose columns define the reflections
   */

   public Matrix getH () {
      Matrix X = new Matrix(m,n);
      double[][] H = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            if (i >= j) {
               H[i][j] = QR[i][j];
            } else {
               H[i][j] = 0.0f;
            }
         }
      }
      return X;
   }

   /** Return the upper triangular factor
   @return     R
   */

   public Matrix getR () {
      Matrix X = new Matrix(n,n);
      double[][] R = X.getArray();
      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            if (i < j) {
               R[i][j] = QR[i][j];
            } else if (i == j) {
               R[i][j] = Rdiag[i];
            } else {
               R[i][j] = 0.0f;
            }
         }
      }
      return X;
   }

   /** Generate and return the (economy-sized) orthogonal factor
   @return     Q
   */

   public Matrix getQ () {
      Matrix X = new Matrix(m,n);
      double[][] Q = X.getArray();
      for (int k = n-1; k >= 0; k--) {
         for (int i = 0; i < m; i++) {
            Q[i][k] = 0.0f;
         }
         Q[k][k] = 1.0f;
         for (int j = k; j < n; j++) {
            if (QR[k][k] != 0) {
               double s = 0.0f;
               for (int i = k; i < m; i++) {
                  s += QR[i][k]*Q[i][j];
               }
               s = -s/QR[k][k];
               for (int i = k; i < m; i++) {
                  Q[i][j] += s*QR[i][k];
               }
            }
         }
      }
      return X;
   }

   /** Least squares solution of A*X = B
   @param B    A Matrix with as many rows as A and any number of columns.
   @return     X that minimizes the two norm of Q*R*X-B.
   @exception  IllegalArgumentException  Matrix row dimensions must agree.
   @exception  RuntimeException  Matrix is rank deficient.
   */

   public Matrix solve (Matrix B) {
      if (B.getRowDimension() != m) {
         throw new IllegalArgumentException("Matrix row dimensions must agree.");
      }
      if (!this.isFullRank()) {
         throw new RuntimeException("Matrix is rank deficient.");
      }
      
      // Copy right hand side
      int nx = B.getColumnDimension();
      double[][] X = B.getArrayCopy();

      // Compute Y = transpose(Q)*B
      for (int k = 0; k < n; k++) {
         for (int j = 0; j < nx; j++) {
            double s = 0.0f; 
            for (int i = k; i < m; i++) {
               s += QR[i][k]*X[i][j];
            }
            s = -s/QR[k][k];
            for (int i = k; i < m; i++) {
               X[i][j] += s*QR[i][k];
            }
         }
      }
      // Solve R*X = Y;
      for (int k = n-1; k >= 0; k--) {
         for (int j = 0; j < nx; j++) {
            X[k][j] /= Rdiag[k];
         }
         for (int i = 0; i < k; i++) {
            for (int j = 0; j < nx; j++) {
               X[i][j] -= X[k][j]*QR[i][k];
            }
         }
      }
      return (new Matrix(X,n,nx).getMatrix(0,n-1,0,nx-1));
   }
  private static final long serialVersionUID = 1;
}
public class Waveform{

    public Waveform (float x, float y, float width, float hight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.hight = hight;

        begin = 0;
        end   = PApplet.parseInt(this.width) - 1 - begin;

        reg = new float[PApplet.parseInt(this.width)];

        INMAX = this.hight / 2.0f;
        INMIN = -INMAX;

        zeroPoint = 0.0f;
    }

    public void add(float inValue) {
        float value = inValue;
        if (value > INMAX) value = INMAX;
        if (value < INMIN) value = INMIN;

        end = begin;
        begin = begin + 1;
        if (begin >= PApplet.parseInt(width)) begin = 0;
        reg[end] = value;
    }

    public void showByPoint() {
        int index = begin;
        showXY();
        noStroke();
        smooth();
        fill(255, 0, 0);
        for (int i = 0; i < PApplet.parseInt(width) - 1; ++i) {
            ellipse(x + i, y + hight / 2 - reg[index] * scale, 2, 2);
            index++;
            if (index >= PApplet.parseInt(width)) index = 0;
        }
        noSmooth();
    }

    public void showByLine() {
        int index = begin;
        int lastIndex = index;
        showXY();
        stroke(255, 0, 0);
        //smooth();
        for (int i = 1; i < PApplet.parseInt(width) - 1; ++i) {
            lastIndex = index;
            index++;
            if (index >= PApplet.parseInt(width)) index = 0;
            line(x + i - 1, y + hight / 2 - reg[lastIndex] * scale, 
                     x + i, y + hight / 2 - reg[index] * scale);
        }
    }

    public void showByPoint(float x, float y) {
        setXY(x, y);
        showByPoint();
    }

    public void showByLine(float x, float y) {
        setXY(x, y);
        showByLine();
    }

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setRange(float max, float min) {
        INMAX = max;
        INMIN = min;

        scale = hight / (INMAX - INMIN);
    }

    private void showXY() {
        stroke(255, 255, 0);
        // show x
        line(x, y + hight / 2 - zeroPoint, x + width, y + hight / 2);
        // show y
        // line(x + width / 2, y, x + width / 2, y + hight);
        noStroke();
    }

    public void setZero(float zeroPoint) {
        this.zeroPoint = zeroPoint;
    }

    private float[] reg;
    private float zeroPoint;

    private int begin;
    private int end;
    // coordinates
    private float x;
    private float y;
    // size
    private float width;
    private float hight;
    // waveform range
    private float INMAX;
    private float INMIN;

    private float scale;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Position" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
