/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Key.java'                                                *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  The KEY class specifies the characteristic individual for
//  unique identifier keys (ID's). It extends on the Individual class
//  and implements its methods (see `Individual.java').
//  A key is represented as a base string with an optional integer.
//  Methods are provided to generate a random base string,
//  and to generate consecutive keys given a base string.

package blue.ind;

import java.util.Random;
import blue.Thing;
import blue.Element;
import blue.struct.Parameter;
import blue.struct.Argument;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.SimpleSort;
import blue.sort.PrimitiveSort;
import blue.form.DiscreteForm;

/**
 * The <tt>Key</tt> class defines the characteristic individual for unique
 * identifier keys (ID's). A key is represented as a base identifier with an
 * optional integer. Methods are provided to generate a random base string,
 * and to generate consecutive keys (with consecutive integer values) for a given base identifier. <p>
 * Forms of keys adhere to a discrete behavior. This characteristic individual accepts a single identifier argument as the
 * first part of the base identifier.
 * @see blue.form.DiscreteForm
 */
public class Key extends Individual {
    static {
        PrimitiveSort.register(Key.class, DiscreteForm.class, Parameter.IDENTIFIER);
        proto(Key.class, "icons/key.gif");
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
     * Constructs a nondescript <tt>Key</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * key. This key must subsequently be assigned a sort and a value.
     * @see Individual#parse
     * @see #parse
     */
    Key() {
        super();
        this.base = "";
        this.offset = 0;
    }

    /**
     * Constructs a <tt>Key</tt> with the specified base identifier and
     * integer, for the specified sort. The sort must allow for keys as
     * individuals. If the characteristic individual already has a base
     * identifier specified, these are concatenated, separated by a '-'.
     * @param sort a sort
     * @param base a base string
     * @param offset a long integer
     * @exception IllegalArgumentException Occurs if the specified string is not an identifier.
     * @see Individual#Individual
     * @see blue.io.ParseReader#isIdentifier
     */
    public Key(Sort sort, String base, long offset) throws IllegalArgumentException {
        super(sort);
        if (!ParseReader.isIdentifier(base))
            throw new IllegalArgumentException("base string must be identifier");
        Argument arg = null;
        Sort basesort = sort.base();
        if (basesort instanceof SimpleSort) arg = ((SimpleSort)basesort).arguments();
        if (arg != null)
            this.base = (String)arg.value() + separator + base;
        else
            this.base = base;
        this.offset = offset;
        Keys context = sort.context().profile().keys();
        Long e = context.getKey(base);
        if ((e == null) || (e.longValue() < offset))
            context.putKey(base, new Long(offset));
    }

    /**
     * Constructs a <tt>Key</tt> with the specified base identifier, for
     * the specified sort. The sort must allow for keys as individuals.
     * If the characteristic individual already has a base identifier
     * specified, these are concatenated, separated by a '-'. If another key
     * has been constructed with the same base identifier, an integer is
     * added that is 1 higher than the highest integer already used with this
     * base identifier (minimum value 1). Otherwise, no integer is added.
     * @param sort a sort
     * @param base a base string
     * @exception IllegalArgumentException Occurs if the specified string is not an identifier.
     * @see Individual#Individual
     * @see blue.io.ParseReader#isIdentifier
     */
    public Key(Sort sort, String base) throws IllegalArgumentException {
        super(sort);
        if (!ParseReader.isIdentifier(base))
            throw new IllegalArgumentException("base string must be identifier");
        Argument arg = null;
        Sort basesort = sort.base();
        if (basesort instanceof SimpleSort) arg = ((SimpleSort)basesort).arguments();
        if (arg != null)
            this.base = (String)arg.value() + separator + base;
        else
            this.base = base;
        this.offset = 0;
        Keys context = sort.context().profile().keys();
        Long e = context.getKey(base);
        if (e != null) this.offset = e.longValue() + 1;
        context.putKey(base, new Long(this.offset));
    }

    /**
     * Constructs a <tt>Key</tt> for the specified sort. The sort must allow
     * for keys as individuals. If the characteristic individual has a base
     * identifier specified, this is used as the base identifier of the key.
     * If another key has already been constructed with the same base
     * identifier, an integer is added that is 1 higher than the highest
     * integer already used with this base identifier (minimum value 1).
     * Otherwise, no integer is added. If no base identifier is specified for
     * the characteristic individual, an identifier of four random characters
     * preceded by a '_' is generated that is a unique base identifier. In this case, no integer is added.
     * @param sort a sort
     * @see Individual#Individual
     */
    public Key(Sort sort) {
        super(sort);
        Keys context = sort.context().profile().keys();
        Argument arg = null;
        Sort basesort = sort.base();
        if (basesort instanceof SimpleSort) arg = ((SimpleSort)basesort).arguments();
        if (arg != null) {
            this.base = (String)arg.value();
            this.offset = 1;
            Long e = context.getKey(base);
            if (e != null) this.offset = e.longValue() + 1;
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
                    this.base += (char)(r < 26 ? r + 65 : r + 71);
                }
            } while (context.getKey(base) != null);
        }
        context.putKey(base, new Long(this.offset));
    }

    private static int mod(int value, int quotient) {
        return value - (value / quotient) * quotient;
    }
    // access methods

    /**
     * Returns the key's key string. This is a concatenation of the base
     * identifier with the optional integer, separated by a '-'.
     * @return a string
     */
    public String getKey() {
        if (offset > 0)
            return this.base + separator + this.offset;
        return this.base;
    }

    /**
     * Tests whether this individual equals nil. A key is a nil individual
     * if the base identifier is defined as an empty string.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return (this.base == ""); }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>Key</tt> with
     * the same identifier but with a new integer, defined for the base sort
     * of this key's sort. This new integer is 1 higher than the highest
     * integer already used with this base identifier (minimum value 1).
     * @return a key
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Key(this.ofSort().base(), this.base, this.offset);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a key. The result is <tt>true</tt> if and only if both keys have equal values.
     * @param other a key
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Key</tt> object.
     */
    boolean equalValued(Individual other) {
        return (this.base.equals(((Key)other).base) && (this.offset == ((Key)other).offset));
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Key</tt> object. Otherwise the result is defined by comparing both keys' base identifiers and optional integers.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Key)) return FAILED;
        int c = this.base.compareTo(((Key)other).base);
        if (c < 0) return LESS;
        if (c > 0) return GREATER;
        if (this.offset < ((Key)other).offset) return LESS;
        if (this.offset > ((Key)other).offset) return GREATER;
        return EQUAL;
    }

    /**
     * Creates a string representation of this individual's value. The result
     * is the key string enclosed in double quotes, that can be included
     * as is in an SDL description and can be subsequently parsed to reveal the original value.
     * @return a quoted string
     * @see #getKey
     * @see #parse
     */
    String valueToString() {
        if (this.nil()) return NIL;
        return '"' + this.getKey() + '"';
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a label node with this key string as its text.
     * @param gc a VRML context
     * @see #getKey
     * @see GraphicsContext#label
     */
    void visualizeValue(GraphicsContext gc) {
        if (this.nil()) gc.label(NIL);
        else
            gc.label(this.getKey());
    }

    /**
     * Reads a string token from a <tt>ParseReader</tt> object
     * and assigns the value to this label. The token's value is unquoted and
     * separated into a base identifier and optional integer.
     * @param reader a token reader
     * @exception ParseException Occurs when no string token could be read.
     * @see #STRING
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
            } catch (NumberFormatException e) { }
    }
}
xception e) { }
    }
}
