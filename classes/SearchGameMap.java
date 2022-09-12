import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;


public class SearchGameMap {
    
    private GameState state;
    private TerrainManager terrMngr;

    public SearchGameMap(GameState state, TerrainManager tmngr) {

        this.state = state;
        this.terrMngr = tmngr;

    }

    public GameNode reachableItem(GameMap map, GameState state, char target) {

        HashSet<GameNode> seen = new HashSet<GameNode>();
        ArrayDeque<GameNode> q = new ArrayDeque<GameNode>();

        q.push(this.state.getCurrNode());

        while(!q.isEmpty()) {

            GameNode curr = q.poll();
            seen.add(curr);

            if(curr.getType() == target) {
                
                if(!curr.isVisited() || curr == map.getHome()) {

                    return curr; //not visited in general, but especially for terrain

                }
            
            }
            
            for(GameNode next: map.getNeighbours(curr)) {

                /*
                 * separating the logic out in this guard:
                 * seen - keeping track
                 * gamenode - whether it is outofbounds
                 * terrain - whether it is valid terrain for the agentstate
                 */
                if(seen.contains(next)) continue; // || next.outOfBounds(this.state)
                if(!this.terrMngr.isValidTerrain(state, state.getAgentState(), next.getType())) {
                    //System.out.println("FLOODFILL: terrain validity failed: '"+next.getType()+"'");
                    continue;
                }

                q.add(next);

            }

        }

        return null;

    }

    public LinkedList<GameNode> pathBFS(GameMap map, GameNode start, GameNode end) {

        ArrayDeque<Goal> queue = new ArrayDeque<Goal>();
        HashSet<Point> seen = new HashSet<Point>();  //change back
        Goal g = new Goal(start);
        queue.add(g);

        while(!queue.isEmpty()) {

            Goal curr = queue.poll();
            GameNode currNode = curr.getGoalNode();
            seen.add(currNode.getPoint());

            if(currNode == end) {

                return curr.getPath();

            }

            for(GameNode n : map.getNeighbours(currNode)) {

                if(seen.contains(n.getPoint()) || n.outOfBounds(this.state) || !this.terrMngr.isValidTerrain(state, state.getAgentState(), n.getType())) continue; 

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

            //System.out.println("EXPLORE GOING TO VISIT "+curr.getPoint().toString());

            if(!seen.contains(curr)) { 

                seen.add(curr);

                if(!curr.isVisited()) toExplore.add(curr); 

                //System.out.println("curr has "+map.getNeighbours(curr).size()+" neighbours");

                for(GameNode n : map.getNeighbours(curr)) {

                    if(seen.contains(n) || n.outOfBounds(this.state)) continue; 
                    if(!this.terrMngr.isValidTerrain(state, state.getAgentState(), n.getType())) {
                        //System.out.println("SEARCH: terrain validity failed");
                        continue;
                    }

                    stack.push(n);

                }

            }

        }

        return constructPath(map,toExplore);

    }

    private class GoalStateCompare implements Comparator<GoalSearchState> {

        public int compare(GoalSearchState a, GoalSearchState b) {
    
            Integer g1 = a.getF();
            Integer g2 = b.getF();
            
            return g1.compareTo(g2); 
            
        }

    }

    public Goal astarSearch(GameMap map, GameNode end, Heuristic h) {

        System.out.println("ASTAR: start on goal with type '"+end.getType()+"' at "+end.getPoint().toString());

        PriorityQueue<GoalSearchState> open = new PriorityQueue<GoalSearchState>(new GoalStateCompare());
        HashSet<GameNode> closed = new HashSet<GameNode>();
        HashMap<GameNode,Integer> fValues = new HashMap<GameNode,Integer>();

        GameNode startNode = this.state.getCurrNode();
        GoalSearchState start = new GoalSearchState(null,startNode);

        start.initState(this.state); //initState
        start.setG(startNode.nodeWeight());
        start.setH(h.score(startNode, end)); //this.state
        open.add(start);
        fValues.put(start.getNode(),start.getG()+h.score(startNode, end));

        while(!open.isEmpty()) {

            GoalSearchState curr = open.poll();
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

                int tmpF = curr.getG() + next.getNode().nodeWeight() + h.score(next.getNode(),end); //separate function once item logic in place...
                
                if(tmpF < fValues.getOrDefault(next.getNode(), Integer.MAX_VALUE)) {

                    next.setG(curr.getG() + next.getNode().nodeWeight());
                    next.setH(h.score(next.getNode(),end));
                    fValues.put(next.getNode(),tmpF);
                    open.add(next);

                }

            }

        }

        System.out.println("ASTAR FAILED to go to '"+end.getType()+"'");
        return null; 

    }
    
}
