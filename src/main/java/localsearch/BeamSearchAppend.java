package localsearch;

import util.tsp.TSPInstance;
import java.util.*;

public class BeamSearchAppend extends BeamSearchInitialization {

    public BeamSearchAppend(TSPInstance tsp, int beamWidth) {
        super(tsp, beamWidth);
    }

    /**
     * Defines how to expand a partial solution by appending each unvisited city at the end
     * @param s a partial solution
     * @return a list of partial solutions
     */
    @Override
    protected List<PartialSolution> expand(PartialSolution s) {
        List<PartialSolution> children = new ArrayList<>();

        for (int city = 0; city < tsp.nCities(); city++) {
            if (s.toVisit.get(city)) {

                ArrayList<Integer> newSol = new ArrayList<>(s.solution);
                newSol.add(city);

                BitSet newToVisit = (BitSet) s.toVisit.clone();
                newToVisit.clear(city);

                double newCost = s.cost;
                if (s.solution.size() > 0) {
                    int lastCity = s.solution.get(s.solution.size() - 1);
                    newCost += tsp.distance(lastCity, city);
                }

                children.add(new PartialSolution(newSol, newCost, newToVisit));
            }
        }

        return children;
        //throw new util.NotImplementedException("BeamSearchAppend.expand");
    }
}
