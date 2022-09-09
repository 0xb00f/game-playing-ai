import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue; 

public class GameState { 

    public PriorityQueue<GameNode> pendingGoals; //move to goalhandler
    public boolean axe;
    public boolean raft;
    public boolean key;
    public boolean treasure;
    public int numBombs;
    public GameNode currentNode;
    public int currentDirection; // 0=north, 1=east, 2=south, 3=west
    public boolean onWater; //moved... but should I? for purposes of cloning...
    public HashSet<Character> validTerrain; //moved
    private AgentState currAgentState;
    private TerrainManager terrMngr;

    private class GoalCompare implements Comparator<GameNode> {

        public int compare(GameNode a, GameNode b) {
    
            Integer g1 = a.goalWeight();
            Integer g2 = b.goalWeight();
            
            return g1.compareTo(g2);
            
        }

    }

    public GameState() {

        this.pendingGoals = new PriorityQueue<GameNode>(new GoalCompare()); //move
        this.axe = false;
        this.raft = false;
        this.key = false;
        this.treasure = false;
        this.numBombs = 0;
        this.currentNode = null; //new GameNode(0,0); // init currnode to start
        this.currentDirection = 0; // north by default
        this.validTerrain = new HashSet<Character>(); //moved
        this.terrMngr = null;
        this.currAgentState = null; //needed???

    }

    public GameState cloneState(GameNode n) { //del

        GameState clonedState = new GameState();
        clonedState.setAgentState(this.currAgentState);
        clonedState.axe = this.hasAxe();
        clonedState.raft = this.hasRaft();
        clonedState.key = this.hasKey();
        clonedState.treasure = this.hasTreasure();
        clonedState.numBombs = this.getNumBombs();
        clonedState.currentDirection = this.getDirection();
        clonedState.validTerrain.addAll(this.validTerrain); 
        clonedState.currentNode = n; 
        
        return clonedState;

    }

    public void setTerrainManager(TerrainManager t) { //del

        this.terrMngr = t;

    }

    public void setAgentState(AgentState s) { //del?
 
        this.currAgentState = s;

    }

    public boolean isValidTerrain(char c) { //del

        return this.terrMngr.isValidTerrain(this.currAgentState,c);

    }

    public void enableWaterTravel() { //del

        this.terrMngr.enableWaterTravel();

    }

    public void enableLandTravel() {  //del

        this.terrMngr.enableLandTravel();

    }

    public void disableWaterTravel() {  //del

        this.terrMngr.disableWaterTravel();

    }

    public void disableLandTravel() { //del

        this.terrMngr.disableLandTravel();

    }

    public void addGoal(GameNode g) { //move

        if(this.pendingGoals.contains(g)) return;

        this.pendingGoals.add(g);

    }

    public void rememberNode(GameNode n) {

        switch(n.getType()) {
     
            //case 'd': this.addSeenBomb(n); break; //change to goal, but a generic one, how????? TODO 
            case 'k': case '$': case 'a': this.addGoal(n); break; 

        }

    }
    
    public boolean isOnWater() {

        return this.onWater;

    }

    public void setOnWater() {

        this.onWater = true;

    }

    public void setOffWater() {

        this.onWater = false;

    }

    public boolean hasAxe() {

        return this.axe;

    }

    public void setAxe() {

        this.axe = true;

    }

    public boolean hasKey() {

        return this.key;

    }

    public void setKey() {

        this.key = true;

    }

    public boolean hasRaft() {

        return this.raft;

    }

    public void setRaft(boolean b) {

        this.raft = b;

    }
    
    public boolean hasTreasure() {

        return this.treasure;

    }

    public void setTreasure() {

        this.treasure = true;

    }

    public boolean hasBomb() {

        return this.numBombs > 0;

    }

    public int getNumBombs() { //del?

        return this.numBombs;

    }

    public void addBomb() {

        this.numBombs++;

    }

    public void useBomb() {

        this.numBombs--;

    }

    public GameNode getCurrNode() {

        return this.currentNode;

    }

    public void setCurrNode(GameNode node) {

        this.currentNode = node;

    }

    public void updateCurrState(GameNode node) { //move to engine

        switch(node.getType()) {

            case 'k' : this.setKey(); break;
            case 'd' : this.addBomb(); break;
            case 'a' : this.setAxe(); break;
            case '$' : this.setTreasure(); break;

        }

        if(node.isItem() && this.pendingGoals.contains(node)) this.pendingGoals.remove(node);
        if(node.getType() != '~') node.clearNode();
        node.setVisited();

    }

    public int getDirection() {

        return this.currentDirection;

    }

    public void turnDirection(int d) { 

        this.currentDirection = Math.floorMod(this.currentDirection+d, 4);

    }

}
