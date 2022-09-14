public class TransitionToLandExplore implements AgentState {

    private AgentEngine agentEngine;

    public TransitionToLandExplore(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }
    
    public void doTask(char[][] view) {

        System.out.println("IN TRANSIITON TO LAND");

        //if(this.agentEngine.isOnWater()) {

            GameNode landNode = this.agentEngine.findReachableNode(' ');

            if(landNode == null) { 
                
                System.out.println("TRANSIITON LAND: PANIC - NO LAND FOUND!");
                System.exit(1);

            }

            //System.out.println("TRANSIITON LAND: FOUND LAND AT "+landNode.getPoint().toString()+" visited="+landNode.isVisited());
            this.agentEngine.addGoal(landNode);
            this.agentEngine.pursueGoal();
        
        //}

        this.agentEngine.setAgentState(this.agentEngine.exploreLand);

    }

    
}
