public class WaterExploreAgentState implements AgentState {

    private AgentEngine agentEngine;

    public WaterExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public Character doTask(char[][] view) {

        System.out.println("IN WATER EXPLORE");

        /*
         * think about this. 
         * At first it will be encessary to have both water and aldn as valid terrain as it may need to 
         * transition from one to the other. After it is on water, though, in this mode it should
         * disable land travel.
         * ALSO MUST UNSET RAFT ONCE AGENT LEAVES WATER
         */

        // map the current view
        this.agentEngine.mapView(view);

        //enabled water travel in last state...

        // WILL NEED TO FETCH RAFT!!
        if(!this.agentEngine.hasRaft() && this.agentEngine.hasSeenRafts()) {

            this.agentEngine.fetchRaft();
        
        }
        
        // if pending actions, return next action
        if(this.agentEngine.hasNextAction()) {

            Character c = this.agentEngine.getAgentAction();
            this.agentEngine.processAction(c);
            return c;

        } 

        // enqueue actions to exlpore
        Goal g = this.agentEngine.exploreWater();

        //disable land travel here?
        this.agentEngine.disableLandTravel();

        if(g.hasPath()) {

            this.agentEngine.addGoalActions(g);

            if(this.agentEngine.hasNextAction()) {

                Character c = this.agentEngine.getAgentAction();
                this.agentEngine.processAction(c);
                return c;
    
            } 

        }else{

            // if no pending actions, change state
            if(this.agentEngine.canPursueGoal()) { //or has seen items??
                //this.agentEngine.setAgentState(this.agentEngine.pursueGoal); //agentstate retrieves goal from gamestate
                System.exit(1);
            }else if(this.agentEngine.hasUnexploredLand()){
                System.out.println("GOING INTO LAND EXPLORE MODE");
                //enable land travel
                this.agentEngine.setAgentState(this.agentEngine.exploreLand);
            }else{
                System.out.println("PANIC");
                System.exit(1);

                //panic?
            }

        }

        return this.agentEngine.playGame(view); //get actions from next state

    }

}
