public class LandExploreAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public LandExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public Character doTask(char[][] view) {


        // map the current view
        this.agentEngine.mapView(view);
        
        // if pending actions, return next action
        if(this.agentEngine.hasNextAction()) {
            Character c = this.agentEngine.getAgentAction();
            this.agentEngine.processAction(c);
            return c;
        } 

        // enqueue actions to exlpore
        Goal g = this.agentEngine.exploreLand(); 

        // disable water travel here? unset raft?
        this.agentEngine.disableWaterTravel();

        if(g.hasPath()) {

            this.agentEngine.addGoalActions(g);
            if(this.agentEngine.hasNextAction()) { //adding this logic here saved the damn map SMDH
                Character c = this.agentEngine.getAgentAction();
                this.agentEngine.processAction(c);
                return c;
            }

        }else{

            // if no pending actions, change state
            if(this.agentEngine.canPursueGoal()) { //or has seen items??
                System.out.println("GOING INTO GOAL PURSUIT MODE");
                //System.out.println("PANIC");
                //System.exit(1);
                this.agentEngine.setAgentState(this.agentEngine.pursueGoal); 
                //System.out.println("DUMMY MOVE");

                return 'x'; // dummy for now

            }else if(this.agentEngine.hasUnexploredWater() && this.agentEngine.hasAxe()){
                System.out.println("GOING INTO WATER EXPLORE MODE");
                //enable water travel
                this.agentEngine.enableWaterTravel();
                this.agentEngine.setAgentState(this.agentEngine.exploreWater);
            }else{
                System.out.println("PANIC");
                System.exit(1);

                //panic?
            }

        }

        return this.agentEngine.playGame(view); //get actions from next state

    }

}
