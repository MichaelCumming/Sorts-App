/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Key.java'                                                *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import java.util.Random;

import cassis.Thing;
import cassis.Element;
import cassis.struct.Parameter;
import cassis.struct.Argument;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.SimpleSort;
import cassis.sort.PrimitiveSort;
import cassis.form.DiscreteForm;

/**
 * A <b>key</b> specifies a unique identifier key (ID).
 * <p>
 * The <tt>Key</tt> class defines the characteristic individual for keys.
 * A key is represented as a base identifier with an optional integer offset.
 * This characteristic individual accepts a single identifier argument as the
 * first part of the base identifier. Forms of keys adhere to a discrete behavior.
 * Methods are provided to generate a random base string, and to generate
 * consecutive keys (with consecutive integer offsets) for a given base identifier.
 * @see cassis.form.DiscreteForm
 * @see cassis.struct.Parameter#IDENTIFIER
 */
public class Key extends Individual {
    static {
	PrimitiveSort.register(Key.class, DiscreteForm.class, Parameter.IDENTIFIER);
	new cassis.visit.vrml.Proto(Key.class, "icons/key.gif");
    }

    // constants
    private final static String prefix = "_";
    private final static String separator = "-";
    private final static Random seed = new Random();

    // representation
    private String base;
    private long offset;

    // constructors

    /**
     * Constructs a nondescript <b>Key</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * key. This key must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Key() {
	super();
	this.base = "";
	this.offset = 0;
    }
    /**
     * Constructs a <b>Key</b> from a base identifier and integer offset,
     * for the specified sort. The sort must allow for keys as individuals.
     * If the characteristic individual already has a base identifier specified,
     * these are concatenated, separated by a '-'.
     * @param sort a {@link cassis.sort.Sort} object
     * @param base a base string
     * @param offset a long integer
     * @throws IllegalArgumentException if the specified string is not an identifier
     * @see cassis.parse.ParseReader#isIdentifier
     */
    public Key(Sort sort, String base, long offset) throws IllegalArgumentException {
	super(sort);
	if (!ParseReader.isIdentifier(base))
	    throw new IllegalArgumentException("base string must be identifier");
	Argument arg = null;
        Sort basesort = sort.base();
        if (basesort instanceof SimpleSort) arg = ((SimpleSort) basesort).arguments();
	if (arg != null)
	    this.base = (String) arg.value() + separator + base;
	else this.base = base;
	this.offset = offset;
	Keys context = sort.context().profile().keys();
	Long enum_ = context.getKey(base);
	if ((enum_ == null) || (enum_.longValue() < offset))
	    context.putKey(base, new Long(offset));
    }
    /**
     * Constructs a <b>Key</b> from a base identifier, for the specified sort.
     * The sort must allow for keys as individuals. If the characteristic individual
     * already has a base identifier specified, these are concatenated,
     * separated by a '-'. If another key has been constructed with the same base
     * identifier, an integer offset is added that is 1 higher than the highest
     * offset already used with this base identifier (minimum value 1).
     * Otherwise, no integer offset is added.
     * @param sort a {@link cassis.sort.Sort} object
     * @param base a base string
     * @throws IllegalArgumentException if the specified string is not an identifier
     * @see cassis.parse.ParseReader#isIdentifier
     */
    public Key(Sort sort, String base) throws IllegalArgumentException {
	super(sort);
	if (!ParseReader.isIdentifier(base))
	    throw new IllegalArgumentException("base string must be identifier");
	Argument arg = null;
        Sort basesort = sort.base();
        if (basesort instanceof SimpleSort) arg = ((SimpleSort) basesort).arguments();
	if (arg != null)
	    this.base = (String) arg.value() + separator + base;
	else this.base = base;
	this.offset = 0;
	Keys context = sort.context().profile().keys();
	Long enum_ = context.getKey(base);
	if (enum_ != null) this.offset = enum_.longValue() + 1;
	context.putKey(base, new Long(this.offset));
    }
    /**
     * Constructs a <b>Key</b> for the specified sort. The sort must allow
     * for keys as individuals. If the characteristic individual has a base
     * identifier specified, this is used as the base identifier of the key.
     * If another key has already been constructed with the same base
     * identifier, an integer offset is added that is 1 higher than the highest
     * offset already used with this base identifier (minimum value 1).
     * Otherwise, no integer offset is added. If no base identifier is specified for
     * the characteristic individual, an identifier of four random characters
     * preceded by a '_' is generated that is a unique base identifier.
     * In this case, no integer is added.
     * @param sort a {@link cassis.sort.Sort} object
     */
    public Key(Sort sort) {
	super(sort);
	Keys context = sort.context().profile().keys();
	Argument arg = null;
        Sort basesort = sort.base();
        if (basesort instanceof SimpleSort) arg = ((SimpleSort) basesort).arguments();
	if (arg != null) {
	    this.base = (String) arg.value();
	    this.offset = 1;
	    Long enum_ = context.getKey(base);
	    if (enum_ != null) this.offset = enum_.longValue() + 1;
	} else {
	    int n, r;
	    this.offset = 0;
	    do {
		this.base = prefix;
		for (n = 0; n < 4; n++) {
		    r = seed.nextInt();
		    if (r < 0) r *= -1;
		    r = mod(r, 52);
		    // System.out.println(r + " -> " + (char) (r < 26 ? r + 65 : r + 71));
		    this.base += (char) (r < 26 ? r + 65 : r + 71);
		}
	    } while (context.getKey(base) != null);
	}
	context.putKey(base, new Long(this.offset));
    }

    private static int mod(int value, int quotient) {
	return value - (value/quotient)*quotient;
    }

    // access methods

    /**
     * Returns the key's <b>key</b> string. This is a concatenation of the base
     * identifier with the optional integer offset, separated by a '-'.
     * @return a string
     */
    public String getKey() {
	if (offset > 0)
	    return this.base + separator + this.offset;
	return this.base;
    }

    /**
     * Tests whether this key equals nil, i.e., the base identifier defines an empty
     * string.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return (this.base == ""); }

    // methods

    /**
     * <b>Duplicates</b> this key. It returns a new individual with the same base
     * identifier but a different integer offset, defined for the base sort of this
     * key's sort. This integer offset is 1 higher than the highest offset already
     * used with this base identifier (minimum value 1).
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Key(this.ofSort().base(), this.base, this.offset);
    }

    /**
     * Checks whether this key has <b>equal value</b> to another individual.
     * This condition applies if both keys have equal base identifiers and
     * integer offsets.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a key
     */
    boolean equalValued(Individual other) {
	return (this.base.equals(((Key) other).base) &&
		(this.offset == ((Key) other).offset));
    }

    /**
     * Compares this key to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a key.
     * Otherwise the result is defined by comparing the base identifiers and optional
     * integer offsets of both dates.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Key)) return FAILED;
	int c = this.base.compareTo(((Key) other).base);
	if (c < 0) return LESS;
	if (c > 0) return GREATER;
	if (this.offset < ((Key) other).offset) return LESS;
	if (this.offset > ((Key) other).offset) return GREATER;
	return EQUAL;
    }

    /**
     * Converts the key's <b>value to a string</b>. The result is the
     * key string enclosed in double quotes.
     * This string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #getKey
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil()) return NIL;
	return '"' + this.getKey() + '"';
    }

    /**
     * Reads an SDL description of a key from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this key. This description defines a quoted string 
     * consisting of a base identifier and, optionally, an integer offset separated
     * with a '-'.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a key
     * @see #toString
     */
    void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != STRING)
	    throw new ParseException(reader, "string expected");
	this.base = reader.tokenString();
	this.base = this.base.substring(1, this.base.length() - 1);
	int n = this.base.lastIndexOf(separator);
	if (n > 0)
	    try {
		this.offset = Long.parseLong(this.base.substring(n + 1));
		this.base = this.base.substring(0, n);
	    } catch (NumberFormatException e) {}
    }
}
