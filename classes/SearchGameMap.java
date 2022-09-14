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

        //System.out.println("FLOODFILL BEGIN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        HashSet<GameNode> seen = new HashSet<GameNode>();
        ArrayDeque<GameNode> q = new ArrayDeque<GameNode>();

        q.push(this.state.getCurrNode());
        seen.add(this.state.getCurrNode());

        while(!q.isEmpty()) {

            GameNode curr = q.poll();
            //System.out.println("FLOODFILL: visiting: '"+curr.getPoint().toString()+"'");

            if(curr.getType() == target) {
                
                //if(!curr.isVisited() || (state.hasTreasure() && curr == map.getHome())) { //too restrictive, cant get to water s0
                    if(curr.getType() == ' ' || curr.getType() == '~') {

                        if(!curr.isVisited()) return curr;

                    }else{
                    
                        return curr; //not visited in general, but especially for terrain
                    
                    }

                //}
            
            }
            
            for(GameNode next: map.getNeighbours(curr)) {

                if(seen.contains(next)) continue;
                if(next.outOfBounds(this.state)) continue;
                if(!this.terrMngr.isValidTerrain(state, state.getAgentState(), next.getType())) continue;

                seen.add(next);
                q.add(next);

            }

        }

        return null;

    }

    public LinkedList<GameNode> bfs(GameMap map, GameNode start, GameNode end) {

        ArrayDeque<GameNode> q = new ArrayDeque<GameNode>();
        HashSet<GameNode> seen = new HashSet<GameNode>(); 
        HashMap<GameNode,GameNode> prevs = new HashMap<GameNode,GameNode>();
        
        q.add(start);
        seen.add(start);

        while(!q.isEmpty()) {

            GameNode curr = q.poll();

            if(curr == end) {
                LinkedList<GameNode> path = new LinkedList<GameNode>();
                GameNode n = curr;
                do {
                    path.addFirst(n);
                    n = prevs.get(n);
                    //System.out.println("n is "+n.getPoint().toString());
                } while(n != start);

                return path;

            }

            for(GameNode n : map.getNeighbours(curr)) {

                if(seen.contains(n) || n.outOfBounds(this.state)) continue;
                if(!this.terrMngr.isValidTerrain(state, state.getAgentState(), n.getType())) continue;

                prevs.put(n,curr); 
                seen.add(n);
                q.add(n);

            }

        }

        return null;

    }

    public LinkedList<GameNode> pathBFS(GameMap map, GameNode start, GameNode end) {

        /*
         * REVIEW THIS LOGIC
         * -just path from A to B
         */

        ArrayDeque<Goal> queue = new ArrayDeque<Goal>();
        HashSet<GameNode> seen = new HashSet<GameNode>();  //change back
        Goal g = new Goal(start);
        queue.add(g);

        while(!queue.isEmpty()) {

            Goal curr = queue.poll();
            GameNode currNode = curr.getGoalNode();
            seen.add(currNode);
            System.out.println("BFS: visiting node of type '"+currNode.getType()+"' at "+currNode.getPoint().toString());
            if(currNode == end) {

                return curr.getPath();

            }

            for(GameNode n : map.getNeighbours(currNode)) {

                if(seen.contains(n) || n.outOfBounds(this.state) || !this.terrMngr.isValidTerrain(state, state.getAgentState(), n.getType())) continue; 

                Goal next = new Goal(n);
                next.extendPath(curr.getPath());
                next.addToPath(n);
                queue.add(next);

            }

        }

        return null; //not reachable

    }

    public Goal constructPath(GameMap map, LinkedList<GameNode> nodes) {

        /*
         * REVIEW THIS LOGIC
         * -test adjacency?
         */

        Goal g = new Goal(nodes.getLast());// get(nodes.size()-1));
        GameNode prev = nodes.remove(0);
        LinkedList<GameNode> subPath = null;

        while(!nodes.isEmpty()) {
            GameNode curr = nodes.remove(0);
            subPath = this.pathBFS(map, prev, curr);
            //debug
            if(subPath == null) {
                System.out.println("SEARCH: construct path: subpath null!!");
                continue; //this would mean its unreachable though..
            }
            g.extendPath(subPath);
            prev = curr;
        }

        return g;

    }

    public Goal makePath(GameMap map, LinkedList<GameNode> nodes) {

        LinkedList<GameNode> path = new LinkedList<GameNode>();
        for(int i=0; i < nodes.size()-1; i++) {

            GameNode curr = nodes.get(i), next = nodes.get(i+1);
            if(!map.areAdjacent(curr, next)) {
                
                path.addAll(this.bfs(map, curr, next));

            }else{

                path.add(next);

            }

        }

        Goal g = new Goal(nodes.getLast());
        g.extendPath(path);
        return g;

    }

    public Goal exploreDFS(GameMap map, char terrain) { 

        //System.out.println("EXPLORING '"+terrain+"'");

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
                    //System.out.println("SEARCH: will visit node of type '"+n.getType()+"' at "+n.getPoint().toString());
                    stack.push(n);

                }

            }

        }

        return makePath(map, toExplore);//constructPath(map,toExplore);

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
            for(GoalSearchState next: curr.genSuccessors(map,this.state,this.terrMngr)) {

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
