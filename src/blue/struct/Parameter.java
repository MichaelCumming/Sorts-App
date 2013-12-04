/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Parameter.java'                                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 30.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.struct;

public final class Parameter {
    public final static Parameter NONE = new Parameter(true, 0);
    public final static Parameter IDENTIFIER = new Parameter(true, 1);
    public final static Parameter UPPERBOUND = new Parameter(true, 1);
    public final static Parameter LOCALE = new Parameter(true, 2);
    public final static Parameter SORTSLINK = new Parameter(false, 2);
    // representation
    private boolean optional;
    private int args;

    private Parameter(boolean optional, int args) {
        this.optional = optional;
        this.args = args;
    }

    public boolean optional() {
        return this.optional;
    }

    public Argument argument(Object value) {
        return new Argument(this, value);
    }

    /** Returns the cardinality of */
    public int cardinality() {
        return this.args;
    }
}
