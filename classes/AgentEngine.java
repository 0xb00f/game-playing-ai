public class AgentEngine {
    
    public AgentState exploreLand;
    public AgentState exploreWater;
    public AgentState pursueGoal;
    private AgentState currentState;
    private AgentActions actions;
    private GameState state;
    private GameMap map;
    private GoalManager goalMngr;


    public AgentEngine() {

        this.exploreLand = new LandExploreAgentState(this);
        this.exploreWater = new WaterExploreAgentState(this);
        this.pursueGoal = new GoalPursuitAgentState(this);
        this.state = new GameState(); 
        this.state.enableLandTravel(); //starting out on land
        this.map = new GameMap(this.state);
        this.actions = new AgentActions(this.state);
        this.currentState = this.exploreLand;
        this.goalMngr = new GoalManager(this.actions, this.map, this.state);
        
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

    public void setAgentState(AgentState newState) {

        //System.out.println("!!!!!!!!!!!!!!!CHANGING STATE!!");
        this.currentState = newState;

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

    public void processAction(Character c) {

        switch(c) {
            case 'f': this.state.move(this.map.getMap()); break;
            case 'l': this.state.turnDirection(-1); break;
            case 'r': this.state.turnDirection(1); break;
            case 'b': this.state.useBomb(); break;
            default: return; //worry about unmapping the door? or will this be done automatically?
        }

    }

    public void addGoalActions(Goal g) { //del

        this.actions.goToGoal(g);

    }

    public boolean hasUnexploredWater() { //del?

        return this.state.hasUnexploredWater();

    }

    public boolean hasUnexploredLand() { //del?

        return this.state.hasUnexploredLand();

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

}
