import java.awt.Point;
import java.util.LinkedList;

public class GameMap {

    private Graph map; //GameNode[][] map; //
    private SearchGameMap search;
    private GameState state;

    public GameMap(GameState state) {

        this.map = new Graph(); //GameNode[this.mapDimension][this.mapDimension];
        this.state = state;
        this.search = new SearchGameMap(state);
        //this.initGraph(); //wont be needed...
        // agent automatically begins at (100,100)
        //this.state.setCurrNode(new GameNode(0,0)); //this.state.setCurrNode(this.map[100][100]); //

    }
    /* 
    private void initGraph() { //del

        for(int i=0; i < this.mapDimension; i++) {
            for(int j=0; j < this.mapDimension; j++) {
                this.map[i][j] = new GameNode(i,j); 
            }
        }

    }*/

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
    
    public void mapView(char[][] view) {

        this.orientView(view);

        GameNode playerNode = this.state.getCurrNode();
        Point pos = playerNode.getPoint();
        int playerX = pos.x;
        int playerY = pos.y;
        

        for(int i=0; i < 5; i++) {

            for(int j=0; j < 5; j++) {

                int trueX = j-2+playerX; //i-2+playerX; //
                int trueY = -(i-2)+playerY; //j-2+playerY; //

                Point currP = new Point(trueX,trueY);
                GameNode curr = null;
                //check if node/point exists
                if(this.map.pointExists(currP)) {
                    //if yes, return it
                     curr = this.map.nodeFromPoint(currP);
                }else{
                    //if not, create and add to map
                    curr = new GameNode(trueX,trueY);
                    this.map.addNode(curr);
                }

                this.map.connectNode(curr);

                if(i==2 && j==2) { 
                    this.state.updateCurrState(curr); 
                    //debug
                    if(this.state.getCurrNode() != curr) System.out.println("MAP: CURR NODE MISMATCH!");
                }else{
                    curr.recordNode(view[i][j]);
                }

                this.state.rememberNode(curr);

            }

        }

    }

    // explore - does this return a goal or add it to be retrieved from state?
    private Goal explore(char terrain) {

        return this.search.exploreDFS(this,terrain);

    }

    public Graph getMap() { //change

        return this.map;

    }

    // explore land (wrapper) - does this return a goal or add it to be retrieved from state?
    public Goal exploreLand() {

        //this.state.disableWaterTravel();
        return this.explore(' ');

    }

    // expore water (wrapper)
    public Goal exploreWater() {

        //this.state.disableLandTravel();
        return this.explore('~');

    }

    public Goal pursueGoal(GameNode n) {

        return this.search.astarSearch(this, n, new ManhattanDistanceHeuristic());

    }

    public LinkedList<GameNode> getNeighbours(GameNode n) { 

        return this.map.getAllNeighbours(n);

    }

    public GameNode findReachable(char target) { 

        return this.search.reachableItem(this, target);

    }

    public GameNode getHome() {

        return this.map.getHomeNode();

    }

}
