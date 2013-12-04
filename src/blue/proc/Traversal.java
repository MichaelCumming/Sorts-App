/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Traversal.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.proc;

import blue.ind.Individual;

public abstract class Traversal {
    // representation
    private String title;

    // constructors
    Traversal() {
        super();
        this.title = null;
        this.initialize();
    }

    Traversal(String title) {
        super();
        this.title = title;
        this.initialize();
    }

    // methods
    abstract public void initialize();

    abstract public String toString();

    public void visit(Individual current) { }

    public void leave(Individual current) { }

    public String getTitle() { return this.title; }
}
