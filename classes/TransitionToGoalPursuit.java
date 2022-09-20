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

            if(raftNode == null) {
    
                System.out.println("TRANSIITON GOAL: NO RAFT!");
    
            }else{

                System.out.println("TRANSIITON GOAL: RAFT FOUND!");
                this.agentEngine.addGoal(raftNode);
                this.agentEngine.pursueGoal();

            }

        }else if(this.agentEngine.isOnWater()) { 
           
            System.out.println("TRANSIITON GOAL: GOING TO LAND");
    
            //floodfill find raft
            GameNode nextGoal = this.agentEngine.peekGoal();
            GameNode landNode = this.agentEngine.getToLand(nextGoal);

            if(landNode != null) {

                System.out.println("TRANSIITON GOAL: LAND FOUND!");
                this.agentEngine.addGoal(landNode);
                this.agentEngine.pursueGoal();
    
            }else{

                System.out.println("TRANSIITON GOAL: NO LAND!");

            }
        }

        this.agentEngine.setAgentState(this.agentEngine.pursueGoal);
        
    }
    
}
