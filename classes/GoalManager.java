import java.awt.Point;
import java.util.LinkedList;

public class GoalManager {

    //pending goals...
    private AgentActions actions;
    private GameMap map;
    private GameState state;
    private Heuristic manhattanDistance;

    public GoalManager(AgentActions a, GameMap m, GameState s) {

        this.actions = a;
        this.map = m;
        this.state = s;
        this.manhattanDistance = new ManhattanDistanceHeuristic(); //del

    }

    //check goal node is reachable
    public boolean nodeReachable(GameNode n) { //del

        return this.map.reachable(n) != null;

    }

    //get nearest node type
    public GameNode getNearest(LinkedList<GameNode> list) { //del

        GameNode n = null;
        int minDist = Integer.MAX_VALUE;

        for(GameNode m: list) {

            if(!this.map.isReachable(m)) { 
                continue;
            }

            //System.out.println("REACHABLE NODE OF TYPE'"+m.getType()+" FOUND");

            Point nodePos = m.getPoint();
            Point currPos = this.state.getCurrNode().getPoint();
            int tmpDist = Math.abs(nodePos.x - currPos.x) + Math.abs(nodePos.y - currPos.y);

            if(tmpDist < minDist) {

                minDist = tmpDist;
                n = m;

            }

        }

        if(n == null) return null;

        list.remove(n);

        return n;

    }

    public boolean hasGoal() {

        return this.state.pendingGoals.size() > 0;

    }

    public void addGoal(GameNode g) {

        //more complex with bomb logic, they have an empty path....

        this.state.addGoal(g);

    }

    public GameNode getReachable(LinkedList<GameNode> list) { //del

        GameNode n = null;

        for(GameNode m : list) {

            if(this.nodeReachable(m)) {
                n = m;
                list.remove(m);
                break;
            }

        }

        return n;

    }

    public GameNode getNextGoal() {

        return this.state.pendingGoals.poll();

    }

    public boolean hasPotentialGoals() { //del

        return this.state.hasSeenBombs() || this.state.hasUnexploredLand(this.map) || this.state.hasUnexploredWater(this.map);

    }
    
    public GameNode getNearestRaft() { //del

        GameNode n = this.getNearest(this.state.seenRafts);

        return n;

    }

    public GameNode getNearestBomb() { //change

        GameNode n = this.getNearest(this.state.seenBombs);
        return n;

    }

    public GameNode getNearestLand() { //change

        return this.getNearest(this.state.unexploredLand);

    }

    public GameNode getNearestWater() { //change

        return this.getNearest(this.state.unexploredWater);

    }

    
    public boolean enqueueBombGoal() { //del

        if(this.state.hasSeenBombs()) {

            GameNode n = this.getNearestBomb();
            this.addGoal(n);
            return true;

        }

        return false;

    }

    public boolean getRaft() { //change to floodfill

        GameNode n = this.getNearestRaft();
        Goal g = this.map.pursueGoal(n);

        if(g != null) {
            System.out.println("GOING FOR RAFT");
            this.actions.goToGoal(g);
        }else{
            return false;
        }

        return true;

    }

    public boolean goToWater() { //floodfill

        GameNode n = this.getNearestWater();
        Goal g = this.map.pursueGoal(n);

        if(g != null) {
            System.out.println("GOING TO WATER");
            this.actions.goToGoal(g);
        }else{
            return false;
        }

        return true;

    }

    public boolean goToLand() { //floodfill

        GameNode n = this.getNearestLand();
        Goal g = this.map.pursueGoal(n);

        if(g != null) {
            System.out.println("GOING TO LAND");
            this.actions.goToGoal(g);
        }else{
            return false;
        }

        return true;

    }

    public boolean pursueGoal() { //think better logic

        GameNode n = null;

        if(this.state.hasTreasure() && !this.state.pendingGoals.contains(this.map.getHome())) {

            this.addGoal(this.map.getHome());

        }

        if(this.hasGoal()) { 
            n = this.getNextGoal();
            System.out.println("GETTING GOAL '"+n.getType()+"'");
        }else if(this.state.hasSeenBombs()) { 
            System.out.println("GETTING BOMB");
            n = this.getNearestBomb();
        }

        if(n != null) {

            System.out.println("GOING FOR GOAL '"+n.getType()+"'");
            Goal g = this.map.pursueGoal(n);
            if(g != null) {
                this.actions.goToGoal(g);
            }

        }

        return n != null;

    }

    //late night stoned idea...
    /*
     * path-clearing heuristic that, every time a tree or piece of wall or something not
     * terrain is removed, counts the amount of goals now reachable because of that removed node
     * if a new item is reachable by removing it, its a lower-cost path! 
     */
    
}
