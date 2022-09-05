public class WaterExploreAgentState implements AgentState {

    private AgentEngine agentEngine;

    public WaterExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    /*
     * THINK ABOUT OVERALL LOGIC HERE
     * - want to make sure on water once we begin here...
     */

    public void doTask(char[][] view) {

        System.out.println("IN WATER EXPLORE, onwater="+this.agentEngine.isOnWater());

        // enqueue actions to exlpore
        Goal g = this.agentEngine.exploreWater();

        if(g.hasPath()) {

            this.agentEngine.addGoalActions(g);

            if(this.agentEngine.hasNextAction()) return;

        }else{

            System.out.println("WATER EXPLORE FAIL: explore has no path");

            // if no pending actions, change state
            if(this.agentEngine.hasGoal() || this.agentEngine.hasTreasure()) { //or has seen items??
                System.out.println("GOING INTO GOAL PURSUIT MODE");
                this.agentEngine.setAgentState(this.agentEngine.pursueGoal); //agentstate retrieves goal from gamestate
                //System.exit(1);
            }else if(this.agentEngine.hasUnexploredLand()){
                if(this.agentEngine.isOnWater()) {

                    System.out.println("GOIGN TO LAND");
                    // go to nearest unexplored
                    this.agentEngine.enableLandTravel();
                    this.agentEngine.goToLand();
        
                }
                System.out.println("GOING INTO LAND EXPLORE MODE");
                //enable land travel
                this.agentEngine.setAgentState(this.agentEngine.exploreLand);
            }else{
                System.out.println("PANIC");
                System.exit(1);

                //panic?
            }

        }

        return;

    }

}
