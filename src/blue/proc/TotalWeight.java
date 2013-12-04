/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `TotalWeight.java'                                        *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.proc;

import blue.ind.Weight;
import blue.ind.Individual;

public final class TotalWeight extends Traversal {
    // representation
    private double count;

    // constructors
    public TotalWeight() {
        super();
    }

    public TotalWeight(String title) {
        super(title);
    }

    // access method
    public double toDouble() { return this.count; }

    public String toString() { return String.valueOf(this.count); }

    // methods
    public void initialize() {
        this.count = 0.0;
    }

    public void visit(Individual current) {
        if (current.getClass() == Weight.class)
            this.count += ((Weight)current).value();
    }
}
