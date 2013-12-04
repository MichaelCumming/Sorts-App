/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Label.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import blue.Thing;
import blue.Element;
import blue.io.*;
import blue.struct.Parameter;
import blue.sort.Sort;
import blue.sort.PrimitiveSort;
import blue.form.DiscreteForm;

/**
 * The <tt>Label</tt> class defines the characteristic individual for labels. A label is defined as an arbitrary string. <p>
 * Forms of labels adhere to a discrete behavior. This characteristic individual accepts no parameters.
 * @see blue.form.DiscreteForm
 */
public class Label extends Individual {
    static {
        PrimitiveSort.register(Label.class, DiscreteForm.class, Parameter.NONE);
        proto(Label.class, "icons/label.gif");
    }

    // representation
    private String s, encoded;
    // constructors

    /**
     * Constructs a nondescript <tt>Label</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * label. This label must subsequently be assigned a sort and a string.
     * @see Individual#parse
     * @see #parse
     */
    Label() {
        super();
        this.s = this.encoded = "";
    }

    private Label(Sort sort, String s, String encoded) {
        super(sort);
        this.s = s;
        this.encoded = encoded;
    }

    /**
     * Constructs a <tt>Label</tt> with the specified string,
     * for the specified sort. The sort must allow for labels as individuals.
     * @param sort a sort
     * @param s a string
     * @see Individual#Individual
     */
    public Label(Sort sort, String s) {
        this(sort, s, null);
    }

    /**
     * Constructs a <tt>Label</tt> with the specified char array as a string,
     * for the specified sort. The sort must allow for labels as individuals.
     * @param sort a sort
     * @param s a character array
     * @see Individual#Individual
     */
    public Label(Sort sort, char s[]) {
        this(sort, new String(s), null);
    }
    // access method

    /**
     * Returns the label's string.
     * @return a string
     */
    public String label() {
        return this.s;
    }

    /**
     * Tests whether this individual equals nil. A label is a nil individual if the label is defined as an empty string.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return this.s.equals(""); }
    // Individual interface methods

    /**
     * Duplicates this individual. It returns a new <tt>Label</tt> with
     * the same string, defined for the base sort of this label's sort.
     * @return a duplicate label
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Label(this.ofSort().base(), this.s, this.encoded);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a label. The result is <tt>true</tt> if and only if both labels have equal strings.
     * @param other a label
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Label</tt> object.
     */
    boolean equalValued(Individual other) {
        return this.s.equals(((Label)other).s);
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Label</tt> object. Otherwise the result is defined by comparing both labels' strings.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Label)) return FAILED;
        int c = this.s.compareTo(((Label)other).s);
        if (c < 0) return LESS;
        if (c > 0) return GREATER;
        return EQUAL;
    }

    /**
     * Creates a string representation of this individual's value. The result
     * is an escape-encoded version of the label's string that can be included
     * as is in an SDL description and can be subsequently parsed to reveal the original value.
     * @return a quoted, escape-encoded string
     * @see #parse
     */
    String valueToString() {
        if (this.nil()) return NIL;
        if (this.encoded != null) return this.encoded;
        this.encoded = this.s;
        for (int n = 0; n < this.encoded.length(); n++)
            switch (this.encoded.charAt(n)) {
                case '"':
                case '\\':
                    this.encoded = this.encoded.substring(0, n) + '\\' + this.encoded.substring(n);
                    n++;
                    break;
                case '\n':
                    this.encoded = this.encoded.substring(0, n) + "\\n" + this.encoded.substring(n + 1);
                    n++;
                    break;
                case '\t':
                    this.encoded = this.encoded.substring(0, n) + "\\t" + this.encoded.substring(n + 1);
                    n++;
                    break;
                case '_':
                    break;
                default:
                    char c = this.encoded.charAt(n);
                    if ((c >= 'a') && (c <= 'z')) continue;
                    if ((c >= 'A') && (c <= 'Z')) continue;
                    if ((n != 0) && (c >= '0') && (c <= '9')) continue;
                    break;
            }
        this.encoded = '"' + this.encoded + '"';
        return this.encoded;
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a label node with this label's string as its text.
     * @param gc a VRML context
     * @see GraphicsContext#label
     */
    void visualizeValue(GraphicsContext gc) {
        if (this.nil()) gc.label(NIL);
        else
            gc.label(this.s);
    }

    /**
     * Reads an identifier or string token from a <tt>ParseReader</tt> object
     * and assigns the value to this label. If the token is of type <tt>STRING</tt>, it's value is unquoted and decoded.
     * @param reader a token reader
     * @exception ParseException Occurs when no identifier or string token could be read.
     * @see #IDENTIFIER
     * @see #STRING
     */
    void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() == IDENTIFIER) {
            this.encoded = this.s = reader.tokenString();
            return;
        }
        if (reader.token() != STRING)
            throw new ParseException(reader, "identifier or string expected");
        this.encoded = reader.tokenString();
        this.s = this.encoded.substring(1, this.encoded.length() - 1);
        int n = 0;
        while ((n < this.s.length()) && ((n = this.s.indexOf('\\', n)) >= 0))
            switch (this.s.charAt(n + 1)) {
                case 'n':
                    this.s = this.s.substring(0, n) + '\n' + this.s.substring(n + 2);
                    n++;
                    break;
                case 't':
                    this.s = this.s.substring(0, n) + '\t' + this.s.substring(n + 2);
                    n++;
                    break;
                default:
                    this.s = this.s.substring(0, n) + this.s.substring(n + 1);
                    n++;
                    break;
            }
    }
}
