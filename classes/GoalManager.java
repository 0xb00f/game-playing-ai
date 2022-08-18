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

    public void addGoal(Goal g) {

        this.state.addGoal(g);

    }

    public Goal getNextGoal() {

        return this.state.pendingGoals.poll();

    }

    //generate a goal from seen items, etc

    //goals available
    public boolean hasPotentialGoals() {

        return this.state.hasSeenBombs() || this.state.hasUnexploredLand() || this.state.hasUnexploredWater();

    }

    //enqueue raft goal

    //enqueue bomb goal

    
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

    
    public void enqueueBombGoal() { //del

        if(this.state.hasSeenBombs()) {

            GameNode n = this.getNearestBomb();
            //reachable? dowhile
            this.addGoal(new Goal(n));

        }

    }

    public void enqueueRaftGoal() { //del

        if(this.state.hasSeenRafts()) {
            //reachable? dowhile
            this.addGoal(new Goal(this.getNearestRaft()));

        }

    }

    //add goal actions

    //let the logic of choosing goals be here, agent engine jsut clicks a button that will somehow pursue a goal
    /*
     * 1. click the 'go to a goal' button
     * 2. if there's a reachable goal ready to go, poll it and go
     * 3. if no reachable goals on hand, if there are reachable bombs, go for the closest
     * 4. if nothing, return false, explore more
     */
    private boolean pursueGoal() {

        if(this.state.hasGoal()) {

            Goal g = this.state.peekGoal(); //should the queue just be gamenodes? and goals passed to actions?
            if(this.nodeReachable(g.getGoalNode())) {
             
                //Goal g = this.map.pursueGoal(g);
                //feed to actions
                //return true
                
            }

        }

        if(this.hasPotentialGoals()) {

            //get nearest (reachable!) in order of preference: bombs, land, water
            //send it off to astar
            //feed to actions
            // return true

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
