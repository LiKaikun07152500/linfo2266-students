package astar.problems;

import astar.solver.Model;
import astar.solver.Transition;
import util.tsp.TSPInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;


public class TSP extends Model<TSPState> {

    TSPInstance instance;
    TSPState root;

    public TSP(TSPInstance instance) {
        this.instance = instance;
        // TODO initialize root state, assuming the salesman starts at city 0(Done)
        BitSet rootVisited = new BitSet(instance.nCities());
        rootVisited.set(0);
        this.root = new TSPState(0, rootVisited);
    }

    @Override
    public boolean isTerminalState(TSPState state) {
        // TODO checks if the state is a terminal state(Done)
        return state.getVisitedCities().cardinality() == instance.nCities();
        //return false;
    }

    @Override
    public double getTerminalStateValue(TSPState state) {
        // TODO return the value of the terminal state, don't forget the salesman must go back to city 0(Done)
        if (!isTerminalState(state)) {
            throw new IllegalArgumentException("can't calculate cost for non-terminal state");
        }
        return instance.distance(state.getCurrentCity(), 0);
        //return 0;
    }

    @Override
    public TSPState getRootState() {
        return root;
    }

    @Override
    public List<Transition<TSPState>> getTransitions(TSPState state) {
        // TODO specify the transitions applicable to the given state(Done)
        List<Transition<TSPState>> transitions = new ArrayList<>();
        int totalCities = instance.nCities();
        BitSet visited = state.getVisitedCities();
        int currentCity = state.getCurrentCity();
        for (int nextCity = 0; nextCity < totalCities; nextCity++) {
            if (!visited.get(nextCity)) {
                BitSet newVisited = (BitSet) visited.clone();
                newVisited.set(nextCity);
                TSPState nextState = new TSPState(nextCity, newVisited);
                double cost = instance.distance(currentCity, nextCity);
                transitions.add(new Transition<>(nextState, nextCity, cost));
            }
        }
        return transitions;
        //return null;
    }
    // calculte the MST heuristic
    private double calculateMSTHeuristic(TSPState state) {
        int totalCities = instance.nCities();
        BitSet visited = state.getVisitedCities();
        int currentCity = state.getCurrentCity();
        List<Integer> unvisited = new ArrayList<>();
        for (int i = 0; i < totalCities; i++) {
            if (!visited.get(i)) {
                unvisited.add(i);
            }
        }
        if (unvisited.isEmpty()) {
            return 0.0;
        }
        int n = unvisited.size();
        double[] key = new double[n];
        boolean[] inMST = new boolean[n];
        Arrays.fill(key, Double.MAX_VALUE);
        key[0] = 0.0;
        double mstWeight = 0.0;
        for (int count = 0; count < n; count++) {
            int u = -1;
            double minKey = Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!inMST[i] && key[i] < minKey) {
                    minKey = key[i];
                    u = i;
                }
            }
            if (u == -1) break;
            inMST[u] = true;
            mstWeight += minKey;
            int cityU = unvisited.get(u);
            for (int v = 0; v < n; v++) {
                if (!inMST[v]) {
                    int cityV = unvisited.get(v);
                    double dist = instance.distance(cityU, cityV);
                    if (dist < key[v]) {
                        key[v] = dist;
                    }
                }
            }
        }
        double minDistToMST = Double.MAX_VALUE;
        for (int city : unvisited) {
            minDistToMST = Math.min(minDistToMST, instance.distance(currentCity, city));
        }
        double minDistToStart = Double.MAX_VALUE;
        for (int city : unvisited) {
            minDistToStart = Math.min(minDistToStart, instance.distance(city, 0));
        }
        return minDistToMST + mstWeight + minDistToStart;
    }
    @Override
    public double h(TSPState state) {
        // TODO return the heuristic value for the given state(Done)
        if (isTerminalState(state)) {
            return 0.0;
        }
        return calculateMSTHeuristic(state);
        //return Double.MAX_VALUE;
    }

}
