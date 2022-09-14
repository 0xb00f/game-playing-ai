public class LandExploreAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public LandExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        /*
         * HANGS HERE ON s9
         * seems not to explore land again
         * misses collecting bomb
         * WTF is the loop>?? its in constrcutPath - review the logic
         * BE AWARE GOAL PURSUIT CAN TAKE PLACE IN OTHER MODES
         */

        System.out.println("IN LAND EXPLORE");

        Goal g = this.agentEngine.exploreLand(); 

        if(g.hasPath()) {

            System.out.println("LANDEXP: GOAL HAS ACTIONS");
            this.agentEngine.addGoalActions(g);

        }else if(this.agentEngine.hasGoal()) { 

            System.out.println("LANEXP: GOING INTO GOAL PURSUIT MODE");
            this.agentEngine.setAgentState(this.agentEngine.transGoal); 

        }else if(this.agentEngine.hasAxe()){

            System.out.println("LANDEXP: GOING INTO WATER EXPLORE MODE");
            this.agentEngine.setAgentState(this.agentEngine.transWater);

        }else{

            System.out.println("LANDEXP: PANIC - AGENT IS LOST!");
            System.exit(1);

        }

    }

}
