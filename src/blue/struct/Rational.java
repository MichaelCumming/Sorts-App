/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Rational.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  A RATIONAL object is never modified after creation, thus,
//  it can be used multiply.

package blue.struct;

import blue.Thing;
import blue.io.*;

public final class Rational extends NumberThing {
    // constants
    static final Rational INFINITY = new Rational(Coord.ONE, new Coord(0));
    static final Rational MINFINITY = new Rational(new Coord(-1), new Coord(0));
    // representation
    private Coord n, d;

    // constructors
    public Rational(Coord n) { this(n, Coord.ONE); }

    public Rational(Coord n, Coord d) throws ArithmeticException {
        super();
        if (d.sign() == LESS) {
            this.n = n.negate();
            this.d = d.negate();
        } else {
            this.n = n;
            this.d = d;
        }
        this.reduce();
    }

    // access methods
    Coord n() { return this.n; }

    Coord d() { return this.d; }

    // number interface methods
    public int intValue() { return (int)this.longValue(); }

    public long longValue() {
        return this.n.longValue() / this.d.longValue();
    }

    public float floatValue() { return (float)this.doubleValue(); }

    public double doubleValue() {
        return this.n.doubleValue() / this.d.doubleValue();
    }

    // methods
    // REDUCE this rational to its canonical form
    private void reduce() throws ArithmeticException {
        Coord common;
        if (this.n.isZero() && this.d.isZero())
            throw new ArithmeticException("Rational 0/0 undefined");
        common = this.n.gcd(this.d);
        if (common.compare(Coord.ONE) == GREATER) {
            this.n = this.n.divide(common);
            this.d = this.d.divide(common);
        }
    }

    // return the SIGN of this rational
    public int sign() {
        return this.n.sign();
    }

    // check whether this rational IS ZERO
    public boolean isZero() {
        return this.n.isZero();
    }

    // check whether this rational IS ONE
    public boolean isOne() {
        return this.n.equals(this.d);
    }

    // check whether this rational IS POSITIVE
    public boolean isPositive() {
        return this.n.isPositive();
    }

    // check whether this rational IS NEGATIVE
    public boolean isNegative() {
        return this.n.isNegative();
    }

    // check whether this rational IS INFINITE
    public boolean isInfinite() {
        return this.d.isZero();
    }

    // check whether this rational EQUALS another rational
    public boolean equals(Object other) {
        return ((other instanceof Rational) && this.n.equals(((Rational)other).n) && this.d.equals(((Rational)other).d));
    }

    // COMPARE two rationals and
    // return whether these are EQUAL, LESS or GREATER
    public int compare(Thing other) {
        if (!(other instanceof Rational)) return FAILED;
        return this.compare((Rational)other);
    }

    private int compare(Rational other) {
        Coord d;
        if (this.n.sign() < other.n.sign()) return LESS;
        if (this.n.sign() > other.n.sign()) return GREATER;
        if (this.d.isZero())
            return (other.d.isZero()) ? this.n.compare(other.n) : this.n.sign();
        if (other.d.isZero()) return other.n.sign();
        d = this.d.lcm(other.d);
        return this.n.multiply(d.divide(this.d)).compare(other.n.multiply(d.divide(other.d)));
    }

    public Rational minimum(Rational other) {
        return (this.lessOrEqual(other)) ? this : other;
    }

    public Rational maximum(Rational other) {
        return (this.greaterOrEqual(other)) ? this : other;
    }

    // return the NEGATEd equivalent of this rational
    public Rational negate() {
        if (this.n.sign() == EQUAL) return this;
        return new Rational(this.n.negate(), this.d);
    }

    // return the ABSolute equivalent of this rational
    public Rational abs() {
        return (this.n.sign() == LESS) ? this.negate() : this;
    }

    // SCALE this rational by a coord factor
    public Rational scale(Coord factor) throws ArithmeticException {
        Coord d = this.d, common;
        if (factor.isOne() || this.n.isZero()) return this;
        if (this.d.isZero())
            if (factor.isZero())
                throw new ArithmeticException("1/0 * 0 is undefined");
            else
                return this;
        common = factor.gcd(d);
        if (!common.isOne()) {
            factor = factor.divide(common);
            d = d.divide(common);
        }
        return new Rational(this.n.multiply(factor), d);
    }

    // ADD two rationals
    public Rational add(Rational other) throws ArithmeticException {
        Coord n, d;
        if (this.d.isZero())
            if (other.d.isZero() && (this.n.sign() != other.n.sign()))
                throw new ArithmeticException("1/0 + (-1/0) is undefined");
            else
                return this;
        if (other.n.isZero()) return this;
        if (this.n.isZero() || other.d.isZero()) return other;
        d = this.d.lcm(other.d);
        n = this.n.multiply(d.divide(this.d)).add(other.n.multiply(d.divide(other.d)));
        return new Rational(n, d);
    }

    // SUBTRACT two rationals
    public Rational subtract(Rational other) throws ArithmeticException {
        Coord n, d;
        if (this.d.isZero())
            if (other.d.isZero() && (this.n.sign() == other.n.sign()))
                throw new ArithmeticException("1/0 - 1/0 is undefined");
            else
                return this;
        if (other.n.isZero()) return this;
        if (this.n.isZero() || other.d.isZero()) return other.negate();
        d = this.d.lcm(other.d);
        n = this.n.multiply(d.divide(this.d)).subtract(other.n.multiply(d.divide(other.d)));
        return new Rational(n, d);
    }

    // MULTIPLY two rationals
    public Rational multiply(Rational other) throws ArithmeticException {
        Coord fac1, fac2;
        if (this.n.isZero())
            if (other.d.isZero())
                throw new ArithmeticException("0 * 1/0 is undefined");
            else
                return this;
        if (other.n.isZero())
            if (this.d.isZero())
                throw new ArithmeticException("1/0 * 0 is undefined");
            else
                return other;
        if (other.isOne()) return this;
        if (this.isOne()) return other;
        if (this.d.isZero())
            return (other.n.isPositive()) ? this : this.negate();
        if (other.d.isZero())
            return (this.n.isPositive()) ? other : other.negate();
        fac1 = this.n.gcd(other.d);
        fac2 = other.n.gcd(this.d);
        return new Rational(n.divide(fac1).multiply(other.n.divide(fac2)), d.divide(fac2).multiply(other.d.divide(fac1)));
    }

    // DIVIDE two rationals
    public Rational divide(Rational other) throws ArithmeticException {
        Coord fac1;
        Coord fac2;
        if (this.n.isZero())
            if (other.n.isZero())
                throw new ArithmeticException("0 / 0 is undefined");
            else
                return this;
        if (this.d.isZero())
            if (other.d.isZero())
                throw new ArithmeticException("1/0 / 1/0 is undefined");
            else
                return (other.n.isPositive()) ? this : this.negate();
        if (other.isOne()) return this;
        if (this.isOne()) return new Rational(other.d, other.n);
        if (other.d.isZero()) return new Rational(new Coord(0));
        if (other.n.isZero())
            return (this.n.isPositive()) ? INFINITY : MINFINITY;
        fac1 = this.n.gcd(other.n);
        fac2 = this.d.gcd(other.d);
        return new Rational(n.divide(fac1).multiply(other.d.divide(fac2)), d.divide(fac2).multiply(other.n.divide(fac1)));
    }

    // convert this rational inTO a STRING
    public String toString() {
        if (this.d.isOne()) return this.n.toString();
        return this.n.toString() + "/" + this.d.toString();
    }

    // PARSE a string and construct a rational initialized to its value
    public static Rational parse(ParseReader reader) throws ParseException {
        try {
            Coord n = Coord.parse(reader);
            if (reader.previewToken() != '/')
                return new Rational(n);
            reader.newToken();
            return new Rational(n, Coord.parse(reader));
        } catch (ArithmeticException e) {
            throw new ParseException(reader, e.getMessage());
        }
    }
}
