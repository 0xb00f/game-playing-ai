import java.awt.Point;
import java.util.LinkedList;

public class GoalManager {

    /*
     * this is all called from agentEngine - higher level logic, ddeclutter agent states and gamestate
     */

    private AgentActions actions;
    private GameMap map;
    private GameState state;

    public GoalManager(AgentActions a, GameMap m, GameState s) {

        this.actions = a;
        this.map = m;
        this.state = s;

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

        this.state.pendingGoals.add(g);

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
            this.addGoal(new Goal(n));

        }

    }

    public void enqueueRaftGoal() { //del

        if(this.state.hasSeenRafts()) {

            this.addGoal(new Goal(this.getNearestRaft()));

        }

    }

    //add goal actions

    
}
