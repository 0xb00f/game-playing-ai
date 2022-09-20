public class GoalPursuitAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public GoalPursuitAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        /*
         * HANGS IN GOAL PURSUIT ON s7
         * seems not to explore the whole land
         * and doesnt see critical bomb,
         * instead loops on unreachable one
         */

        System.out.println("IN GOALPURSUIT");
        boolean success = false;

        if(this.agentEngine.hasGoal()) { 

            System.out.println("IN GOALPURSUIT: PURSUING GOAL!");
            success = this.agentEngine.pursueGoal();

        }
        
        if(!success){

            //first try land exploration
            GameNode n = this.agentEngine.findUnexploredRegion(' ');

            if(n != null && !this.agentEngine.isWorthExploring(n)) return;

            if(n != null) { 
                
                System.out.println("GOALPURSUIT: GOING INTO LAND EXPLORE");
                this.agentEngine.setAgentState(this.agentEngine.transLand);
                return;

            }else if(this.agentEngine.hasAxe()){
                
                System.out.println("GOALPUSUIT: GOING INTO WATER EXPLORE!");
                this.agentEngine.setAgentState(this.agentEngine.transWater);

            }else{
                
                System.out.println("GOALPUSUIT: PANIC - AGENT LOST!");
                System.exit(1);

            }

        }

        return;

    }

}
