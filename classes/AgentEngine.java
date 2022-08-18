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

        return this.currentState.doTask(view);

    }

    public void setAgentState(AgentState newState) {

        //System.out.println("!!!!!!!!!!!!!!!CHANGING STATE!!");
        this.currentState = newState;

    }

    public boolean canPursueGoal() { //del

        return this.goalMngr.hasPotentialGoals();

    }

    public void pursueGoal(GameNode n) { //del?

        //this.goalMngr.pursueItem(n);
        this.map.pursueGoal(n);

    }

    public boolean hasGoal() { //del?

        return this.state.hasGoal();

    }

    public Goal getNextGoal() { //del?

        //
        return this.state.getNextGoal();

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

    public boolean hasAxe() {

        return this.state.hasAxe();

    }

    public boolean hasRaft() {

        return this.state.hasRaft();

    }

    public boolean hasSeenRafts() { //del?

        return this.state.hasSeenRafts();

    }

    public boolean hasSeenBombs() { //del?

        return this.state.hasSeenBombs();

    }

    public void getNearestBomb() { //del

        this.state.addGoal(new Goal(this.state.getNearestBomb()));

    }

    public void fetchRaft() { //del

        this.state.setRaft(true);
        this.state.enqueueRaftGoal();

    }

    public void pursueNextBestGoal() { //del

        Goal g = this.map.nextBestGoal();
        this.actions.goToGoal(g);

    }

    public boolean shouldPursueBomb() { //?del

        return this.state.shouldPursueBomb();

    }

    public boolean goalsAvailable() { //del

        return this.state.goalsAvailable();

    }

    //neater? more logical?
    public void generateGoal() { //del

        this.state.generateGoal();

    }

}
