import java.util.LinkedList;

public class AgentEngine {
    
    public AgentState exploreLand;
    public AgentState exploreWater;
    public AgentState pursueGoal;
    public AgentState transLand;
    public AgentState transWater;
    public AgentState transGoal;
    private AgentState currentState;
    private AgentActions actions;
    private GameState state;
    private GameMap map;
    private GoalManager goalMngr;
    private TerrainManager terrMngr;


    public AgentEngine() {

        this.exploreLand = new LandExploreAgentState(this);
        this.exploreWater = new WaterExploreAgentState(this);
        this.pursueGoal = new GoalPursuitAgentState(this);
        this.transLand = new TransitionToLandExplore(this);
        this.transWater = new TransitionToWaterExplore(this);
        this.transGoal = new TransitionToGoalPursuit(this);
        this.currentState = this.exploreLand;
        this.state = new GameState(this.currentState); 
        this.terrMngr = new TerrainManager(this.exploreLand, this.exploreWater, this.pursueGoal);
        this.map = new GameMap(this.state, new SearchGameMap(this.state, this.terrMngr));
        this.actions = new AgentActions(this.state);
        this.goalMngr = new GoalManager();
        
        this.state.setCurrNode(this.map.getHome());
        
    }

    public Character playGame(char[][] view) {

        this.mapView(view);

        //if(!this.hasNextAction()) {
            while(!this.hasNextAction()) this.currentState.doTask(view);
        //}
        
        Character c = this.actions.getNextAction();
        this.processAction(c);
        return c;

    }

    public GameNode findReachableNode(char target) { //new

        return this.map.findReachable(target);

    }

    public void setAgentState(AgentState newState) {

        this.currentState = newState;
        this.state.setAgentState(newState); 
        //terr?

    }

    public void addGoal(GameNode n) {

        this.goalMngr.addGoal(n);

    }

    public GameNode peekGoal() {

        return this.goalMngr.getNextGoal();

    }

    public GameNode getToLand(GameNode start) {

        return this.map.getToLand(start);

    }

    public boolean pursueGoal() { 

        return this.goalMngr.pursueGoal(this.map,this.actions);

    }

    public boolean hasGoal() { 

        return this.goalMngr.hasGoal();

    }

    public boolean hasTreasure() { //del

        return this.state.hasTreasure();

    }

    public void mapView(char[][] view) {

        this.map.mapView(view,this.goalMngr);

    }

    public LinkedList<GameNode> exploreLand() {

        return this.map.exploreLand();

    }

    public LinkedList<GameNode> exploreWater() {

        return this.map.exploreWater();

    }

    public boolean hasNextAction() {

        return this.actions.hasNextAction();

    }

    public void processAction(char c) { //new

        switch(c) {

            case 'f': this.move(); break; 
            case 'l': this.state.turnDirection(-1); break;
            case 'r': this.state.turnDirection(1); break;
            case 'b': this.state.useBomb(); break;
            case 'c': this.state.setRaft(true); break; //terr at all?

        }

    }
    
    public void move() { //new

        GameNode curr = this.state.getCurrNode();
        Graph graph = this.map.getMap();

        switch(this.state.getDirection()) {

            case 0: 
                GameNode north = graph.getNorthNeighbour(curr);
                this.terrMngr.processTerrainChange(this.state,curr,north.getType());
                this.state.setCurrNode(north);
                break;
            case 1:
                GameNode east = graph.getEastNeighbour(curr);
                this.terrMngr.processTerrainChange(this.state,curr,east.getType());
                this.state.setCurrNode(east);
                break;
            case 2:
                GameNode south = graph.getSouthNeighbour(curr);
                this.terrMngr.processTerrainChange(this.state,curr,south.getType());
                this.state.setCurrNode(south);
                break;
            case 3:
                GameNode west = graph.getWestNeighbour(curr);
                this.terrMngr.processTerrainChange(this.state,curr,west.getType());
                this.state.setCurrNode(west);
                break;

        }

    }

    public boolean isWorthExploring(GameNode n) {

        return this.map.isWorthExploring(n);

    }

    public GameNode findUnexploredRegion(char target) {

        return this.map.findUnexploredRegion(target);

    }

    public void addGoalActions(LinkedList<GameNode> path) { 

        this.actions.goToGoal(path);

    }

    public boolean hasAxe() {

        return this.state.hasAxe();

    }

    public boolean hasRaft() {

        return this.state.hasRaft();

    }

    public boolean hasKey() {

        return this.state.hasKey();

    }

    public void setRaft(boolean b) {

        this.state.setRaft(b);

    }

    public boolean isOnWater() {

        return this.state.isOnWater(); 

    }

    public void setOnWater() {

        this.state.setOnWater(); //terr

    }

    public void setOffWater() {

        this.state.setOffWater(); //as above

    }

}
