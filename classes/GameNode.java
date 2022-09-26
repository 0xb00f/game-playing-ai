import java.awt.Point;

public class GameNode {

    private Point pos;
    private char nodeType; 
    private boolean visited;
    private int weight;

    public GameNode(int x, int y) {

        this.pos = new Point(x,y);
        this.nodeType = '.'; 
        this.visited = false;
        this.weight = this.nodeWeight(); 

    }

    @Override
    public boolean equals(Object x) {

        Point curr = this.pos;
        if(x instanceof GameNode) {

            GameNode cand = (GameNode)x;
            Point other = cand.getPoint();
            return curr.equals(other);

        }

        return false;

    }

    @Override
    public int hashCode() {

        return this.pos.hashCode();

    }

    public int getPathWeight() {

        return this.weight;

    }

    public void setPathWeight(int w) { 

        this.weight = w;

    }

    public int nodeWeight() { 

        switch(this.nodeType) {

            case '$' : case 'a': case 'k': case 'd': return 0; 
            case ' ': return 1; 
            case '~' : return 2;
            case 'T': return 3;
            case '*': return 16; 
            default: return 100; 

        }

    }

    public int goalWeight() { 

        switch(this.nodeType) {

            case ' ' : case '~' : case 'T' : return 0;
            case 'a': return 1;
            case 'k': return 2;
            case 'd': return 3;
            case '$': return 4; 
            default: return 100;

        }

    }

    public Point getPoint() {

        return this.pos;

    }

    public void clearNode() {

        this.nodeType = ' ';

    }

    public boolean isVisited() {

        return this.visited;

    }

    public void setVisited() {

        this.visited = true;

    }

    public void recordNode(char tile) { 

        this.nodeType = tile;
        this.weight = this.nodeWeight(); 

    }

    public char getType() {

        return this.nodeType;

    }

    public boolean outOfBounds(GameState state) {

        if(this.isItem()) return false;

        switch(this.nodeType) {

            case '.' : return true; 
            case '*' : return true;
            case '-' : return state.hasKey() == false;

        }

        return false; 

    }

    public boolean isItem() { 

        switch(this.nodeType) {

            case 'k' : case 'a' : case 'd' : case '$' : return true;
            default : return false;

        }

    }

    public boolean isTree() {

        return this.nodeType == 'T';

    }

    public boolean isClearableObstacle(GameState state) {

        switch(this.nodeType) {

            case '-' : return state.hasKey();
            case '*' : return state.hasBomb();
            case 'T' : return state.hasAxe();
            default : return false;

        }

    }
    
}
