/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Url.java'                                                *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import java.net.URL;
import java.net.MalformedURLException;

import cassis.Thing;
import cassis.Element;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;
import cassis.struct.Parameter;
import cassis.struct.Argument;
import cassis.form.DiscreteForm;

/**
 * A <b>url</b> is a representation of a Universal Resource Locator.
 * <p>
 * The <tt>Url</tt> class defines the characteristic individual for URLs.
 * A url is defined as an arbitrary address string.
 * This characteristic individual accepts no parameters.
 * Forms of urls adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 */
public class Url extends Individual {
    static {
	PrimitiveSort.register(Url.class, DiscreteForm.class, Parameter.NONE);
	new cassis.visit.vrml.Proto(Url.class, "icons/image.gif");
    }
    
    static String NILADDRESS = "";

    // representation
    private String address;

    // constructors

    /**
     * Constructs a nondescript <b>Url</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * url. This url must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Url() {
	super();
	this.address = NILADDRESS;
    }
    /**
     * Constructs a <b>Url</b> with the specified address,
     * for the specified sort. The sort must allow for urls as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param address a URL string
     */
    public Url(Sort sort, String address) {
	super(sort);
	this.address = address;
    }

    /**
     * <b>Sets</b> the url's value. Used by subclass constructors. A url's value can
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
     * Returns the <b>url</b>'s address as a <tt>URL</tt> object.
     * @return a <tt>java.net.URL</tt> object
     * @throws MalformedURLException if the url's address is not a valid URL
     */
    public URL getURL() throws MalformedURLException {
	return new URL(this.address);
    }

    /**
     * Tests whether this url equals <b>nil</b>, i.e., it has an empty address string.
     * @return <tt>true</tt> if the url equals nil, <tt>false</tt> otherwise.
     */
    public boolean nil() { return (this.address.equals(NILADDRESS)); }

    // methods

    /**
     * <b>Duplicates</b> this url. It returns a new individual with
     * the same address, defined for the base sort of this url's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Url(this.ofSort().base(), this.address);
    }

    /**
     * Checks whether this url has <b>equal value</b> to another individual.
     * This condition applies if both urls have equal adresses.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a url
     */
    boolean equalValued(Individual other) {
	return this.address.equals(((Url) other).address);
    }

    /**
     * Compares this url to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a url.
     * Otherwise the result is defined by comparing the adresses of both urls.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Url)) return FAILED;
	int d = this.address.compareTo(((Url) other).address);
	return (d < 0) ? LESS : ((d == 0) ? EQUAL : GREATER);
    }

    /**
     * Converts the url's <b>value to a string</b>. The result is the url's address,
     * enclosed in double quotes. This string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil()) return NIL;
	return '"' + this.address + '"';
    }

    /**
     * Reads an SDL description of a url from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this url. This description consists of 
     * a quoted string representing a URL.
     * @param reader a token reader
     * @exception ParseException if the description does not correctly describe a url
     * @see #toString
     */
    void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != STRING)
	    throw new ParseException(reader, "string expected");
	this.address = reader.tokenString();
	this.address = this.address.substring(1, this.address.length() - 1);
    }
}
