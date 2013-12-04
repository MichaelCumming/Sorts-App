/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Argument.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.struct;

public final class Argument {

    // representation
    private Parameter type;
    private Object instance;

    Argument(Parameter type, Object instance) {
	this.type = type;
	this.instance = instance;
    }

    Parameter type() {
	return this.type;
    }

    public Object value() {
	return this.instance;
    }
}
