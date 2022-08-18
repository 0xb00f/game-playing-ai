import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue; 

public class GameState { 

    public PriorityQueue<Goal> pendingGoals; 
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
    public boolean onWater; //?? needs to be some way of ahdnling the logic of terrain
    public HashSet<Character> validTerrain;

    private class GoalCompare implements Comparator<Goal> {

        public int compare(Goal a, Goal b) {
    
            Integer g1 = a.getWeight();
            Integer g2 = b.getWeight();
            
            return g1.compareTo(g2);
            
        }

    }

    public GameState() {

        this.pendingGoals = new PriorityQueue<Goal>(new GoalCompare());
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
        this.validTerrain = new HashSet<Character>();
        //this.enableLandTravel(); //messes with cloning?

    }

    public GameState cloneState(GameNode n) {

        GameState clonedState = new GameState();
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

    public boolean isValidTerrain(char c) {

        return this.validTerrain.contains(c);

    }

    public void enableWaterTravel() {

        this.validTerrain.add('~');

    }

    public void enableLandTravel() {

        this.validTerrain.add(' ');

    }

    public void disableWaterTravel() {

        this.validTerrain.remove('~');

    }

    public void disableLandTravel() {

        this.validTerrain.remove(' ');

    }

    public boolean hasGoal() { //del

        return this.pendingGoals.size() > 0;

    }

    //neater? more logical?
    public boolean goalsAvailable() { //del

        return this.hasSeenBombs() || this.hasUnexploredLand() || this.hasUnexploredWater();

    }

    //neater? more logical?
    public void generateGoal() { //del

        Goal g = null;

        // exploration first
        if(this.hasUnexploredLand()) { 
            System.out.println("GOAL GENERATE: unexplored land");
            g = new Goal(this.getNearestLand());

        }else if(this.hasUnexploredWater()) { // could trap the agent if its a "puddle", how to avoid? ignroe edge case?
           System.out.println("GOAL GENERATE: unexplored water");
            g = new Goal(this.getNearestWater());

        }else if(this.hasSeenBombs()) {
            System.out.println("GOAL GENERATE: unexplored bombs");
            g = new Goal(this.getNearestBomb());
        }

        this.addGoal(g);

    }

    public boolean shouldPursueBomb() { //del

        Goal nextGoal = this.pendingGoals.peek();

        if(nextGoal == null) return false;

        return 6 < nextGoal.getWeight();

    }

    public void enqueueBombGoal() { //del

        if(this.hasSeenBombs()) {

            GameNode n = this.getNearestBomb();
            this.addGoal(new Goal(n));

        }

    }

    public void enqueueRaftGoal() { //del

        if(this.hasSeenRafts()) {

            this.addGoal(new Goal(this.getNearestRaft()));

        }

    }

    public Goal peekGoal() {

        return this.pendingGoals.peek();

    }

    public Goal getNextGoal() { //del?? why

        return this.pendingGoals.poll();

    }

    public void addGoal(Goal g) { //keep this, basic setter

        for(Goal x: this.pendingGoals) {

            if(x.getGoalNode() == g.getGoalNode()) return;

        }

        this.pendingGoals.add(g);

    }

    public boolean hasSeenRafts() {

        return this.seenRafts.size() > 0;

    }

    //abtracted 
    public GameNode getNearest(LinkedList<GameNode> list) { //del

        GameNode n = null;
        int minDist = 0;

        for(GameNode i: list) {

            if(i.isVisited()) continue;

            Point nodePos = i.getPoint();
            Point currPos = this.getCurrPos();
            int tmpDist = Math.abs(nodePos.x - currPos.x) + Math.abs(nodePos.y - currPos.y);

            if(tmpDist < minDist) {

                minDist = tmpDist;
                n = i;

            }

        }

        if(n == null) return null;

        list.remove(n);

        return n;

    }

    public GameNode getNearestRaft() { //del

        return this.getNearest(this.seenRafts);

    }

    public GameNode getNearestBomb() { //del

        return this.getNearest(this.seenBombs);

    }

    public GameNode getNearestLand() { //del

        return this.getNearest(this.unexploredLand);

    }

    public GameNode getNearestWater() { //del

        return this.getNearest(this.unexploredWater);

    }

    public boolean hasSeenBombs() {

        return this.seenBombs.size() > 0;

    }

    public void rememberNode(GameNode n) {

        switch(n.getType()) {

            case ' ': if(!n.isVisited()) this.unexploredLand.add(n); break;
            case '~': if(!n.isVisited()) this.unexploredWater.add(n); break;            
            case 'T': this.seenRafts.add(n); break;
            case 'd': this.seenBombs.add(n); break;
            case 'k': case '$': case 'a': this.pendingGoals.add(new Goal(n)); //what about going home?, duplicates too how is this called

        }

    }

    public boolean hasUnexploredWater() {

        return this.unexploredWater.size() > 0;

    }

    public void addUnexploredWater(GameNode g) {

        this.unexploredWater.add(g);

    }

    public boolean hasUnexploredLand() {

        return this.unexploredLand.size() > 0;

    }

    public void addUnexploredLand(GameNode g) {

        this.unexploredLand.add(g);

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

        if(b) {

            this.enableWaterTravel();

        }else{

            this.disableWaterTravel();

        }

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

    public Point getCurrPos() {

        return this.currentNode.getPoint();

    }

    public GameNode getCurrNode() {

        return this.currentNode;

    }

    public void setCurrNode(GameNode node) {

        this.currentNode = node;
        //update point or do sanity check?

    }

    public void updateCurrState(GameNode node) {

        switch(node.getType()) {

            case 'T' : this.setRaft(true); break; //where and when to unset raft?
            case 'k' : this.setKey(); break;
            case 'd' : this.addBomb(); break;
            case 'a' : this.setAxe(); break;
            case '$' : this.setTreasure(); break;
            //default : this.onWater = (node.getType() == '~'); //?

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
    public void move(GameNode[][] map) { //bit hacky inlcuding map here, maybe making up for bad logic elsewhere...

        GameNode curr = this.currentNode;
        int trueX = curr.getPoint().x;
        int trueY = curr.getPoint().y;

        switch(this.currentDirection) {

            case 0: 
                this.isBad(map[trueX-1][trueY]);
                this.currentNode = map[trueX-1][trueY];
                break;
            case 1:
                this.isBad(map[trueX][trueY+1]);
                this.currentNode = map[trueX][trueY+1];
                break;
            case 2:
                this.isBad(map[trueX+1][trueY]);
                this.currentNode = map[trueX+1][trueY];
                break;
            case 3:
               this.isBad(map[trueX][trueY-1]);
                this.currentNode = map[trueX][trueY-1];
                break;
            default: //debug
                System.out.println("For some reason, move() in state fell to default with direction: "+this.currentDirection);

        }

    }

    public int getDirection() {

        return this.currentDirection;

    }

    public void turnDirection(int d) { 

        this.currentDirection = Math.floorMod(this.currentDirection+d, 4);

    }

}
