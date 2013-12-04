/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Label.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import cassis.Thing;
import cassis.Element;
import cassis.parse.*;
import cassis.struct.Parameter;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;
import cassis.form.DiscreteForm;

/**
 * A <b>label</b> is an alphanumerical data entity.
 * <p>
 * The <tt>Label</tt> class defines the characteristic individual for labels.
 * A label is defined as an arbitrary string.
 * This characteristic individual accepts no parameters.
 * It specifies an exact {@link cassis.map.Mapping} as default.
 * Forms of labels adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 */
public class Label extends Individual {
    static {
        PrimitiveSort.register(Label.class, DiscreteForm.class, Parameter.NONE, cassis.map.Mapping.EXACT);
        new cassis.visit.vrml.Proto(Label.class, "icons/label.gif");
    }
    
    // representation
    private String s, encoded;
    
    // constructors
    
    /**
     * Constructs a nondescript <b>Label</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * label. This label must subsequently be assigned a sort and value.
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
     * Constructs a <b>Label</b> from a string, for the specified sort.
     * This sort must allow for labels as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param s a string
     */
    public Label(Sort sort, String s) {
        this(sort, s, null);
    }
    /**
     * Constructs a <tt>Label</tt> with a character array,
     * for the specified sort. This sort must allow for labels as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param s a char array
     */
    public Label(Sort sort, char s[]) {
        this(sort, new String(s), null);
    }
    
    // access method
    
    /**
     * Returns the <b>label</b>'s string.
     * @return a string
     */
    public String label() {
        return this.s;
    }
    
    /**
     * Tests whether this label equals <b>nil</b>, i.e., the label's string is empty.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise
     */
    public boolean nil() { return this.s.equals(""); }
    
    // Individual interface methods
    
    /**
     * <b>Duplicates</b> this label. It returns a new individual with
     * the same string, defined for the base sort of this label's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
        return new Label(this.ofSort().base(), this.s, this.encoded);
    }
    
    /**
     * Checks whether this label has <b>equal value</b> to another individual.
     * This condition applies if both labels have equal strings.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a label
     */
    boolean equalValued(Individual other) {
        return this.s.equals(((Label) other).s);
    }
    
    /**
     * Compares this label to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a label.
     * Otherwise the result is defined by comparing the strings of both labels.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
        if (!(other instanceof Label)) return FAILED;
        int c = this.s.compareTo(((Label) other).s);
        if (c < 0) return LESS;
        if (c > 0) return GREATER;
        return EQUAL;
    }
    
    /**
     * Converts the label's <b>value to a string</b>. The result is an
     * escape-encoded version of the label's string.
     * This string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
        if (this.nil()) return NIL;
        if (this.encoded != null) return this.encoded;
        
        this.encoded = this.s;
        for (int n = 0; n < this.encoded.length(); n++)
            switch (this.encoded.charAt(n)) {
                case '"': case '\\':
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
     * Reads an SDL description of a label from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this label. This description consists of
     * an identifier or a quoted string.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a label
     * @see #toString
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
        while ((n < this.s.length()) &&
                ((n = this.s.indexOf('\\', n)) >= 0))
            switch (this.s.charAt(n + 1)) {
                case 'n':
                    this.s = this.s.substring(0, n) + '\n' + this.s.substring(n+2);
                    n++;
                    break;
                case 't':
                    this.s = this.s.substring(0, n) + '\t' + this.s.substring(n+2);
                    n++;
                    break;
                default:
                    this.s = this.s.substring(0, n) + this.s.substring(n + 1);
                    n++;
                    break;
            }
    }
}
