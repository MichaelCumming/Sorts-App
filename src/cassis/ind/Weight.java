/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Weight.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import cassis.Thing;
import cassis.Element;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.SimpleSort;
import cassis.sort.PrimitiveSort;
import cassis.struct.Parameter;
import cassis.struct.Argument;
import cassis.form.OrdinalForm;

/**
 * A <b>weight</b> is a numerical data entity with an ordinal behavior, i.e.,
 * the value of the sum of two weights is the maximum value of both weigths.
 * <p>
 * The <b>Weight</b> class defines the characteristic individual for weights.
 * A weight is represented as a positive double.
 * This characteristic individual accepts an upperbound parameter.
 * Forms of weights adhere to an ordinal behavior.
 * @see cassis.form.OrdinalForm
 * @see cassis.struct.Parameter#UPPERBOUND
 */
public class Weight extends Individual {
    static {
	PrimitiveSort.register(Weight.class, OrdinalForm.class, Parameter.UPPERBOUND);
	new cassis.visit.vrml.Proto(Weight.class, "icons/weight.gif");
    }

    private static final double MIN = 0.0;

    // representation
    private double value;

    // constructors

    /**
     * Constructs a nondescript <b>Weight</b>. This constructor exists for
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
     * Constructs a <b>Weight</b> from a numerical value,
     * for the specified sort. The sort must allow for weights as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param value a double
     * @throws IllegalArgumentException if the value is negative or
     * the sort has an upperbound specified and the value is above this bound
     */
    public Weight(Sort sort, double value) throws IllegalArgumentException {
	super(sort);
	if (value <= MIN)
	    throw new IllegalArgumentException("Argument must be a positive value");
        Argument arg = null;
        Sort base = sort.base();
        if (base instanceof SimpleSort) arg = ((SimpleSort) base).arguments();
	if ((arg != null) && (value > ((Double) arg.value()).doubleValue()))
	    throw new IllegalArgumentException("Argument must be smaller than specified maximum");
	this.value = value;
    }
    /**
     * Constructs a <b>Weight</b> from a string, for the specified sort.
     * The sort must allow for weights as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param s the <tt>String</tt> object
     * @throws NumberFormatException if the string cannot be parsed to a number
     * @throws IllegalArgumentException if the value is negative or
     * the sort has an upperbound specified and the value is above this bound
     */
    public Weight(Sort sort, String s) throws NumberFormatException, IllegalArgumentException {
	this(sort, Double.valueOf(s).doubleValue());
    }

    // access method

    /**
     * Returns this weight's <b>value</b>.
     * @return a double
     */
    public double value() { return this.value; }

    /**
     * Tests whether this weight equals <b>nil</b>, i.e., it's value equals 0.
     * @return <tt>true</tt> if this weight equals nil, <tt>false</tt> otherwise
     */
    public boolean nil() { return (this.value == MIN); }

    // methods

    /**
     * <b>Duplicates</b> this weight. It returns a new individual with
     * the same value, defined for the base sort of this weight's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Weight(this.ofSort().base(), this.value);
    }

    /**
     * Checks whether this weight has <b>equal value</b> to another individual.
     * This condition applies if both weights have equal values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a weight
     */
    boolean equalValued(Individual other) {
	return (this.value == ((Weight) other).value);
    }

    /**
     * Compares this weight to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a weight.
     * Otherwise the result is defined by comparing the values of both weights.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Weight)) return FAILED;
	double d = this.value - ((Weight) other).value;
	return (d < MIN) ? LESS : ((d == MIN) ? EQUAL : GREATER);
    }

    /**
     * Tests if this weight <b>contains</b> another individual. A weight
     * contains another weight if the first has a greater or equal value.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean contains(Individual other) {
	return ((other instanceof Weight) &&
		(this.value >= ((Weight) other).value));
    }

    /**
     * Tests if this weight is disjoint from another individual.
     * Two weights are never disjoint.
     * @param other an {@link Individual} object
     * @return <tt>false</tt>
     */
    public boolean disjoint(Individual other) {
	return false;
    }

    /**
     * <b>Combines</b> this weight with another individual.
     * The result is this weight with the greater of both weights' values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean combine(Individual other) {
	if (!(other instanceof Weight)) return false;

	if (this.value < ((Weight) other).value) this.value = ((Weight) other).value;
	return (!this.nil());
    }

    /**
     * Determines the <b>common</b> part of this weight with another
     * individual. The result is this weight with the lesser of both weights' values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean common(Individual other) {
	if (!(other instanceof Weight)) return false;

	if (this.value > ((Weight) other).value) this.value = ((Weight) other).value;
	return (!this.nil());
    }

    /**
     * Determines the <b>complement</b> part of this weight wrt another
     * individual. A weight has a complement wrt another weight if it has a
     * value that is strictly greater. The result is this weight.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean complement(Individual other) {
	if (!(other instanceof Weight)) return false;

	return (this.value > ((Weight) other).value);
    }

    /**
     * Converts the weight's <b>value to a string</b>.
     * This string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil()) return NIL;
	return String.valueOf(this.value);
    }

    /**
     * Reads an SDL description of a weight from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this weight. This description consists of a number.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a weight
     * @see #toString
     */
    void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != NUMBER)
	    throw new ParseException(reader, "number expected");
	this.value = Double.valueOf(reader.tokenString()).doubleValue();
    }
}
