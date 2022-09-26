import java.util.LinkedList;

public class WaterExploreAgentState implements AgentState {

    private AgentEngine agentEngine;

    public WaterExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        System.out.println("IN WATER EXPLORE");

        LinkedList<GameNode> path = this.agentEngine.exploreWater();

        if(path.size() > 0) {

            this.agentEngine.addGoalActions(path);
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
