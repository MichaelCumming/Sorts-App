/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Matching.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import blue.struct.Coord;
import blue.struct.Rational;

interface Matching {
    // constants
    // matching levels
    static final int identicalLevel = 0;
    static final int equivalentLevel = 0;
    static final int stronglySimLevel = 1;
    static final int weaklySimLevel = 2;
    static final int similarLevel = 2;
    static final int convertibleLevel = 3;
    static final int incompleteLevel = 4;
    static final int incongruousLevel = 5;
    // matching grades
    static final int concordantGrade = 0;
    static final int partOfGrade = 1;
    static final int subsumptiveGrade = 2;
    static final int partialGrade = 3;
    // units
    static final Coord ZERO = Coord.ZERO;
    static final Coord ONE = Coord.ONE;
    static final Coord TWO = new Coord(2);
    static final Coord THREE = new Coord(3);
    static final Rational UNIT = new Rational(ONE, ONE);
    static final Rational HALF = new Rational(ONE, TWO);
    static final Rational NAME_UNIT = HALF;
    static final Rational PRIM_UNIT = HALF;
    static final Rational SWAP_UNIT = UNIT;
    static final Rational SKIP_UNIT = new Rational(TWO, ONE);
    static final boolean LONGSWAPS = false;
}
