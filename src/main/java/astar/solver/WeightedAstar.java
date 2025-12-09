package astar.solver;

import astar.problems.KnapsackState;
import astar.problems.TSPState;
import util.Solution;

import java.util.*;
import java.util.function.BiConsumer;


public class WeightedAstar<S extends State> implements Solver {
    Model<S> model; // the model to solve
    private final double hWeight; // the weight accorded to the heuristic in the f score

    private final Map<S, Double> gScores = new HashMap<>();
    private final Map<S, S> parentMap = new HashMap<>();
    private final Map<S, Integer> decisionMap = new HashMap<>();

    /**
     * Creates a WeightedAstar object to solve the given problem
     * @param model the model to solve
     * @param hWeight the heuristic weight accorded to the heuristic in the f score
     */
    public WeightedAstar(Model<S> model, double hWeight) {
        this.model = model;
        this.hWeight = hWeight;
    }

    /**
     * Search for a solution for the given problem.
     * Returns the first solution found.
     * @return the solution containing the objective value and the decisions taken
     */
    @Override
    public void solve(BiConsumer<Solution, SearchStatistics> onSolution) {
        // TODO(done)
        long startTime = System.currentTimeMillis();
        int nodesExpanded = 0;
        PriorityQueue<Node<S>> openSet = new PriorityQueue<>();
        S root = model.getRootState();
        gScores.put(root, 0.0);
        double rootF = gScores.get(root) + hWeight * model.h(root);
        openSet.add(new Node<>(root, rootF));
        while (!openSet.isEmpty()) {
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
                Solution solution = rebuildSolution(currentState, totalValue);
                long timeMs = System.currentTimeMillis() - startTime;
                SearchStatistics stats = new SearchStatistics(timeMs, nodesExpanded);
                onSolution.accept(solution, stats);
                return;
            }
            for (Transition<S> transition : model.getTransitions(currentState)) {
                S nextState = transition.getSuccessor();
                int decision = transition.getDecision();
                double stepCost = transition.getValue();
                double newG = gScores.get(currentState) + stepCost;
                if (!gScores.containsKey(nextState) || newG < gScores.get(nextState)) {
                    gScores.put(nextState, newG);
                    parentMap.put(nextState, currentState);
                    decisionMap.put(nextState, decision);
                    double newF = newG + hWeight * model.h(nextState);
                    openSet.add(new Node<>(nextState, newF));
                }
            }
        }
        throw new IllegalArgumentException("No solution found");
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
