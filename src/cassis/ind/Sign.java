/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Sign.java'                                               *
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
import cassis.form.OrdinalForm;

/**
 * A <b>sign</b> is a binary data entity, distinguishing a positive from a negative
 * value.
 * <p>
 * The <tt>Sign</tt> class defines the characteristic individual for signs.
 * A sign is represented as an integer, taking the values of -1 and +1.
 * This characteristic individual accepts no parameters.
 * Forms of signs adhere to an ordinal behavior.
 * @see cassis.form.OrdinalForm
 */
public class Sign extends Individual {
    static {
	PrimitiveSort.register(Sign.class, OrdinalForm.class, Parameter.NONE);
	new cassis.visit.vrml.Proto(Sign.class, "icons/sign.gif");
    }

    public static final int POSITIVE = 1;
    public static final int NEGATIVE = -1;
    private static final int ZERO = 0;

    // representation
    private int value;

    // constructors

    /**
     * Constructs a nondescript <b>Sign</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * sign. This sign must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Sign() {
	super();
	this.value = ZERO;
    }
    /**
     * Constructs a <b>Sign</b> from an integer value,
     * for the specified sort. Any positive value results in 1,
     * any negative value in -1. The sort must allow for signs as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param value an int
     * @throws IllegalArgumentException if the value equals 0
     */
    public Sign(Sort sort, int value) throws IllegalArgumentException {
	super(sort);
	if (value == ZERO)
	    throw new IllegalArgumentException("Argument must be a non-zero value");
	this.value = Sign.of(value);
    }
    /**
     * Constructs a <b>Sign</b> from a string, for the specified sort.
     * The sort must allow for numerics as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param s the string to be parsed
     * @throws NumberFormatException if the string cannot be parsed to a number
     * @throws IllegalArgumentException if the parsed number equals 0
     */
    public Sign(Sort sort, String s) throws NumberFormatException, IllegalArgumentException {
	this(sort, Integer.parseInt(s));
    }

    private static int of(int value) {
	return ((value > ZERO) ? POSITIVE : (value < ZERO) ? NEGATIVE : ZERO);
    }

    // access method

    /**
     * Returns the sign's <b>value</b>.
     * @return an int
     */
    public int value() { return this.value; }

    /**
     * Tests whether this individual equals <b>nil</b>, i.e., it's value equals 0.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise
     */
    public boolean nil() { return (this.value == ZERO); }

    // methods

    /**
     * <b>Duplicates</b> this sign. It returns a new individual with
     * the same value, defined for the base sort of this sign's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Sign(this.ofSort().base(), this.value);
    }

    /**
     * Checks whether this sign has <b>equal value</b> to another individual.
     * This condition applies if both signs have equal values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a sign
     */
    boolean equalValued(Individual other) {
	return (this.value == ((Sign) other).value);
    }

    /**
     * Compares this sign to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a sign.
     * Otherwise the result is defined by comparing the values of both signs.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Sign)) return FAILED;
	double d = this.value - ((Sign) other).value;
	return (d < 0) ? LESS : ((d == 0) ? EQUAL : GREATER);
    }

    /**
     * Tests if this sign <b>contains</b> another individual. A sign
     * contains another sign if these have the same value.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean contains(Individual other) {
	return ((other instanceof Sign) &&
		(this.value == ((Sign) other).value));
    }

    /**
     * Tests if this sign is disjoint from another individual.
     * Two signs are disjoint if these have opposite values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean disjoint(Individual other) {
	return ((other instanceof Sign) &&
		(this.value + ((Sign) other).value) == ZERO);
    }

    /**
     * <b>Combines</b> this sign with another individual. Two signs combine if these
     * are not disjoint. The result equals the sign of the sum of both values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean combine(Individual other) {
	if (!(other instanceof Sign)) return false;

	int temp = Sign.of(this.value + ((Sign) other).value);
	if (temp == ZERO) return false;
	this.value = temp;
	return true;
    }

    /**
     * Determines the <b>common</b> part of this sign with another individual.
     * Two signs share a common part if these have the same value.
     * The result is this sign.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean common(Individual other) {
	if (!(other instanceof Sign)) return false;

	return ((this.value == ((Sign) other).value) && (this.value != ZERO));
    }

    /**
     * Determines the <b>complement</b> part of this sign wrt another
     * individual. A sign has a complement wrt another sign if these have
     * different values. The result is this sign.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean complement(Individual other) {
	if (!(other instanceof Sign)) return false;

	return ((this.value != ((Sign) other).value) && (this.value != ZERO));
    }

    /**
     * Converts the weight's <b>value to a string</b>. This string can be included
     * in an SDL description and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil()) return NIL;
	return String.valueOf(this.value);
    }

    /**
     * Reads an SDL description of a sign from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this sign. This description consists of a number.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a sign
     * @see #toString
     */
    void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != NUMBER)
	    throw new ParseException(reader, "number expected");
	this.value = Integer.parseInt(reader.tokenString());
    }
}
