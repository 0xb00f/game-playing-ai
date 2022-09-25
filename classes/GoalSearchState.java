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

    /*
      public GoalSearchState(GoalSearchState prev, GameNode node) {

        this.prev = prev;
        this.node = node;
        this.hasTreasure = state.hasTreasure();
        this.hasAxe = state.hasAxe();
        this.hasKey = state.hasKey();
        this.hasRaft = state.hasRaft();
        this.onWater = state.isOnWater();
        this.nBombs = state.getNumBombs();

      }

      public GoalSearchState(GameState state, GameNode node) {

        this.prev = null;
        this.node  = node;
        //as above

      }
     
     */

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

    public void updateState() {

        switch(this.node.getType()) {

            case 'k': this.setKey(); break;
            case 'd': this.addBomb(); break;
            case '*': 

                if(this.hasBomb()) {
                    this.useBomb();
                }
                break;

            case 'a': this.setAxe(); break;
            case 'T': 
                
                if(this.hasAxe()) {
                    this.setRaft(true); 
                }
                break;

            case '~' : 

                if(this.hasRaft()) {
                    this.setOnWater();
                }
                break;

            case ' ' : 

                if(this.isOnWater()) {
                    this.setRaft(false);
                    this.setOffWater();
                }
                break;

            case '$' : this.setTreasure(); 

        }

    }

    private boolean validMove(GameState state, TerrainManager tmngr) {

        switch(this.node.getType()) {

            case '-': return this.hasKey();
            case 'k': case 'd' : case 'a' : case ' ' : case '$' : return true;
            case '*': 

                if(!tmngr.isValidTerrain(state, state.getAgentState(), this.node.getType())) return false;
                return this.hasBomb();

            case 'T': return this.hasAxe;
            case '~' : return this.hasRaft;
            default : return false;

        }

    }

    private boolean updateStateOnMove(GameNode next, GoalSearchState nextstate, GameState state, TerrainManager tmngr) { //edit

        switch(next.getType()) {

            case '-': return nextstate.hasKey();
            case 'k': nextstate.setKey(); return true;
            case 'd': nextstate.addBomb(); return true;
            case '*': 
                //System.out.println("GOALSTATE: looking at bomb at "+next.getPoint().toString()+" with bombs="+nextstate.getNumBombs());

                if(!tmngr.isValidTerrain(state, state.getAgentState(), next.getType())) {
                    //System.out.println("GOALSTATE terr failed on '"+next.getType()+"' in "+state.getAgentState()+" and hasBomb="+state.hasBomb());
                    return false;
                }

                if(nextstate.hasBomb()) {
                    nextstate.useBomb();
                    //System.out.println("GOALSTATE BOMB USE SUCCESS");
                    return true;
                }else{
                    //System.out.println("GOALSTATE BOMB USE FAIL");
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
        if(north != null) {

            GoalSearchState northState = new GoalSearchState(this, north);
            
            if(northState.validMove(state,tmngr)) {
                northState.updateState();
                ret.add(northState); 
            }
             
            //if(updateStateOnMove(north,northState,state,tmngr)) ret.add(northState);

        }

        //south
        GameNode south = m.getSouthNeighbour(currNode);
        if(south != null) {
            
            GoalSearchState southState = new GoalSearchState(this, south);
            if(southState.validMove(state,tmngr)) {
                southState.updateState();
                ret.add(southState); 
            }
            //if(updateStateOnMove(south,southState,state,tmngr)) ret.add(southState);

        }

        //east
        GameNode east = m.getEastNeighbour(currNode);
        if(east != null ) {
            
            GoalSearchState eastState = new GoalSearchState(this, east);
            if(eastState.validMove(state,tmngr)) {
                eastState.updateState();
                ret.add(eastState); 
            }
            //if(updateStateOnMove(east,eastState,state,tmngr)) ret.add(eastState);

        }

        //west
        GameNode west = m.getWestNeighbour(currNode);
        if(west != null) {
            
            GoalSearchState westState = new GoalSearchState(this, west);
            if(westState.validMove(state,tmngr)) {
                westState.updateState();
                ret.add(westState); 
            }
            //if(updateStateOnMove(west,westState,state,tmngr)) ret.add(westState);

        }

        return ret;

    }

}
