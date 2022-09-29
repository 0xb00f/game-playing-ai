public class TransitionToGoalPursuit implements AgentState {

    private AgentEngine agentEngine;

    public TransitionToGoalPursuit(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        if(!this.agentEngine.hasRaft()) {
    
            //floodfill find raft
            GameNode raftNode = this.agentEngine.findReachableNode('T');

            if(raftNode != null) {

                this.agentEngine.addGoal(raftNode);
                this.agentEngine.pursueGoal();

            }

        }else if(this.agentEngine.isOnWater()) { 
               
            //floodfill find raft
            GameNode nextGoal = this.agentEngine.peekGoal();
            GameNode landNode = this.agentEngine.getToLand(nextGoal);

            if(landNode != null) {

                this.agentEngine.addGoal(landNode);
                this.agentEngine.pursueGoal();
    
            }
            
        }

        this.agentEngine.setAgentState(this.agentEngine.pursueGoal);
        
    }
    
}
