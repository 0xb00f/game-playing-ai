import java.util.Comparator;

public class GoalManager {

    //private PriorityQueue<GameNode> pendingGoals; 
    private AgentActions actions;
    private GameMap map;
    private GameState state;

    public GoalManager(AgentActions a, GameMap m, GameState s) {

        //this.pendingGoals = new PriorityQueue<GameNode>(new GoalCompare());
        this.actions = a;
        this.map = m;
        this.state = s;

    }

    private class GoalCompare implements Comparator<GameNode> {

        public int compare(GameNode a, GameNode b) {
    
            Integer g1 = a.goalWeight();
            Integer g2 = b.goalWeight();
            
            return g1.compareTo(g2);
            
        }

    }
    
    public boolean hasGoal() {

        return this.state.pendingGoals.size() > 0; //remove state

    }

    public void addGoal(GameNode g) {

        this.state.addGoal(g); //now local

    }

    public GameNode getNextGoal() {

        return this.state.pendingGoals.poll(); //remove state

    }
    
    public boolean pursueGoal() { 

        /*
         * overall logic
         * NOTE: home goal auto added when treaure collected, so just need to pursue goal
         * -if there is a goal, pursue it
         * -if that fails, see if an item is reachable and pursue that if so
         * -return otherwise
         */

        GameNode n = null;

        if(this.hasGoal()) n = this.getNextGoal();

        if(n != null) {

            Goal g = this.map.pursueGoal(n);

            if(g != null) {

                this.actions.goToGoal(g);
                return true;

            }

        }else{

            //see if an item is reachable
            n = this.map.findReachable('d');
            if(n != null) {

                Goal g = this.map.pursueGoal(n);

                if(g != null) {

                    this.actions.goToGoal(g);
                    return true;

                }

            }

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
