package localsearch;

import util.Pair;
import util.tsp.TSPInstance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * Select the best neighbor that is not tabu
 */
public class BestWithTabuSelection implements NeighborSelection {

    int iteration = 0;
    private int tabuSize; // size of the tabu list
    int[][] tabu; // tabu[i][j] is the next iteration when the edge (i,j) become non-tabu

    public BestWithTabuSelection(int tabuSize, TSPInstance tsp) {
        iteration = 0;
        this.tabuSize = tabuSize;
        tabu = new int[tsp.n][tsp.n];
    }


    public void addTabu(int i, int j) {
        tabu[i][j] = iteration + tabuSize;
    }

    public boolean isTabu(int i, int j) {
        return iteration < tabu[i][j];
    }

    /**
     * Selects the best non-tabu 2Opt neighbor
     * It should never return the same candidate as the one given in argument
     * The first removed edge in the selected move becomes tabu
     * @param candidate
     * @return the best non-tabu 2Opt neighbor, the first removed edge in the selected move becomes tabu
     */
    @Override
    public Candidate getNeighbor(Candidate candidate) {
        iteration++;

        int n = candidate.getTour().length;
        Candidate bestNeighbor = null;
        double bestDelta = Double.POSITIVE_INFINITY;
        int tabuA = -1, tabuB = -1;
        int[] originalTour = candidate.getTour().clone();
        for (int j = 2; j < n; j++) {
            int i = 0;
            int cityA = originalTour[i];
            int cityB = originalTour[i + 1];
            if (isTabu(cityA, cityB)) continue;
            double delta = candidate.twoOptDelta(i, j);
            Candidate tempNeighbor = candidate.clone();
            tempNeighbor.twoOpt(i, j);
            if (Arrays.equals(tempNeighbor.getTour(), originalTour)) {
                continue;
            }
            if (delta < bestDelta) {
                bestDelta = delta;
                bestNeighbor = tempNeighbor;
                tabuA = cityA;
                tabuB = cityB;
            }
        }
        if (bestNeighbor == null) {
            for (int i = 1; i < n; i++) {
                for (int j = i + 2; j < n; j++) {
                    int cityA = originalTour[i];
                    int cityB = originalTour[(i + 1) % n];
                    if (isTabu(cityA, cityB)) continue;
                    double delta = candidate.twoOptDelta(i, j);
                    Candidate tempNeighbor = candidate.clone();
                    tempNeighbor.twoOpt(i, j);
                    if (Arrays.equals(tempNeighbor.getTour(), originalTour)) {
                        continue;
                    }
                    if (delta < bestDelta) {
                        bestDelta = delta;
                        bestNeighbor = tempNeighbor;
                        tabuA = cityA;
                        tabuB = cityB;
                    }
                }
            }
        }
        if (bestNeighbor == null) {
            throw new RuntimeException("No valid 2-opt move found");
        }
        addTabu(tabuA, tabuB);
        return bestNeighbor;
        //throw new util.NotImplementedException("SelectionTabu.getNeighbor");
    }

}

