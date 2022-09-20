public class TransitionToLandExplore implements AgentState {

    private AgentEngine agentEngine;

    public TransitionToLandExplore(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }
    
    public void doTask(char[][] view) {

        System.out.println("IN TRANSIITON TO LAND");

        GameNode landNode = this.agentEngine.findUnexploredRegion(' ');

        if(landNode != null && !this.agentEngine.isWorthExploring(landNode)) return;

        if(landNode == null) { 
            
            System.out.println("TRANSIITON LAND: PANIC - NO LAND FOUND!");
            System.exit(1);

        }

        this.agentEngine.addGoal(landNode);
        this.agentEngine.pursueGoal();
        
        this.agentEngine.setAgentState(this.agentEngine.exploreLand);

    }

    
}
