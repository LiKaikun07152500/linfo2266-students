package constraintprogramming.solver;
import java.util.BitSet;
import java.util.function.IntFunction;
import java.util.function.BiFunction;

/**
 * A constraint between two
 * consecutive variables of a knight's tour
 * ensuring that the knight can move from one
 * position to the other.
 */
public class KnightMove extends Constraint {
    private final Variable x;
    private final Variable y;
    private final int n;

    /**
     * y is a position of the chess board accessible from x
     * by a knight move.
     * @param x
     * @param y
     * @param n the size of the board (n x n)
     */
    public KnightMove(Variable x, Variable y, int n) {
        this.x = x;
        this.y = y;
        this.n = n;

    }

    @Override
    boolean propagate() {
        boolean changed = false;
        IntFunction<int[]> posToCoord = pos -> new int[]{pos / n, pos % n};
        BiFunction<Integer, Integer, Integer> coordToPos = (row, col) -> row * n + col;
        BitSet allowedY = new BitSet(n * n);
        for (int xPos : x.dom) {
            int[] coord = posToCoord.apply(xPos);
            int row = coord[0], col = coord[1];
            int[][] moves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                    {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
            for (int[] m : moves) {
                int newRow = row + m[0];
                int newCol = col + m[1];
                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n) {
                    int yPos = coordToPos.apply(newRow, newCol);
                    allowedY.set(yPos);
                }
            }
        }
        Domain yDomCopy = y.dom.clone();
        for (int yPos : yDomCopy) {
            if (!allowedY.get(yPos)) {
                if (y.dom.remove(yPos)) {
                    changed = true;
                }
            }
        }
        BitSet allowedX = new BitSet(n * n);
        for (int yPos : y.dom) {
            int[] coord = posToCoord.apply(yPos);
            int row = coord[0], col = coord[1];
            int[][] moves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                    {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
            for (int[] m : moves) {
                int newRow = row + m[0];
                int newCol = col + m[1];
                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n) {
                    int xPos = coordToPos.apply(newRow, newCol);
                    allowedX.set(xPos);
                }
            }
        }
        Domain xDomCopy = x.dom.clone();
        for (int xPos : xDomCopy) {
            if (!allowedX.get(xPos)) {
                if (x.dom.remove(xPos)) {
                    changed = true;
                }
            }
        }
        return changed;
        //throw new util.NotImplementedException("EulerConstraint");
    }

}
