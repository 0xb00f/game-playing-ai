import java.util.LinkedList;

public class GoalSearchState {
    
    private GoalSearchState prev;
    private GameNode node;
    private int hValue; 
    private int gValue; //path cost

    //TO ADD - remove localstate
    private boolean hasTreasure;
    private boolean hasAxe;
    private boolean hasRaft;
    private boolean hasKey;
    private boolean onWater;
    private int nBombs;

    public GoalSearchState(GoalSearchState prev, GameNode node) { //two constructors?

        this.prev = prev;
        this.node = node;

        if(prev != null) this.setState(prev);

    }

    public void initState(GameState state) { //works initially but what about in searching???

        this.hasTreasure = state.hasTreasure();
        this.hasAxe = state.hasAxe();
        this.hasKey = state.hasKey();
        this.hasRaft = state.hasRaft();
        this.onWater = state.isOnWater();
        this.nBombs = state.getNumBombs();

    }

    public void setState(GoalSearchState state) { //above fix?

        this.hasTreasure = state.hasTreasure();
        this.hasAxe = state.hasAxe();
        this.hasKey = state.hasKey();
        this.hasRaft = state.hasRaft();
        this.onWater = state.isOnWater();
        this.nBombs = state.getNumBombs();

    }

    public boolean hasAxe() {

        return this.hasAxe;

    }

    public void setAxe() {

        this.hasAxe = true;

    }

    public boolean hasKey() {

        return this.hasKey;

    }

    public void setKey() {

        this.hasKey = true;

    }

    public boolean hasRaft() {

        return this.hasRaft;

    }

    public void setRaft(boolean b) {

        this.hasRaft = b;

    }

    public boolean hasBomb() {

        return this.nBombs > 0;

    }

    public int getNumBombs() { 

        return this.nBombs;

    }

    public void addBomb() {

        this.nBombs++;

    }

    public void useBomb() {

        this.nBombs--;

    }

    public GoalSearchState getPrev() {

        return this.prev;

    }

    public int getF() {

        return this.gValue+this.hValue; 

    }

    public void setG(int g) {

        this.gValue = g;

    }

    public int getG() {

        return this.gValue;

    }

    public int getH() {

        return this.hValue;

    }

    public void setH(int h) {

        this.hValue = h;

    }

    public GameNode getNode() {

        return this.node;

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

    public boolean hasTreasure() {

        return this.hasTreasure;

    }

    public void setTreasure() {

        this.hasTreasure = true;

    }

    private boolean updateStateOnMove(GameNode next, GoalSearchState nextstate, GameState state, TerrainManager tmngr) { //edit

        switch(next.getType()) {

            case '-': return nextstate.hasKey();
            case 'k': nextstate.setKey(); return true;
            case 'd': nextstate.addBomb(); return true;
            case '*': 

                if(!tmngr.isValidTerrain(state, state.getAgentState(), next.getType())) {
                    System.out.println("GOALSTATE terr failed on '"+next.getType()+"' in "+state.getAgentState()+" and hasBomb="+state.hasBomb());
                    return false;
                }

                if(nextstate.hasBomb()) {
                    nextstate.useBomb();
                    return true;
                }else{
                    return false;
                }

            case 'a': nextstate.setAxe(); return true;
            case 'T': 
                
                if(nextstate.hasAxe()) {
                    nextstate.setRaft(true); 
                    return true; //power through trees pursuing goal
                }else{
                    return false;
                }

            case '~' : 

                if(nextstate.hasRaft()) {
                    nextstate.setOnWater();
                    return true;
                }else{
                    return false;
                }

            case ' ' : 

                if(nextstate.isOnWater()) {
                    nextstate.setRaft(false);
                    nextstate.setOffWater();
                }
                return true; //if onwater, disable raft, return true

            case '$' : nextstate.setTreasure(); return true;
            default : return false;


        }

    }

    public LinkedList<GoalSearchState> genSuccessors(GameMap map, GameState state, TerrainManager tmngr) {

        LinkedList<GoalSearchState> ret = new LinkedList<GoalSearchState>();
        GameNode currNode = this.node;

        Graph m = map.getMap();

        //north
        GameNode north = m.getNorthNeighbour(currNode);
        GoalSearchState northState = new GoalSearchState(this, north);
        if(north != null && updateStateOnMove(north,northState,state,tmngr)) {

            northState.setG(this.gValue + north.nodeWeight()); //this si done in searchmap....
            ret.add(northState);

        }

        //south
        GameNode south = m.getSouthNeighbour(currNode);
        GoalSearchState southState = new GoalSearchState(this, south);
        if(south != null && updateStateOnMove(south,southState,state,tmngr)) {

            southState.setG(this.gValue + south.nodeWeight());
            ret.add(southState);

        }

        //east
        GameNode east = m.getEastNeighbour(currNode);
        GoalSearchState eastState = new GoalSearchState(this, east);
        if(east != null && updateStateOnMove(east,eastState,state,tmngr)) {

            eastState.setG(this.gValue + east.nodeWeight());
            ret.add(eastState);

        }

        //west
        GameNode west = m.getWestNeighbour(currNode);
        GoalSearchState westState = new GoalSearchState(this, west);
        if(west != null && updateStateOnMove(west,westState,state,tmngr)) {

            westState.setG(this.gValue + west.nodeWeight());
            ret.add(westState);

        }

        return ret;

    }

}
