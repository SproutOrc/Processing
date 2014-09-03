public class IllustrateAPI
{
    public final float T;
    public final float G = 9.8;
    public final int rectWidth;

    public int[] _nowPlace;
    public int[] _margin;
    public float[] _downSpeed;
    public IllustrateAPI (int N, int setWidth) {
        _nowPlace  = new int[N];
        _margin    = new int[N];
        _downSpeed = new float[N];

        // T = 1.0 / frameRate;
        T = 0.6;
        rectWidth = setWidth * 2 / N;

        for (int i = 0; i < N; i++) {
            _nowPlace[i]  = 0;
            _margin[i]    = 0;
            _downSpeed[i] = 0.0;
        }
    }

    public void motion (int margin, int i) {
        _margin[i] = margin;
        // h = H - (v*t + 1/2*g*t^2)
        int h = int(_nowPlace[i] - _downSpeed[i] * T - 4.9 * T  * T);
        _downSpeed[i] = _downSpeed[i] + G * T;
        if (h > _margin[i]) {
            _nowPlace[i] = h;
        } else {
            _nowPlace[i] = _margin[i];
            _downSpeed[i] = 0.0;
        }
    }

    public void show (int i) {
        noStroke();
        pushMatrix();
        translate(0, height);
        fill (35, 145, 216);
        rect (rectWidth * i, 0, rectWidth - 1, -_margin[i]);
        fill (27, 255, 8);
        rect (rectWidth * i, -_nowPlace[i], rectWidth - 1, -2);
        popMatrix();
    }
}

