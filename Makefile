JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class: $(JC) $(JFLAGS) $*.java

CLASSES = \
./classes/AgentActions.java \
./classes/AgentClient.java \
./classes/AgentEngine.java \
./classes/GameMap.java \
./classes/GameNode.java \
./classes/Goal.java \
./classes/GameState.java \
./classes/GoalPursuitAgentState.java \
./classes/LandExploreAgentState.java \
./classes/WaterExploreAgentState.java \
./classes/SearchGameMap.java \
./engine/GameEngine.java \
 
default: classes

classes: $(CLASSES:.java=.class)

clean: $(RM) *.class
