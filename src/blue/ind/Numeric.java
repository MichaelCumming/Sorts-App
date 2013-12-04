/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Numeric.java'                                            *
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
import blue.form.DiscreteForm;

/**
 * The <tt>Numeric</tt> class defines the characteristic individual for
 * numeric labels. A numeric is represented as a double and a nil flag. <p> Forms of numerics adhere to a discrete behavior.
 * This characteristic individual accepts no parameters.
 * @see blue.form.DiscreteForm
 */
public class Numeric extends Individual {
    static {
        PrimitiveSort.register(Numeric.class, DiscreteForm.class, Parameter.NONE);
        proto(Numeric.class, "icons/numeric.gif");
    }

    // representation
    private double value;
    private boolean nil;
    // constructors

    /**
     * Constructs a nondescript <tt>Numeric</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * numeric. This numeric must subsequently be assigned a sort and a value.
     * @see Individual#parse
     * @see #parse
     */
    Numeric() {
        super();
        this.value = 0.0;
        this.nil = true;
    }

    private Numeric(Sort sort, double value, boolean nil) {
        super(sort);
        this.value = value;
        this.nil = nil;
    }

    /**
     * Constructs a <tt>Numeric</tt> with the specified value,
     * for the specified sort. The sort must allow for numerics as individuals.
     * @param sort a sort
     * @param value a double
     * @see Individual#Individual
     */
    public Numeric(Sort sort, double value) {
        this(sort, value, false);
    }

    /**
     * Constructs a <tt>Numeric</tt> with the value represented by the
     * specified string, for the specified sort. The sort must allow for numerics as individuals.
     * @param sort a sort
     * @param s the string to be parsed
     * @exception NumberFormatException Occurs if the string does not contain a parsable number.
     */
    public Numeric(Sort sort, String s) throws NumberFormatException {
        this(sort, Double.valueOf(s).doubleValue(), false);
    }
    // access method

    /**
     * Returns the numeric's value.
     * @return a double
     */
    public double value() {
        return this.value;
    }

    /**
     * Tests whether this individual equals nil. A numeric is a nil individual if the nil flag is raised.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return this.nil; }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>Numeric</tt> with
     * the same value, defined for the base sort of this numeric's sort.
     * @return a duplicate numeric
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Numeric(this.ofSort().base(), this.value, this.nil);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a numeric. The result is <tt>true</tt> if and only if both numerics have equal values.
     * @param other a numeric
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Numeric</tt> object.
     */
    boolean equalValued(Individual other) {
        if (this.nil) return (other.nil());
        return (this.value == ((Numeric)other).value);
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Numeric</tt> object. Otherwise the result is defined by comparing both numerics' values.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Numeric)) return FAILED;
        double d = this.value - ((Numeric)other).value;
        return (d < 0.0) ? LESS : ((d == 0.0) ? EQUAL : GREATER);
    }

    /**
     * Creates a string representation of this individual's value. The result
     * can be included as is in an SDL description and can be subsequently parsed to reveal the original value.
     * @return a numeric string
     * @see #parse
     */
    String valueToString() {
        if (this.nil) return NIL;
        return String.valueOf(this.value);
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a label node with this numeric's value as its text.
     * @param gc a VRML context
     * @see GraphicsContext#label
     */
    void visualizeValue(GraphicsContext gc) {
        gc.label(this.valueToString());
    }

    /**
     * Reads a number token from a <tt>ParseReader</tt> object and assigns the value to this numeric.
     * @param reader a token reader
     * @exception ParseException Occurs when no number token could be read.
     * @see #NUMBER
     */
    void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != NUMBER)
            throw new ParseException(reader, "number expected");
        this.value = Double.valueOf(reader.tokenString()).doubleValue();
        this.nil = false;
    }
}
