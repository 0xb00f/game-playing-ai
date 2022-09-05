import java.util.HashSet;

public class TerrainManager {

    private GameState state;
    private AgentState landExplore;
    private AgentState waterExplore;
    private AgentState goalPursuit;
    private HashSet<Character> validTerrain;
    //private boolean onWater;

    public TerrainManager(GameState s, AgentState l, AgentState w, AgentState g) {

        this.state = s;
        this.landExplore = l;
        this.waterExplore = w;
        this.goalPursuit = g;
        this.validTerrain = new HashSet<Character>();

    }

    public boolean isValidTerrain(AgentState currState, char c) {

        if(currState == this.landExplore) {

            switch(c) {

                case ' ' : return true;
                case '~' : return false; //this.state.isOnWater();
                case 'a' : case 'k' : case 'd' : case '$' : case '-' : case 'T' : return true;
                default : return false;

            }

        }else if(currState == this.waterExplore) {

            switch(c) {

                case ' ' : return false; //return this.state.isOnWater() == false;
                case '~' : return true;
                default : return false;

            }

        }else if(currState == this.goalPursuit) {

            switch(c) {

                case ' ' : return true;
                case '~' : return this.state.hasRaft();
                case '-' : return this.state.hasKey();
                case 'a' : case 'k' : case 'd' : case '$' : return true;
                default : return false;

            }

        }

        return false;

    }

    public void raftCollected() {

        this.state.setRaft(true);
        this.enableWaterTravel();

    }

    public void processTerrainChange(char nextTerrain) {

        char currTerrain = this.state.getCurrNode().getType();

        if(currTerrain == '~' && nextTerrain == ' ') { 

            this.disableWaterTravel();
            this.enableLandTravel();
            this.state.setRaft(false);
            this.setOffWater();

        }else if(currTerrain == ' ' && nextTerrain == '~') { //water travel enabled when collecting axe

            this.setOnWater();
            
        }

    }

    public void enableWaterTravel() {

        this.validTerrain.add('~');

    }

    public void enableLandTravel() {

        this.validTerrain.add(' ');

    }

    public void disableWaterTravel() {

        this.validTerrain.remove('~');

    }

    public void disableLandTravel() {

        this.validTerrain.remove(' ');

    }

    public void setOnWater() {

        this.state.setOnWater();

    }

    public void setOffWater() {

        this.state.setOffWater();

    }
    
}
