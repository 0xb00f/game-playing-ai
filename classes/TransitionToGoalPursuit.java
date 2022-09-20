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

        }else if(this.agentEngine.isOnWater()) { //else if on water? yes, go to land, dont pursue goals from water, but go to land NEAR THE GOAL
           
            System.out.println("TRANSIITON GOAL: GOING TO LAND");
    
            //floodfill find raft
            GameNode nextGoal = this.agentEngine.peekGoal();
            GameNode landNode = this.agentEngine.getToLand(nextGoal);

            //if(landNode != null && !this.agentEngine.isWorthExploring(landNode)) return;
            //or floodfill from goal??
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
