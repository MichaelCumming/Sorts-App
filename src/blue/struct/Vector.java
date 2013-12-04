/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Vector.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  A VECTOR specifies a position in a three-dimensional cartesian space.
//  If normalized, it only specifies a direction.
//  A Vector is defined as a triple of Coord's, reduced to canonical form,
//  and a Rational multiplication factor to reflect the Vector's length.
//  (See `Coord.java' and `Rational.java')
//  A Vector object is never modified after creation, thus,
//  it can be used multiply.

package blue.struct;

import blue.Thing;
import blue.io.*;

public class Vector implements Thing {
    // constants
    public final static Vector X = new Vector(Coord.ONE, Coord.ZERO, Coord.ZERO);
    public final static Vector Y = new Vector(Coord.ZERO, Coord.ONE, Coord.ZERO);
    public final static Vector Z = new Vector(Coord.ZERO, Coord.ZERO, Coord.ONE);
    // representation
    private Coord x, y, z;
    private Rational w;

    // constructors
    public Vector(Coord x, Coord y, Coord z) throws ArithmeticException {
        this(x, y, z, Coord.ONE);
    }

    public Vector(Coord x, Coord y, Coord z, Coord w) throws ArithmeticException {
        this(x, y, z, new Rational(Coord.ONE, w));
    }

    Vector(Coord x, Coord y, Coord z, Rational w) throws ArithmeticException {
        super();
        if (w.isZero()) x = y = z = Coord.ZERO;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.reduce();
    }

    Vector(Rational x, Rational y, Rational z, Rational w) throws ArithmeticException {
        super();
        this.w = new Rational(Coord.ONE, x.d().lcm(y.d()).lcm(z.d()));
        this.w = this.w.multiply(w);
        this.x = x.n().multiply(this.w.d().divide(x.d()));
        this.y = y.n().multiply(this.w.d().divide(y.d()));
        this.x = x.n().multiply(this.w.d().divide(z.d()));
        if (w.isZero()) this.x = this.y = this.z = Coord.ZERO;
        this.reduce();
    }

    // coordinate access methods
    public Coord x() { return this.x; }

    public Coord y() { return this.y; }

    public Coord z() { return this.z; }

    public Rational w() { return this.w; }

    // methods
    // REDUCE this vector to its canonical form
    private void reduce() throws ArithmeticException {
        int sgn;
        Coord common;
        if (this.isZero())
            if (this.w.isInfinite())
                throw new ArithmeticException("1/0 (0,0,0) is undefined");
            else { this.w = new Rational(Coord.ONE); return; }
        sgn = this.x.sign();
        if (sgn == EQUAL) sgn = this.y.sign();
        if (sgn == EQUAL) sgn = this.z.sign();
        if (sgn == LESS) {
            this.x = this.x.negate();
            this.y = this.y.negate();
            this.z = this.z.negate();
            this.w = this.w.negate();
        }
        common = this.x.gcd(this.y).gcd(this.z);
        if (!common.isOne()) {
            this.x = this.x.divide(common);
            this.y = this.y.divide(common);
            this.z = this.z.divide(common);
            this.w = this.w.scale(common);
        }
    }

    // return the SIGN of this vector
    public int sign() {
        return (this.isZero()) ? EQUAL : this.w.sign();
    }

    // check whether this vector IS the ZERO vector
    public boolean isZero() {
        return ((this.x.sign() == EQUAL) && (this.y.sign() == EQUAL) && (this.z.sign() == EQUAL));
    }

    // check whether this vector IS a POSITIVE vector
    public boolean isPositive() {
        return (this.isZero()) ? false : this.w.isPositive();
    }

    // check whether this vector IS a NEGATIVE vector
    public boolean isNegative() {
        return (this.isZero()) ? false : this.w.isNegative();
    }

    // check whether this vector IS an INFINITE vector
    public boolean isInfinite() {
        return this.w.isInfinite();
    }

    // check whether these vectors are PARALLEL
    public boolean parallel(Vector other) {
        return (this.isZero() || other.isZero() ||
            (this.x.equals(other.x) && this.y.equals(other.y) && this.z.equals(other.z)));
    }

    // check whether these vectors are PERPENDICULAR
    public boolean perpendicular(Vector other) {
        return (this.isZero() || other.isZero() || this.dotProduct(other).isZero());
    }

    // check whether a vector EQUALS another vector
    public boolean equals(Object other) {
        return ((other instanceof Vector) && this.x.equals(((Vector)other).x) && this.y.equals(((Vector)other).y) &&
            this.z.equals(((Vector)other).z) && this.w.equals(((Vector)other).w));
    }

    // COMPARE two vectors and return whether these are EQUAL, LESS or GREATER
    public int compare(Thing other) {
        int c;
        if (!(other instanceof Vector)) return FAILED;
        c = this.x.compare(((Vector)other).x);
        if (c != EQUAL) return c;
        c = this.y.compare(((Vector)other).y);
        if (c != EQUAL) return c;
        c = this.z.compare(((Vector)other).z);
        if (c != EQUAL) return c;
        return this.w.compare(((Vector)other).w);
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

    // return the NEGATEd equivalent of this vector
    public Vector negate() {
        if (this.isZero()) return this;
        return new Vector(this.x, this.y, this.z, this.w.negate());
    }

    // return the ABSolute equivalent of this vector
    public Vector abs() {
        return (this.w.isNegative()) ? this.negate() : this;
    }

    // NORMALIZE this vector to a direction vector
    public Vector normalize() {
        if (w.n().equals(w.d()))
            return this;
        return new Vector(this.x, this.y, this.z);
    }

    // SCALE this vector by a coord factor
    public Vector scale(Coord factor) {
        if (factor.equals(Coord.ONE) || this.isZero())
            return this;
        return new Vector(this.x, this.y, this.z, this.w.scale(factor));
    }

    // SCALE this vector by a rational factor
    public Vector scale(Rational factor) {
        if (factor.isOne() || this.isZero())
            return this;
        return new Vector(this.x, this.y, this.z, this.w.multiply(factor));
    }

    // SCALE this vector by the projection length of a vector
    public Vector scale(Vector other) {
        return this.scale(other.dotProduct(this).divide(this.dotProduct(this)));
    }

    // ADD two vectors
    public Vector add(Vector other) throws ArithmeticException {
        Coord common, fac1, fac2;
        if (this.w.isInfinite())
            if (other.w.isInfinite() && !this.equals(other))
                throw new ArithmeticException("Adding two infinite vectors is undefined");
            else
                return this;
        if (other.isZero()) return this;
        if (this.isZero() || other.w.isInfinite()) return other;
        common = this.w.d().lcm(other.w.d());
        fac1 = common.divide(this.w.d()).multiply(this.w.n());
        fac2 = common.divide(other.w.d()).multiply(other.w.n());
        return new Vector(x.multiply(fac1).add(other.x.multiply(fac2)), y.multiply(fac1).add(other.y.multiply(fac2)),
            z.multiply(fac1).add(other.z.multiply(fac2)), common);
    }

    // SUBTRACT two vectors
    public Vector subtract(Vector other) throws ArithmeticException {
        Coord common, fac1, fac2;
        if (this.w.isInfinite())
            if (other.w.isInfinite() && !this.equals(other.negate()))
                throw new ArithmeticException("Subtracting two infinite vectors is undefined");
            else
                return this;
        if (other.isZero()) return this;
        if (this.isZero() || other.w.isInfinite()) return other.negate();
        common = this.w.d().lcm(other.w.d());
        fac1 = common.divide(this.w.d()).multiply(this.w.n());
        fac2 = common.divide(other.w.d()).multiply(other.w.n());
        return new Vector(x.multiply(fac1).subtract(other.x.multiply(fac2)), y.multiply(fac1).subtract(other.y.multiply(fac2)),
            z.multiply(fac1).subtract(other.z.multiply(fac2)), common);
    }

    // return the cross-PRODUCT of these vectors
    public Vector product(Vector other) throws ArithmeticException {
        if (this.isZero()) return this;
        if (other.isZero()) return other;
        return new Vector(y.multiply(other.z).subtract(z.multiply(other.y)), z.multiply(other.x).subtract(x.multiply(other.z)),
            x.multiply(other.y).subtract(y.multiply(other.x)), w.multiply(other.w));
    }

    // return the DOT-PRODUCT of these vectors
    public Rational dotProduct(Vector other) throws ArithmeticException {
        return this.w.multiply(other.w).scale(x.multiply(other.x).add(y.multiply(other.y)).add(z.multiply(other.z)));
    }

    // return the projection scalar of a vector with respect to this vector
    public Rational scalar(Vector other) {
        return other.dotProduct(this).divide(this.dotProduct(this));
    }

    public boolean colinear(Vector pos2, Vector pos3) {
        return this.perpendicular(pos2.product(pos3));
    }

    // convert this vector inTO a STRING
    public String toString() {
        if (this.w.isOne())
            return "(" + this.x.toString() + "," + this.y.toString() + "," + this.z.toString() + ")";
        return this.w.toString() + "(" + this.x.toString() + "," + this.y.toString() + "," + this.z.toString() + ")";
    }

    // PARSE a string and construct a vector initialized to its value
    public static Vector parse(ParseReader reader) throws ParseException {
        try {
            Rational w;
            if (reader.previewToken() == '(')
                w = new Rational(Coord.ONE);
            else
                w = Rational.parse(reader);
            if (reader.newToken() != '(')
                throw new ParseException(reader, "'(' expected");
            Coord x = Coord.parse(reader);
            if (reader.newToken() != ',')
                throw new ParseException(reader, "',' expected");
            Coord y = Coord.parse(reader);
            if (reader.newToken() != ',')
                throw new ParseException(reader, "',' expected");
            Coord z = Coord.parse(reader);
            if (reader.newToken() != ')')
                throw new ParseException(reader, "')' expected");
            return new Vector(x, y, z, w);
        } catch (ArithmeticException e) {
            throw new ParseException(reader, e.getMessage());
        }
    }
}
