/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `ListOutOfBoundsException.java'                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  The LIST-OUT-OF-BOUNDS EXCEPTION signals an attempt to read outside
//  the bounds of a list or from an empty list.

package cassis.struct;

final class ListOutOfBoundsException extends IndexOutOfBoundsException {

    // constructors

    ListOutOfBoundsException() { super(); }

    ListOutOfBoundsException(String s) { super(s); }
}
