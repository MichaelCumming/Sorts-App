/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `ParseException.java'                                     *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.parse;

public final class ParseException extends Exception {
    public ParseException(ParseReader p, String s) {
	super(p.toExceptionString(s));
    }
}
