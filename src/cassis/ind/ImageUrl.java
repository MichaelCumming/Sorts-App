/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `ImageUrl.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import java.net.URL;
import java.net.MalformedURLException;

import cassis.Thing;
import cassis.Element;
import cassis.struct.Parameter;
import cassis.struct.Argument;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;
import cassis.form.DiscreteForm;

/**
 * An <b>image-url</b> is a representation of a Universal Resource Locator with
 * a corresponding image URL.
 * <p>
 * The <tt>ImageUrl</tt> class defines the characteristic individual for image URLs.
 * An image url is defined as a url with an additional icon URL string.
 * This characteristic individual accepts no parameters.
 * Forms of urls adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 */
public class ImageUrl extends Url {
    static {
	PrimitiveSort.register(ImageUrl.class, DiscreteForm.class, Parameter.NONE);
	new cassis.visit.vrml.Proto(ImageUrl.class, "icons/image.gif");
    }

    // representation
    private String icon;

    // constructors

    /**
     * Constructs a nondescript <b>ImageUrl</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * image url. This url must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    ImageUrl() {
	super();
	this.icon = NILADDRESS;
    }

    /**
     * Constructs a <tt>ImageUrl</tt> with the specified address and icon
     * address, for the specified sort. The sort must allow for image urls
     * as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param address a URL string
     * @param icon a URL string
     */
    public ImageUrl(Sort sort, String address, String icon) {
	super(sort, address);
	this.icon = icon;
    }

    // access method

    /**
     * Returns the url's icon address.
     * @return a URL string
     */
    public String icon() {
	return this.icon;
    }

    /**
     * Returns the <b>url's icon</b> address as a <tt>URL</tt> object.
     * @return a <tt>java.net.URL</tt> object
     * @throws MalformedURLException if the url's address is not a valid URL
     */
    public URL getIconURL() throws MalformedURLException {
	return new URL(this.icon);
    }

    // methods

    /**
     * <b>Duplicates</b> this image url. It returns a new individual with
     * the same value, defined for the base sort of this image url's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new ImageUrl(this.ofSort().base(), this.address(), this.icon);
    }

    /**
     * Checks whether this image url has <b>equal value</b> to another individual.
     * This condition applies if both image urls have equal values.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not an image url
     */
    boolean equalValued(Individual other) {
	return (super.equalValued(other) &&
		this.icon.equals(((ImageUrl) other).icon));
    }

    /**
     * Compares this image url to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not an
     * image url. Otherwise the result is defined by comparing the adresses
     * and icon adresses of both urls.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof ImageUrl)) return FAILED;

	int d = super.compare(other);
	if (d == 0) d = this.icon.compareTo(((ImageUrl) other).icon);
	return (d < 0) ? LESS : ((d == 0) ? EQUAL : GREATER);
    }

    /**
     * Converts the image url's <b>value to a string</b>. The result is
     * a parenthesized list of the image url's address and icon address,
     * both enclosed in double quotes. This string can be included in an SDL
     * description and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    String valueToString(Individual assoc) {
	return "(\"" + this.address() + "\", \"" + this.icon + "\")";
    }

    /**
     * Reads an SDL description of an image url from a {@link cassis.parse.ParseReader}
     * object and assigns the value to this image url. This description consists of 
     * a parenthesized pair of quoted strings, each representing a URL.
     * @param reader a token reader
     * @exception ParseException if the description does not correctly describe
     * an image url
     * @see #valueToString
     */
    void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != '(')
	    throw new ParseException(reader, "'(' expected");
	if (reader.newToken() != STRING)
	    throw new ParseException(reader, "String expected");
	String address = reader.tokenString();
	address = address.substring(1, address.length() - 1);
	super.set(address);
	if (reader.newToken() != ',')
	    throw new ParseException(reader, "',' expected");
	if (reader.newToken() != STRING)
	    throw new ParseException(reader, "String expected");
	this.icon = reader.tokenString();
	this.icon = this.icon.substring(1, this.icon.length() - 1);
	if (reader.newToken() != ')')
	    throw new ParseException(reader, "')' expected");
    }
}
