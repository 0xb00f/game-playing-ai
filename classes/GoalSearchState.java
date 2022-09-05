import java.awt.Point;
import java.util.LinkedList;

public class GoalSearchState {
    
    private GameState localState;
    private GoalSearchState prev;
    private GameNode node;
    private int hValue; 
    private int gValue; //path cost

    public GoalSearchState(GoalSearchState prev, GameNode node) {

        this.localState = null;
        this.prev = prev;
        this.node = node;

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


    public void setState(GameState state) {

        this.localState = state.cloneState(this.node);

    }

    public GameState getState() {

        return this.localState;

    }

    public GameNode getNode() {

        return this.node;

    }

    private boolean updateStateOnMove(GameNode next, GameState nextstate) {

        switch(next.getType()) {

            case 'k': nextstate.setKey(); return true;
            case 'd': nextstate.addBomb(); return true;
            case '*': 

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
                    return true; //power through trees? what if they're needed?
                }else{
                    return false;
                }
            case '~' : return nextstate.hasRaft(); //changed these after terrain mngr.... might wanna come back, clone if probs
            case ' ' : {
                if(nextstate.isOnWater()) {
                    nextstate.setRaft(false);
                }
                return true; //if onwater, disable raft, return true
            }
            case '$' : nextstate.setTreasure(); return true;
            default : return false;


        }

    }

    public LinkedList<GoalSearchState> genSuccessors(GameMap map) {

        LinkedList<GoalSearchState> ret = new LinkedList<GoalSearchState>();
        GameNode currNode = this.node;
        Point p = currNode.getPoint();
        int x = p.x, y = p.y;

        GameNode[][] m = map.getMap();

        //north
        GameNode north = m[x-1][y];
        GameState northState = this.localState.cloneState(north);
        if(updateStateOnMove(north,northState)) {
            GoalSearchState nextNorth = new GoalSearchState(this, north);
            nextNorth.setG(this.gValue + north.nodeWeight());
            nextNorth.setState(northState);
            ret.add(nextNorth);
        }

        //south
        GameNode south = m[x+1][y];
        GameState southState = this.localState.cloneState(south);
        if(updateStateOnMove(south,southState)) {
            GoalSearchState nextSouth = new GoalSearchState(this, south);
            nextSouth.setState(southState);
            ret.add(nextSouth);
        }

        //east
        GameNode east = m[x][y+1];
        GameState eastState = this.localState.cloneState(east);
        if(updateStateOnMove(east,eastState)) {
            GoalSearchState nextEast = new GoalSearchState(this, east);
            nextEast.setState(eastState);
            ret.add(nextEast);
        }

        //west
        GameNode west = m[x][y-1];
        GameState westState = this.localState.cloneState(west);
        if(updateStateOnMove(west,westState)) {
            GoalSearchState nextWest = new GoalSearchState(this, west);
            nextWest.setState(westState);
            ret.add(nextWest);
        }

        return ret;

    }


}
