import java.util.LinkedList;

public class LandExploreAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public LandExploreAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        LinkedList<GameNode> path = this.agentEngine.exploreLand(); 

        if(path.size() > 0) {

            this.agentEngine.addGoalActions(path);

        }else if(this.agentEngine.hasGoal()) { 

            this.agentEngine.setAgentState(this.agentEngine.transGoal); 

        }else if(this.agentEngine.hasAxe()){

            this.agentEngine.setAgentState(this.agentEngine.transWater);

        }else{

            System.exit(1);

        }

    }

}
