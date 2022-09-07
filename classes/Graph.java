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

    //debug
    public boolean sanity() {

        for(GameNode n: this.graph.keySet()) {

            //System.out.println("Neighbours of node at "+n.getPoint().toString()+":");
            for(GameNode m: this.graph.get(n)) {
                
                GameNode x = this.nodeFromPoint(m.getPoint());
                if(m != x) {

                    System.out.println("GRAPH SANITY CHECK FAILED");
                    return false;

                } 
                
            }
            
        }

        System.out.println("GRQAPH SNAITY CHECK PASSED");
        return true;

    }

    //debug
    public void debugGraph() {

        for(GameNode n: this.graph.keySet()) {

            System.out.println("Neighbours of node at "+n.getPoint().toString()+":");
            for(GameNode m: this.graph.get(n)) {
                
                System.out.println("EDGE TO "+m.getPoint().toString());
            }

        }

    }

    //point exists
    public boolean pointExists(Point p) {

        return this.pointDict.containsKey(p);

    }

    //does node exist
    public boolean nodeExists(GameNode n) {

        return this.graph.containsKey(n);

    }

    //add vertex
    public void addNode(GameNode n) {

        if(this.nodeExists(n)) return;

        //System.out.println("ADDING NODE AT "+n.getPoint().toString());

        this.graph.put(n, new LinkedList<GameNode>());
        this.pointDict.put(n.getPoint(), n);

    }

    public GameNode nodeFromPoint(Point p) {

        if(this.pointExists(p)) return this.pointDict.get(p);

        return null;

    }

    //add edge between two vertices
    public void addEdge(GameNode n, GameNode m) {

       // System.out.println("ADDING EDGE FROM "+n.getPoint().toString()+" to "+m.getPoint().toString());
        this.graph.get(n).add(m);
        this.graph.get(m).add(n);

    }

    //return all neighbours
    public LinkedList<GameNode> getAllNeighbours(GameNode n) {

        //System.out.println("GETTING NEIGHS OF "+n.getPoint().toString());

        return this.graph.get(n);

    }

    private boolean edgeExists(GameNode n, GameNode m) {

        return this.graph.get(n).contains(m) && this.graph.get(m).contains(n);

    }

    public void connectNode(GameNode n) {

        GameNode m = null;

        //north
        m = this.getNorthNeighbour(n);
        if(m != null && !this.edgeExists(n, m)) this.addEdge(m, n);
        //south
        m = this.getSouthNeighbour(n);
        if(m != null && !this.edgeExists(n, m)) this.addEdge(m, n);
        //east
        m = this.getEastNeighbour(n);
        if(m != null && !this.edgeExists(n, m)) this.addEdge(m, n);
        //west
        m = this.getWestNeighbour(n);
        if(m != null && !this.edgeExists(n, m)) this.addEdge(m, n);

    }

    //return north neighbour
    public GameNode getNorthNeighbour(GameNode n) {

        Point currP = n.getPoint();
        Point nextP = new Point(currP);
        nextP.translate(0, 1);

        if(this.pointExists(nextP)) return this.pointDict.get(nextP);

        return null;

    }

    //return south neighbour
    public GameNode getSouthNeighbour(GameNode n) {

        Point currP = n.getPoint();
        Point nextP = new Point(currP);
        nextP.translate(0, -1);

        if(this.pointExists(nextP)) return this.pointDict.get(nextP);

        return null;

    }

    //return east neighbour
    public GameNode getEastNeighbour(GameNode n) {

        Point currP = n.getPoint();
        Point nextP = new Point(currP);
        nextP.translate(1, 0);

        if(this.pointExists(nextP)) return this.pointDict.get(nextP);

        return null;

    }

    //return west neighbour
    public GameNode getWestNeighbour(GameNode n) {

        Point currP = n.getPoint();
        Point nextP = new Point(currP);
        nextP.translate(-1, 0);

        if(this.pointExists(nextP)) return this.pointDict.get(nextP);

        return null;

    }


}
