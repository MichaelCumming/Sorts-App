/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `AmbiguityException.java'                                 *
 * written by: Rudi Stouffs                                  *
 * last modified: 06.2.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis;

/**
 * An <b>AmbiguityException</b> is thrown when a parsed specification is
 * ambiguous. An ambiguity-exception has an optional detailed message,
 * a <tt>String</tt> object describing the particular exception.
 */
public final class AmbiguityException extends RuntimeException {

    // constructors

    /**
     * Constructs an <b>AmbiguityException</b> with no detailed message.
     */
    public AmbiguityException() {
	super();
    }
    /*
     * Constructs an <b>AmbiguityException</b> object with a detailed message.
     * @param s the detailed message
     */
    public AmbiguityException(String s) {
	super(s);
    }
}
