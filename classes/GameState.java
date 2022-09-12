import java.util.Comparator;
import java.util.PriorityQueue; 

public class GameState { 

    private PriorityQueue<GameNode> pendingGoals; //move to goalhandler
    private boolean axe;
    private boolean raft;
    private boolean key;
    private boolean treasure;
    private int numBombs;
    private GameNode currentNode;
    private int currentDirection; // 0=north, 1=east, 2=south, 3=west
    private boolean onWater; //only relevant for terrain so shift there?
    private AgentState currAgentState; //not needed?

    private class GoalCompare implements Comparator<GameNode> {

        public int compare(GameNode a, GameNode b) {
    
            Integer g1 = a.goalWeight();
            Integer g2 = b.goalWeight();
            
            return g1.compareTo(g2);
            
        }

    }

    public GameState(AgentState currState) {

        this.pendingGoals = new PriorityQueue<GameNode>(new GoalCompare()); //move
        this.axe = false;
        this.raft = false;
        this.key = false;
        this.treasure = false;
        this.numBombs = 0;
        this.currentNode = null; //new GameNode(0,0); // init currnode to start
        this.currentDirection = 0; // north by default
        this.currAgentState = currState; //needed? for terr decoupling

    }

    public void setAgentState(AgentState s) { 
 
        this.currAgentState = s;

    }

    public AgentState getAgentState() { 
 
        return this.currAgentState;

    }

    public void addGoal(GameNode g) { //del

        if(this.pendingGoals.contains(g)) return;

        this.pendingGoals.add(g);

    }
    
    public boolean isOnWater() { //del

        return this.onWater;

    }

    public void setOnWater() { //del

        this.onWater = true;

    }

    public void setOffWater() { //del

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

    public void updateCurrState(GameMap map, GoalManager gmngr, GameNode node) { //keep here

        switch(node.getType()) {

            case 'k' : this.setKey(); break;
            case 'd' : this.addBomb(); break;
            case 'a' : this.setAxe(); break;
            case '$' : 
                this.setTreasure(); 
                gmngr.addGoal(map.getHome());
                break;

        }

        if(node.isItem()) gmngr.removeGoal(node); //export
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
