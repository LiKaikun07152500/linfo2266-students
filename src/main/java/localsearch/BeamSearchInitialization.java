package localsearch;

import util.tsp.TSPInstance;
import java.util.*;

public abstract class BeamSearchInitialization extends Initialization {

    protected int beamWidth;

    public BeamSearchInitialization(TSPInstance tsp, int beamWidth) {
        super(tsp);
        this.beamWidth = beamWidth;
    }

    /** Defines how to expand a partial solution */
    protected abstract List<PartialSolution> expand(PartialSolution s);

    /**
     * Generates an initial solution using beam search
     * at each iteration, computes all possible expansions of the partial solutions in the beam
     * keeps only the best beamWidth partial solutions for the next iteration
     * Note : initially, the beam contains only one partial solution with the first city (0) visited
     * @return a Candidate representing the initial solution
     */
    @Override
    public Candidate getInitialSolution() {

        ArrayList<Integer> start = new ArrayList<>();
        start.add(0);

        BitSet toVisit = new BitSet(tsp.nCities());
        toVisit.set(1, tsp.nCities());

        PartialSolution first = new PartialSolution(start, 0.0, toVisit);

        List<PartialSolution> beam = new ArrayList<>();
        beam.add(first);

        while (true) {
            List<PartialSolution> allChildren = new ArrayList<>();

            for (PartialSolution ps : beam) {
                if (ps.toVisit.isEmpty()) {
                    int[] tour = new int[ps.solution.size()];
                    for (int i = 0; i < tour.length; i++)
                        tour[i] = ps.solution.get(i);
                    return new Candidate(tsp, tour);
                }
                allChildren.addAll(expand(ps));
            }

            allChildren.sort(Comparator.comparingDouble(a -> a.cost));

            beam = allChildren.subList(0, Math.min(beamWidth, allChildren.size()));
        }
        //throw new util.NotImplementedException("BeamSearchInitialization.getInitialSolution");
    }

    /**
     * Evaluate the cost of a partial solution (not complete tour)
     * @param tour
     * @return the cost of the partial solution : the sum of distances between consecutive cities
     */
    protected double evaluatePartialSolution(ArrayList<Integer> tour) {
        double distance = 0;
        for (int i = 0; i < tour.size() - 1; i++)
            distance += tsp.distance(tour.get(i), tour.get(i + 1));
        return distance;
    }

    /**
     * A class representing a partial solution in the beam search
     * contains the remaining cities to visit as a BitSet
     * the current solution as an ArrayList of integers
     * and the cost of the partial solution
     */
    protected static class PartialSolution {
        ArrayList<Integer> solution;
        double cost;
        BitSet toVisit;

        PartialSolution(ArrayList<Integer> solution, double cost, BitSet toVisit) {
            this.solution = solution;
            this.cost = cost;
            this.toVisit = toVisit;
        }
    }
}
