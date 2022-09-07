import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;


public class SearchGameMap {
    
    private GameState state;

    public SearchGameMap(GameState state) {

        this.state = state;

    }

    /*
     * USES:
     * 1. to return location of nearest reachable raft, land, water (arg should be a char)
     */
    public int floodFill(GameMap map, GameNode node) { //char target

        int nItems = 0;
        HashSet<GameNode> seen = new HashSet<GameNode>();
        ArrayDeque<GameNode> q = new ArrayDeque<GameNode>();

        q.push(this.state.getCurrNode());

        //System.out.println("FLOODFILL: looking for node '"+node.getType()+"'");

        while(!q.isEmpty()) {

            GameNode curr = q.poll();
            seen.add(curr);

            if(curr == node) {
                
                //System.out.println("NODE OF TYPE '"+node.getType()+"' REACHABLE");
                return 1;

            }
            
            for(GameNode next: map.getNeighbours(curr)) {

                if(!seen.contains(next)) {
                    if(node != null && (node.getType() == next.getType() || this.state.isValidTerrain(next.getType()))) q.add(next);
                    if(next.isItem() || (this.state.hasAxe() && next.isTree())) q.add(next);
                }

            }

        }

        return nItems;

    }

    public LinkedList<GameNode> pathBFS(GameMap map, GameNode start, GameNode end) {

        //if(!map.sanity()) System.exit(1); //debug

        //MAP LOSING POSITION!!! tried chanigng to point here to no avail

        ArrayDeque<Goal> queue = new ArrayDeque<Goal>();
        HashSet<Point> seen = new HashSet<Point>(); 
        Goal g = new Goal(start);
        queue.add(g);

        while(!queue.isEmpty()) {

            Goal curr = queue.poll();
            GameNode currNode = curr.getGoalNode();
            seen.add(currNode.getPoint());

            if(currNode == end) {

                return curr.getPath();

            }
            System.out.println("BFS CURR NODE IS "+currNode.getPoint().toString()+" of type '"+currNode.getType()+"'");
            for(GameNode n : map.getNeighbours(currNode)) {

                if(seen.contains(n.getPoint())) continue;
                if(n.outOfBounds(this.state)) continue;
                System.out.println("ENQ NEIGHBOUR "+n.getPoint().toString()+" of type '"+n.getType()+"'");
                Goal next = new Goal(n);
                next.extendPath(curr.getPath());
                next.addToPath(n);
                queue.add(next);

            }

        }

        return null; //not reachable

    }

    public Goal constructPath(GameMap map, LinkedList<GameNode> nodes) {

        //still need to think about paths form nodes to themselves, be neat about constructing, check the logic

        Goal g = new Goal(nodes.getLast());// get(nodes.size()-1));
        GameNode prev = nodes.remove(0);
        LinkedList<GameNode> subPath = null;

        while(!nodes.isEmpty()) {
            GameNode curr = nodes.remove(0);
            subPath = this.pathBFS(map, prev, curr);
            //debug
            if(subPath == null) continue; //this would mean its unreachable though..
            g.extendPath(subPath);
            prev = curr;
        }

        //System.out.println("CONST PATH:");
        //for(GameNode x: g.getPath()) System.out.print(x.getType()+",");
        //System.out.println("");
        return g;

    }

    public Goal exploreDFS(GameMap map, char terrain) { 

        ArrayDeque<GameNode> stack = new ArrayDeque<GameNode>();
        HashSet<GameNode> seen = new HashSet<GameNode>(); 
        LinkedList<GameNode> toExplore = new LinkedList<GameNode>();

        toExplore.add(this.state.getCurrNode());
        stack.push(this.state.getCurrNode());

        while(!stack.isEmpty()) {

            GameNode curr = stack.pop();

            if(!seen.contains(curr)) { 

                seen.add(curr);

                if(!curr.isVisited()) toExplore.add(curr); //shouldn't add curr node!

                //System.out.println("EXPLORE: looking at neighbours of ndoe at "+curr.getPoint().toString());

                for(GameNode n : map.getNeighbours(curr)) {

                    //System.out.println("EXPLORE: checking node of type '"+n.getType()+"' outofbounds="+n.outOfBounds(this.state));

                    if(seen.contains(n) || n.outOfBounds(this.state)) continue;

                    System.out.println("EXPLORING NODE TYPE '"+n.getType()+"'");

                    stack.push(n);

                }

            }

        }

        return constructPath(map,toExplore);

    }

    // Astar - this will employ states and search over them STRATEGY
    // it will figure out SEQUENCES of goals... these will be enqueued in state
    // it is a deep search that may encompass multiple goals (e.g. get raft, open door, get bomb)

    /*
     * Do i want this to pursue a single goal, or find a path that generates multiple goals?
     * first option simpler... but if a goal is across water we need to account for subgoals (or if bombs needed)
     * always choose a path that makes the treasure easier to obtain... h = hValue - treasureCost?
     */

     /*
      * one solution is that astar search is called, assesses seen goals by whether they are reachable, picks one 
      * based on weight/reachability, extracting its path. THEN performs astar search to that goal, so the goal is 
      * picked dynamically based on what is known. Goals do not involve trees, as they are a means to an end. There can be
      * a method that picks the nearest tree and goes to collect it, inserted as a subgoal beforehand.  
      */

    private class GoalStateCompare implements Comparator<GoalSearchState> {

        public int compare(GoalSearchState a, GoalSearchState b) {
    
            Integer g1 = a.getF();
            Integer g2 = b.getF();
            
            return g1.compareTo(g2); //??
            
        }

    }

    /*
     * possibly good idea to adapt this to pursue a list of goals, through them, to the last....
     * means adapting heuristic to calculate the lowest cost path through ALL goals... if walls
     * are increased cost this SHOULD take care of level 8 etc!!!!
     */
    
    public Goal astarSearch(GameMap map, GameNode end, Heuristic h) {

        System.out.println("ASTAR: start on goal with type '"+end.getType()+"' at "+end.getPoint().toString());

        PriorityQueue<GoalSearchState> open = new PriorityQueue<GoalSearchState>(new GoalStateCompare());
        HashSet<GameNode> closed = new HashSet<GameNode>();
        //HashMap<GoalSearchState,GoalSearchState> parent = new HashMap<GoalSearchState,GoalSearchState>(); //neded?
        //HashMap<GoalSearchState,Integer> gValues = new HashMap<GoalSearchState,Integer>(); //needed?
        HashMap<GameNode,Integer> fValues = new HashMap<GameNode,Integer>();

        GameNode startNode = this.state.getCurrNode();
        GoalSearchState start = new GoalSearchState(null,startNode);

        start.setState(this.state);
        start.setG(startNode.nodeWeight());
        start.setH(h.score(start.getState(), startNode, end));
        open.add(start);
        //gValues.put(start, 0);
        fValues.put(start.getNode(),start.getG()+h.score(start.getState(), startNode, end));
        //parent.put(start,null);
        int maxIters = 0;

        while(!open.isEmpty()) {

            //if(maxIters == 9999) System.out.println("MaxIters fail");

            //for(GoalSearchState x: open) System.out.println("PQ: '"+x.getNode().getType()+"' with weight "+x.getF()+" at "+x.getNode().getPoint().toString());

            GoalSearchState curr = open.poll();
            //System.out.println("ASTAR visiting '"+curr.getNode().getType()+"' at "+curr.getNode().getPoint().toString());
            closed.add(curr.getNode());

            // we're at the goal 
            if(curr.getNode() == end) {
                
                LinkedList<GameNode> path = new LinkedList<GameNode>();
                
                while(curr != null) {

                    path.push(curr.getNode());         
                    curr = curr.getPrev();   

                }
                System.out.println("ASTAR SUCCESS");
                Goal g = new Goal(end);
                g.extendPath(path);
                return g;

            }

            // look at successors
            for(GoalSearchState next: curr.genSuccessors(map)) {

                //if(closed.contains(next.getNode())) continue;

                int tmpF = curr.getG() + next.getNode().nodeWeight() + h.score(curr.getState(),next.getNode(),end); //all equal edges UNLESS GOING THROUGH A WALL
                
                if(tmpF < fValues.getOrDefault(next.getNode(), Integer.MAX_VALUE)) {

                    //parent.put(next,curr); //needed if state not node, and path in state?
                    //gValues.put(next,curr.getG()+1);
                    next.setG(curr.getG() + next.getNode().nodeWeight());
                    next.setH(h.score(curr.getState(),next.getNode(),end));
                    fValues.put(next.getNode(),tmpF);
                    open.add(next);

                }

            }

        }
        System.out.println("ASTAR FAILED to go to '"+end.getType()+"'");
        return null; //goal unreachable

    }
    
}
