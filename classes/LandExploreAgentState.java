import java.util.LinkedList;

public class LandExploreAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public LandExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {


        System.out.println("IN LAND EXPLORE");

        LinkedList<GameNode> path = this.agentEngine.exploreLand(); 

        if(path.size() > 0) {

            System.out.println("LANDEXP: GOAL HAS ACTIONS");
            this.agentEngine.addGoalActions(path);

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
