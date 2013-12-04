/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `IllegalOverwriteException.java'                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 06.2.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis;

/**
 * An <b>IllegalOverwriteException</b> is thrown when an attempt is made to set
 * a variable that is meant to be set only once and already has a value
 * assigned. An illegal-overwrite-exception has an optional detailed message,
 * a <tt>String</tt> object describing the particular exception.
 */
public final class IllegalOverwriteException extends RuntimeException {

    // constructors

    /**
     * Constructs an <b>IllegalOverwriteException</b> with no detailed message.
     */
    public IllegalOverwriteException() {
	super();
    }
    /*
     * Constructs an <b>IllegalOverwriteException</b> object with a detailed
     * message.
     * @param s the detail message
     */
    public IllegalOverwriteException(String s) {
	super(s);
    }
}
