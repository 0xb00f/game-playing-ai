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

        System.out.println("IN WATER EXPLORE");

        Goal g = this.agentEngine.exploreWater();

        if(g.hasPath()) {

            this.agentEngine.addGoalActions(g);
            return;

        }

        //first try land exploration
        GameNode n = this.agentEngine.findUnexploredRegion(' ');

        if(n != null && !this.agentEngine.isWorthExploring(n)) return;

        if(n != null) { 
            
            System.out.println("WATEREXP: GOING INTO LAND EXPLORE");
            this.agentEngine.setAgentState(this.agentEngine.transLand);

        }else{

            System.out.println("WATEREXP: GOING INTO GOAL PURSUIT");
            this.agentEngine.setAgentState(this.agentEngine.transGoal);

        }


        
        /*
        }else if(this.agentEngine.hasGoal()) {

            System.out.println("WATEREXP: GOING INTO GOAL PURSUIT MODE");
            this.agentEngine.setAgentState(this.agentEngine.transGoal); 

        }else{

            System.out.println("WATEREXP: GOING INTO LAND EXPLORE MODE");
            this.agentEngine.setAgentState(this.agentEngine.transLand);

        }*/

    }

}
