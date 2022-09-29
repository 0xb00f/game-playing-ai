public class TransitionToWaterExplore implements AgentState {

    private AgentEngine agentEngine;

    public TransitionToWaterExplore(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public void doTask(char[][] view) {

        if(!this.agentEngine.hasRaft()) {
        
            //floodfill find raft
            GameNode raftNode = this.agentEngine.findReachableNode('T');

            if(raftNode == null) { //panic

                System.exit(1);

            }

            this.agentEngine.addGoal(raftNode);
            this.agentEngine.pursueGoal();
            return;

        }

        if(!this.agentEngine.isOnWater()) {

            //floodfill find unexplored water
            GameNode waterNode = this.agentEngine.findUnexploredRegion('~');
            //enqueue actions if found otherwise panic
            if(waterNode == null) {

                System.exit(1);

            }

            this.agentEngine.addGoal(waterNode);
            this.agentEngine.pursueGoal();

        }

        this.agentEngine.setAgentState(this.agentEngine.exploreWater);

    }
    
}
