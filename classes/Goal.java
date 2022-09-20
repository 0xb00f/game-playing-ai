import java.util.LinkedList;

public class Goal {

    private int weight;
    private GameNode goalNode; 
    private LinkedList<GameNode> path;

    public Goal(GameNode n) {

        this.weight = this.calcWeight(n);
        this.goalNode = n;
        this.path = new LinkedList<GameNode>();

    }

    // THIS IS GOAL WEIGHT, NOT NODE WEIGHT
    private int calcWeight(GameNode n) { 

        switch(n.getType()) {

            case ' ': return 1; 
            case '~': return 2;
            case 'a': return 3;
            case 'T': return 4;
            case 'k': return 5;
            case 'd': return 6;
            case '$': return 7; 
            default: return 100;

        }

    }

    public int getWeight() {

        return this.weight; 

    }

    public GameNode getGoalNode() {

        return this.goalNode;

    }

    public LinkedList<GameNode> getPath() {

        return this.path;

    }

    public boolean hasPath() {

        return this.path.size() > 0;

    }

    public void addToPath(GameNode n) {

        this.path.add(n);

    }
    
    public void extendPath(LinkedList<GameNode> path) {

        for(GameNode n: path) this.addToPath(n);

    }

}
