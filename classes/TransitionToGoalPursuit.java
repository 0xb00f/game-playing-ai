public class TransitionToGoalPursuit implements AgentState {

    private AgentEngine agentEngine;

    public TransitionToGoalPursuit(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        System.out.println("IN TRANSIITON TO GOAL");

        if(!this.agentEngine.hasRaft()) {

            System.out.println("TRANSIITON GOAL: GETTING RAFT");
    
            //floodfill find raft
            GameNode raftNode = this.agentEngine.findReachableNode('T');
            //enqueue actions if found, otherwise PANIC - NO RAFT NO GOALS NO LAND
            if(raftNode == null) {
    
                System.out.println("TRANSIITON GOAL: NO RAFT!");
    
            }else{

                System.out.println("TRANSIITON GOAL: RAFT FOUND!");
                this.agentEngine.addGoal(raftNode);
                this.agentEngine.pursueGoal();

            }

        }

        this.agentEngine.setAgentState(this.agentEngine.pursueGoal);
        
    }
    
}
