public class UI extends PApplet {

    public UI () {
        
    }

    public static setSize(int inSize) {
        size = inSize;
        star = inSize - 1;
        win = new int[inSize];
    }

    public static showPoint (int amplitude) {
        win[star] = amplitude;
        int showPointAxisX = star;
        for (int i = 0; i < size; ++i) {
            ellipse(i, win[showPointAxisX], 2, 2);
            if (showPointAxisX == 499) {
                showPointAxisX = 0;
            } else {
                showPointAxisX = showPointAxisX + 1;
            }
        }
        if (star == 0) {
            star = 499;
        } else {
            star = star - 1;
        }
    }

    private final size;
    private static int star;
    private static int[] win;
}