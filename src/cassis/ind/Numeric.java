/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Numeric.java'                                            *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import cassis.Thing;
import cassis.Element;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;
import cassis.struct.Parameter;
import cassis.form.DiscreteForm;

/**
 * A <b>numeric</b> label is a numerical data entity.
 * <p>
 * The <b>Numeric</b> class defines the characteristic individual for
 * numeric labels. A numeric is represented as a double. An additional
 * <i>nil</i> flag specifies a nil value for a numeric label.
 * This characteristic individual accepts no parameters.
 * Forms of numerics adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 */
public class Numeric extends Individual {
    static {
	PrimitiveSort.register(Numeric.class, DiscreteForm.class, Parameter.NONE);
	new cassis.visit.vrml.Proto(Numeric.class, "icons/numeric.gif");
    }

    // representation
    private double value;
    private boolean nil;

    // constructors

    /**
     * Constructs a nondescript <b>Numeric</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * numeric. This numeric must subsequently be assigned a sort and value.
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
     * Constructs a <b>Numeric</b> from a double precision value,
     * for the specified sort. The sort must allow for numerics as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param value a double
     */
    public Numeric(Sort sort, double value) {
	this(sort, value, false);
    }
    /**
     * Constructs a <b>Numeric</b> from a string, for the specified sort.
     * The sort must allow for numerics as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param s the string to be parsed
     * @throws NumberFormatException if the string cannot be parsed to a number
     */
    public Numeric(Sort sort, String s) throws NumberFormatException {
	this(sort, Double.valueOf(s).doubleValue(), false);
    }

    // access method

    /**
     * Returns the numeric's <b>value</b>.
     * @return a double
     */
    public double value() {
	return this.value;
    }

    /**
     * Tests whether this numeric equals <b>nil</b>, i.e., the nil flag is raised.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise
     */
    public boolean nil() { return this.nil; }

    // methods

    /**
     * <b>Duplicates</b> this numeric. It returns a new individual with
     * the same value, defined for the base sort of this numeric's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Numeric(this.ofSort().base(), this.value, this.nil);
    }

    /**
     * Checks whether this numeric has <b>equal value</b> to another individual.
     * This condition applies if both numerics have equal values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a numeric
     */
    boolean equalValued(Individual other) {
	if (this.nil) return (other.nil());
	return (this.value == ((Numeric) other).value);
    }

    /**
     * Compares this numeric to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a numeric.
     * Otherwise the result is defined by comparing the values of both numerics.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Numeric)) return FAILED;
	double d = this.value - ((Numeric) other).value;
	return (d < 0.0) ? LESS : ((d == 0.0) ? EQUAL : GREATER);
    }

    /**
     * Converts the numeric's <b>value to a string</b>.
     * This string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil) return NIL;
	return String.valueOf(this.value);
    }

    /**
     * Reads an SDL description of a numeric from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this numeric. This description consists of a number.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a numeric
     * @see #toString
     */
    void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != NUMBER)
	    throw new ParseException(reader, "number expected");
	this.value = Double.valueOf(reader.tokenString()).doubleValue();
	this.nil = false;
    }
}
