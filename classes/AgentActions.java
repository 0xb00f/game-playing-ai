import java.awt.Point;
import java.util.ArrayDeque;

public class AgentActions {
    
    private ArrayDeque<Character> pendingActions;
    private GameState state;

    public AgentActions(GameState state) {

        this.pendingActions = new ArrayDeque<Character>();
        this.state = state;

    }

    public Character getNextAction() {

        return this.pendingActions.poll();

    }

    public boolean hasNextAction() {

        return this.pendingActions.size() > 0;

    }

    public void enqueueAction(Character c) {

        this.pendingActions.add(c);

    }

    public void unlockDoor() {

        this.enqueueAction('u');

    }

    public void chopTree() {

        this.enqueueAction('c');

    }

    public void useBomb() {

        this.enqueueAction('b');

    }

    public void moveForward() {

        this.enqueueAction('f');

    }

    public void turnLeft() {

        this.enqueueAction('l');

    }

    public void turnRight() {

        this.enqueueAction('r');

    }

    public void turnAround() {

        this.turnRight();
        this.turnRight();

    }

    private void clearObstacle(GameNode n) {

        switch(n.getType()) {

            case 'T' : this.chopTree(); break;
            case '*' : this.useBomb(); break; 
            case '-' : this.unlockDoor(); break;

        }

        n.clearNode();

    }

    public void turnToBearing(int dir, int vdir) {

        int diff = vdir - dir;

        if(diff == 0) return;

        if(diff == -1 || diff == 3) this.turnRight();
        if(diff == 1 || diff == -3)  this.turnLeft();
        if(diff == -2 || diff == 2) this.turnAround();

    }

    public int bearingFrom(GameNode c, GameNode n) {

        Point next = n.getPoint();
        Point curr = c.getPoint();

        if(next.y > curr.y && next.x == curr.x) return 0;
        if(next.y < curr.y && next.x == curr.x) return 2;
        if(next.x > curr.x && next.y == curr.y) return 1;
        if(next.x < curr.x && next.y == curr.y) return 3;

        return -1;

    }

    public void goToGoal(Goal g) {

        GameNode prev = this.state.getCurrNode();
        int vdir = this.state.getDirection();

        for(GameNode next : g.getPath()) { 

            int nextDir = bearingFrom(prev,next);
            if(nextDir == -1) continue; //ought never to happen
            turnToBearing(nextDir,vdir);
            if(next.isClearableObstacle(this.state)) this.clearObstacle(next);
            this.moveForward();
            vdir = nextDir;
            prev = next;

        }

    }

}
