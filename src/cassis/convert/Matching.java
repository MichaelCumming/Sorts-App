/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Matching.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 10.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.convert;

import cassis.struct.Coord;
import cassis.struct.Rational;

interface Matching {

    // constants
    
    // matching levels
    /**
     * A constant to represent an <b>identical</b> match <b>level</b>.
     */
    static final int identicalLevel = 0;
    /**
     * A constant to represent an <b>equivalent</b> match <b>level</b>.
     */
    static final int equivalentLevel = 1;
    /**
     * A constant to represent a <b>similar</b> match <b>level</b>.
     */
    static final int similarLevel = 2;
    /**
     * A constant to represent a <b>convertible</b> match <b>level</b>.
     */
    static final int convertibleLevel = 3;
    /**
     * A constant to represent an <b>incomplete</b> match <b>level</b>.
     */
    static final int incompleteLevel = 4;
    /**
     * A constant to represent an <b>incongruous</b> match <b>level</b>.
     */
    static final int incongruousLevel = 5;

    // matching grades
    /**
     * A constant to represent a <b>concordant</b> match <b>grade</b>.
     */
    static final int concordantGrade = 0;
    /**
     * A constant to represent a <b>part-of</b> match <b>grade</b>.
     */
    static final int partOfGrade = 1;
    /**
     * A constant to represent a <b>subsumptive</b> match <b>grade</b>.
     */
    static final int subsumptiveGrade = 2;
    /**
     * A constant to represent a <b>partial</b> match <b>grade</b>.
     */
    static final int partialGrade = 3;

    // matching operations
    /**
     * A constant to represent <b>no</b> matching <b>operation</b>.
     */
    static final int noOp = -1;
    /**
     * A constant to represent a <b>naming operation</b>.
     */
    static final int namingOp = 0;
    /**
     * A constant to represent a <b>redefinition operation</b>.
     */
    static final int redefinitionOp = 1;
    /**
     * A constant to represent a <b>constraint operation</b>.
     */
    static final int constraintOp = 2;
    /**
     * A constant to represent an <b>rearrangement operation</b>.
     */
    static final int rearrangementOp = 3;
    /**
     * A constant to represent an <b>omission operation</b>.
     */
    static final int omissionOp = 4;
    /**
     * A constant to represent an <b>addition operation</b>.
     */
    static final int additionOp = 5;
    /**
     * A constant to represent a <b>skipping operation</b>.
     */
    static final int skippingOp = 6;
    /**
     * A constant to represent an <b>subsumption operation</b>.
     */
    static final int subsumptionOp = 7;
    /**
     * A constant to represent an <b>part-of operation</b>.
     */
    static final int partOfOp = 8;
    /**
     * A constant to represent the <b>range</b> of matching <b>operations</b>.
     */
    static final int opsRange = 9;
    
    // units
    static final Rational UNIT = new Rational(Coord.ONE, Coord.ONE);
    static final Rational HALF = new Rational(Coord.ONE, new Coord(2));
    static final Rational NAME_UNIT = HALF;
    static final Rational PRIM_UNIT = HALF;
    static final Rational SWAP_UNIT = HALF;
    static final Rational SKIP_UNIT = HALF;
}
