import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class GoalManager {

    private Queue<GameNode> pendingGoals; //was priority

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

        return this.pendingGoals.peek(); 

    }
    
    public GameNode pursueBomb(GameMap map) { 

        LinkedList<GameNode> cands = new LinkedList<GameNode>();

        for(GameNode x : this.pendingGoals) {

            if(x.getType() == 'd') cands.add(x);

        }

        GameNode best = map.getNearest(cands);

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

        for(int i=0; i < arr.length-1; i++) {

            GameNode curr = arr[i], next = arr[i+1];
            LinkedList<GameNode> path = map.findOptimalPath(arr[i], arr[i+1]);

            System.out.println("BOMB LOGIC: finding path from '"+curr.getType()+" at "+curr.getPoint().toString()+" to '"+next.getType()+"' at "+next.getPoint().toString());

            if(path == null) return; 

            for(GameNode n: path) { 
                n.setPathWeight(0);
            }

        }

        //this.pendingGoals = (Queue<GameNode>) Arrays.asList(arr);

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
        
            GameNode next = this.pendingGoals.peek();

            if(next.getType() == 'd') {

                if(timeToOrderBombs()) {
                    //System.out.println("!!!!!!!!!!!!BOMB PATH TIME");
                    findOptimalBombPath(map);
                }
                n = this.pursueBomb(map);

            }else {

                n = this.getNextGoal();

            }

        }

        if(n != null) {

            LinkedList<GameNode> path = map.pursueGoal(n); 

            if(path != null) {

                actions.goToGoal(path); 
                this.pendingGoals.remove(n); //new
                return true;

            }

        }
        System.out.println("MNGR: GOAL PURSUIT FAILED");
        return false;

    }
    
}
