/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `InconsistentFormException.java'                          *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.form;

final class InconsistentFormException extends RuntimeException {

    // constructors

    InconsistentFormException() { super(); }

    InconsistentFormException(String s) { super(s); }
}
