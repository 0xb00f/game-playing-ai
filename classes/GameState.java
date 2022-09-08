import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue; 

public class GameState { 

    public PriorityQueue<GameNode> pendingGoals; 
    public LinkedList<GameNode> unexploredLand; //del
    public LinkedList<GameNode> unexploredWater; //del
    public LinkedList<GameNode> seenRafts; //del
    public LinkedList<GameNode> seenBombs; //del
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

        this.pendingGoals = new PriorityQueue<GameNode>(new GoalCompare());
        this.unexploredLand = new LinkedList<GameNode>(); //del
        this.unexploredWater = new LinkedList<GameNode>(); //del 
        this.seenRafts = new LinkedList<GameNode>(); //del 
        this.seenBombs = new LinkedList<GameNode>(); //del
        this.axe = false;
        this.raft = false;
        this.key = false;
        this.treasure = false;
        this.numBombs = 0;
        this.currentNode = null; //new GameNode(0,0); // init currnode to start
        this.currentDirection = 0; // north by default
        this.validTerrain = new HashSet<Character>(); //moved
        this.terrMngr = null;
        this.currAgentState = null;

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
        clonedState.validTerrain.addAll(this.validTerrain); //change for terrMngr......?
        clonedState.currentNode = n; 
        
        return clonedState;

    }

    public void setTerrainManager(TerrainManager t) {

        this.terrMngr = t;

    }

    public void setAgentState(AgentState s) {

        this.currAgentState = s;

    }

    public void processAction(Character c, GameMap m) {

        switch(c) {

            case 'f': this.move(m); break;
            case 'l': this.turnDirection(-1); break;
            case 'r': this.turnDirection(1); break;
            case 'b': this.useBomb(); break;
            case 'c': this.terrMngr.raftCollected(); break;

        }

    }

    public boolean isValidTerrain(char c) {

        return this.terrMngr.isValidTerrain(this.currAgentState,c);
        //return this.validTerrain.contains(c);

    }

    public void enableWaterTravel() { 

        //System.out.println("ENABLING WATER");
        this.terrMngr.enableWaterTravel();
        //this.validTerrain.add('~');

    }

    public void enableLandTravel() { 

        //this.validTerrain.add(' ');
        this.terrMngr.enableLandTravel();

    }

    public void disableWaterTravel() { 

        //this.validTerrain.remove('~');
        this.terrMngr.disableWaterTravel();

    }

    public void disableLandTravel() { 

        //this.validTerrain.remove(' ');
        this.terrMngr.disableLandTravel();

    }

    public void addGoal(GameNode g) { //transfer goals into goal manager?

        if(this.pendingGoals.contains(g)) return;

        //System.out.println("ADDING GOAL: '"+g.getType()+"'");

        this.pendingGoals.add(g);

    }

    public boolean hasSeenRafts() { //del

        return this.seenRafts.size() > 0;

    }

    public void addSeenRaft(GameNode n) { //del

        if(this.seenRafts.contains(n)) return;

        this.seenRafts.add(n);

    }

    public boolean hasSeenBombs() { //del

        return this.seenBombs.size() > 0;

    }

    public void addSeenBomb(GameNode n) { //del

        if(this.seenBombs.contains(n)) return;

        this.seenBombs.add(n);

    }

    public void rememberNode(GameNode n) {

        switch(n.getType()) {

            case ' ': if(!n.isVisited()) this.addUnexploredLand(n); break; //del
            case '~': if(!n.isVisited()) this.addUnexploredWater(n); break;    //del        
            case 'T': this.addSeenRaft(n); break; //del
            case 'd': this.addSeenBomb(n); break; //change to goal
            case 'k': case '$': case 'a': this.addGoal(n); break; 

        }

        //if(n.getType() == 'k' || n.getType() == '$') System.out.println("ITEM '"+n.getType()+"' seen at "+n.getPoint().toString());

    }

    public void cleanUnexplored() { //del

        LinkedList<GameNode> kill = new LinkedList<GameNode>();

        for(GameNode x : this.unexploredLand) {
            if(x.isVisited()) kill.add(x);
        }

        this.unexploredLand.removeAll(kill);
        kill.clear();

        for(GameNode x : this.unexploredWater) {
            if(x.isVisited()) kill.add(x);
        }

        this.unexploredWater.removeAll(kill);

    }

    public boolean hasUnexploredWater(GameMap map) { //del

        boolean ret = false;

        if(!this.onWater) this.enableWaterTravel(); //? as below, and changed

        for(GameNode x: this.unexploredWater) {

            if(map.isReachable(x)) { 
                ret = true;
                break;
            }

        }

        if(!this.onWater) this.disableWaterTravel();

        return ret;

    }

    public void addUnexploredWater(GameNode g) { //del

        if(this.unexploredWater.contains(g)) return;

        this.unexploredWater.add(g);

    }

    public boolean hasUnexploredLand(GameMap map) { //del

        boolean ret = false;

        if(this.onWater) this.enableLandTravel(); //?

        for(GameNode x: this.unexploredLand) {

            if(map.isReachable(x)) {
                ret = true;
                break;
            }

        }

        if(this.onWater) this.disableLandTravel(); //? changed anyway

        return ret;

        //return this.unexploredLand.size() > 0;

    }

    public void addUnexploredLand(GameNode g) { //del

        if(this.unexploredLand.contains(g)) return;

        this.unexploredLand.add(g);

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

    public int getNumBombs() {

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

    public void updateCurrState(GameNode node) {

        switch(node.getType()) {

            case 'k' : this.setKey(); break;
            case 'd' : this.addBomb(); break;
            case 'a' : this.setAxe(); break;
            case '$' : this.setTreasure(); break;

        }

        if(node.isItem()) {

            //System.out.println("Removing goal: '"+node.getType()+"'");
            if(node.getType() == 'd') this.seenBombs.remove(node);
            this.pendingGoals.remove(node);

        }

        if(node.getType() != '~') node.clearNode();
        node.setVisited();
        //this.currentNode = node; //?

    }

    //debug
    private void isBad(GameNode n) {

        Character c = n.getType();

        if(c != ' ') System.out.println("IS BAD wanted to move into '"+c+"'");

    }

    public void move(GameMap m) { // greatly simplify with graph.... and mvoe to gent engin...

        GameNode curr = this.currentNode;
        Graph map = m.getMap();

        switch(this.currentDirection) {

            case 0: 
                GameNode north = map.getNorthNeighbour(curr);
                this.isBad(north);
                this.terrMngr.processTerrainChange(north.getType());
                this.currentNode = north;
                break;
            case 1:
                //this.isBad(map[trueX][trueY+1]);
                GameNode east = map.getEastNeighbour(curr);
                this.isBad(east);
                this.terrMngr.processTerrainChange(east.getType());
                this.currentNode = east;
                break;
            case 2:
                //this.isBad(map[trueX+1][trueY]);
                GameNode south = map.getSouthNeighbour(curr);
                this.isBad(south);
                this.terrMngr.processTerrainChange(south.getType());
                this.currentNode = south;
                break;
            case 3:
                //this.isBad(map[trueX][trueY-1]);
                GameNode west = map.getWestNeighbour(curr);
                this.isBad(west);
                this.terrMngr.processTerrainChange(west.getType());
                this.currentNode = west;
                break;

        }

        //System.out.println("CURR POS IS "+ this.currentNode.getPoint().toString()+" facing direction="+this.currentDirection);

    }

    public int getDirection() {

        return this.currentDirection;

    }

    public void turnDirection(int d) { 

        this.currentDirection = Math.floorMod(this.currentDirection+d, 4);

    }

}
