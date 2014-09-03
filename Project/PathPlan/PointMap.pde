public static class PointMap{

    public int x;
    public int y;
    
    public final static int xNegative = 1;
    public final static int xPositive = 2;
    public final static int yNegative = 3;
    public final static int yPositive = 4;
    public final static int noDir = 5;
   
    
    public int dir;
    public PointMap(PointMap setPoint) {
        x = setPoint.x;
        y = setPoint.y;
        dir = setPoint.dir;
    }
    
    public PointMap(int inX, int inY) {
        x = inX;
        y = inY;
    }
    
    public PointMap() {
        x = 0;
        y = 0;
    }
    
    public boolean isEqual (PointMap thisPoint) {
        if (this.x == thisPoint.x && this.y == thisPoint.y) {
            return true;
        }
        return false;
    }
    
    public void setToNil () {
        x = -1;
        y = -1;
        dir = noDir;
    }
    
    public boolean isNil() {
        if (x == -1 && y == -1) {
            return true;
        }
        return false;
    }
    
    public void setPoint (int inX, int inY, int inDir) {
        x = inX;
        y = inY;
        dir = inDir;
    }
    
    public void setPoint (PointMap thisPoint) {
        x = thisPoint.x;
        y = thisPoint.y;
        dir = thisPoint.dir;
    }
}
