import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;


public class Graph {
    
    private HashMap<GameNode,LinkedList<GameNode>> graph;
    private HashMap<Point,GameNode> pointDict; 
    private GameNode home;

    public Graph() {

        this.graph = new HashMap<GameNode,LinkedList<GameNode>>();
        this.pointDict = new HashMap<Point,GameNode>();
        this.home = new GameNode(0, 0);
        this.addNode(this.home);

    }

    private class ySort implements Comparator<GameNode> {

        public int compare(GameNode a, GameNode b) {

            Integer pay = a.getPoint().y;
            Integer pby = b.getPoint().y;
            
            int result = pay.compareTo(pby);

            if(result != 0) return -result;

            return result;

        }

    }

    
    private class xSort implements Comparator<GameNode> {

        public int compare(GameNode a, GameNode b) {

            Integer pax = a.getPoint().x;
            Integer pbx = b.getPoint().x;
            
            return pax.compareTo(pbx);

        }

    }

    public void printGraph() {

        HashMap<Integer,LinkedList<GameNode>> pts = new HashMap<Integer,LinkedList<GameNode>>();
        
        for(GameNode n : this.graph.keySet()) {

            if(!pts.containsKey(n.getPoint().y)) pts.put(n.getPoint().y, new LinkedList<GameNode>());
            pts.get(n.getPoint().y).add(n);

        }

        LinkedList<Integer> ys = new LinkedList<Integer>(pts.keySet());

        Collections.sort(ys, Collections.reverseOrder());

        System.out.println("##########################");

        for(Integer y : ys) {

            LinkedList<GameNode> xs = pts.get(y);
            Collections.sort(xs, new xSort());
            for(GameNode x : pts.get(y)) {

                System.out.print(x.getType());

            }

            System.out.println("");

        }

        System.out.println("##########################");

    }

    public GameNode getHomeNode() {

        return this.home;

    }

    public boolean pointExists(Point p) {

        return this.pointDict.containsKey(p);

    }

    public boolean nodeExists(GameNode n) {

        return this.graph.containsKey(n);

    }

    public void addNode(GameNode n) {

        if(this.nodeExists(n)) return;

        this.graph.put(n, new LinkedList<GameNode>());
        this.pointDict.put(n.getPoint(), n);

    }

    public GameNode nodeFromPoint(Point p) {

        if(this.pointExists(p)) return this.pointDict.get(p);

        return null;

    }

    public void addEdge(GameNode n, GameNode m) {

        this.graph.get(n).add(m);
        this.graph.get(m).add(n);

    }

    public LinkedList<GameNode> getAllNeighbours(GameNode n) {

        return this.graph.get(n);

    }

    public boolean edgeExists(GameNode n, GameNode m) {

        return this.graph.get(n).contains(m) && this.graph.get(m).contains(n);

    }

    public void connectNode(GameNode n) {

        GameNode m = null;

        m = this.getNorthNeighbour(n);
        if(m != null && !this.edgeExists(n, m)) this.addEdge(m, n);
        m = this.getSouthNeighbour(n);
        if(m != null && !this.edgeExists(n, m)) this.addEdge(m, n);
        m = this.getEastNeighbour(n);
        if(m != null && !this.edgeExists(n, m)) this.addEdge(m, n);
        m = this.getWestNeighbour(n);
        if(m != null && !this.edgeExists(n, m)) this.addEdge(m, n);

    }

    public GameNode getNorthNeighbour(GameNode n) {

        Point currP = n.getPoint();
        Point nextP = new Point(currP);
        nextP.translate(0, 1);

        if(this.pointExists(nextP)) return this.pointDict.get(nextP);

        return null;

    }

    public GameNode getSouthNeighbour(GameNode n) {

        Point currP = n.getPoint();
        Point nextP = new Point(currP);
        nextP.translate(0, -1);

        if(this.pointExists(nextP)) return this.pointDict.get(nextP);

        return null;

    }

    public GameNode getEastNeighbour(GameNode n) {

        Point currP = n.getPoint();
        Point nextP = new Point(currP);
        nextP.translate(1, 0);

        if(this.pointExists(nextP)) return this.pointDict.get(nextP);

        return null;

    }

    public GameNode getWestNeighbour(GameNode n) {

        Point currP = n.getPoint();
        Point nextP = new Point(currP);
        nextP.translate(-1, 0);

        if(this.pointExists(nextP)) return this.pointDict.get(nextP);

        return null;

    }

}
