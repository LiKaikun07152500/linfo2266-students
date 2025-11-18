package localsearch;

import java.util.HashMap;

/**
 * Implements a KOpt neighborhood for the TSP problem
 * also known as the Lin-Kernighan heuristic
 */
public class KOptSelection implements NeighborSelection {

    int maxK;

    public KOptSelection(int maxK) {
        this.maxK = maxK;
    }

    @Override
    public Candidate getNeighbor(Candidate candidate) {

        // hint : use a map to save intermediate solutions
        Candidate current = candidate;
        double currentCost = candidate.getCost();

        for (int k = 0; k < maxK; k++) {

            Candidate bestNeighbor = current;
            double bestDelta = 0;

            int n = current.getTour().length;

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {

                    double delta = current.twoOptDelta(i, j);

                    if (delta < bestDelta) {
                        bestDelta = delta;

                        Candidate improved = current.clone();
                        improved.twoOpt(i, j);
                        bestNeighbor = improved;
                    }
                }
            }

            if (bestDelta >= 0) {
                break;
            }

            current = bestNeighbor;
            currentCost = current.getCost();
        }

        return current;
        //throw new util.NotImplementedException("KOpt.getNeighbor");
    }

}
