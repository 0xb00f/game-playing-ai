import java.awt.Point;
import java.util.LinkedList;

public class GoalManager {

    /*
     * this is all called from agentEngine - higher level logic, ddeclutter agent states and gamestate
     */

    private AgentActions actions;
    private GameMap map;
    private GameState state;
    private Heuristic manhattanDistance;

    public GoalManager(AgentActions a, GameMap m, GameState s) {

        this.actions = a;
        this.map = m;
        this.state = s;
        this.manhattanDistance = new ManhattanDistanceHeuristic();

    }

    //check goal node is reachable
    public boolean nodeReachable(GameNode n) {

        return this.map.reachable(n) != null;

    }

    //get nearest node type
    public GameNode getNearest(LinkedList<GameNode> list) {

        GameNode n = null;
        int minDist = 0;

        for(GameNode m: list) {

            if(m.isVisited()) continue;
            if(!this.nodeReachable(m)) continue;

            Point nodePos = m.getPoint();
            Point currPos = this.state.getCurrPos();
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

        this.state.addGoal(g);

    }

    public GameNode getReachable(LinkedList<GameNode> list) { //?

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

        /*
        GameNode n = null;

        for(GameNode m : this.state.pendingGoals) {

            if(this.nodeReachable(m)) {
                n = m;
                this.state.pendingGoals.remove(m);
                break;
            }

        }

        return n;*/
        return this.state.pendingGoals.poll();

    }

    //generate a goal from seen items, etc

    //goals available
    public boolean hasPotentialGoals() {

        return this.state.hasSeenBombs() || this.state.hasUnexploredLand() || this.state.hasUnexploredWater();

    }
    
    public GameNode getNearestRaft() { //del

        return this.getNearest(this.state.seenRafts);

    }

    public GameNode getNearestBomb() { //del

        return this.getNearest(this.state.seenBombs);

    }

    public GameNode getNearestLand() { //del

        return this.getNearest(this.state.unexploredLand);

    }

    public GameNode getNearestWater() { //del

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

    public boolean enqueueRaftGoal() { //del

        if(this.state.hasSeenRafts()) {

            this.addGoal(this.getNearestRaft());
            return true;

        }

        return false;

    }

    public boolean pursueGoal() {

        GameNode n = null;

        if(this.state.hasTreasure() && !this.state.pendingGoals.contains(this.map.getHome())) {

            this.addGoal(this.map.getHome());

        }

        if(this.hasGoal()) { 
            System.out.println("GETTING GOAL");
            n = this.getNextGoal();
        } else if(this.state.hasSeenBombs()) { 
            System.out.println("GETTING BOMB");
            n = this.getNearestBomb();
        }

        if(n != null) {

            System.out.println("GOING FOR GOAL '"+n.getType()+"'");
            Goal g = this.map.pursueGoal(n);
            this.actions.goToGoal(g);
            return true;

        }

        return false;

    }

    //late night stoned idea...
    /*
     * path-clearing heuristic that, every time a tree or piece of wall or something not
     * terrain is removed, counts the amount of goals now reachable because of that removed node
     * if a new item is reachable by removing it, its a lower-cost path! 
     */
    
}
