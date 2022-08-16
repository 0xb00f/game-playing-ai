import java.awt.Point;

public class ManhattanDistanceHeuristic implements Heuristic {

    public int score(GameState state, GameNode curr, GameNode end) { //state?

        Point currPoint = curr.getPoint();
        Point endPoint = end.getPoint();
        return Math.abs(currPoint.x - endPoint.x) + Math.abs(currPoint.y - endPoint.y);

    }
    
}
