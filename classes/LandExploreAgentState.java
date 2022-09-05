public class LandExploreAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public LandExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    /*
     * !!!!!!!!!!!!!!!!!!!!!!!LOGIC IN THESE STATES!!!!
     * THINK ABOUT OVERALL LOGIC HERE - PANIC on s6!!! (also shits itself on s4, as goes for $ before d.... powering trees would solve but..)
     * - want to make sure on land already...
     */

    public void doTask(char[][] view) {

        System.out.println("IN LAND EXPLORE");

        // enqueue actions to exlpore
        Goal g = this.agentEngine.exploreLand(); 

        /*
         * enabling water somewhere???
         */

        if(g.hasPath()) {
            System.out.println("LANDEXP: ENQ GOAL ACTIONS");
            this.agentEngine.addGoalActions(g);
            return;

        }else if(!this.agentEngine.hasGoal()  && this.agentEngine.hasUnexploredLand()) {

            System.out.println("LANDEXP: ENQ UNEXPLORED LAND");
            //presumably the present land block ahs been explored so rethink this logic..
            this.agentEngine.enqueueUnexploredLand();

        }

        // if no pending actions, change state
        if(this.agentEngine.hasGoal() || this.agentEngine.hasTreasure()) { //or has seen items??
            System.out.println("GOING INTO GOAL PURSUIT MODE");
            //System.out.println("PANIC");
            //System.exit(1);
            this.agentEngine.setAgentState(this.agentEngine.pursueGoal); 
            //System.out.println("DUMMY MOVE");

        }else if(this.agentEngine.hasUnexploredWater() && this.agentEngine.hasAxe()){

            if(!this.agentEngine.isOnWater() && !this.agentEngine.hasRaft()) { 

                System.out.println("WATER EXPLORE: GETTING RAFT");
    
                // fetch raft if not in possession
                if(!this.agentEngine.hasRaft()) {
                    
                    this.agentEngine.getRaft(); 
                    this.agentEngine.enableWaterTravel();
                    this.agentEngine.goToWater();

                }
            }

            System.out.println("GOING INTO WATER EXPLORE MODE");
            //enable water travel
            this.agentEngine.setAgentState(this.agentEngine.exploreWater);
        }else{
            System.out.println("PANIC");
            System.exit(1);

            //panic?
        }

        return;

    }

}
