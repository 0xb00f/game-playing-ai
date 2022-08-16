public class AgentEngine {
    
    public AgentState exploreLand;
    public AgentState exploreWater;
    public AgentState pursueGoal;
    private AgentState currentState;
    private AgentActions actions;
    private GameState state;
    private GameMap map;


    public AgentEngine() {

        this.exploreLand = new LandExploreAgentState(this);
        this.exploreWater = new WaterExploreAgentState(this);
        this.pursueGoal = new GoalPursuitAgentState(this);
        this.state = new GameState(); 
        this.state.enableLandTravel(); //starting out on land
        this.map = new GameMap(this.state);
        this.actions = new AgentActions(this.state);
        this.currentState = this.exploreLand;
        
    }

    public Character playGame(char[][] view) {

        return this.currentState.doTask(view);

    }

    public void setAgentState(AgentState newState) {

        //System.out.println("!!!!!!!!!!!!!!!CHANGING STATE!!");
        this.currentState = newState;

    }

    public boolean canPursueGoal() {

        return this.hasGoal() || this.hasSeenBombs();

    }

    public void pursueGoal(GameNode n) {

        this.map.pursueGoal(n);

    }

    public boolean hasGoal() {

        return this.state.hasGoal();

    }

    public Goal getNextGoal() {

        return this.state.getNextGoal();

    }

    public GameNode getNextBestItem() {

        return null; //used for getting seen items for searching dynamically??

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

    public void addGoalActions(Goal g) {

        this.actions.goToGoal(g);

    }

    public boolean hasUnexploredWater() {

        return this.state.hasUnexploredWater();

    }

    public boolean hasUnexploredLand() {

        return this.state.hasUnexploredLand();

    }

    public boolean hasAxe() {

        return this.state.hasAxe();

    }

    public boolean hasRaft() {

        return this.state.hasRaft();

    }

    public boolean hasSeenRafts() {

        return this.state.hasSeenRafts();

    }

    public boolean hasSeenBombs() {

        return this.state.hasSeenBombs();

    }

    public void getNearestBomb() {

        this.state.addGoal(new Goal(this.state.getNearestBomb()));

    }

    public void fetchRaft() {

        this.state.setRaft(true);
        this.state.enqueueRaftGoal();

    }

    public void pursueNextBestGoal() {

        Goal g = this.map.nextBestGoal();
        this.actions.goToGoal(g);

    }

    public boolean shouldPursueBomb() { //?

        return this.state.shouldPursueBomb();

    }

    public boolean goalsAvailable() {

        return this.state.goalsAvailable();

    }

    //neater? more logical?
    public void generateGoal() {

        this.state.generateGoal();

    }

}
