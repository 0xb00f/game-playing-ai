public class LandExploreAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public LandExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {


        // map the current view
        //this.agentEngine.mapView(view);

        /*
         * map fucking out on transition in s0??
         */

        // disable water travel here? unset raft?
        this.agentEngine.disableWaterTravel();

        // enqueue actions to exlpore
        Goal g = this.agentEngine.exploreLand(); 

        if(g.hasPath()) {

            this.agentEngine.addGoalActions(g);
            return;

        }else if(!this.agentEngine.hasGoal()  && this.agentEngine.hasUnexploredLand()) {

            this.agentEngine.enqueueUnexploredLand();

        }

        // if no pending actions, change state
        if(this.agentEngine.hasGoal() || this.agentEngine.hasTreasure()) { //or has seen items??
            System.out.println("GOING INTO GOAL PURSUIT MODE");
            //System.out.println("PANIC");
            //System.exit(1);
            this.agentEngine.setAgentState(this.agentEngine.pursueGoal); 
            //System.out.println("DUMMY MOVE");

        }else if(this.agentEngine.hasUnexploredWater() && this.agentEngine.hasRaft()){
            System.out.println("GOING INTO WATER EXPLORE MODE");
            //enable water travel
            this.agentEngine.enableWaterTravel();
            this.agentEngine.setAgentState(this.agentEngine.exploreWater);
        }else{
            System.out.println("PANIC");
            System.exit(1);

            //panic?
        }

        return;

    }

}
