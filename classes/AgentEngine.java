public class AgentEngine {
    
    public AgentState exploreLand;
    public AgentState exploreWater;
    public AgentState pursueGoal;
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
        this.currentState = this.exploreLand;
        this.state = new GameState(); 
        this.state.setAgentState(this.currentState);
        this.map = new GameMap(this.state);
        this.actions = new AgentActions(this.state);
        this.goalMngr = new GoalManager(this.actions, this.map, this.state);
        this.terrMngr = new TerrainManager(this.state, this.exploreLand, this.exploreWater, this.pursueGoal);
        this.state.setTerrainManager(this.terrMngr); //del
        this.state.enableLandTravel(); 
        this.state.setCurrNode(this.map.getHome());
        
    }

    public Character playGame(char[][] view) {

        this.mapView(view);

        if(!this.hasNextAction()) {
            while(!this.hasNextAction()) this.currentState.doTask(view);
        }
        
        Character c = this.getAgentAction();
        this.processAction(c);
        return c;

    }

    public GameNode findReachableNode(char target) { //new

        return this.map.findReachable(target);

    }

    public void setAgentState(AgentState newState) {

        this.currentState = newState;
        this.state.setAgentState(newState); //needed? terrain mngr here now... can pass state as arg

    }

    public boolean pursueGoal() { //del?

        return this.goalMngr.pursueGoal();

    }

    public boolean hasGoal() { //del?

        return this.goalMngr.hasGoal();

    }

    public boolean hasTreasure() {

        return this.state.hasTreasure();

    }

    public GameNode getNextGoal() { //del?

        return this.goalMngr.getNextGoal();

    }

    public void mapView(char[][] view) {

        this.map.mapView(view);

    }

    public Goal exploreLand() {

        return this.map.exploreLand();

    }

    public Goal exploreWater() {

        return this.map.exploreWater();

    }

    public void enableWaterTravel() {

        this.state.enableWaterTravel();

    }

    public void disableWaterTravel() {

        this.state.disableWaterTravel();

    }

    public void enableLandTravel() {

        this.state.enableLandTravel();

    }

    public void disableLandTravel() {

        this.state.disableLandTravel();

    }

    public boolean hasNextAction() {

        return this.actions.hasNextAction();

    }

    public Character getAgentAction() {

        return this.actions.getNextAction();

    }

    public void updateCurrState(GameNode node) { //new

        switch(node.getType()) {

            case 'k' : this.state.setKey(); break;
            case 'd' : this.state.addBomb(); break;
            case 'a' : this.state.setAxe(); break;
            case '$' : {
                this.state.setTreasure(); 
                this.state.addGoal(this.map.getHome());
                break;
            }

        }

        //if(node.isItem() && this.pendingGoals.contains(node)) this.pendingGoals.remove(node);
        if(node.getType() != '~') node.clearNode();
        node.setVisited();

    }

    public void processAction(Character c) { //new

        switch(c) {

            case 'f': this.move(); break; 
            case 'l': this.state.turnDirection(-1); break;
            case 'r': this.state.turnDirection(1); break;
            case 'b': this.state.useBomb(); break;
            case 'c': this.terrMngr.raftCollected(); break;

        }

    }
    
    public void move() { //new

        GameNode curr = this.state.getCurrNode();
        Graph graph = this.map.getMap();

        switch(this.state.getDirection()) {

            case 0: 
                GameNode north = graph.getNorthNeighbour(curr);
                this.terrMngr.processTerrainChange(north.getType());
                this.state.setCurrNode(north);
                break;
            case 1:
                GameNode east = graph.getEastNeighbour(curr);
                this.terrMngr.processTerrainChange(east.getType());
                this.state.setCurrNode(east);
                break;
            case 2:
                GameNode south = graph.getSouthNeighbour(curr);
                this.terrMngr.processTerrainChange(south.getType());
                this.state.setCurrNode(south);
                break;
            case 3:
                GameNode west = graph.getWestNeighbour(curr);
                this.terrMngr.processTerrainChange(west.getType());
                this.state.setCurrNode(west);
                break;

        }

    }

    public void addGoalActions(Goal g) { //??

        this.actions.goToGoal(g);

    }

    public boolean hasAxe() {

        return this.state.hasAxe();

    }

    public boolean hasRaft() {

        return this.state.hasRaft();

    }

    public boolean getRaft() { //edit

        return false;//this.goalMngr.getRaft();

    }

    public boolean isOnWater() {

        return this.state.isOnWater(); //isOnWater()

    }

    public boolean goToWater() { //edit
 
        return false; //this.goalMngr.goToWater();

    }

    public boolean goToLand() { //edit

        return false; //this.goalMngr.goToLand();

    }

}
