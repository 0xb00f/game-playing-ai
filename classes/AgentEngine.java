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
        this.state.setTerrainManager(this.terrMngr);
        this.state.enableLandTravel(); //starting out on land
        this.state.setCurrNode(this.map.getHome());
        
    }

    public Character playGame(char[][] view) {

        this.mapView(view);

        if(!this.hasNextAction()) {
            while(!this.hasNextAction()) this.currentState.doTask(view);
        }
        
        Character c = this.getAgentAction();
        this.state.processAction(c,this.map);
        return c;

    }

    public GameNode findReachableNode(char target) { //new

        return this.map.findReachable(target);

    }

    public void setAgentState(AgentState newState) {

        this.currentState = newState;
        this.state.setAgentState(newState);

    }

    public boolean pursueGoal() { //del?

        return this.goalMngr.pursueGoal();

    }

    public boolean hasGoal() { //del?

        return this.state.hasTreasure() || this.goalMngr.hasGoal();

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

    public void processAction(Character c) { //delete, moved

        /*  
        switch(c) {
            case 'f': this.state.move(this.map.getMap()); break; //this.terrMngr.processTerrainChange(this.state.move(this.map.getMap()));
            case 'l': this.state.turnDirection(-1); break;
            case 'r': this.state.turnDirection(1); break;
            case 'b': this.state.useBomb(); break;
            //case 'c' : this.terrMngr.axeCollected(); break;
            default: return; 
        }*/

    }

    public void addGoalActions(Goal g) { //del

        this.actions.goToGoal(g);

    }

    public boolean hasUnexploredWater() { //del?

        return this.state.hasUnexploredWater(this.map);

    }

    public boolean hasUnexploredLand() { //del?

        return this.state.hasUnexploredLand(this.map);

    }

    public void enqueueUnexploredLand() {

        GameNode n = this.goalMngr.getNearestLand();
        this.goalMngr.addGoal(n);

    }

    public boolean hasAxe() {

        return this.state.hasAxe();

    }

    public boolean hasRaft() {

        return this.state.hasRaft();

    }

    public boolean getRaft() {

        return this.goalMngr.getRaft();

    }

    public boolean isOnWater() {

        return this.state.isOnWater(); //isOnWater()

    }

    public boolean goToWater() {

        return this.goalMngr.goToWater();

    }

    public boolean goToLand() {

        return this.goalMngr.goToLand();

    }

}
