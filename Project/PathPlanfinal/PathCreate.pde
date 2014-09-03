public static class PathCreate {

    public PathCreate () {
        
    }

    private static boolean notToNext(PointMap thisPoint, PointMap nextPoint) {
        if (nextPoint.x > thisPoint.x && thisPoint.dir == PointMap.xPositive) {
            return true;
        }
        
        if (nextPoint.x < thisPoint.x && thisPoint.dir == PointMap.xNegative) {
            return true;
        }
        
        if (nextPoint.y > thisPoint.y && thisPoint.dir == PointMap.yPositive) {
            return true;
        }
        
        if (nextPoint.y < thisPoint.y && thisPoint.dir == PointMap.yNegative) {
            return true;
        }

        return false;
    }

    public static boolean pathPlan (PointMap nextPoint, PointMap originNode, PointMap targetNode, int obstacleNum, PointMap obstacle[]) {
        
        if (originNode.isEqual(targetNode)) {
            //fatherNode.pathNode.clear();
            println("IsEqual!");
            return false;
        }
        PointMap[] pathPoint = new PointMap[16];
        for (int i = 0; i < 16; i++)
          pathPoint[i] = new PointMap();
        int num = 0;
        
        Node fatherNode = new Node(originNode);
        Node.haveRootNode = false;
        fatherNode.getAllRoundNode(obstacleNum, obstacle);
        fatherNode.setNextNodePriority(targetNode);
        Node next;
        next = fatherNode;
        while (true) {
            boolean canCreate = next.create();
            if (!canCreate) {
                if (next.rootNode == null) {
                    println("NO NODE!");
                    println("ORIGIN x = " + originNode.x + ", y = " + originNode.y + ", dir =" + originNode.dir);
                    fatherNode.pathNode.clear();
                    return false;
                } else {
                    next = next.rootNode;
                    //next->nextNode->~Node();
                    
                }
            } else {
                next = next.nextNode;
                next.getAllRoundNode(obstacleNum, obstacle);
                next.setNextNodePriority(targetNode);
            }
            
            if (next.coord.isEqual(targetNode)) {
                next = fatherNode;
                for (int i = 0;; ++i) {
                    pathPoint[i].setPoint(next.coord);
                    if (next.nextNode == null) {
                        num = i + 1;
                        break;
                    } else {
                        next = next.nextNode;
                    }
                }
                if (num > 3) {
                    for (int i = 0; i < num - 3; ++i) {
                        for (int j = i + 3; j < num; ++j) {
                            if (((pathPoint[i].x + 2 == pathPoint[j].x) && (pathPoint[i].y == pathPoint[j].y))
                                || ((pathPoint[i].x - 2 == pathPoint[j].x) && (pathPoint[i].y == pathPoint[j].y))
                                || ((pathPoint[i].y + 2 == pathPoint[j].y) && (pathPoint[i].x == pathPoint[j].x))
                                || ((pathPoint[i].y - 2 == pathPoint[j].y) && (pathPoint[i].x == pathPoint[j].x))) {
                                int a;
                                for (a = 0; a < obstacleNum; a++) {
                                    if (((pathPoint[i].x + pathPoint[j].x) / 2 == obstacle[a].x
                                        && (pathPoint[i].y + pathPoint[j].y) / 2 == obstacle[a].y)
                                        || notToNext(pathPoint[i], pathPoint[j])
                                    ) {
                                        break;
                                    }
                                }
                                
                                if (a == obstacleNum) {
                                    for (int z = 0; z + j < num; ++z) {
                                        pathPoint[z + 1 + i].setPoint(pathPoint[j + z]);
                                    }
                                    num = num - (j - i) + 1;
                                }
                                
                            }
                        }
                    }
                }
                for (int i = 0; i < num; ++i) {
                    println(i + " (" + pathPoint[i].x + ", " + pathPoint[i].y + ")");
                }
                nextPoint.x = int((pathPoint[0].x + pathPoint[1].x) / 2);
                nextPoint.y = int((pathPoint[0].y + pathPoint[1].y) / 2);
                if (pathPoint[1].x > pathPoint[0].x) {
                    nextPoint.dir = PointMap.xNegative;
                } else if (pathPoint[1].x < pathPoint[0].x) {
                    nextPoint.dir = PointMap.xPositive;
                } else if (pathPoint[1].y > pathPoint[0].y) {
                    nextPoint.dir = PointMap.yNegative;
                } else if (pathPoint[1].y < pathPoint[0].y) {
                    nextPoint.dir = PointMap.yPositive;
                }
                break;
            }
        }
        next = null;
        fatherNode.pathNode.clear();
        return true;
    }
}
