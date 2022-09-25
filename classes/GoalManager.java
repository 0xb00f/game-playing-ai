import java.util.Arrays;
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

        System.out.println("PEEKING GOAL '"+this.pendingGoals.peek().getType()+"'");

        return this.pendingGoals.peek(); //was poll... trialling new logic below removing only when success

    }

    public GameNode pursueBomb(GameMap map) { //well we do want the nearest....


        LinkedList<GameNode> cands = new LinkedList<GameNode>();

        for(GameNode x : this.pendingGoals) {

            if(x.getType() == 'd') cands.add(x);

        }

        //what do we want to do?? nearest reachable?
        GameNode best = map.getNearest(cands);
        //this.pendingGoals.remove(best);

        return best;

    }

    public void findOptimalBombPath(GameMap map) {

        
        GameNode[] arr = this.pendingGoals.toArray(new GameNode[0]);
        GameNode treasureNode = arr[arr.length-1];

        Arrays.sort(arr, new Comparator<GameNode>() {
            
            public int compare(GameNode a, GameNode b) {

                ManhattanDistanceHeuristic m = new ManhattanDistanceHeuristic();
                Integer aDist = m.score(treasureNode, a);
                Integer bDist = m.score(treasureNode, b);

                return bDist.compareTo(aDist);

            }

        });

        //this.pendingGoals.clear();
        //this.pendingGoals.addAll(Arrays.asList(arr));

        for(int i=0; i < arr.length-1; i++) {

            GameNode curr = arr[i], next = arr[i+1];
            Goal g = map.findOptimalPath(arr[i], arr[i+1]);

            System.out.println("BOMB LOGIC: finding path from '"+curr.getType()+" at "+curr.getPoint().toString()+" to '"+next.getType()+"' at "+next.getPoint().toString());

            if(g == null) return; 

            for(GameNode n: g.getPath()) { 
                n.setPathWeight(0);
            }

        }

    }

    private boolean timeToOrderBombs() {

        for(GameNode n : this.pendingGoals) {

            if(n.getType() == '$' || n.getType() == 'd') continue;
            return false;

        }

        return true;

    }
    
    public boolean pursueGoal(GameMap map, AgentActions actions) { 

        GameNode n = null;

        if(this.hasGoal()) { 
        
            //peek goal first see if next goal reachable...
            GameNode next = this.pendingGoals.peek();

            if(next.getType() == 'd') {

                if(timeToOrderBombs()) {
                    System.out.println("!!!!!!!!!!!!BOMB PATH TIME");
                    findOptimalBombPath(map);
                    //return true;
                }
                n = this.pursueBomb(map);

            }else {

                n = this.getNextGoal();

            }

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
