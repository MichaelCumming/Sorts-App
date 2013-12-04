/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Point.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import blue.Thing;
import blue.Element;
import blue.struct.*;
import blue.io.*;
import blue.sort.Sort;
import blue.sort.PrimitiveSort;
import blue.form.DiscreteForm;

/**
 * The <tt>Point</tt> class defines the characteristic individual for points.
 * A point is represented as a geometry with a position vector and a nil flag.
 * The co-descriptor of a point is the point itself; a point has no boundary. <p>
 * Forms of points adhere to a discrete behavior.<p> This characteristic individual accepts no parameters.
 * @see blue.form.DiscreteForm
 */
public class Point extends Geometry {
    static {
        PrimitiveSort.register(Point.class, DiscreteForm.class, Parameter.NONE);
        String str = "PROTO Point [\n field SFVec3f mainTranslation -0.4 -0.8 -0.4\n field MFNode children [] ] {\n Transform { translation IS mainTranslation\n  children IS children } }\n\n";
        str += "PROTO PointShape [] {\n Shape {\n  appearance Appearance {\n   material Material {\n    diffuseColor 0.41 0.35 0.04 } }\n  geometry Sphere { radius 0.2 } } }\n\n";
        proto(Point.class, "icons/point.gif", str);
    }

    // constants
    private static final double PRECISION = 5040.0;
    // representation
    private Vector position;
    private boolean nil;
    // constructors

    /**
     * Constructs a nondescript <tt>Point</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * point. This point must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Point() {
        super();
        this.position = NILVECTOR;
        this.nil = true;
    }

    /**
     * Constructs a <tt>Point</tt> from a position vector, for the specified sort.
     * @param sort a sort
     * @param position a position vector
     */
    public Point(Sort sort, Vector position) {
        super(sort);
        this.position = position;
    }

    private Point(Sort sort, Coord x, Coord y, Coord z, Coord w) {
        this(sort, new Vector(x, y, z, w));
    }

    /**
     * Constructs a <tt>Point</tt> from four integral coordinates, for the
     * specified sort. The first three integers correspond to the X, Y, and Z coordinates, the last is a scalar.
     * @param sort a sort
     * @param x an integral X coordinate
     * @param y an integral Y coordinate
     * @param z an integral Z coordinate
     * @param w an integral scalar coordinate
     */
    public Point(Sort sort, long x, long y, long z, long w) {
        this(sort, new Coord(x), new Coord(y), new Coord(z), new Coord(w));
    }

    /**
     * Constructs a <tt>Point</tt> from three integral coordinates, for the
     * specified sort. These integers correspond to the X, Y, and Z coordinates.
     * @param sort a sort
     * @param x an integral X coordinate
     * @param y an integral Y coordinate
     * @param z an integral Z coordinate
     */
    public Point(Sort sort, long x, long y, long z) { this(sort, x, y, z, 1); }
    // public Point(Sort sort) { this(sort, 0, 0, 0, 1); }

    /**
     * Constructs a <tt>Point</tt> from three floating point coordinates,
     * for the specified sort. Each coordinate is converted to an integer value upon multiplying it with 5040.
     * @param sort a sort
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @param w an integral scalar coordinate
     */
    public Point(Sort sort, double x, double y, double z) {
        this(sort, (long)(x * PRECISION), (long)(y * PRECISION), (long)(z * PRECISION), (long)(PRECISION));
    }

    private Point(Sort sort, Vector position, boolean nil) {
        super(sort);
        this.position = position;
        this.nil = nil;
    }
    // access methods

    /**
     * Returns a vector specifying the position of the point
     * @return a vector
     */
    public Vector position() { return this.position; }

    /**
     * Returns a rational specifying the X coordinate of the point
     * @return a rational
     */
    public Rational getX() { return this.position.w().scale(this.position.x()); }

    /**
     * Returns a rational specifying the Y coordinate of the point
     * @return a rational
     */
    public Rational getY() { return this.position.w().scale(this.position.y()); }

    /**
     * Returns a rational specifying the Z coordinate of the point
     * @return a rational
     */
    public Rational getZ() { return this.position.w().scale(this.position.z()); }

    /**
     * Checks if this individual equals nil. A point is nil if the nil flag is on.
     * @return <tt>true</tt> if this point equals nil; <tt>false</tt> otherwise
     */
    public boolean nil() { return this.nil; }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>Point</tt> with
     * the same specifications, defined for the base sort of this line's sort.
     * @return a duplicate point
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Point(this.ofSort().base(), this.position, this.nil);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a point. The result is <tt>true</tt> if and only if both points have equal values.
     * @param other a point
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Point</tt> object.
     */
    boolean equalValued(Individual other) {
        if (this.nil) return (other.nil());
        return (this.position.equals(((Point)other).position));
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Point</tt> object. Otherwise the result is defined by comparing the position vectors of both points.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Point)) return FAILED;
        return this.position.compare(((Point)other).position);
    }

    /**
     * Draws this point wrt the specified graphics context.
     * @param g a graphics context
     * @see GraphicsContext#point
     */
    public void draw(GraphicsContext g) {
        if (!this.nil) g.point(this.position);
    }

    /**
     * Creates a string representation of this individual's value. The result is a string version of the position vector.
     * This string can be included in an SDL description and subsequently parsed to reveal the original value.
     * @return a vector string
     * @see #parse
     */
    String valueToString() {
        if (this.nil) return NIL;
        return this.position.toString();
    }

    private static String proto() {
        String str = "PROTO Point [\n";
        str += "  field SFVec3f mainTranslation -0.4 -0.8 -0.4\n";
        str += "  field MFNode children []\n]\n{\n";
        str += "  Transform { translation IS mainTranslation\n";
        str += "    children IS children\n  }\n}\n\n";
        str += "PROTO PointShape [\n]\n{\n";
        str += "  Shape {\n    appearance Appearance {\n";
        str += "      material Material {\n";
        str += "        diffuseColor 0.41 0.35 0.04\n      }\n    }\n";
        str += "    geometry Sphere { radius 0.2 }\n  }\n}\n\n";
        return str;
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a point node at the point's position.
     * @param gc a VRML context
     */
    void visualizeValue(GraphicsContext gc) {
        if (this.nil) gc.label(NIL);
        else
            gc.point(this.position);
    }

    /**
     * Transforms this point according to the specified transformation matrix.
     * @param a transformation matrix
     * @return a new, transformed point, defined for the base sort of this point's sort
     * @see blue.sort.Sort#base
     */
    public Individual transform(Transform mat) {
        if (this.nil) return (Individual)this.duplicate();
        return new Point(this.ofSort().base(), mat.transform(this.position));
    }

    /**
     * Reads an SDL description of a vector from a <tt>ParseReader</tt> object and assigns the value to this point.
     * @param reader a token reader
     * @exception ParseException Occurs when the description does not correctly describe a vector.
     * @see blue.struct.Vector#parse
     */
    void parse(ParseReader reader) throws ParseException {
        this.position = Vector.parse(reader);
    }
}
