import java.awt.Point;
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
    public boolean onWater; //?? needs to be some way of ahdnling the logic of terrain
    public HashSet<Character> validTerrain;

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

        System.out.println("ENABLING WATER");

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

    public boolean hasUnexploredWater() {

        return this.unexploredWater.size() > 0;

    }

    public void addUnexploredWater(GameNode g) {

        if(this.unexploredWater.contains(g)) return;

        this.unexploredWater.add(g);

    }

    public boolean hasUnexploredLand() {

        return this.unexploredLand.size() > 0;

    }

    public void addUnexploredLand(GameNode g) {

        if(this.unexploredLand.contains(g)) return;

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

            case 'k' : this.setKey(); break;
            case 'd' : this.addBomb(); break;
            case 'a' : this.setAxe(); break;
            case '$' : this.setTreasure(); break;
            //default : this.onWater = (node.getType() == '~'); //?

        }

        if(node.isItem()) {

            System.out.println("Removing goal: '"+node.getType()+"'");
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

    private void manangeTerrain(char curr, char next) {

        if(curr == '~' && next == ' ') { 
            this.disableWaterTravel();
            this.enableLandTravel();
            this.setRaft(false);
            this.onWater = false;
        }else if(curr == ' ' && next == '~') {
            this.enableWaterTravel();
            this.disableLandTravel();
            this.onWater = true;
        }

    }

    // could pass map as a param to this
    public void move(GameNode[][] map) { //bit hacky inlcuding map here, maybe making up for bad logic elsewhere...

        GameNode curr = this.currentNode;
        int trueX = curr.getPoint().x;
        int trueY = curr.getPoint().y;
        char prevType = curr.getType();

        switch(this.currentDirection) {

            case 0: 
                this.isBad(map[trueX-1][trueY]);
                this.manangeTerrain(prevType, map[trueX-1][trueY].getType());
                this.currentNode = map[trueX-1][trueY];
                break;
            case 1:
                this.isBad(map[trueX][trueY+1]);
                this.manangeTerrain(prevType, map[trueX][trueY+1].getType());
                this.currentNode = map[trueX][trueY+1];
                break;
            case 2:
                this.isBad(map[trueX+1][trueY]);
                this.manangeTerrain(prevType, map[trueX+1][trueY].getType());
                this.currentNode = map[trueX+1][trueY];
                break;
            case 3:
                this.isBad(map[trueX][trueY-1]);
                this.manangeTerrain(prevType, map[trueX][trueY-1].getType());
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
