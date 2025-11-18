package localsearch;

import util.tsp.TSPInstance;
import java.util.*;

public class BeamSearchInsert extends BeamSearchInitialization {

    public BeamSearchInsert(TSPInstance tsp, int beamWidth) {
        super(tsp, beamWidth);
    }

    /**
     * Defines how to expand a partial solution by inserting each unvisited city at every possible position
     * @param s
     * @return a list of partial solutions
     */
    @Override
    protected List<PartialSolution> expand(PartialSolution s) {

        List<PartialSolution> children = new ArrayList<>();

        for (int city = 0; city < tsp.nCities(); city++) {
            if (s.toVisit.get(city)) {

                for (int pos = 0; pos <= s.solution.size(); pos++) {

                    ArrayList<Integer> newSol = new ArrayList<>(s.solution);
                    newSol.add(pos, city);

                    BitSet newToVisit = (BitSet) s.toVisit.clone();
                    newToVisit.clear(city);

                    double newCost = evaluatePartialSolution(newSol);

                    children.add(new PartialSolution(newSol, newCost, newToVisit));
                }
            }
        }

        return children;
        //throw new util.NotImplementedException("BeamSearchInsert.expand");
    }
}
