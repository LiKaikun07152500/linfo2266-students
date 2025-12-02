package constraintprogramming.problems;

import constraintprogramming.solver.TinyCSP;
import constraintprogramming.solver.Variable;
import util.NotImplementedException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Solves a magic square problem
 * A magic square is a square of size n*n, with the following constraints:
 * - each number within a cell is included between 1 (included) and n*n (included)
 * - each number can appear once and only once within the magic square
 * - the sum of each row, columns and of the 2 diagonals are equal
 */
public class MagicSquareSolver {

    public final MagicSquareInstance instance; // the instance to solve
    public final TinyCSP csp; // the solver to use to solve the instance

    public MagicSquareSolver(MagicSquareInstance instance) {
        this.instance = instance;
        this.csp = new TinyCSP();
    }

    /**
     * Solves the instance {@link MagicSquareSolver#instance} by
     * - creating the variables {@link TinyCSP#makeVariable(int)} of the problems
     * - adding the constraints of the problem to the variables
     *   (see the methods within {@link TinyCSP} for a list of the constraints)
     * and returns all {@link constraintprogramming.problems.MagicSquareInstance.Solution} related to it
     *
     * @return list of solutions to the given instance (possibly empty if no solution exists)
     */
    public List<MagicSquareInstance.Solution> solve() {
        List<MagicSquareInstance.Solution> listSol = new ArrayList<>();
        int n = instance.n();
        int size = n * n;
        Variable[][] vars = new Variable[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                vars[i][j] = csp.makeVariable(size + 1);
                vars[i][j].dom.remove(0);
                if (instance.isValue(i, j)) {
                    vars[i][j].dom.fix(instance.value(i, j));
                }
            }
        }
        Variable[] allVars = new Variable[size];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                allVars[idx++] = vars[i][j];
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                csp.notEqual(allVars[i], allVars[j]);
            }
        }
        int magicConstant = getMagicConstant();
        // 行约束
        for (int i = 0; i < n; i++) {
            csp.sum(vars[i], magicConstant);
        }
        // 列约束
        for (int j = 0; j < n; j++) {
            Variable[] col = new Variable[n];
            for (int i = 0; i < n; i++) col[i] = vars[i][j];
            csp.sum(col, magicConstant);
        }
        // 主对角线（i=j）
        Variable[] diag1 = new Variable[n];
        for (int i = 0; i < n; i++) diag1[i] = vars[i][i];
        csp.sum(diag1, magicConstant);
        // 副对角线（i+j = n-1）
        Variable[] diag2 = new Variable[n];
        for (int i = 0; i < n; i++) diag2[i] = vars[i][n - 1 - i];
        csp.sum(diag2, magicConstant);
        csp.dfs(() -> {
            int[][] solution = new int[n][n];
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    Iterator<Integer> it = vars[i][j].dom.iterator();
                    solution[i][j] = it.next();
                }
            }
            MagicSquareInstance.Solution sol = new MagicSquareInstance.Solution(solution);
            //System.out.println(sol+"\n");
            listSol.add(sol);
        });
        return listSol;
    }

    /**
     * Computes the magic constant of the magic square
     * The magic constant is the value of the sum that needs to be found
     *
     * @return expected sum across all rows, columns and diagonals
     */
    public int getMagicConstant() {
        int n = instance.n();
        return n * (n * n + 1) / 2;
        //throw new NotImplementedException("getMagicConstant");
    }


    public static void main(String[] args) {
        int[][] values = new int[][] {
                {6, 0, 0},
                {1, 0, 0},
                {0, 0, 0},
        };
        MagicSquareInstance instance = new MagicSquareInstance(values);
        System.out.println(instance);
        MagicSquareSolver solver = new MagicSquareSolver(instance);
        List<MagicSquareInstance.Solution> solutionList = solver.solve();
        System.out.println("# solutions = " + solutionList.size());
        System.out.println("1st solution = \n" + solutionList.get(0));
    }

}
