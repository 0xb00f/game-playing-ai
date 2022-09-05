import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameMap {

    private GameNode[][] map;
    private SearchGameMap search;
    private GameState state;
    private int mapDimension = 200;

    public GameMap(GameState state) {

        this.map = new GameNode[this.mapDimension][this.mapDimension];
        this.state = state;
        this.search = new SearchGameMap(state);

        for(int i=0; i < this.mapDimension; i++) {
            for(int j=0; j < this.mapDimension; j++) {
                this.map[i][j] = new GameNode(i,j); 
            }
        }

        // agent automatically begins at (100,100)
        this.state.setCurrNode(this.map[100][100]);

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
    
    private void printMap() {

        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        for(int i=50; i < 150; i++) {
            for(int j=50; j < 150; j++) {
                if(this.map[i][j] == this.state.getCurrNode()) {
                    System.out.print("^");
                    continue;
                }
                System.out.print(this.map[i][j].getType());
            }
            System.out.println("");
        }

        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

    }
    
    public void mapView(char[][] view) {

        this.orientView(view);

        GameNode playerNode = this.state.getCurrNode();
        Point pos = playerNode.getPoint();
        int playerX = pos.x;
        int playerY = pos.y;

        for(int i=0; i < 5; i++) {

            for(int j=0; j < 5; j++) {

                int trueX = i-2+playerX; 
                int trueY = j-2+playerY; 

                GameNode curr = this.map[trueX][trueY];

                if(i==2 && j==2) { 
                    this.state.updateCurrState(curr); 
                }else{
                    curr.recordNode(view[i][j]);
                }

                //if(curr.isItem()) this.state.addSeenItem(view[i][j],curr); 
                this.state.rememberNode(curr);

            }

        }

        this.state.cleanUnexplored();
        printMap();

    }

    // explore - does this return a goal or add it to be retrieved from state?
    private Goal explore(char terrain) {

        return this.search.exploreDFS(this,terrain);

    }

    public GameNode[][] getMap() {

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

    // pursue goal - does this return a goal or add it to be retrieved from state?
    public Goal pursueGoal(GameNode n) {

        return this.search.astarSearch(this, n, new ManhattanDistanceHeuristic());

    }

    public ArrayList<GameNode> getNeighbours(GameNode g) {

        ArrayList<GameNode> neighbours = new ArrayList<GameNode>();

        Point p = g.getPoint();
        int x = p.x, y = p.y;
        
        neighbours.add(this.map[x-1][y]); //no bounds checking, relying on maps being small..
        neighbours.add(this.map[x+1][y]);
        neighbours.add(this.map[x][y-1]);
        neighbours.add(this.map[x][y+1]);

        return neighbours;

    }

    public LinkedList<GameNode> reachable(GameNode n) {

        return this.search.pathBFS(this, this.state.currentNode, n);

    }

    public boolean isReachable(GameNode n) {

        return this.search.floodFill(this, n) > 0;

    }

    //

    public GameNode getHome() {

        return this.map[100][100];

    }

}
