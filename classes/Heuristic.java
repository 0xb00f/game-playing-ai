public interface Heuristic {

    public int score(GameState state, GameNode curr, GameNode end);
    
}
