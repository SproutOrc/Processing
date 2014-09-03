void setup(){
    size (800, 600);
    background(125);
}

// time
float t = 0.0;
float h = 0.0;
float s = 600.0;
float hover = 600.0;

void draw(){
    background(125);
    l = h + 0.5 * 10 * t * t;
    if (s >= 600) {
        s = 600;
    } else {
        s = l;
    }
    fill(0, 255, 0);
    noStroke();
    rect (20, s, 10, 5);
}

void mousePressed(){
    if (mouseY < s) {
        h = mouseY;
        t = 0.0;

    }
}

public class point
{
    // point coordinate
    public int[] _height;
    public int[] _hover;
    private int state = 0;
    private final float G = 9.8;
    private final float T = 0.05;

    private final int drop = 0;
    private final int keep = 1;
    private final int up   = 2;

    public point (int N) {
        _height = new int[N];
        _hover  = new int[N];
        for (int i = 0; i < N; i++){
            _height[i] = 0;
            _hover[i]  = 0;
        }
    }

    public void action (int heightFFT, int n) {
        switch (state) {
            if (_height[n] <= heightFFT) {
                _height[n] = heightFFT;
            }
            case drop : 

            break;
            case keep :
                
            break;    
            case up :
                
            break;    
        }        
    }
}
