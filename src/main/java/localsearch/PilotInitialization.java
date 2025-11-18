package localsearch;

import util.tsp.TSPInstance;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Initializes with a tour using the Pilot method
 */
public class PilotInitialization extends BeamSearchInitialization {


    public PilotInitialization(TSPInstance tsp) {
        super(tsp, 1);
    }


    @Override
    protected List<PartialSolution> expand(PartialSolution ps) {
        List<PartialSolution> children = new ArrayList<>();
        int lastCity = ps.solution.get(ps.solution.size() - 1);

        // iterate over all cities not yet visited
        for (int city = ps.toVisit.nextSetBit(0); city >= 0; city = ps.toVisit.nextSetBit(city + 1)) {
            ArrayList<Integer> newSolution = new ArrayList<>(ps.solution);
            newSolution.add(city);

            BitSet newToVisit = (BitSet) ps.toVisit.clone();
            newToVisit.clear(city);

            double newCost = evaluatePartialSolution(newSolution);

            children.add(new PartialSolution(newSolution, newCost, newToVisit));
        }

        return children;
    }
}
