public class GoalPursuitAgentState implements AgentState {
    
    private AgentEngine agentEngine;

    public GoalPursuitAgentState(AgentEngine agentEngine) {

        this.agentEngine = agentEngine;

    }

    public Character doTask(char[][] view) {

        // map the current view
        this.agentEngine.mapView(view);

        /*
         * the overall logic should be:
         * 1. if there's an action to return, we're not done with the previous task, so return the action
         * 2. if there's a goal to pursue, pursue it and return next action
         * 3. if there's no goal, check for items or unexplored territory, and pursue it, return an action
         * 4. if no goal and nothing yet pursuable, change state, favouring land over water
         */

        // if pending actions, return next action
        if(this.agentEngine.hasNextAction()) {
            Character c = this.agentEngine.getAgentAction();
            this.agentEngine.processAction(c);
            return c;
        }

        // if no pending actions retrieve a goal if one exists to enqueue actions
        if(this.agentEngine.hasGoal()) {
            System.out.println("GOAL MODE: has a goal");
            this.agentEngine.pursueNextBestGoal();

            if(this.agentEngine.hasNextAction()) {
                Character c = this.agentEngine.getAgentAction();
                this.agentEngine.processAction(c);
                return c;
            }

        }

        // enqueue a goal and pursue it if there's bombs left to get or territory to explore
        if(this.agentEngine.goalsAvailable()) {
            System.out.println("GOAL MODE: NEXT BEST GOAL");
            this.agentEngine.generateGoal();
            this.agentEngine.pursueNextBestGoal();
            if(this.agentEngine.hasNextAction()) {
                Character c = this.agentEngine.getAgentAction();
                this.agentEngine.processAction(c);
                return c;
            }
        }

        // if no pending goals, change state
        if(this.agentEngine.hasUnexploredLand()) { //conflicts with above logic where its a "goal"?
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

        return this.agentEngine.playGame(view); //get actions from next state

    }



}
