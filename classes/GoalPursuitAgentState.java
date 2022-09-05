public class GoalPursuitAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public GoalPursuitAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        if(this.agentEngine.hasAxe() && !this.agentEngine.hasRaft() && this.agentEngine.getRaft()) {
            return;
        }

        // if no pending actions retrieve a goal if one exists to enqueue actions
        if(this.agentEngine.pursueGoal()) {
            
            return;

        // if no pending goals, change state
        }else if(this.agentEngine.hasUnexploredLand()) { //conflicts with above logic where its a "goal"?
            //enable land travel
            System.out.println("GOAL MODE: changing state to land");
            this.agentEngine.setAgentState(this.agentEngine.exploreLand); //agentstate retrieves goal from gamestate
        }else if(this.agentEngine.hasUnexploredWater()){
            //enable water travel
            System.out.println("GOAL MODE: changing state to water");
            this.agentEngine.enableLandTravel();
            this.agentEngine.setAgentState(this.agentEngine.exploreWater);
        }else{
            //panic?
        }

        return;

    }



}
