/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Vector.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.struct;

import javax.vecmath.Vector3d;

import cassis.Thing;
import cassis.parse.*;

/**
 * A <b>vector</b> specifies a position in a three-dimensional cartesian space.
 * If normalized, it only specifies a direction.
 * <p>
 * The <b>Vector</b> class implements the {@link Thing} interface.
 * A <i>vector</i> is defined as a triple of {@link Coord}'s, reduced to their
 * canonical form, a {@link Rational} multiplication factor to reflect the
 * vector's length, and an integer tag that can be used for temporary purposes.
 * The vector's length may be infinite, unless the coordinates are all zero.
 * The canonical form of a vector is achieved by determining the greatest common
 * divider of all three coordinates, dividing each coordinate by this value and
 * scaling the multiplication factor accordingly. The first non-zero coordinate
 * (in the order x, y, z) is always reduced to a positive value. If all three
 * coordinates are zero, then the multiplication factor is set to 1.
 * <p>
 * A <b>Vector</b> object is never modified after creation, thus,
 * it can be used multiple times.
 */
public class Vector implements Thing {
    
    // constants
    
    /**
     * The unit vector (1,0,0) on the <b>X</b> axis of the cartesian space.
     */
    public final static Vector X = new Vector(Coord.ONE, Coord.ZERO, Coord.ZERO);
    /**
     * The unit vector (0,1,0) on the <b>Y</b> axis of the cartesian space.
     */
    public final static Vector Y = new Vector(Coord.ZERO, Coord.ONE, Coord.ZERO);
    /**
     * The unit vector (0,0,1) on the <b>Z</b> axis of the cartesian space.
     */
    public final static Vector Z = new Vector(Coord.ZERO, Coord.ZERO, Coord.ONE);
    
    // representation
    
    private Coord x, y, z;
    private Rational w;
    
    // constructors
    
    /**
     * Constructs a <b>vector</b> with the specified integral coordinates x, y and z
     * and with multiplication factor 1.
     * @param x a {@link Coord} object
     * @param y a {@link Coord} object
     * @param z a {@link Coord} object
     */
    public Vector(Coord x, Coord y, Coord z) throws ArithmeticException {
        this(x, y, z, Coord.ONE);
    }
    /**
     * Constructs a <b>vector</b> with the specified homogeneous coordinates x, y, z
     * and w.
     * @param x a {@link Coord} object
     * @param y a {@link Coord} object
     * @param z a {@link Coord} object
     * @param w a {@link Coord} object
     * @throws ArithmeticException if all four coordinates are equal to zero
     */
    public Vector(Coord x, Coord y, Coord z, Coord w) throws ArithmeticException {
        this(x, y, z, new Rational(Coord.ONE, w));
    }
    /**
     * Constructs a <b>vector</b> with the specified integral coordinates x, y and z
     * and rational multiplication factor w.
     * @param x a {@link Coord} object
     * @param y a {@link Coord} object
     * @param z a {@link Coord} object
     * @param w a {@link Rational} object
     * @throws ArithmeticException if all three coordinates are equal to zero and
     * the multiplication factor equals infinity
     */
    Vector(Coord x, Coord y, Coord z, Rational w) throws ArithmeticException {
        super();
        if (w.isZero()) x = y = z = Coord.ZERO;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.reduce();
    }
    /**
     * Constructs a <b>vector</b> with the specified rational coordinates x, y and z
     * and rational multiplication factor w.
     * @param x a {@link Rational} object
     * @param y a {@link Rational} object
     * @param z a {@link Rational} object
     * @param w a {@link Rational} object
     * @throws ArithmeticException if all three coordinates are equal to zero and
     * the multiplication factor equals infinity, or all three coordinates are equal
     * to infinity
     */
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
    
    /**
     * Returns the <b>x</b> coordinate of this vector.
     * @return a {@link Coord} object
     */
    public Coord x() { return this.x; }
    /**
     * Returns the <b>y</b> coordinate of this vector.
     * @return a {@link Coord} object
     */
    public Coord y() { return this.y; }
    /**
     * Returns the <b>z</b> coordinate of this vector.
     * @return a {@link Coord} object
     */
    public Coord z() { return this.z; }
    /**
     * Returns the multiplication factor <b>w</b> of this vector.
     * @return a {@link Rational} object
     */
    public Rational w() { return this.w; }
    
    /**
     * Returns a rational specifying the <b>X</b> coordinate of this vector.
     * @return a {@link Rational} object
     */
    public Rational getX() { return this.w.scale(this.x); }
    /**
     * Returns a rational specifying the <b>Y</b> coordinate of this vector.
     * @return a {@link Rational} object
     */
    public Rational getY() { return this.w.scale(this.y); }
    /**
     * Returns a rational specifying the <b>Z</b> coordinate of this vector.
     * @return a {@link Rational} object
     */
    public Rational getZ() { return this.w.scale(this.z); }
    /**
     * Returns a <tt>Vector3d</tt> representation of this vector.
     * @return a <tt>Vector3d</tt> object
     */
    public Vector3d vector3dValue() {
        return new Vector3d(this.w.scale(this.x).doubleValue(),
                this.w.scale(this.y).doubleValue(),
                this.w.scale(this.z).doubleValue());
    }
    
    // methods
    
    /**
     * <b>Reduces</b> this vector to its canonical form. This canonical form
     * specifies that the greatest common divider of all three coordinates equals 1,
     * unless all coordinates are zero, and the first non-zero coordinate
     * (in the order x, y, z) is positive. If all coordinates are zero, then
     * the multiplication factor equals 1.
     * @throws ArithmeticException if all three coordinates equal zero and the
     * multiplication factor equals infinity
     */
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
    
    /**
     * Returns the <b>sign</b> of this vector. This is the sign of the rational
     * multiplication factor. If the vector equals zero, the sign is also zero.
     * @return an integer value
     * @see Rational#sign()
     */
    public int sign() {
        return (this.isZero()) ? EQUAL : this.w.sign();
    }
    
    /**
     * Checks whether this vector <b>is the zero</b> vector, that is, all coordinates
     * are zero.
     * @return a boolean value
     */
    public boolean isZero() {
        return ((this.x.sign() == EQUAL) && (this.y.sign() == EQUAL) &&
                (this.z.sign() == EQUAL));
    }
    /**
     * Checks whether this vector <b>is a positive</b> vector, that is, the vector is
     * not equal to zero and the multiplication factor is positive.
     * @return a boolean value
     * @see Rational#isPositive()
     */
    public boolean isPositive() {
        return (this.isZero()) ? false : this.w.isPositive();
    }
    /**
     * Checks whether this vector <b>is a negative</b> vector, that is, the vector is
     * not equal to zero and the multiplication factor is negative.
     * @return a boolean value
     * @see Rational#isNegative()
     */
    public boolean isNegative() {
        return (this.isZero()) ? false : this.w.isNegative();
    }
    /**
     * Checks whether this vector <b>is an infinite</b> vector, that is,
     * the multiplication factor is infinite.
     * @return a boolean value
     * @see Rational#isInfinite()
     */
    public boolean isInfinite() {
        return this.w.isInfinite();
    }
    
    /**
     * Checks whether this vector is <b>parallel</b> to another vector.
     * Two vectors are parallel if either vector equals zero or if their respective
     * coordinates are equal.
     * @param other another vector
     * @return a boolean value
     */
    public boolean parallel(Vector other) {
        return (this.isZero() || other.isZero() ||
                (this.x.equals(other.x) && this.y.equals(other.y) &&
                this.z.equals(other.z)));
    }
    /**
     * Checks whether this vector is <b>perpendicular</b> to another vector.
     * Two vectors are perpendicular if either vector equals zero or if their
     * dot-product equals zero.
     * @param other another vector
     * @return a boolean value
     * @see #dotProduct(Vector)
     */
    public boolean perpendicular(Vector other) {
        return (this.isZero() || other.isZero() ||
                this.dotProduct(other).isZero());
    }
    
    /**
     * Checks whether this vector <b>equals</b> another object.
     * Two vectors are equal if their respective coordinates and multiplication
     * factor are equal.
     * @param other the comparison object
     * @return a boolean value
     */
    public boolean equals(Object other) {
        return ((other instanceof Vector) &&
                this.x.equals(((Vector) other).x) &&
                this.y.equals(((Vector) other).y) &&
                this.z.equals(((Vector) other).z) &&
                this.w.equals(((Vector) other).w));
    }
    
    /**
     * <b>Compares</b> this vector to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a vector.
     * Otherwise the result is defined by comparing the respective coordinates and
     * multiplication factor.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
        int c;
        
        if (!(other instanceof Vector)) return FAILED;
        c = this.x.compare(((Vector) other).x);
        if (c != EQUAL) return c;
        c = this.y.compare(((Vector) other).y);
        if (c != EQUAL) return c;
        c = this.z.compare(((Vector) other).z);
        if (c != EQUAL) return c;
        return this.w.compare(((Vector) other).w);
    }
    
    /**
     * Tests whether this vector is strictly <b>less than</b> another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessThan(Thing other) {
        return (this.compare(other) == LESS);
    }
    /**
     * Tests whether this vector is strictly <b>greater than</b> another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterThan(Thing other) {
        return (this.compare(other) == GREATER);
    }
    /**
     * Tests whether this vector is strictly <b>less than or equal</b> to another
     * thing. Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == LESS) || (c == EQUAL));
    }
    /**
     * Tests whether this vector is strictly <b>greater than or equal</b> to another
     * thing. Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == GREATER) || (c == EQUAL));
    }
    
    /**
     * Returns the <b>negated</b> equivalent of this vector. This is a vector with
     * the same coordinates and sign-wise opposite multiplication factor. If this
     * vector is zero, the same vector is returned.
     * @return a <tt>Vector</tt>
     */
    public Vector negate() {
        if (this.isZero()) return this;
        return new Vector(this.x, this.y, this.z, this.w.negate());
    }
    /**
     * Returns the <b>absolute</b> equivalent of this vector. If this vector is
     * negative, the negated equivalent is returned, otherwise the same vector is
     * returned.
     * @return a <tt>Vector</tt>
     * @see #isNegative()
     * @see #negate()
     */
    public Vector abs() {
        return (this.w.isNegative()) ? this.negate() : this;
    }
    
    /**
     * <b>Normalizes</b> this vector to a direction vector. This is a vector with
     * the same coordinates and with multiplication factor equal to 1. If this vector
     * is already normalized, then this vector is returned.
     * @return a <tt>Vector</tt>
     */
    public Vector normalize() {
        if (w.n().equals(w.d()))
            return this;
        return new Vector(this.x, this.y, this.z);
    }
    
    /**
     * <b>Scales</b> this vector by an integral factor. The result is a vector with
     * the same coordinates and scaled multiplication factor. If the factor equals one
     * or this vector equals zero, then this vector is returned.
     * @param factor a {@link Coord} object
     * @return a <tt>Vector</tt>
     * @see Rational#scale(Coord)
     */
    public Vector scale(Coord factor) {
        if (factor.equals(Coord.ONE) || this.isZero())
            return this;
        return new Vector(this.x, this.y, this.z, this.w.scale(factor));
    }
    /**
     * <b>Scales</b> this vector by a rational factor. The result is a vector with
     * the same coordinates and scaled multiplication factor. If the factor equals one
     * or this vector equals zero, then this vector is returned.
     * @param factor a {@link Rational} object
     * @return a <tt>Vector</tt>
     * @see Rational#multiply(Rational)
     */
    public Vector scale(Rational factor) {
        if (factor.isOne() || this.isZero())
            return this;
        return new Vector(this.x, this.y, this.z, this.w.multiply(factor));
    }
    /**
     * <b>Scales</b> this vector by the projection length of another vector.
     * @param other another vector
     * @return a <tt>Vector</tt>
     * @see #scalar(Vector)
     */
    public Vector scale(Vector other) {
        return this.scale(this.scalar(other));
    }
    
    /**
     * <b>Adds</b> another vector to this vector. Returns a new vector that is
     * the sum of both vectors. If either vector is zero, the other vector is
     * returned; if one vector is infinite, this infinite vector is returned.
     * @param other another vector
     * @return a <tt>Vector</tt>
     * @throws ArithmeticException if both vectors are infinite
     */
    public Vector add(Vector other) throws ArithmeticException {
        Coord common, fac1, fac2;
        
        if (this.w.isInfinite())
            if (other.w.isInfinite() && !this.equals(other))
                throw new ArithmeticException("Adding two infinite vectors is undefined");
            else return this;
        if (other.isZero()) return this;
        if (this.isZero() || other.w.isInfinite()) return other;
        
        common = this.w.d().lcm(other.w.d());
        fac1 = common.divide(this.w.d()).multiply(this.w.n());
        fac2 = common.divide(other.w.d()).multiply(other.w.n());
        return new Vector(x.multiply(fac1).add(other.x.multiply(fac2)),
                y.multiply(fac1).add(other.y.multiply(fac2)),
                z.multiply(fac1).add(other.z.multiply(fac2)),
                common);
    }
    
    /**
     * <b>Subtracts</b> another vector from this vector. Returns a new vector that is
     * the difference of both vectors. If this vector is infinite or the other vector
     * equals zero, this vector is returned.
     * @param other another vector
     * @return a <tt>Vector</tt>
     * @throws ArithmeticException if both vectors are infinite
     */
    public Vector subtract(Vector other) throws ArithmeticException {
        Coord common, fac1, fac2;
        
        if (this.w.isInfinite())
            if (other.w.isInfinite() && !this.equals(other.negate()))
                throw new ArithmeticException("Subtracting two infinite vectors is undefined");
            else return this;
        if (other.isZero()) return this;
        if (this.isZero() || other.w.isInfinite()) return other.negate();
        
        common = this.w.d().lcm(other.w.d());
        fac1 = common.divide(this.w.d()).multiply(this.w.n());
        fac2 = common.divide(other.w.d()).multiply(other.w.n());
        return new Vector(x.multiply(fac1).subtract(other.x.multiply(fac2)),
                y.multiply(fac1).subtract(other.y.multiply(fac2)),
                z.multiply(fac1).subtract(other.z.multiply(fac2)),
                common);
    }
    
    /**
     * Determines the <b>cross-product</b> of this vector with another vector.
     * If either vector equals zero, this zero vector is returned.
     * @param other another vector
     * @return a <tt>Vector</tt>
     * @throws ArithmeticException if both vectors are infinite
     */
    public Vector product(Vector other) throws ArithmeticException {
        if (this.isZero()) return this;
        if (other.isZero()) return other;
        
        return new Vector(y.multiply(other.z).subtract(z.multiply(other.y)),
                z.multiply(other.x).subtract(x.multiply(other.z)),
                x.multiply(other.y).subtract(y.multiply(other.x)),
                w.multiply(other.w));
    }
    
    /**
     * Determines the <b>dot-product</b> of this vector with another vector.
     * @param other another vector
     * @return a {@link Rational} object
     * @throws ArithmeticException if a 0/0 division is encountered
     */
    public Rational dotProduct(Vector other) throws ArithmeticException {
        return this.w.multiply(other.w).scale(x.multiply(other.x).add(y.multiply(other.y)).add(z.multiply(other.z)));
    }
    
    /**
     * Determines the projection <b>scalar</b> of another vector wrt this vector.
     * This projection scalar is calculated by dividing the dot-product of this vector
     * with the other vector by the dot-product of this vector with itself.
     * @param other another vector
     * @return a {@link Rational} object
     */
    public Rational scalar(Vector other) {
        return other.dotProduct(this).divide(this.dotProduct(this));
    }
    
    /**
     * Checks if the position in cartesian space defined by this vector is
     * <b>colinear</b> with the positions defined by two other vectors.
     * This is the case if the vectors determined by subtracting this vector from
     * both other vectors are parallel.
     * @param pos2 a second vector
     * @param pos3 a third vector
     * @return a boolean value
     */
    public boolean colinear(Vector pos2, Vector pos3) {
        return pos2.subtract(this).parallel(pos3.subtract(this));
    }
    /**
     * Checks if this vector is <b>coplanar</b> to two other vectors, that is,
     * this vector is perpendicular to the cross-product of the other two vectors.
     * @param pos2 another vector
     * @param pos3 another vector
     * @return a boolean value
     * public boolean coplanar(Vector pos2, Vector pos3) {
     * return this.perpendicular(pos2.product(pos3));
     * }
     */
    
    /**
     * Converts this vector <b>to a string</b>. This string consists of the tuple
     * of x, y and z coordinates, separated with comma's and enclosed in parentheses.
     * if the multiplication factor is different from one, a string representation
     * of this rational preceeds the parentesized tuple.
     * @return a <tt>String</tt> object
     * @see Coord#toString()
     * @see Rational#toString()
     */
    public String toString() {
        if (this.w.isOne())
            return  "(" + this.x.toString() + "," + this.y.toString() + "," +
                    this.z.toString() + ")";
        return this.w.toString() + "(" + this.x.toString() + "," +
                this.y.toString() + "," + this.z.toString() + ")";
    }
    
    /**
     * Constructs a vector corresponding the specified expression.
     * The expression must consist of a triple of integer coordinates,
     * separated with comma's and enclosed in parentheses. This expression
     * may be preceeded by an integral or rational number.
     * @param reader a {@link cassis.parse.ParseReader} object that presents
     * the vector's expression
     * @return the resulting vector
     * @throws ParseException if the expression is invalid, or the vector's
     * coordinates are all zero and the multiplication factor is infinite.
     * @see Coord#parse(ParseReader)
     * @see Rational#parse(ParseReader)
     */
    public static Vector parse(ParseReader reader) throws ParseException {
        try {
            Rational w;
            if (reader.previewToken() == '(')
                w = new Rational(Coord.ONE);
            else w = Rational.parse(reader);
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
