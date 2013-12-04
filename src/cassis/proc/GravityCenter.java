/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `GravityCenter.java'                                      *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.proc;

import cassis.struct.Coord;
import cassis.struct.Rational;
import cassis.struct.Vector;
import cassis.ind.Individual;
import cassis.ind.Point;
import cassis.ind.Weight;

public final class GravityCenter extends Traversal {
    // constant
    private static final Coord HUNDRED = new Coord(100);
    // representation
    private double count, weight;
    private Vector center, position;

    // constructors
    public GravityCenter() {
        super();
    }

    public GravityCenter(String title) {
        super(title);
    }

    // access method
    public Vector toVector() {
        return this.center.scale(new Rational(HUNDRED, new Coord((long)(this.count * 100.0))));
    }

    public String toString() { return this.toVector().toString(); }

    // methods
    public void initialize() {
        this.count = 0.0;
        this.weight = 0.0;
        this.center = new Vector(Coord.ZERO, Coord.ZERO, Coord.ZERO);
        this.position = null;
    }

    public void visit(Individual current) {
        if (current.getClass() == Weight.class)
            this.weight = ((Weight)current).value();
        else if (current.getClass() == Point.class)
            this.position = ((Point)current).position();
        else
            return;
        if ((this.weight != 0.0) && (this.position != null)) {
            this.count += this.weight;
            this.center = this.center.add(this.position.scale(new Rational(new Coord((long)(this.weight * 100.0)), HUNDRED)));
        }
    }

    public void leave(Individual current) {
        if (current.getClass() == Weight.class)
            this.weight = 0.0;
        else if (current.getClass() == Point.class)
            this.position = null;
    }
}
