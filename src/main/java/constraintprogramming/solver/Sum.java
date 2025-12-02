package constraintprogramming.solver;

import util.NotImplementedException;

public class Sum extends Constraint{

    private final Variable[] x;
    private final Variable y;

    /**
     * Creates a sum constraint.
     * This constraint holds iff
     * {@code x[0]+x[1]+...+x[x.length-1] == y}.
     *
     * @param x the non empty left hand side of the sum
     * @param y the right hand side of the sum
     */
    public Sum(Variable[] x, Variable y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a sum constraint.
     * This constraint holds iff
     * {@code x[0]+x[1]+...+x[x.length-1] == y}.
     *
     * @param x the non empty left hand side of the sum
     * @param y the right hand side of the sum
     */
    public Sum(Variable[] x, int y) {
        this(x, x[0].getCsp().makeVariable(y+1));
        this.y.dom.fix(y);
    }


    @Override
    boolean propagate() {
        // TODO 1 update the value of y based on x(done)
        boolean changed = false;
        int sumMin = 0;
        int sumMax = 0;
        for (Variable xi : x) {
            sumMin += xi.dom.min();
            sumMax += xi.dom.max();
        }
        if (y.dom.removeBelow(sumMin)) changed = true;
        if (y.dom.removeAbove(sumMax)) changed = true;
        // TODO 2 update the value of each x[i] based on y and the other x[i]'s
        int yMin = y.dom.min();
        int yMax = y.dom.max();
        for (int i = 0; i < x.length; i++) {
            Variable xi = x[i];
            int otherMin = 0, otherMax = 0;
            for (int j = 0; j < x.length; j++) {
                if (j != i) {
                    otherMin += x[j].dom.min();
                    otherMax += x[j].dom.max();
                }
            }
            int xiNewMin = yMin - otherMax;
            int xiNewMax = yMax - otherMin;
            if (xi.dom.removeBelow(xiNewMin)) changed = true;
            if (xi.dom.removeAbove(xiNewMax)) changed = true;
        }
        return changed;
        //throw new NotImplementedException("sum");
    }
}
