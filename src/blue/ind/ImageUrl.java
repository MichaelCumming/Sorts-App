/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `ImageUrl.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import java.net.URL;
import java.net.MalformedURLException;
import blue.Thing;
import blue.Element;
import blue.struct.Parameter;
import blue.struct.Argument;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.PrimitiveSort;
import blue.form.DiscreteForm;

/**
 * The <tt>Url</tt> class defines the characteristic individual for image
 * URLs. An image url is defined as a url with an additional icon URL string. <p>
 * Forms of image urls adhere to a discrete behavior. This characteristic individual accepts no parameters.
 * @see blue.form.DiscreteForm
 */
public class ImageUrl extends Url {
    static {
        PrimitiveSort.register(ImageUrl.class, DiscreteForm.class, Parameter.NONE);
        proto(ImageUrl.class, "icons/image.gif", proto());
    }

    // representation
    private String icon;
    // constructors

    /**
     * Constructs a nondescript <tt>ImageUrl</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * image url. This url must subsequently be assigned a sort and a value.
     * @see Individual#parse
     * @see #parse
     */
    ImageUrl() {
        super();
        this.icon = NILADDRESS;
    }

    /**
     * Constructs a <tt>ImageUrl</tt> with the specified address and icon
     * address, for the specified sort. The sort must allow for image urls as individuals.
     * @param sort a sort
     * @param address a URL string
     * @param icon a URL string
     * @see Individual#Individual
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
     * Returns the url's icon address as a <tt>URL</tt> object.
     * @return a URL object
     * @exception MalformedURLException Occurs if the url's icon address is not a valid URL.
     */
    public URL getIconURL() throws MalformedURLException {
        return new URL(this.icon);
    }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>ImageUrl</tt> with
     * the same value, defined for the base sort of this image url's sort.
     * @return a duplicate image url
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new ImageUrl(this.ofSort().base(), this.address(), this.icon);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify an image url. The result is <tt>true</tt> if and only if both image urls have equal values.
     * @param other an image url
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not an <tt>ImageUrl</tt> object.
     */
    boolean equalValued(Individual other) {
        return (super.equalValued(other) && this.icon.equals(((ImageUrl)other).icon));
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * an <tt>ImageUrl</tt> object. Otherwise the result is defined by comparing both urls' addresses and icon addresses.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof ImageUrl)) return FAILED;
        int d = super.compare(other);
        if (d == 0) d = this.icon.compareTo(((ImageUrl)other).icon);
        return (d < 0) ? LESS : ((d == 0) ? EQUAL : GREATER);
    }

    /**
     * Creates a string representation of this individual's value. The result
     * is the a parenthesized list of the url's address and icon address,
     * both enclosed in double quotes, that can be included as is in an SDL
     * description and can be subsequently parsed to reveal the original value.
     * @return a string containing a parenthesized pair of quoted addresses
     * @see #parse
     */
    String valueToString() {
        return "(\"" + this.address() + "\", \"" + this.icon + "\")";
    }

    private static String proto() {
        String str = "PROTO ImageUrl [\n field SFVec3f mainTranslation  -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";
        str += "PROTO ImageUrlShape [\n field SFVec3f textTranslation 1 0.7 0.4\n field SFColor panelColor 0.5 0.5 0.5\n field SFColor textColor 0.32 0.4 0.698\n field MFString imageURL \"icons/default.gif\"\n";
        str += " field MFString linkURL \"\"\n field SFString linkDescription \"view the image\"\n field MFVec3f panelFrame [0 0 0, 2 0 0, 2 1.5 0,  0 1.5 0]\n field MFString textLabel \"\"\n field SFFloat textSize 0.5\n]\n{\n";
        str += " Anchor {\n  children Billboard {\n   children [\n    Shape {\n     appearance Appearance {\n      material Material {\n       ambientIntensity 0.25\n       diffuseColor IS panelColor\n";
        str += "       specularColor 0.0955906 0.0955906 0.0955906\n       emissiveColor 0 0 0\n       shininess 0.078125\n       transparency 0 }\n      texture ImageTexture {\n       url IS imageURL } }\n";
        str += "     geometry IndexedFaceSet {\n      coord Coordinate {\n       point IS panelFrame }\n      coordIndex [ 0, 1, 2, 3, -1]\n      solid FALSE\n } }\n";
        str += "    Transform { translation IS textTranslation\n     children Shape {\n      appearance Appearance {\n       material Material {\n        diffuseColor IS textColor } }\n";
        str += "      geometry Text {\n       string IS textLabel\n       fontStyle FontStyle {\n        size IS textSize\n        justify [\"MIDDLE\"] } } } } ]\n   axisOfRotation 0 1 0 }\n";
        str += "  parameter [ \"target=IMAGE_PAGE\" ]\n  url IS linkURL\n  description IS linkDescription } }\n\n";
        return str;
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a link node with
     * this url's address as its text and this url's icon as its image.
     * @param gc a VRML context
     * @see GraphicsContext#link
     */
    void valueToVrml(GraphicsContext gc) {
        gc.link(this.address(), this.address(), this.icon);
    }

    /**
     * Reads a series of tokens from a <tt>ParseReader</tt> object and assigns the value to this url. The tokens must define a
     * parenthesized pair of strings, where each string is unquoted.
     * @param reader a token reader
     * @exception ParseException Occurs when no acceptable series of tokens could be read.
     * @see #STRING
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
