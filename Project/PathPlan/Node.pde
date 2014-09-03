public static class Node {
    public Node (PointMap nodePoint) {
        coord.setPoint(nodePoint);
        pathNode.add(nodePoint);
        nextPathNum = 4;
        rootNode = null;
        nextNode = null;
        allNode[0] = new PointMap();
        allNode[1] = new PointMap();
        allNode[2] = new PointMap();
        allNode[3] = new PointMap();
        allNode[0].setToNil();
        allNode[1].setToNil();
        allNode[2].setToNil();
        allNode[3].setToNil();
    }
    
    // 生成下一个节点
    public boolean create () {
        //deleteNextNode();
        if (4 > nextPathNum) {
            allNode[nextPathNum].setToNil();
        }
        
        int i;
        int min = 4;
        for (i = 0; i < 4; ++i) {
            if ((min > nextNodePriority[i]) && (!allNode[i].isNil())) {
                min = nextNodePriority[i];
                nextPathNum = i;
            }
        }
        //nextPathNum = i;
        if (4 == min) {
            return false;
        } else {
            nextNode = new Node(allNode[nextPathNum]);
            nextNode.rootNode = this;
            return true;
        }
    }
    
    // 释放下一个节点
//    public void deleteNextNode () {
//        delete nextNode;
//        nextNode = NULL;
//    }
    // 获取当前节点附近的节点
    public void getAllRoundNode (int num, PointMap obstacle[]) {
        PointMap nextPoint = new PointMap(0, 0);
        nextPathNum = 4;
        
        // y轴负方向
        nextPoint.x = coord.x;
        nextPoint.y = coord.y - 1;
        if (canToNextNode(nextPoint, num, obstacle)) {
            allNode[0].setPoint(coord.x, coord.y - 2, PointMap.yPositive);
            if (isRootNode(allNode[0]) || isInPathNode(allNode[0])) {
                allNode[0].setToNil();
            }
        } else {
            allNode[0].setToNil();
        }
        
        // x轴正方向
        nextPoint.x = coord.x + 1;
        nextPoint.y = coord.y;
        if (canToNextNode(nextPoint, num, obstacle)) {
            allNode[1].setPoint(coord.x + 2, coord.y, PointMap.xNegative);
            if (isRootNode(allNode[1]) || isInPathNode(allNode[1])) {
                allNode[1].setToNil();
            }
        } else {
            allNode[1].setToNil();
        }

        // y轴正方向
        nextPoint.x = coord.x;
        nextPoint.y = coord.y + 1;
        if (canToNextNode(nextPoint, num, obstacle)) {
            allNode[2].setPoint(coord.x, coord.y + 2, PointMap.yNegative);
            if (isRootNode(allNode[2]) || isInPathNode(allNode[2])) {
                allNode[2].setToNil();
            }
        } else {
            allNode[2].setToNil();
        }

        // x轴负方向
        nextPoint.x = coord.x - 1;
        nextPoint.y = coord.y;
        if (canToNextNode(nextPoint, num, obstacle)) {
            allNode[3].setPoint(coord.x - 2, coord.y, PointMap.xPositive);
            if (isRootNode(allNode[3]) || isInPathNode(allNode[3])) {
                allNode[3].setToNil();
            }
        } else {
            allNode[3].setToNil();
        }
        if (!haveRootNode) {
            haveRootNode = true;
            switch (coord.dir) {
                case PointMap.xNegative:
                    allNode[3].setToNil();
                    break;
                    
                case PointMap.xPositive:
                    allNode[1].setToNil();
                    break;
                    
                case PointMap.yNegative:
                    allNode[0].setToNil();
                    break;
                    
                case PointMap.yPositive:
                    allNode[2].setToNil();
                    break;
                    
                default:
                    break;
            }
        }
    }

    // 设置下一个节点的优先级
    public void setNextNodePriority (PointMap targetPoint) {
        int xDir;
        int yDir;
        
        if (targetPoint.x > coord.x) {
            xDir = PointMap.xNegative;
        } else if (targetPoint.x < coord.x) {
            xDir = PointMap.xPositive;
        } else {
            xDir = PointMap.noDir;
        }
        
        if (targetPoint.y > coord.y) {
            yDir = PointMap.yNegative;
        } else if (targetPoint.y < coord.y) {
            yDir = PointMap.yPositive;
        } else {
            yDir = PointMap.noDir;
        }
        
        for (int i = 0; i < 4; ++i) {
            if (!allNode[i].isNil()) {
                if ((allNode[i].dir == coord.dir || allNode[i].dir == coord.dir) 
                    && (xDir == allNode[i].dir || yDir == allNode[i].dir)) 
                {
                    nextNodePriority[i] = 0;
                } else if (xDir == allNode[i].dir || yDir == allNode[i].dir) {
                    nextNodePriority[i] = 1;
                } else if ((allNode[i].dir == coord.dir) || (allNode[i].dir == coord.dir)) {
                    nextNodePriority[i] = 2;
                } else {
                    nextNodePriority[i] = 3;
                }
            } else {
                nextNodePriority[i] = 4;
            }
        }
    }
    
    // 存储所有经过的节点
    public static ArrayList<PointMap> pathNode = new ArrayList<PointMap>();
    // 当前节点坐标
    public PointMap coord = new PointMap();
    // 父节点
    public Node rootNode;
    // 下一个节点
    public Node nextNode;
    
    // 上一个路径的序号
    private int nextPathNum;
    
    // 当前节点附近的所有的节点
    private PointMap[] allNode = new PointMap[4];
    
    // 设置优先级
    private int[] nextNodePriority = new int[4];
    
    // 当前节点是否有父节点
    private static boolean haveRootNode;

    // 设置地图大小
    private final static int XMAX = 6;
    private final static int YMAX = 6;
    
    // 判断两个节点之间是否有障碍物和节点是否出了边界
    private boolean canToNextNode (PointMap pathPoint, int num, PointMap[] obstacle) {
        int i;
        for (i = 0; i < num; ++i) {
            if ((pathPoint.x == obstacle[i].x && pathPoint.y == obstacle[i].y)
                || pathPoint.x < 0 || pathPoint.x > 6
                || pathPoint.y < 0 || pathPoint.y > 6) {
                return false;
            }
        }
        return true;
    }
    
    //  判断下一个节点是否是父节点
    private boolean isRootNode (PointMap thisNode) {
        if (this.rootNode != null) {
            if (this.rootNode.coord.isEqual(thisNode)){
                return true;
            }
        }
        return false;
    }
    
    // 判断节点是否在pathNode中
    private boolean isInPathNode (PointMap thisPoint) {
        for (int i = pathNode.size()-1; i >= 0; i--)
        {
            if (pathNode.get(i).isEqual(thisPoint)) {
                return true;
            }
        }
        return false;
    }
}
