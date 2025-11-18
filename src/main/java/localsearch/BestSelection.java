package localsearch;

import java.util.Random;

/**
 * Select the best neighbor that improves the solution.
 * If no neighbor improves the solution, return the original solution
 */
public class BestSelection implements NeighborSelection {
    @Override
    public Candidate getNeighbor(Candidate candidate) {
        int n = candidate.getTour().length;
        int bestI = -1;
        int bestJ = -1;
        double bestDelta = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                double delta = candidate.twoOptDelta(i, j);
                if (delta < 0 && delta < bestDelta) {
                    bestDelta = delta;
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        Candidate bestNeighbor = candidate.clone();

        if (bestI != -1 && bestJ != -1) {
            bestNeighbor.twoOpt(bestI, bestJ);
        }
        return bestNeighbor;
        //throw new util.NotImplementedException("BestSelection.getNeighbor");
    }
}
