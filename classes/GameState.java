public class GameState { 

    private boolean axe;
    private boolean raft;
    private boolean key;
    private boolean treasure;
    private int numBombs;
    private GameNode currentNode;
    private int currentDirection; // 0=north, 1=east, 2=south, 3=west
    private boolean onWater; 
    private AgentState currAgentState; 

    public GameState(AgentState currState) {

        this.axe = false;
        this.raft = false;
        this.key = false;
        this.treasure = false;
        this.numBombs = 0;
        this.currentNode = null; 
        this.currentDirection = 0; // north by default
        this.currAgentState = currState; 

    }

    public void setAgentState(AgentState s) { 
 
        this.currAgentState = s;

    }

    public AgentState getAgentState() { 
 
        return this.currAgentState;

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

    public void updateCurrState(GameMap map, GoalManager gmngr, GameNode node) { 

        switch(node.getType()) {

            case 'k' : this.setKey(); break;
            case 'd' : this.addBomb(); break;
            case 'a' : this.setAxe(); break;
            case '$' : 
                this.setTreasure(); 
                gmngr.addGoal(map.getHome());
                break;

        }

        if(node.isItem()) gmngr.removeGoal(node); 
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
