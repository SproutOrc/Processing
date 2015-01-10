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
        yn[0] = 0.0;

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