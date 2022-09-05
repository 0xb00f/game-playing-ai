import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue; 

public class GameState { 

    public PriorityQueue<GameNode> pendingGoals; 
    //public HashMap<Character,ArrayList<GameNode>> seenItems; // BUT there will need to be a priority to these... most important first
    public LinkedList<GameNode> unexploredLand; //used?
    public LinkedList<GameNode> unexploredWater; //used?
    public LinkedList<GameNode> seenRafts; 
    public LinkedList<GameNode> seenBombs; 
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
        //this.seenItems = new HashMap<Character,ArrayList<GameNode>>(); 
        this.unexploredLand = new LinkedList<GameNode>();
        this.unexploredWater = new LinkedList<GameNode>();
        this.seenRafts = new LinkedList<GameNode>();
        this.seenBombs = new LinkedList<GameNode>();
        this.axe = false;
        this.raft = false;
        this.key = false;
        this.treasure = false;
        this.numBombs = 0;
        this.currentNode = null; // filled in mapView
        this.currentDirection = 0; // north by default
        this.validTerrain = new HashSet<Character>(); //moved
        this.terrMngr = null;
        this.currAgentState = null;

    }

    public GameState cloneState(GameNode n) {

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

    public void enableWaterTravel() { //edit

        //System.out.println("ENABLING WATER");
        this.terrMngr.enableWaterTravel();
        //this.validTerrain.add('~');

    }

    public void enableLandTravel() { //edit

        //this.validTerrain.add(' ');
        this.terrMngr.enableLandTravel();

    }

    public void disableWaterTravel() { //edit

        //this.validTerrain.remove('~');
        this.terrMngr.disableWaterTravel();

    }

    public void disableLandTravel() { //edit

        //this.validTerrain.remove(' ');
        this.terrMngr.disableLandTravel();

    }

    public void addGoal(GameNode g) { //keep this, basic setter

        if(this.pendingGoals.contains(g)) return;

        System.out.println("ADDING GOAL: '"+g.getType()+"'");

        this.pendingGoals.add(g);

    }

    public boolean hasSeenRafts() {

        return this.seenRafts.size() > 0;

    }

    public void addSeenRaft(GameNode n) {

        if(this.seenRafts.contains(n)) return;

        this.seenRafts.add(n);

    }

    public boolean hasSeenBombs() {

        return this.seenBombs.size() > 0;

    }

    public void addSeenBomb(GameNode n) {

        if(this.seenBombs.contains(n)) return;

        this.seenBombs.add(n);

    }

    public void rememberNode(GameNode n) {

        switch(n.getType()) {

            case ' ': if(!n.isVisited()) this.addUnexploredLand(n); break;
            case '~': if(!n.isVisited()) this.addUnexploredWater(n); break;            
            case 'T': this.addSeenRaft(n); break;
            case 'd': this.addSeenBomb(n); break;
            case 'k': case '$': case 'a': this.addGoal(n); break; //what about going home?, duplicates too how is this called

        }

    }

    public void cleanUnexplored() {

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

    public boolean hasUnexploredWater(GameMap map) {

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

    public void addUnexploredWater(GameNode g) {

        if(this.unexploredWater.contains(g)) return;

        this.unexploredWater.add(g);

    }

    public boolean hasUnexploredLand(GameMap map) {

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

    public void addUnexploredLand(GameNode g) {

        if(this.unexploredLand.contains(g)) return;

        this.unexploredLand.add(g);

    }

    public boolean isOnWater() {

        return this.onWater;

    }

    public void setOnWater() {

        //or keep in ehre for cloning...
        this.onWater = true;

    }

    public void setOffWater() {

        // as above...
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

            System.out.println("Removing goal: '"+node.getType()+"'");
            if(node.getType() == 'd') this.seenBombs.remove(node);
            this.pendingGoals.remove(node);

        }

        if(node.getType() != '~') node.clearNode();
        node.setVisited();

    }

    //debug
    private void isBad(GameNode n) {

        Character c = n.getType();

        if(c != ' ') System.out.println("IS BAD wanted to move into '"+c+"'");

    }

    // could pass map as a param to this
    public void move(GameMap m) { //bit hacky inlcuding map here, maybe making up for bad logic elsewhere...

        GameNode curr = this.currentNode;
        int trueX = curr.getPoint().x;
        int trueY = curr.getPoint().y;
        char prevType = curr.getType();
        GameNode[][] map = m.getMap();

        switch(this.currentDirection) {

            case 0: 
                //this.isBad(map[trueX-1][trueY]);
                this.terrMngr.processTerrainChange(map[trueX-1][trueY].getType());
                this.currentNode = map[trueX-1][trueY];
                break;
            case 1:
                //this.isBad(map[trueX][trueY+1]);
                this.terrMngr.processTerrainChange(map[trueX][trueY+1].getType());
                this.currentNode = map[trueX][trueY+1];
                break;
            case 2:
                //this.isBad(map[trueX+1][trueY]);
                this.terrMngr.processTerrainChange(map[trueX+1][trueY].getType());
                this.currentNode = map[trueX+1][trueY];
                break;
            case 3:
                //this.isBad(map[trueX][trueY-1]);
                this.terrMngr.processTerrainChange(map[trueX][trueY-1].getType());
                this.currentNode = map[trueX][trueY-1];
                break;

        }

    }

    public int getDirection() {

        return this.currentDirection;

    }

    public void turnDirection(int d) { 

        this.currentDirection = Math.floorMod(this.currentDirection+d, 4);

    }

}
