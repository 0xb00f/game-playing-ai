import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class GoalManager {

    private PriorityQueue<GameNode> pendingGoals; 

    public GoalManager() {

        this.pendingGoals = new PriorityQueue<GameNode>(new GoalCompare());

    }

    private class GoalCompare implements Comparator<GameNode> {

        public int compare(GameNode a, GameNode b) {
    
            Integer g1 = a.goalWeight();
            Integer g2 = b.goalWeight();

            return g1.compareTo(g2);
            
        }

    }
    
    public boolean hasGoal() {

        return this.pendingGoals.size() > 0; 

    }

    public void addGoal(GameNode n) {

        if(this.pendingGoals.contains(n)) return;
        System.out.println("ADDING GOAL '"+n.getType()+"' at "+n.getPoint().toString());
        this.pendingGoals.add(n); 

    }

    public void removeGoal(GameNode n) {

        System.out.println("REMOVING GOAL '"+n.getType()+"'");

        this.pendingGoals.remove(n);

    }

    public GameNode getNextGoal() {

        System.out.println("DEQUEUING GOAL '"+this.pendingGoals.peek().getType()+"'");

        return this.pendingGoals.peek(); //was poll... trialling new logic below removing only when success

    }

    public GameNode pursueBomb(GameMap map) {


        LinkedList<GameNode> cands = new LinkedList<GameNode>();

        for(GameNode x : this.pendingGoals) {

            if(x.getType() == 'd') cands.add(x);

        }

        //what do we want to do?? nearest reachable?
        GameNode best = map.getNearest(cands);
        //this.pendingGoals.remove(best);

        return best;

    }
    
    public boolean pursueGoal(GameMap map, AgentActions actions) { 

        GameNode n = null;

        if(this.hasGoal()) { 
        
            //peek goal first see if next goal reachable...
            GameNode next = this.pendingGoals.peek();

            if(next.getType() == 'd') {

                n = this.pursueBomb(map);

            }else {

                n = this.getNextGoal();

            }

            /* 
            if(map.findReachable(next.getType()) == null) { 
                //System.out.println("GOAL OF TYPE '"+next.getType()+"' AT "+next.getPoint().toString()+" IS NOT REACHBLE!!!!!!!!!!!!!");
                return false;    
            }

            n = this.getNextGoal();*/
            //System.out.println("GOAL OF TYPE '"+n.getType()+"' AT "+n.getPoint().toString()+" IS REACHBLE");

        }

        if(n != null) {

            Goal g = map.pursueGoal(n); 

            if(g != null) {

                actions.goToGoal(g); 
                this.pendingGoals.remove(n); //new
                return true;

            }

        }
        System.out.println("MNGR: GOAL PURSUIT FAILED");
        return false;

    }
    
}
