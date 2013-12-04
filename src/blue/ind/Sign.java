/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Sign.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import blue.Thing;
import blue.Element;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.PrimitiveSort;
import blue.struct.Parameter;
import blue.form.OrdinalForm;

/**
 * The <tt>Sign</tt> class defines the characteristic individual for signs.
 * A sign is represented as an integer, taking the values -1 or +1. <p> Forms of signs adhere to an ordinal behavior.
 * This characteristic individual accepts no parameters.
 * @see blue.form.OrdinalForm
 */
public class Sign extends Individual {
    static {
        PrimitiveSort.register(Sign.class, OrdinalForm.class, Parameter.NONE);
        proto(Sign.class, "icons/sign.gif");
    }

    private static final int POSITIVE = 1;
    private static final int NEGATIVE = -1;
    private static final int ZERO = 0;
    // representation
    private int value;
    // constructors

    /**
     * Constructs a nondescript <tt>Sign</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * sign. This sign must subsequently be assigned a sort and a value.
     * @see Individual#parse
     * @see #parse
     */
    Sign() {
        super();
        this.value = ZERO;
    }

    /**
     * Constructs a <tt>Sign</tt> with the specified value, for the specified sort. Any positive value results in 1,
     * any negative value in -1. The sort must allow for signs as individuals.
     * @param sort a sort
     * @param value an int
     * @exception IllegalArgumentException Occurs if the value equals 0.
     * @see blue.ind.Individual#Individual(Sort)
     */
    public Sign(Sort sort, int value) throws IllegalArgumentException {
        super(sort);
        if (value == ZERO)
            throw new IllegalArgumentException("Argument must be a non-zero value");
        this.value = Sign.of(value);
    }

    /**
     * Constructs a <tt>Sign</tt> with the value represented by the
     * specified string, for the specified sort. The sort must allow for numerics as individuals.
     * @param sort a sort
     * @param s the string to be parsed
     * @exception NumberFormatException Occurs if the string does not contain a parsable number.
     * @exception IllegalArgumentException Occurs if the parsed number equals 0.
     */
    public Sign(Sort sort, String s) throws NumberFormatException, IllegalArgumentException {
        this(sort, Integer.parseInt(s));
    }

    private static int of(int value) {
        return ((value > ZERO) ? POSITIVE : (value < ZERO) ? NEGATIVE : ZERO);
    }
    // access method

    /**
     * Returns the sign's value.
     * @return an int
     */
    public int value() { return this.value; }

    /**
     * Tests whether this individual equals nil. A sign is a nil individual if it's value equals 0.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return (this.value == ZERO); }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>Sign</tt> with
     * the same value, defined for the base sort of this sign's sort.
     * @return a duplicate sign
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Sign(this.ofSort().base(), this.value);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a sign. The result is <tt>true</tt> if and only if both signs have equal values.
     * @param other a sign
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Sign</tt> object.
     */
    boolean equalValued(Individual other) {
        return (this.value == ((Sign)other).value);
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Sign</tt> object. Otherwise the result is defined by comparing both signs' values.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Sign)) return FAILED;
        double d = this.value - ((Sign)other).value;
        return (d < 0) ? LESS : ((d == 0) ? EQUAL : GREATER);
    }

    /**
     * Tests if this individual contains the specified individual. A sign contains another sign if these have the same value.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>Sign</tt> class and the first sign contains the second; <tt>false</tt> otherwise
     */
    public boolean contains(Individual other) {
        return ((other instanceof Sign) && (this.value == ((Sign)other).value));
    }

    /**
     * Tests if this individual is disjoint from the specified individual.
     * Two signs are disjoint if these have opposite values.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the <tt>Sign</tt> class and both signs are disjoint;
     * <tt>false</tt> otherwise
     */
    public boolean disjoint(Individual other) {
        return ((other instanceof Sign) && (this.value + ((Sign)other).value) == ZERO);
    }

    /**
     * Combines this individual with the specified individual. Two signs
     * combine if these are not disjoint. The result is the sign of the sum of both values.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>Sign</tt> class and both signs combine; <tt>false</tt> otherwise
     */
    public boolean combine(Individual other) {
        if (!(other instanceof Sign)) return false;
        int temp = Sign.of(this.value + ((Sign)other).value);
        if (temp == ZERO) return false;
        this.value = temp;
        return true;
    }

    /**
     * Determines the common part of this individual with the specified
     * individual. Two signs share a common part if these have the same value. The result is this segment.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>Sign</tt> class and both signs share a common part; <tt>false</tt> otherwise
     */
    public boolean common(Individual other) {
        if (!(other instanceof Sign)) return false;
        return ((this.value == ((Sign)other).value) && (this.value != ZERO));
    }

    /**
     * Determines the complement part of this individual wrt the specified
     * individual. A sign has a complement wrt another sign if these have different values. The result is this sign.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>Sign</tt> class and both signs have different values; <tt>false</tt> otherwise
     */
    public boolean complement(Individual other) {
        if (!(other instanceof Sign)) return false;
        return ((this.value != ((Sign)other).value) && (this.value != ZERO));
    }

    /**
     * Creates a string representation of this individual's value. The result
     * can be included as is in an SDL description and can be subsequently parsed to reveal the original value.
     * @return a numeric string
     * @see #parse
     */
    String valueToString() {
        if (this.nil()) return NIL;
        return String.valueOf(this.value);
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a label node with this sign's value as its text.
     * @param gc a VRML context
     * @see GraphicsContext#label
     */
    void visualizeValue(GraphicsContext gc) {
        if (this.value == POSITIVE)
            gc.label("+");
        else if (this.value == NEGATIVE)
            gc.label("-");
        else
            gc.label(NIL);
    }

    /**
     * Reads a number token from a <tt>ParseReader</tt> object and assigns the value to this sign.
     * @param reader a token reader
     * @exception ParseException Occurs when no number token could be read.
     * @see #NUMBER
     */
    void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != NUMBER)
            throw new ParseException(reader, "number expected");
        this.value = Integer.parseInt(reader.tokenString());
    }
}
