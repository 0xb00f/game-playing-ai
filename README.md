<h3 align="center">Game-Playing AI</h3>

This project is an autonomous agent that plays a simple text-based adventure game. The agent is written in Java and uses graph search
algorithms in order to explore, collect items, and pursue goals to win the game. This is an old university assignment I always wanted to return to simply because it was fun and challenging. 

This agent is able to win all 10 levels from the original assignment.

For more information about the game itself, as well as how to run it, see the original assignment spec [here](http://www.cse.unsw.edu.au/~cs3411/17s1/hw3raft/) or in the file spec.htm

In order to run the game from a terminal type 

```bash
java GameEngine -p <PORT> -i <PATH/TO/LEVEL>
```

and in order to plug the agent into the game, type in a separate terminal

```bash
java AgentClient -p <PORT>
```

