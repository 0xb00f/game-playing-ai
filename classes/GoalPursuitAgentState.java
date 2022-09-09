public class GoalPursuitAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public GoalPursuitAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        if(this.agentEngine.pursueGoal()) return;

        GameNode n = this.agentEngine.findReachableNode(' ');
        if(n != null) { 
            
            System.out.println("GOAL MODE: changing state to land");
            this.agentEngine.setAgentState(this.agentEngine.exploreLand);

        }
        
        n = this.agentEngine.findReachableNode('~');
        if(n != null && this.agentEngine.hasAxe()) {

            //enable water travel
            System.out.println("GOAL MODE: changing state to water");
            this.agentEngine.enableLandTravel();
            this.agentEngine.setAgentState(this.agentEngine.exploreWater);

        }else{
            
            System.out.println("GOAL MODE: PANIC!");
            System.exit(1);

        }

        return;

    }



}
