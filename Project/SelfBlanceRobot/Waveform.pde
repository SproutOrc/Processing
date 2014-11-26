public class Waveform{

    public Waveform (float x, float y, float width, float hight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.hight = hight;

        begin = 0;
        end   = int(this.width) - 1 - begin;

        reg = new float[int(this.width)];

        INMAX = this.hight / 2.0;
        INMIN = -INMAX;
    }

    public void add(float inValue) {
        float value = inValue;
        if (value > INMAX) value = INMAX;
        if (value < INMIN) value = INMIN;

        end = begin;
        begin = begin + 1;
        if (begin >= int(width)) begin = 0;
        reg[end] = value;
    }

    public void showByPoint() {
        int index = begin;
        showXY();
        noStroke();
        smooth();
        fill(255, 0, 0);
        for (int i = 0; i < int(width) - 1; ++i) {
            ellipse(x + i, y + hight / 2 - reg[index] * scale, 2, 2);
            index++;
            if (index >= int(width)) index = 0;
        }
        noSmooth();
    }

    public void showByLine() {
        int index = begin;
        int lastIndex = index;
        showXY();
        stroke(255, 0, 0);
        //smooth();
        for (int i = 1; i < int(width) - 1; ++i) {
            lastIndex = index;
            index++;
            if (index >= int(width)) index = 0;
            line(i - 1, y + hight / 2 - reg[lastIndex] * scale, 
                     i, y + hight / 2 - reg[index] * scale);
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
