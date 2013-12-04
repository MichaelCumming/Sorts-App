/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Url.java'                                                *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import java.net.URL;
import java.net.MalformedURLException;
import blue.Thing;
import blue.Element;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.PrimitiveSort;
import blue.struct.Parameter;
import blue.struct.Argument;
import blue.form.DiscreteForm;

/**
 * The <tt>Url</tt> class defines the characteristic individual for URLs. A url is defined as an arbitrary address string. <p>
 * Forms of urls adhere to a discrete behavior. This characteristic individual accepts no parameters.
 * @see blue.form.DiscreteForm
 */
public class Url extends Individual {
    static {
        PrimitiveSort.register(Url.class, DiscreteForm.class, Parameter.NONE);
        proto(Url.class, "icons/image.gif", proto());
    }

    static String NILADDRESS = "";
    // representation
    private String address;
    // constructors

    /**
     * Constructs a nondescript <tt>Url</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * url. This url must subsequently be assigned a sort and an address.
     * @see Individual#parse
     * @see #parse
     */
    Url() {
        super();
        this.address = NILADDRESS;
    }

    /**
     * Constructs a <tt>Url</tt> with the specified address,
     * for the specified sort. The sort must allow for urls as individuals.
     * @param sort a sort
     * @param address a URL string
     * @see Individual#Individual
     */
    public Url(Sort sort, String address) {
        super(sort);
        this.address = address;
    }

    /**
     * Sets the url's value. Used by subclass constructors. A url's value can
     * only be set once, either at initialization or using this method.
     * @param address a URL string
     */
    void set(String address) {
        if (this.address == NILADDRESS)
            this.address = address;
    }
    // access methods

    /**
     * Returns the url's address.
     * @return a URL string
     */
    public String address() { return this.address; }

    /**
     * Returns the url's address as a <tt>URL</tt> object.
     * @return a URL object
     * @exception MalformedURLException Occurs if the url's address is not a valid URL.
     */
    public URL getURL() throws MalformedURLException {
        return new URL(this.address);
    }

    /**
     * Tests whether this individual equals nil. A url is a nil individual if the address is defined as an empty string.
     * @return <tt>true</tt> if the individual equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return (this.address.equals(NILADDRESS)); }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>Url</tt> with
     * the same address, defined for the base sort of this url's sort.
     * @return a duplicate url
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Url(this.ofSort().base(), this.address);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a url. The result is <tt>true</tt> if and only if both urls have equal addresses.
     * @param other a url
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Url</tt> object.
     */
    boolean equalValued(Individual other) {
        return this.address.equals(((Url)other).address);
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Url</tt> object. Otherwise the result is defined by comparing both urls' addresses.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Url)) return FAILED;
        int d = this.address.compareTo(((Url)other).address);
        return (d < 0) ? LESS : ((d == 0) ? EQUAL : GREATER);
    }

    /**
     * Creates a string representation of this individual's value. The result
     * is the url's address, enclosed in double quotes, that can be included
     * as is in an SDL description and can be subsequently parsed to reveal the original value.
     * @return a quoted string
     * @see #parse
     */
    String valueToString() {
        if (this.nil()) return NIL;
        return '"' + this.address + '"';
    }

    private static String proto() {
        String str = "PROTO Url [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";
        str += "PROTO UrlShape [\n field SFVec3f textTranslation 0.4 0.4 0.4\n field SFColor textColor 0.32 0.4 0.698\n field MFString linkURL \"\"\n field SFString linkDescription \"view the page\"\n field MFString textLabel \"\"\n field SFFloat textSize 0.5 ] {\n";
        str += " Anchor {\n  children Billboard {\n   children [\n    Transform { translation IS textTranslation\n     children Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor IS textColor } }\n";
        str += "      geometry Text {\n       string IS textLabel\n       fontStyle FontStyle {\n        size IS textSize\n        justify [\"MIDDLE\"] } } } } ]\n   axisOfRotation 0 1 0 }\n";
        str += "  parameter [ \"target=IMAGE_PAGE\" ]\n  url IS linkURL\n  description IS linkDescription } }\n\n";
        return str;
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a link node with this url's address as its text.
     * @param gc a VRML context
     * @see GraphicsContext#link
     */
    void visualizeValue(GraphicsContext gc) {
        if (this.nil()) gc.label(NIL);
        else
            gc.link(this.address, this.address, null);
    }

    /**
     * Reads an identifier or string token from a <tt>ParseReader</tt> object
     * and assigns the value to this url. The token's value is unquoted.
     * @param reader a token reader
     * @exception ParseException Occurs when no string token could be read.
     * @see #STRING
     */
    void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != STRING)
            throw new ParseException(reader, "string expected");
        this.address = reader.tokenString();
        this.address = this.address.substring(1, this.address.length() - 1);
    }
}
