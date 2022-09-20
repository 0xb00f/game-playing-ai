public class WaterExploreAgentState implements AgentState {

    private AgentEngine agentEngine;

    public WaterExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

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

    }

}
