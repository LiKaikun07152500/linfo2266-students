package constraintprogramming.solver;

import util.NotImplementedException;

public class LessOrEqual extends Constraint {

    Variable x, y;

    /**
     * Creates a constraint such
     * that {@code x <= y}
     *
     * @param x the left member
     * @param y the right memer
     * @see TinyCSP#notEqual(Variable, Variable)
     */
    public LessOrEqual(Variable x, Variable y) {
        this.x = x;
        this.y = y;
    }

    @Override
    boolean propagate() {
        boolean changed = false;
        int newXMax = Math.min(x.dom.max(), y.dom.max());
        if (x.dom.removeAbove(newXMax)) changed = true;
        int newYMin = Math.max(y.dom.min(), x.dom.min());
        if (y.dom.removeBelow(newYMin)) changed = true;
        return changed;
        //throw new NotImplementedException("lessOrEqual");
    }

}