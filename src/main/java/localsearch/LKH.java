package localsearch;

import util.Pair;
import util.tsp.TSPInstance;

import java.util.Arrays;

public class LKH {

    private TSPInstance instance;

    public LKH(TSPInstance instance) {
        this.instance = instance;
    }

    /**
     * Convert a tour representation to a successor representation
     *
     * @param c a candidate solution
     * @return an array of successors
     */
    int[] tourToSucc(Candidate c) {

        int[] tour = c.getTour();
        int n = tour.length;
        int[] succ = new int[n];
        for (int i = 0; i < n; i++) {
            succ[tour[i]] = tour[(i + 1) % n]; // successor of tour[i] is the next city
        }
        return succ;
        //throw new util.NotImplementedException("LKH.tourToSucc");
    }

    /**
     * Reverse the path from b to c in the successor representation
     * and connect b to d
     *
     * @param succ successor representation of the current tour
     * @param b    start of the path to reverse
     * @param c    end of the path to reverse
     * @param d    node to connect b to
     */
    void reversePath(int[] succ, int b, int c, int d) {
        int i = b;
        int si = succ[b];
        succ[b] = d;
        while (i != c) {
            int temp = succ[si];
            succ[si] = i;
            i = si;
            si = temp;
        }
    }

    /**
     * Convert a successor representation to a tour representation
     * the tour starts from node 0
     * e.g., succ = [2,0,1] -> tour = [0,2,1]
     *
     * @param succ
     * @return
     */
    int[] succToTour(int[] succ) {

        int n = succ.length;
        int[] tour = new int[n];
        int current = 0; // start from node 0
        for (int i = 0; i < n; i++) {
            tour[i] = current;
            current = succ[current];
        }
        return tour;
        //throw new util.NotImplementedException("LKH.succToTour");
    }



    /**
     * Apply the Lin-Kernighan-Helsgaun heuristic to improve the candidate solution
     *
     * @param candidate
     * @return an improved candidate solution
     */
    public Candidate applyLKH(Candidate candidate) {

        Candidate improved = new Candidate(candidate.getTsp(), candidate.getTour().clone());

        int n = improved.getTour().length;
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            double bestDelta = 0;
            int bestI = -1, bestJ = -1;
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    double delta = improved.twoOptDelta(i, j);
                    if (delta < bestDelta) {
                        bestDelta = delta;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }
            if (bestDelta < -1e-6) {
                improved.twoOpt(bestI, bestJ);
                improvement = true;
            }
        }

        return improved;
    }
}
