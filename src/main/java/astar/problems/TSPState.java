package astar.problems;

import astar.solver.State;

import java.util.BitSet;


public class TSPState extends State {

    // TODO specify state fields and constructor(Done)
    private final int currentCity;
    private final BitSet visitedCities;
    // constructor
    public TSPState(int currentCity, BitSet visitedCities) {
        this.currentCity = currentCity;
        this.visitedCities = (BitSet) visitedCities.clone();
    }

    // get current city
    public int getCurrentCity() {
        return currentCity;
    }

    // get visited cities
    public BitSet getVisitedCities() {
        return (BitSet) visitedCities.clone();
    }

    @Override
    public int hash() {
        // TODO implement hash of state, see interface(Done)
        int result = Integer.hashCode(currentCity);
        result = 31 * result + visitedCities.hashCode();
        return result;
        //return 0;
    }

    @Override
    public boolean isEqual(State s) {
        // TODO implement equality of states, see interface(Done)
        if (!(s instanceof TSPState)) {
            return false;
        }
        TSPState other = (TSPState) s;
        return this.currentCity == other.currentCity
                && this.visitedCities.equals(other.visitedCities);
        //return false;
    }

}
