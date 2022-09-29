public class GoalPursuitAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public GoalPursuitAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        boolean success = false;

        if(this.agentEngine.hasGoal()) { 

            success = this.agentEngine.pursueGoal();

        }
        
        if(!success){

            //first try land exploration
            GameNode n = this.agentEngine.findUnexploredRegion(' ');

            if(n != null && !this.agentEngine.isWorthExploring(n)) return;

            if(n != null) { 
                
                this.agentEngine.setAgentState(this.agentEngine.transLand);
                return;

            }else if(this.agentEngine.hasAxe()){
                
                this.agentEngine.setAgentState(this.agentEngine.transWater);

            }else{
                
                System.exit(1);

            }

        }

        return;

    }

}
