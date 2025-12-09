package astar.solver;

import astar.problems.Knapsack;
import astar.problems.KnapsackState;
import astar.problems.TSPState;
import util.Solution;
import util.knapsack.KnapsackInstance;

import java.util.*;
import java.util.function.BiConsumer;



public class AnytimeWeightedAstar<S extends State> implements Solver {


    final double hWeight; // the weight accorded to the heuristic in the f score
    final Model<S> model; // the model to solve
    final int timeLimit;  // the time limit (!!! in seconds !!!) before returning the best found solution

    private final Map<S, Double> gScores = new HashMap<>();
    private final Map<S, S> parentMap = new HashMap<>();
    private final Map<S, Integer> decisionMap = new HashMap<>();


    /**
     * Creates an AnytimeWeightedAstar object to solve the given problem
     * @param model the model to solve
     * @param hWeight the heuristic weight accorded to the heuristic in the f score
     * @param timeLimit the time limit (!!! in seconds !!!)
     */
    public AnytimeWeightedAstar(Model<S> model, double hWeight, int timeLimit) {
        this.model = model;
        this.hWeight = hWeight;
        this.timeLimit = timeLimit;
    }

    /**
     * Search for solutions for the given problem.
     * Returns the best solution found when the optimal one is found or when the time limit is reached
     * @return the solution containing the objective value and the decisions taken
     */
    @Override
    public void solve(BiConsumer<Solution, SearchStatistics> onSolution) {
        // TODO(Done)
        long startTime = System.currentTimeMillis();
        int nodesExpanded = 0;
        PriorityQueue<Node<S>> openSet = new PriorityQueue<>();
        S root = model.getRootState();
        gScores.put(root, 0.0);
        double rootF = gScores.get(root) + hWeight * model.h(root);
        openSet.add(new Node<>(root, rootF));
        Solution bestSolution = null;
        double bestValue = Double.MAX_VALUE;
        long timeThreshold = startTime + (long) timeLimit * 1000;
        while (!openSet.isEmpty() && System.currentTimeMillis() < timeThreshold) {
            Node<S> currentNode = openSet.poll();
            S currentState = currentNode.state;
            nodesExpanded++;
            if (model.isTerminalState(currentState)) {
                //double totalValue = gScores.get(currentState) + model.getTerminalStateValue(currentState);
                double totalValue;
                if (currentState instanceof KnapsackState) {
                    totalValue = gScores.get(currentState);
                }
                else if (currentState instanceof TSPState) {
                    totalValue = gScores.get(currentState) + model.getTerminalStateValue(currentState);
                }
                else {
                    totalValue = gScores.get(currentState) + model.getTerminalStateValue(currentState);
                }
                if (totalValue < bestValue) {
                    bestValue = totalValue;
                    bestSolution = rebuildSolution(currentState, totalValue);
                    long currentTimeMs = System.currentTimeMillis() - startTime;
                    SearchStatistics currentStats = new SearchStatistics(currentTimeMs, nodesExpanded);
                    onSolution.accept(bestSolution, currentStats);
                }
                continue;
            }
            for (Transition<S> transition : model.getTransitions(currentState)) {
                S nextState = transition.getSuccessor();
                int action = transition.getDecision();
                double stepCost = transition.getValue();
                double newG = gScores.get(currentState) + stepCost;
                double newF = newG + hWeight * model.h(nextState);
                if (!gScores.containsKey(nextState) || newG < gScores.get(nextState)) {
                    gScores.put(nextState, newG);
                    parentMap.put(nextState, currentState);
                    decisionMap.put(nextState, action);

                    openSet.add(new Node<>(nextState, newF));
                }
            }
        }
        long endTimeMs = System.currentTimeMillis() - startTime;
        SearchStatistics finalStats = new SearchStatistics(endTimeMs, nodesExpanded);
        onSolution.accept(bestSolution, finalStats);
    }


    /**
     * Rebuild and returns the optimal solution of the problem and the set of decisions taken to obtain it
     * @return the optimal solution of the problem and the set of decisions taken to obtain it
     */
    private Solution rebuildSolution(S state, double value) {
        // TODO(Done)
        List<Integer> decisions = new ArrayList<>();
        S current = state;
        while (parentMap.containsKey(current)) {
            S parent = parentMap.get(current);
            int decision = decisionMap.get(current);
            decisions.add(decision);
            current = parent;
        }
        Collections.reverse(decisions);
        //decisions.add(0, 0);

        if (state instanceof TSPState) {
            int cityCount = 0;
            S temp = state;
            while (parentMap.containsKey(temp)) {
                cityCount++;
                temp = parentMap.get(temp);
            }
            cityCount++;
            if (!decisions.contains(0) && !decisions.isEmpty()) {
                decisions.add(0, 0);
            }
            while (decisions.size() < cityCount) {
                decisions.add(0);
            }
        }
        return new Solution(value, decisions);
        //return null;
    }

}





