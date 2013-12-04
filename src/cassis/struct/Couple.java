/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Couple.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  A COUPLE specifies a position in a two-dimensional cartesian space.
//  If normalized, it only specifies a direction.
//  A Couple is defined as a pair of rationals (See `Rational.java').

//  A Couple object is never modified after creation, thus,
//  it can be used multiply.

package cassis.struct;

import cassis.Thing;
import cassis.parse.*;

public class Couple implements Thing {

    // representation

    private Rational u, v;

    // constructors

    public Couple(Coord u, Coord v) {
	this(new Rational(u), new Rational(v));
    }

    public Couple(Rational u, Rational v) {
	super();
	this.u = u;
	this.v = v;
    }

    // coordinate access methods

    public Rational u() { return this.u; }
    public Rational v() { return this.v; }

    // methods

    // return the SIGN of this couple
    public int sign() {
	int c = this.u.sign();
	if (c != EQUAL) return c;
	return this.v.sign();
    }

    // check whether this couple IS the ZERO couple
    public boolean isZero() {
	return ((this.u.sign() == EQUAL) && (this.v.sign() == EQUAL));
    }

    // check whether this couple IS a POSITIVE couple
    public boolean isPositive() {
	return (this.sign() == GREATER);
    }

    // check whether this couple IS a NEGATIVE couple
    public boolean isNegative() {
	return (this.sign() == LESS);
    }

    // check whether this couple IS an INFINITE couple
    public boolean isInfinite() {
	return (this.u.isInfinite() || this.v.isInfinite());
    }

    // check whether these couples are PARALLEL
    public boolean parallel(Couple other) {
	return (this.isZero() || other.isZero() || 
		this.product(other).isZero());
    }

    // check whether these couples are PERPENDICULAR
    public boolean perpendicular(Couple other) {
	return (this.isZero() || other.isZero() || 
		this.dotProduct(other).isZero());
    }

    // check whether a couple EQUALS another couple
    public boolean equals(Object other) {
        return ((other instanceof Couple) &&
		this.u.equals(((Couple) other).u) &&
		this.v.equals(((Couple) other).v));
        }

    // COMPARE two couples and return whether these are EQUAL, LESS or GREATER
    public int compare(Thing other) {
	if (!(other instanceof Couple)) return FAILED;
	int c = this.u.compare(((Couple) other).u);
	if (c != EQUAL) return c;
	return this.v.compare(((Couple) other).v);
    }

    /**
     * Tests whether this thing is strictly less than a specified other thing.
     * Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean lessThan(Thing other) {
	return (this.compare(other) == LESS);
    }

    /**
     * Tests whether this thing is strictly greater than a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean greaterThan(Thing other) {
	return (this.compare(other) == GREATER);
    }

    /**
     * Tests whether this thing is less than or equal to a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean lessOrEqual(Thing other) {
	int c = this.compare(other);
	return ((c == LESS) || (c == EQUAL));
    }

    /**
     * Tests whether this thing is greater than or equal to a specified other
     * thing. Uses the <tt>compare</tt> method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see #compare
     */
    public final boolean greaterOrEqual(Thing other) {
	int c = this.compare(other);
	return ((c == GREATER) || (c == EQUAL));
    }

    // return the NEGATEd equivalent of this couple
    public Couple negate() {
	if (this.isZero()) return this;
	return new Couple(this.u.negate(), this.v.negate());
    }

    // return the ABSolute equivalent of this couple
    public Couple abs() {
	return (this.isNegative()) ? this.negate() : this;
    }
/*
    // NORMALIZE this couple to a direction couple
    public Couple normalize() {
	if (w.n().equals(w.d()))
	    return this;
	return new Couple(this.x, this.y, this.z);
    }
*/
    // SCALE this couple by a coord factor
    public Couple scale(Coord factor) {
	if (factor.equals(Coord.ONE) || this.isZero())
	    return this;
	return new Couple(this.u.scale(factor), this.v.scale(factor));
    }

    // SCALE this couple by a rational factor
    public Couple scale(Rational factor) {
	if (factor.isOne() || this.isZero())
	    return this;
	return new Couple(this.u.multiply(factor), this.v.multiply(factor));
    }

    // ADD two couples
    public Couple add(Couple other) throws ArithmeticException {
	if (this.isInfinite())
	    if (other.isInfinite() && !this.equals(other))
		throw new ArithmeticException("Adding two infinite couples is undefined");
	    else return this;
	if (other.isZero()) return this;
	if (this.isZero() || other.isInfinite()) return other;

	return new Couple(this.u.add(other.u), this.v.add(other.v));
    }

    // SUBTRACT two couples
    public Couple subtract(Couple other) throws ArithmeticException {
	if (this.isInfinite())
	    if (other.isInfinite() && !this.equals(other.negate()))
		throw new ArithmeticException("Subtracting two infinite couples is undefined");
	    else return this;
	if (other.isZero()) return this;
	if (this.isZero() || other.isInfinite()) return other.negate();

	return new Couple(this.u.subtract(other.u), this.v.subtract(other.v));
    }

    // return the length of the cross-PRODUCT of these couples
    public Rational product(Couple other) throws ArithmeticException {
	return this.u.multiply(other.v).subtract(this.v.multiply(other.u));
    }

    // return the DOT-PRODUCT of these couples
    public Rational dotProduct(Couple other) throws ArithmeticException {
	return this.u.multiply(other.u).add(this.v.multiply(other.v));
    }

    // return the projection scalar of a couple with respect to this couple
    public Rational scalar(Couple other) {
	return other.dotProduct(this).divide(this.dotProduct(this));
    }

    // convert this couple inTO a STRING
    public String toString() {
	return  "(" + this.u.toString() + "," + this.v.toString() + ")";
    }

    // PARSE a string and construct a couple initialized to its value
    public static Couple parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != '(')
	    throw new ParseException(reader, "'(' expected");
	Rational u = Rational.parse(reader);
	if (reader.newToken() != ',')
	    throw new ParseException(reader, "',' expected");
	Rational v = Rational.parse(reader);
	return new Couple(u, v);
    }

    public Couple minimum(Couple other) {
	return (this.lessOrEqual(other)) ? this : other;
    }

    public Couple maximum(Couple other) {
	return (this.greaterOrEqual(other)) ? this : other;
    }
}
