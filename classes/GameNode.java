import java.awt.Point;

public class GameNode {

    private Point pos;
    private char nodeType; //or enum class?
    private boolean visited;

    public GameNode(int x, int y) {

        this.pos = new Point(x,y);
        this.nodeType = '.'; //default out of bounds until otherwise mapped
        this.visited = false;

    }

    public int nodeWeight() {

        switch(this.nodeType) {

            case 'a': case 'k': case 'd': case '$': return -1;
            case ' ': case '~': return 0;
            case '*': return 1;
            case 'T': return 2;
            default: return 100; //should never happen

        }

    }

    public int goalWeight() {

        switch(this.nodeType) {

            case 'a': return 1;
            case 'T': return 2;
            case 'k': return 3;
            case 'd': return 4;
            case '$': return 5; 
            default: return 100;

        }

    }

    public Point getPoint() {

        return this.pos;

    }

    public void clearNode() {

        this.nodeType = ' ';

    }

    // is visited
    public boolean isVisited() {

        return this.visited;

    }

    // set visited
    public void setVisited() {

        // clear node?
        this.visited = true;

    }

    // map a node
    public void recordNode(char tile) { 

        this.nodeType = tile;

    }

    public char getType() {

        return this.nodeType;

    }

    public boolean outOfBounds(GameState state) {

        //THIS WAS FUCKING SO MUCH SHIT UP

        /*
         * exploring water in land mode for some reason
         */

        if(this.isItem()) return false;

        switch(this.nodeType) {

            case '.' : return true; 
            case '*' : return true;
            case '-' : return !state.hasKey();
            case '~' : case ' ' : return !state.isValidTerrain(this.nodeType);
            case 'T' : return !state.hasAxe(); //hmmmm
            //default : 
                //return !state.isValidTerrain(this.nodeType);

        }

        return false; //shouldnt get here but dont want to go wherever triggers this

    }

    public boolean isItem() { 

        switch(this.nodeType) {

            case 'k' : return true;
            case 'a' : return true;
            case 'd' : return true;
            case '$' : return true;
            //case 'T' : return true; //this fucked it.. or is it an item?
            default : return false;

        }

    }

    public boolean isClearableObstacle(GameState state) {

        switch(this.nodeType) {

            case '-' : return state.hasKey();
            case '*' : return state.hasBomb();
            case 'T' : return state.hasAxe();
            default : return false;

        }

    }

    public boolean isTerrain() {

        return this.nodeType == ' ' || this.nodeType == '~';

    }
    
}
