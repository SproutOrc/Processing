import processing.serial.*;

//生成串口对象
Serial myPort;

//生成字体对象
PFont myFont;

//生成背景图片
PImage myImage;

//mouse input var
int varMouseX = 0;
int varMouseY = 0;
int mouseInputX = 3;
int mouseInputY = 0;
int decode[][] = {
	{1, 2, 3,  0, 15, 15},
	{4, 5, 6, 10, 12, 14},
	{7, 8, 9, 11, 13, 14}
};

byte[] inBuffer = new byte[26];
int mouseInput = 0;

Calculator1 myCal = new Calculator1();


void setup(){
	size(800, 600);	//生成窗口大小800X600;
	println(Serial.list());	//显示当前串口列表;
	myPort = new Serial(this, "COM3", 115200);	//初始化串口;
	myFont = createFont("Consolas", 16, true);	//设置字体;
	textFont (myFont);

	frameRate(30);	//设置帧数

	myImage = loadImage("calculator.jpg");

	myPort.bufferUntil(0x3c);
}

void draw(){
	image (myImage, 0, 0);
	// line (0, mouseY, 800, mouseY); 
	// println ("mouseY: " + mouseY);
	// line (mouseX, 0, mouseX, 600); 
	// println ("mouseX: " + mouseX);
	int a= 0;
	if (inBuffer[20] == 0x2B || inBuffer[20] == 0x2D
		|| inBuffer[20] == 0x2A || inBuffer[20] == 0x2F) 
	{
		myCal.display ("0", 750, 250, 100);
	} else {
		for (int i = 0; i < 8; i++) {
			a = a * 10 + inBuffer[i + 13] - 48;
		}
		myCal.display (a, 750, 250, 100);
	}
	
	// println(a);
	if (myCal.varEqual == true) {
		textAlign (LEFT, TOP);
		switch (myCal.varOperator) {
			case 10 :
				myCal.display (myCal.varA + "+" + myCal.varB + "=", 55, 45, 50);
			break;	
			case 11 :
				myCal.display (myCal.varA + "-" + myCal.varB + "=", 55, 45, 50);
			break;	
			case 12 :
				myCal.display (myCal.varA + "x" + myCal.varB + "=", 55, 45, 50);
			break;	
			case 13 :
				myCal.display (myCal.varA + "/" + myCal.varB + "=", 55, 45, 50);
			break;	
		}
		textAlign (RIGHT, BOTTOM);
		// myCal.display (a, 750, 250, 100);

	} else if (myCal.varOperator >= 10 && myCal.varOperator <= 13) {
		textAlign (LEFT, TOP);
		switch (myCal.varOperator) {
			case 10 :
				myCal.display (myCal.varA + "+", 55, 45, 50);
			break;	
			case 11 :
				myCal.display (myCal.varA + "-", 55, 45, 50);
			break;	
			case 12 :
				myCal.display (myCal.varA + "x", 55, 45, 50);
			break;	
			case 13 :
				myCal.display (myCal.varA + "/", 55, 45, 50);
			break;	
		}
		textAlign (RIGHT, BOTTOM);
		// myCal.display (a, 750, 250, 100);
	} else if (myCal.varOperator == 0){
		textAlign (RIGHT, BOTTOM);
		// myCal.display (a, 750, 250, 100);
	}
}

public class Process
{
	
	//--------------------------------------
	//  CONSTRUCTOR
	//--------------------------------------
	
	public Process () {
		// expression
	}

	//运算;
	public int add (int summand, int addend) {
		return summand + addend;
	}

	public int sub (int  minuend, int subtrahend) {
		return minuend - subtrahend;
	}

	public int mul (int multiplicand, int multiplier) {
		return multiplicand * multiplier;
	}

	public int div (int dividend, int divisor) {
		return dividend / divisor;
	}
}

public class Calculator1 extends Process
{
	
	//--------------------------------------
	//  CONSTRUCTOR
	public int varA = 0;
	public int varB = 0;
	public int varOperator = 0;
	public boolean varEqual = false;
	public int varResult = 0;
	public boolean flag = false;
	//--------------------------------------
	public Calculator1 () {

	}

	//鼠标点击输入处理;
	public void mouseInputProcess (int mouseInput) {
		if (mouseInput >=0 && mouseInput <= 9) {
			if (flag == false) {
				calculatorClear ();
				flag = true;
			}
			int temp = 0;
			if (varOperator == 0) {
				temp = varA * 10 + mouseInput;
				if (temp <= 99999999) {
					varA = temp;
				}
			} else {
				temp = varB * 10 + mouseInput;
				if (temp <= 99999999) {
					varB = temp;
				}
			}
		} else if (mouseInput >= 10 && mouseInput <= 13) {
			varOperator = mouseInput;
		} else if (mouseInput == 14) {
			varEqual = true;
			flag = false;
			switch (varOperator){
				case 10 :
					varResult = add (varA, varB);
					break;
				case 11 :
					varResult = sub (varA, varB);	
					break;
				case 12 :
					varResult = mul (varA, varB);	
					break;			
				case 13 :
					varResult = div (varA, varB);
					break;	
				default :
					varOperator = 0;
					break;
			}
		} else if (mouseInput == 15) {
			calculatorClear ();
		}
	}

	//清除计算器
	public void calculatorClear() {
		varA = 0;
		varB = 0;
		varOperator = 0;
		varResult = 0;
		varEqual = false;
		flag = false;
	}

	public void display(String disString, int displayX, int displayY, int fontSize) {
		fill (128, 50, 60);
		textSize (fontSize);
		text (disString, displayX, displayY);
		noFill ();
	}

	public void display(int disVar, int displayX, int displayY, int fontSize) {
		fill (128, 50, 60);
		textSize (fontSize);
		text (disVar, displayX, displayY);
		noFill ();
	}
}

void mousePressed () {
	if (mouseX > 45 && mouseX < 765 
		&& mouseY > 250 && mouseY < 580) {
		varMouseX = (int) mouseX;
		varMouseY = (int) mouseY;
		mouseInputX = (varMouseX - 45) / 120;
		mouseInputY = (varMouseY - 250) / 110;
		mouseInput = decode[mouseInputY][mouseInputX];
		myCal.mouseInputProcess (mouseInput);
		println("mouseInputX: " + mouseInputX);
		println("mouseInputY: " + mouseInputY);
		myPort.write ((byte)(mouseInput + 0x30));
		myPort.write ((byte)(mouseInput + 0x30));
		myPort.write ((byte)(mouseInput + 0x30));
		myPort.write ((byte)(mouseInput + 0x30));
		myPort.write ((byte)(mouseInput + 0x30));
		myPort.write ((byte)(mouseInput + 0x30));
		myPort.write ((byte)(mouseInput + 0x30));
		myPort.write ((byte)(mouseInput + 0x30));
	}
}

void serialEvent(Serial myPort) {
	inBuffer = myPort.readBytesUntil (0x3c);
}
