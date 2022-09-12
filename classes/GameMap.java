import java.awt.Point;
import java.util.LinkedList;

public class GameMap {

    private Graph map;
    private SearchGameMap search;
    private GameState state;
    private GoalManager goalMngr;

    public GameMap(GameState state, SearchGameMap search) {

        this.map = new Graph(); 
        this.state = state;
        this.search = search; //new SearchGameMap(state); //and terr, rethink construction sequence
        this.goalMngr = null;

    }

    private void rotateLeft(char[][] view) {

        for (int x = 0; x < 5 / 2; x++) {

            for (int y = x; y < 5 - x - 1; y++) {

                char temp = view[x][y];
                view[x][y] = view[y][5 - 1 - x];
                view[y][5 - 1 - x] = view[5 - 1 - x][5 - 1 - y];
                view[5 - 1 - x][5 - 1 - y] = view[5 - 1 - y][x];
                view[5 - 1 - y][x] = temp;

            }

        }

    }

    private void rotateRight(char[][] view) {

        for (int i = 0; i < 5 / 2; i++) {

            for (int j = i; j < 5 - i - 1; j++) {

                char temp = view[i][j];
                view[i][j] = view[5 - 1 - j][i];
                view[5 - 1 - j][i] = view[5 - 1 - i][5 - 1 - j];
                view[5 - 1 - i][5 - 1 - j] = view[j][5 - 1 - i];
                view[j][5 - 1 - i] = temp;

            }

        }
        
    }

    private void orientView(char[][] view) {

        int dir = this.state.getDirection();

        switch(dir) {

            case 0: //north
                break;
            case 1: //east
                this.rotateRight(view);
                break;
            case 2: //south
                this.rotateRight(view);
                this.rotateRight(view);
                break;
            case 3: //west
                this.rotateLeft(view);
                break;

        }

    }
    
    public void mapView(char[][] view, GoalManager goalMngr) {

        this.orientView(view);

        GameNode playerNode = this.state.getCurrNode();
        Point pos = playerNode.getPoint();
        int playerX = pos.x;
        int playerY = pos.y;

        for(int i=0; i < 5; i++) {

            for(int j=0; j < 5; j++) {

                int trueX = j-2+playerX; 
                int trueY = -(i-2)+playerY; 

                Point currP = new Point(trueX,trueY);
                GameNode curr = null;

                if(this.map.pointExists(currP)) {

                    curr = this.map.nodeFromPoint(currP);

                }else{

                    curr = new GameNode(trueX,trueY);
                    this.map.addNode(curr);

                }

                this.map.connectNode(curr);

                if(i==2 && j==2) { 

                    this.state.updateCurrState(this,goalMngr,curr); 

                }else{

                    curr.recordNode(view[i][j]);

                }

                if(curr.isItem()) goalMngr.addGoal(curr); 

            }

        }

    }

    private Goal explore(char terrain) { //terrain redundant

        return this.search.exploreDFS(this,terrain);

    }

    public Graph getMap() {

        return this.map;

    }

    public Goal exploreLand() { //redundant

        return this.explore(' ');

    }

    public Goal exploreWater() { //redundant

        return this.explore('~');

    }

    public Goal pursueGoal(GameNode n) {

        return this.search.astarSearch(this, n, new ManhattanDistanceHeuristic());

    }

    public LinkedList<GameNode> getNeighbours(GameNode n) { 

        return this.map.getAllNeighbours(n);

    }

    public GameNode findReachable(char target) { 

        return this.search.reachableItem(this, this.state, target);

    }

    public GameNode getHome() {

        return this.map.getHomeNode();

    }

}
