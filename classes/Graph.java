import java.awt.Point;
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

    private boolean edgeExists(GameNode n, GameNode m) {

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
