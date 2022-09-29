public class TransitionToLandExplore implements AgentState {

    private AgentEngine agentEngine;

    public TransitionToLandExplore(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }
    
    public void doTask(char[][] view) {

        GameNode landNode = this.agentEngine.findUnexploredRegion(' ');

        if(landNode != null && !this.agentEngine.isWorthExploring(landNode)) return;

        if(landNode == null) { //panic
            
            System.exit(1);

        }

        this.agentEngine.addGoal(landNode);
        this.agentEngine.pursueGoal();
        
        this.agentEngine.setAgentState(this.agentEngine.exploreLand);

    }

    
}
