/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Coord.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  The COORD/COORDINATE is the representational unit for geometric
//  representations in a three dimensional cartesian space.
//  The COORDINATE interface specifies the methods for the Coord class.
//  I envision two Coord classes, one using long integers, the other
//  using BigInt's (see "http://www.swiss.ai.mit.edu/~adams/BigInt.java"),
//  only one of which will be available at compile and run-time.

package blue.struct;

import blue.Thing;
import blue.io.*;

interface Coordinate {
    int sign();

    boolean isZero();

    boolean isOne();

    boolean isPositive();

    boolean isNegative();

    Coord negate();

    Coord abs();

    Coord add(Coord n);

    Coord subtract(Coord n);

    Coord divide(Coord n);

    Coord multiply(Coord n);

    Coord remainder(Coord n);

    Coord gcd(Coord n);

    Coord lcm(Coord n);
}


//  This COORD class implements the Coordinate interface using long integers.
//  A Coord object is never modified after creation, thus,
//  it can be used multiply.
public final class Coord extends NumberThing implements Coordinate {
    // constants
    public final static Coord ZERO = new Coord(0);
    public final static Coord ONE = new Coord(1);
    // representation
    private long coord;

    // constructors
    public Coord(long l) { super(); this.coord = l; }

    public Coord(String s) throws NumberFormatException {
        super();
        this.coord = Long.parseLong(s);
    }

    // number interface methods
    public int intValue() { return (int)this.coord; }

    public long longValue() { return this.coord; }

    public float floatValue() { return (float)this.coord; }

    public double doubleValue() { return (double)this.coord; }

    // Coordinate interface methods
    // return the SIGN of a coord
    public int sign() {
        return sign(this.coord);
    }

    private int sign(long l) {
        return (l < 0) ? LESS : ((l == 0) ? EQUAL : GREATER);
    }

    // check whether this coord IS ZERO
    public boolean isZero() {
        return (this.coord == 0);
    }

    // check whether this coord IS ONE
    public boolean isOne() {
        return (this.coord == 1);
    }

    // check whether this coord IS POSITIVE
    public boolean isPositive() {
        return (this.coord > 0);
    }

    // check whether this coord IS NEGATIVE
    public boolean isNegative() {
        return (this.coord < 0);
    }

    // check whether this coord EQUALS another coord
    public boolean equals(Object other) {
        return ((other instanceof Coord) && (this.coord == ((Coord)other).coord));
    }

    // COMPARE two coord's and return whether these are EQUAL, LESS or GREATER
    public int compare(Thing other) {
        if (!(other instanceof Coord)) return FAILED;
        return sign(this.coord - ((Coord)other).coord);
    }

    // return the NEGATEd equivalent of this coord
    public Coord negate() {
        if (this.coord == 0) return this;
        return new Coord(-this.coord);
    }

    // return the ABSolute equivalent of this coord
    public Coord abs() {
        return (this.coord >= 0) ? this : this.negate();
    }

    // ADD two coord's
    public Coord add(Coord other) {
        if (other.coord == 0) return this;
        if (this.coord == 0) return other;
        return new Coord(this.coord + other.coord);
    }

    // SUBTRACT two coord's
    public Coord subtract(Coord other) {
        if (other.coord == 0) return this;
        return new Coord(this.coord - other.coord);
    }

    // MULTIPLY two coord's
    public Coord multiply(Coord other) {
        if ((other.coord == 1) || (this.coord == 0)) return this;
        if ((this.coord == 1) || (other.coord == 0)) return other;
        return new Coord(this.coord * other.coord);
    }

    // DIVIDE two coord's
    public Coord divide(Coord other) {
        if ((other.coord == 1) || (this.coord == 0)) return this;
        if (other.coord == 0)
            throw new IllegalArgumentException("Coord division by 0");
        return new Coord(this.coord / other.coord);
    }

    // determines the REMAINDER of two coord's
    public Coord remainder(Coord other) {
        if (other.coord > this.coord) return this;
        return new Coord(this.coord % other.coord);
    }

    // determine the GCD (Greatest Common Divider) of two coord's
    public Coord gcd(Coord other) {
        if ((other.coord == this.coord) || (this.coord == 1) || (other.coord == 0)) return this;
        if ((other.coord == 1) || (this.coord == 0)) return other;
        return new Coord(gcd(this.coord, other.coord));
    }

    private long gcd(long x, long y) {
        long t;
        if (x < 0) x = -x;
        if (y < 0) y = -y;
        if (y > x) {
            t = y; y = x; x = t;
        }
        while (x != 0) {
            t = x; x = y % t; y = t;
        }
        return y;
    }

    // determine the LCM (Least Common Multiple) of two coord's
    public Coord lcm(Coord other) {
        if ((other.coord == this.coord) || (other.coord == 1) || (this.coord == 0)) return this;
        if ((this.coord == 1) || (other.coord == 0)) return other;
        return new Coord(lcm(this.coord, other.coord));
    }

    private long lcm(long x, long y) {
        return (x / gcd(x, y) * y);
    }

    // convert this coord inTO a STRING
    public String toString() {
        return String.valueOf(this.coord);
    }

    // PARSE a string and construct a coord initialized to its value
    public static Coord parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != Parser.NUMBER)
            throw new ParseException(reader, "integer expected");
        try {
            return new Coord(reader.tokenString());
        } catch (NumberFormatException e) {
            throw new ParseException(reader, "integer expected");
        }
    }

    // main (only for testing purposes)
    public static void main(String args[]) {
        Coord x, y, z;
        x = new Coord(32);
        y = new Coord(72);
        z = x.gcd(y);
        System.out.println("gcd equals " + z.coord);
        z = x.lcm(y);
        System.out.println("lcm equals " + z.coord);
        z = x.add(y);
        System.out.println("add equals " + z.coord);
    }
}
