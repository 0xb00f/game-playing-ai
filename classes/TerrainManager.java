public class TerrainManager {

    private AgentState landExplore;
    private AgentState waterExplore;
    private AgentState goalPursuit;

    public TerrainManager(AgentState l, AgentState w, AgentState g) {

        this.landExplore = l;
        this.waterExplore = w; 
        this.goalPursuit = g;

    }

    public boolean isValidTerrain(GameState state, AgentState currState, char c) { 

        if(currState == this.landExplore) {

            switch(c) {

                case ' ' : return true;
                case '-' : return state.hasKey();
                case 'a' : case 'k' : case 'd' : case '$' : return true; 
                default : return false;

            }

        }else if(currState == this.waterExplore) {

            switch(c) {

                case '~' : return true;
                default : return false;

            }

        }else if(currState == this.goalPursuit) { 

            switch(c) {

                case ' ' : return true;
                case '*' : return state.hasBomb();
                case '~' : return state.hasRaft(); 
                case '-' : return state.hasKey(); 
                case 'T' : return state.hasAxe();
                case 'a' : case 'k' : case 'd' : case '$' : return true; 
                default : return false;

            }

        }else{ 

            switch(c) {

                case ' ' : return true;
                case '*' : return false; 
                case '~' : return state.hasRaft(); 
                case '-' : return state.hasKey(); 
                case 'T' : return state.hasAxe();
                case 'a' : case 'k' : case 'd' : case '$' : return true; 
                default : return false;

            }

        }

    }

    public void processTerrainChange(GameState state, GameNode curr, char nextTerrain) {  //move to agentengine?

        char currTerrain = curr.getType();

        if(currTerrain == '~' && nextTerrain == ' ') { 

            state.setRaft(false); 
            state.setOffWater(); 

        }else if(currTerrain == ' ' && nextTerrain == '~') { 

            state.setOnWater(); 
            
        }

    }
    
}
