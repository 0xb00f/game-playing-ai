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

    public boolean isWorthExploring(GameMap map, GameNode start) {

        HashSet<GameNode> seen = new HashSet<GameNode>();
        ArrayDeque<GameNode> q = new ArrayDeque<GameNode>();

        q.push(start);
        seen.add(start);

        int nItems = 0;
        int nRafts = 0;

        while(!q.isEmpty()) {

            GameNode curr = q.poll();

            if(curr.isItem()) nItems++;
            if(curr.isTree()) nRafts++;
            
            for(GameNode next: map.getNeighbours(curr)) {

                if(seen.contains(next)) continue;
                if(next.outOfBounds(this.state) || next.getType() == '~') continue;

                seen.add(next);
                q.add(next);

            }

        }

        System.out.print("!!!!WORTH EXPLORING? items="+nItems+" rafts="+nRafts+": ");

        //this logic?
        if(nRafts == 0) { //less or eq?

            System.out.println("NOT WORTH IT");

            for(GameNode n : seen) n.setVisited();
            return false;

        }

        System.out.println("WORTH IT");
        return nRafts > nItems;

    }

    public GameNode findUnexploredRegion(GameMap map, GameState state, char target) {
        
        HashSet<GameNode> seen = new HashSet<GameNode>();
        ArrayDeque<GameNode> q = new ArrayDeque<GameNode>();

        q.push(this.state.getCurrNode());
        seen.add(this.state.getCurrNode());

        while(!q.isEmpty()) {

            GameNode curr = q.poll();

            if(curr.getType() == target && !curr.isVisited()) return curr;

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

    public GameNode reachableItem(GameNode start, GameMap map, GameState state, char target, boolean brute) {

        HashSet<GameNode> seen = new HashSet<GameNode>();
        ArrayDeque<GameNode> q = new ArrayDeque<GameNode>();

        q.push(start);
        seen.add(start);

        while(!q.isEmpty()) {

            GameNode curr = q.poll();

            if(curr.getType() == target) return curr;
            
            for(GameNode next: map.getNeighbours(curr)) {

                if(seen.contains(next)) continue;
                if(!brute && next.outOfBounds(this.state)) continue;
                if(!brute && !this.terrMngr.isValidTerrain(state, state.getAgentState(), next.getType())) continue;

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

        System.out.println("ASTAR: start at "+this.state.getCurrNode().getPoint().toString()+" on goal with type '"+end.getType()+"' at "+end.getPoint().toString());

        PriorityQueue<GoalSearchState> open = new PriorityQueue<GoalSearchState>(new GoalStateCompare());
        HashSet<GameNode> closed = new HashSet<GameNode>(); //used??
        HashMap<GameNode,Integer> fValues = new HashMap<GameNode,Integer>();

        GameNode startNode = this.state.getCurrNode();
        GoalSearchState start = new GoalSearchState(null,startNode);

        start.initState(this.state); //initState
        start.setG(0); //startNode.getPathWeight()
        start.setH(h.score(startNode, end)); //this.state
        open.add(start);
        fValues.put(start.getNode(),h.score(startNode, end));

        while(!open.isEmpty()) {

            GoalSearchState curr = open.poll();
            //closed.add(curr.getNode()); //used?

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

                //if(closed.contains(next.getNode())) continue;

                //try computing the below INSIDE goalstate...
                int tmpF = curr.getG() + next.getNode().getPathWeight() + h.score(next.getNode(),end); //separate function once item logic in place...
                
                if(tmpF < fValues.getOrDefault(next.getNode(), Integer.MAX_VALUE)) {
                    
                    //System.out.println("ASTAR: considering path to "+next.getNode().getPoint().toString()+" with g="+curr.getG()+" and next="+next.getNode().getPathWeight());
                    next.setG(curr.getG() + next.getNode().getPathWeight());
                    next.setH(h.score(next.getNode(),end));
                    fValues.put(next.getNode(),tmpF);
                    //closed.add(next.getNode()); //trial
                    open.add(next);

                }

            }

        }

        System.out.println("ASTAR FAILED to go to '"+end.getType()+"'");
        return null; 

    }
    
}
