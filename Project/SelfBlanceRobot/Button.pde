public class Button {

    public Button (int x, int y, int width, int hight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.hight = hight;
        this.pressed = false;  
    }

    public Button() {
        this.pressed = false;
    }

    public void setProperty(int x, int y, int width, int hight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.hight = hight;
        this.pressed = false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void showEllipseButton() {
        smooth();
        ellipse(this.x, this.y, this.width, this.hight);

    }

    public boolean isPressed(int x, int y) {

        return true;
    }

    private boolean pressed;
    private String title;

    private int x;
    private int y;
    private int width;
    private int hight;
}