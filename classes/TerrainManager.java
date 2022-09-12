public class TerrainManager {

    private AgentState landExplore;
    private AgentState waterExplore;
    private AgentState goalPursuit;

    public TerrainManager(AgentState l, AgentState w, AgentState g) {

        this.landExplore = l;
        this.waterExplore = w; 
        this.goalPursuit = g;

    }

    public boolean isValidTerrain(GameState state, AgentState currState, char c) { //gamestate arg

        if(currState == this.landExplore) {

            switch(c) {

                case ' ' : return true;
                case '-' : return state.hasKey();
                case 'a' : case 'k' : case 'd' : case '$' : return true; //T????????
                default : return false;

            }

        }else if(currState == this.waterExplore) {

            switch(c) {

                case '~' : return true;
                default : return false;

            }

        }else{ //catch all?

            switch(c) {

                case ' ' : return true;
                case '*' : return state.hasBomb();
                case '~' : return state.hasRaft(); //kinda needed
                case '-' : return state.hasKey(); //as above, BUT can pass state as arg?
                case 'T' : return state.hasAxe();
                case 'a' : case 'k' : case 'd' : case '$' : return true; //pursue thorugh trees
                default : return false;

            }

        }

    }

    public void processTerrainChange(GameState state, GameNode curr, char nextTerrain) { //can pass state in to toggle things!!!!

        char currTerrain = curr.getType();

        if(currTerrain == '~' && nextTerrain == ' ') { 

            state.setRaft(false); //arg state
            state.setOffWater(); //shifting onwater in here soon...

        }else if(currTerrain == ' ' && nextTerrain == '~') { 

            state.setOnWater(); //as above
            
        }

    }
    
}
