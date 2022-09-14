public class TransitionToWaterExplore implements AgentState {

    private AgentEngine agentEngine;

    public TransitionToWaterExplore(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        System.out.println("IN TRANSIITON TO WATER");

        if(!this.agentEngine.hasRaft()) {

            System.out.println("IN TRANSIITON TO WATER: GETTING RAFT");
        
            //floodfill find raft
            GameNode raftNode = this.agentEngine.findReachableNode('T');
            //enqueue actions if found, otherwise PANIC - NO RAFT NO GOALS NO LAND
            if(raftNode == null) {

                System.out.println("TRANSIITON WATER: PANIC - NO RAFT!");
                System.exit(1);

            }

            System.out.println("IN TRANSITION TO WATER: RAFT FOUND!");
            this.agentEngine.addGoal(raftNode);
            this.agentEngine.pursueGoal();
            return;

        }

        if(!this.agentEngine.isOnWater()) {

            System.out.println("TRANS WATER: GOING TO WATER");

            //floodfill find unexplored water
            GameNode waterNode = this.agentEngine.findReachableNode('~');
            //enqueue actions if found otherwise panic
            if(waterNode == null) {

                System.out.println("TRANS WATER: PANIC - NO WATER!");
                System.exit(1);

            }

            this.agentEngine.addGoal(waterNode);
            this.agentEngine.pursueGoal();

        }

        this.agentEngine.setAgentState(this.agentEngine.exploreWater);

    }
    
}
