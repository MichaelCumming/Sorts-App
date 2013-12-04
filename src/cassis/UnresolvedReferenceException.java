/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `UnresolvedReferenceException.java'                       *
 * written by: Rudi Stouffs                                  *
 * last modified: 06.2.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis;

/**
 * An <b>UnresolvedReferenceException</b> is thrown when a parsed reference
 * cannot be matched with a known element.
 * An unresolved-reference-exception has an optional detailed message,
 * a <tt>String</tt> object describing the particular exception.
 */
public final class UnresolvedReferenceException extends Exception {

    // constructors

    /**
     * Constructs a <b>UnresolvedReferenceException</b> with no detailed
     * message.
     */
    public UnresolvedReferenceException() {
	super();
    }
    /*
     * Constructs a <b>UnresolvedReferenceException</b> object with a detailed
     * message.
     * @param s the detail message
     */
    public UnresolvedReferenceException(String s) {
	super(s);
    }
}
