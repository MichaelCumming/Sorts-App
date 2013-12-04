/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Weight.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import blue.Thing;
import blue.Element;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.SimpleSort;
import blue.sort.PrimitiveSort;
import blue.struct.Parameter;
import blue.struct.Argument;
import blue.form.OrdinalForm;

/**
 * The <tt>Weight</tt> class defines the characteristic individual for weights. A weight is represented as a positive double.
 * <p> Forms of weights adhere to an ordinal behavior. This characteristic individual accepts an upperbound argument.
 * @see blue.form.OrdinalForm
 * @see blue.struct.Parameter#UPPERBOUND
 */
public class Weight extends Individual {
    static {
        PrimitiveSort.register(Weight.class, OrdinalForm.class, Parameter.UPPERBOUND);
        proto(Weight.class, "icons/weight.gif");
    }

    private static final double MIN = 0.0;
    // representation
    private double value;
    // constructors

    /**
     * Constructs a nondescript <tt>Weight</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * weight. This weight must subsequently be assigned a sort and a value.
     * @see Individual#parse
     * @see #parse
     */
    Weight() {
        super();
        this.value = MIN;
    }

    /**
     * Constructs a <tt>Weight</tt> with the specified value,
     * for the specified sort. The sort must allow for weights as individuals.
     * @param sort a sort
     * @param value a double
     * @exception IllegalArgumentException Occurs if the value is negative or
     * the sort has an upperbound specified and the value is above this bound.
     * @see Individual#Individual
     */
    public Weight(Sort sort, double value) throws IllegalArgumentException {
        super(sort);
        if (value <= MIN)
            throw new IllegalArgumentException("Argument must be a positive value");
        Argument arg = null;
        Sort base = sort.base();
        if (base instanceof SimpleSort) arg = ((SimpleSort)base).arguments();
        if ((arg != null) && (value > ((Double)arg.value()).doubleValue()))
            throw new IllegalArgumentException("Argument must be smaller than specified maximum");
        this.value = value;
    }

    /**
     * Constructs a <tt>Weight</tt> with the value represented by the
     * specified string, for the specified sort. The sort must allow for numerics as individuals.
     * @param sort a sort
     * @param s the string to be parsed
     * @exception NumberFormatException Occurs if the string does not contain a parsable number.
     * @exception IllegalArgumentException Occurs if the parsed number is
     * negative or the sort has an upperbound specified and the number is above this bound.
     */
    public Weight(Sort sort, String s) throws NumberFormatException, IllegalArgumentException {
        this(sort, Double.valueOf(s).doubleValue());
    }
    // access method

    /**
     * Returns the weight's value.
     * @return a double
     */
    public double value() { return this.value; }

    /**
     * Tests whether this individual equals nil. A weight is a nil individual if it's value equals 0.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return (this.value == MIN); }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>Weight</tt> with
     * the same value, defined for the base sort of this weight's sort.
     * @return a duplicate weight
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Weight(this.ofSort().base(), this.value);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a weight. The result is <tt>true</tt> if and only if both weights have equal values.
     * @param other a sign
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Weight</tt> object.
     */
    boolean equalValued(Individual other) {
        return (this.value == ((Weight)other).value);
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Weight</tt> object. Otherwise the result is defined by comparing both weights' values.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Weight)) return FAILED;
        double d = this.value - ((Weight)other).value;
        return (d < MIN) ? LESS : ((d == MIN) ? EQUAL : GREATER);
    }

    /**
     * Tests if this individual contains the specified individual. A weight
     * contains another weight if this has a greater or equal value.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>Weight</tt> class and the first weight contains the second; <tt>false</tt> otherwise
     */
    public boolean contains(Individual other) {
        return ((other instanceof Weight) && (this.value >= ((Weight)other).value));
    }

    /**
     * Tests if this individual is disjoint from the specified individual. Two weights are never disjoint.
     * @param other an individual
     * @return <tt>false</tt>
     */
    public boolean disjoint(Individual other) {
        return false;
    }

    /**
     * Combines this individual with the specified individual. The result is this weight with the greater of both values.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>Weight</tt> class and both weights combine; <tt>false</tt> otherwise
     */
    public boolean combine(Individual other) {
        if (!(other instanceof Weight)) return false;
        if (this.value < ((Weight)other).value) this.value = ((Weight)other).value;
        return (!this.nil());
    }

    /**
     * Determines the common part of this individual with the specified
     * individual. The result is this weight with the lesser of both values.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>Weight</tt> class and both weights share a common part; <tt>false</tt> otherwise
     */
    public boolean common(Individual other) {
        if (!(other instanceof Weight)) return false;
        if (this.value > ((Weight)other).value) this.value = ((Weight)other).value;
        return (!this.nil());
    }

    /**
     * Determines the complement part of this individual wrt the specified
     * individual. A weight has a complement wrt another weight if it has a
     * value that is striuct greater. The result is this weight.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the <tt>Weight</tt> class and a complement part exists;
     * <tt>false</tt> otherwise
     */
    public boolean complement(Individual other) {
        if (!(other instanceof Weight)) return false;
        return (this.value > ((Weight)other).value);
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
     * specified context. This description consists of a label node with this weight's value as its text.
     * @param gc a VRML context
     * @see GraphicsContext#label
     */
    void visualizeValue(GraphicsContext gc) {
        gc.label(this.valueToString());
    }

    /**
     * Reads a number token from a <tt>ParseReader</tt> object and assigns the value to this weight.
     * @param reader a token reader
     * @exception ParseException Occurs when no number token could be read.
     * @see #NUMBER
     */
    void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != NUMBER)
            throw new ParseException(reader, "number expected");
        this.value = Double.valueOf(reader.tokenString()).doubleValue();
    }
}
