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

        if(nRafts == 0) { 

            for(GameNode n : seen) n.setVisited();
            return false;

        }

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

    public LinkedList<GameNode> makePath(GameMap map, LinkedList<GameNode> nodes) {

        LinkedList<GameNode> path = new LinkedList<GameNode>();
        for(int i=0; i < nodes.size()-1; i++) {

            GameNode curr = nodes.get(i), next = nodes.get(i+1);
            if(!map.areAdjacent(curr, next)) {
                
                path.addAll(this.bfs(map, curr, next));

            }else{

                path.add(next);

            }

        }

        //Goal g = new Goal(nodes.getLast());
        //g.extendPath(path);
        return path;//g;

    }

    public LinkedList<GameNode> exploreDFS(GameMap map, char terrain) { 

        ArrayDeque<GameNode> stack = new ArrayDeque<GameNode>();
        HashSet<GameNode> seen = new HashSet<GameNode>(); 
        LinkedList<GameNode> toExplore = new LinkedList<GameNode>();

        toExplore.add(this.state.getCurrNode());
        stack.push(this.state.getCurrNode());

        while(!stack.isEmpty()) {

            GameNode curr = stack.pop();

            if(!seen.contains(curr)) { 

                seen.add(curr);

                if(!curr.isVisited()) toExplore.add(curr); 

                for(GameNode n : map.getNeighbours(curr)) {

                    if(seen.contains(n) || n.outOfBounds(this.state)) continue; 
                    if(!this.terrMngr.isValidTerrain(state, state.getAgentState(), n.getType())) {
                        continue;
                    }

                    stack.push(n);

                }

            }

        }

        return makePath(map, toExplore);

    }

    private class GoalStateCompare implements Comparator<GoalSearchState> {

        public int compare(GoalSearchState a, GoalSearchState b) {
    
            Integer g1 = a.getF();
            Integer g2 = b.getF();
            
            return g1.compareTo(g2); 
            
        }

    }

    public LinkedList<GameNode> astarSearch(GameMap map, GameNode begin, GameNode end, Heuristic h, boolean bombPath) {

        PriorityQueue<GoalSearchState> open = new PriorityQueue<GoalSearchState>(new GoalStateCompare());
        HashMap<GameNode,Integer> fValues = new HashMap<GameNode,Integer>();

        GoalSearchState start = new GoalSearchState(this.state,begin);
        start.setG(0); 
        start.setH(h.score(begin, end)); 
        start.updateState();

        open.add(start);
        fValues.put(start.getNode(),h.score(begin, end));

        while(!open.isEmpty()) {

            GoalSearchState curr = open.poll();

            if(curr.getNode() == end) {
                
                LinkedList<GameNode> path = new LinkedList<GameNode>();
                
                while(curr != null) {

                    path.push(curr.getNode());         
                    curr = curr.getPrev();   

                }

                return path;

            }

            for(GoalSearchState next: curr.genSuccessors(map,this.state,this.terrMngr)) {

                int tmpF = curr.getG() + next.getNode().getPathWeight() + h.score(next.getNode(),end); 
                
                if(tmpF < fValues.getOrDefault(next.getNode(), Integer.MAX_VALUE)) {
                    
                    next.setG(curr.getG() + next.getNode().getPathWeight());
                    next.setH(h.score(next.getNode(),end));
                    fValues.put(next.getNode(),tmpF);
                    open.add(next);

                }

            }

        }

        return null; 

    }
    
}
