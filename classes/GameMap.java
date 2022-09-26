import java.awt.Point;
import java.util.Comparator;
import java.util.LinkedList;

public class GameMap {

    private Graph map;
    private SearchGameMap search;
    private GameState state;
    //private GoalManager goalMngr;

    public GameMap(GameState state, SearchGameMap search) {

        this.map = new Graph(); 
        this.state = state;
        this.search = search; //new SearchGameMap(state); //and terr, rethink construction sequence
        //this.goalMngr = null;

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

                if(i==2 && j==2) { //currP.equals(this.state.getCurrPos)

                    this.state.updateCurrState(this,goalMngr,curr); 

                }else{

                    curr.recordNode(view[i][j]);

                }

                if(curr.isItem()) goalMngr.addGoal(curr); 

            }

        }

        //this.map.printGraph();

    }

    private LinkedList<GameNode> explore(char terrain) { //terrain redundant

        return this.search.exploreDFS(this,terrain);

    }

    public Graph getMap() {

        return this.map;

    }

    public LinkedList<GameNode> exploreLand() { //redundant

        return this.explore(' ');

    }

    public LinkedList<GameNode> exploreWater() { //redundant

        return this.explore('~');

    }

    public LinkedList<GameNode> findOptimalPath(GameNode begin, GameNode end) { //?

        return this.search.astarSearch(this, begin, end, new ManhattanDistanceHeuristic(),true);

    }

    public LinkedList<GameNode> pursueGoal(GameNode n) {

        return this.search.astarSearch(this, this.state.getCurrNode(), n, new ManhattanDistanceHeuristic(),false);

    }

    public LinkedList<GameNode> getNeighbours(GameNode n) { 

        return this.map.getAllNeighbours(n);

    }

    public boolean areAdjacent(GameNode a, GameNode b) {

        return this.map.edgeExists(a, b);

    }

    public GameNode findReachable(char target) { 

        return this.search.reachableItem(this.state.getCurrNode(), this, this.state, target, false);

    }

    public GameNode getToLand(GameNode start) {

        return this.search.reachableItem(start, this, this.state, ' ', true);

    }
    
    public GameNode getNearest(LinkedList<GameNode> list) { //temp?

        GameNode curr = this.state.getCurrNode();

        list.sort(new Comparator<GameNode>() {
            
            public int compare(GameNode a, GameNode b) {

                ManhattanDistanceHeuristic m = new ManhattanDistanceHeuristic();
                Integer aDist = m.score(curr, a);
                Integer bDist = m.score(curr, b);

                return aDist.compareTo(bDist);

            }

        });

        return list.getFirst();

    }
    
    public GameNode getHome() {

        return this.map.getHomeNode();

    }

    public boolean isWorthExploring(GameNode start) {

        return this.search.isWorthExploring(this, start);

    }

    public GameNode findUnexploredRegion(char target) {

        return this.search.findUnexploredRegion(this, this.state, target);

    }


}