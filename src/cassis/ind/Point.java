/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Point.java'                                              *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import cassis.Thing;
import cassis.Element;
import cassis.struct.*;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;
import cassis.form.DiscreteForm;

/**
 * A <b>point</b> is a 0-domensional geometric data entity.
 * The co-descriptor of a point is the point itself; a point has no boundary.
 * <p>
 * The <b>Point</b> class defines the characteristic individual for points.
 * A point is represented as a geometry with a position vector. An additional
 * <i>nil</i> flag specifies a nil value for a numeric label.
 * This characteristic individual accepts no parameters.
 * It specifies a <i>euclidean</i> {@link cassis.map.Mapping} as default.
 * Forms of points adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 */
public class Point extends Geometry {
    static {
        PrimitiveSort.register(Point.class, DiscreteForm.class, Parameter.NONE, cassis.map.Mapping.EUCLIDEAN);
        new cassis.visit.vrml.Proto(Point.class, "icons/point.gif");
    }
    
    // constants
    private static final double PRECISION = 5040.0;
    
    // representation
    private Vector position;
    private boolean nil;
    
    // constructors
    
    /**
     * Constructs a nondescript <b>Point</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * point. This point must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    public Point() {
        super();
        this.position = NILVECTOR;
        this.nil = true;
    }
    /**
     * Constructs a <b>Point</b> from a position vector, for the
     * specified sort. This sort must allow for labels as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param position a {@link cassis.struct.Vector} object
     */
    public Point(Sort sort, Vector position) {
        super(sort);
        this.position = position;
    }
    private Point(Sort sort, Coord x, Coord y, Coord z, Coord w) {
        this(sort, new Vector(x, y, z, w));
    }
    /**
     * Constructs a <b>Point</b> from four integral coordinates, for the specified
     * sort. The first three integers correspond to the X, Y, and Z coordinates,
     * the last is a scalar. The sort must allow for labels as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param x an integral X coordinate
     * @param y an integral Y coordinate
     * @param z an integral Z coordinate
     * @param w an integral scalar coordinate
     */
    public Point(Sort sort, long x, long y, long z, long w) {
        this(sort, new Coord(x), new Coord(y), new Coord(z), new Coord(w));
    }
    /**
     * Constructs a <b>Point</b> from three integral coordinates, for the
     * specified sort. These integers correspond to the X, Y, and Z
     * coordinates. The sort must allow for labels as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param x an integral X coordinate
     * @param y an integral Y coordinate
     * @param z an integral Z coordinate
     */
    public Point(Sort sort, long x, long y, long z) { this(sort, x, y, z, 1); }
    // public Point(Sort sort) { this(sort, 0, 0, 0, 1); }
    /**
     * Constructs a <b>Point</b> from three floating point coordinates,
     * for the specified sort. Each coordinate is converted to an integer value
     * upon multiplying it with 5040. The sort must allow for labels as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    public Point(Sort sort, double x, double y, double z) {
        this(sort, (long) (x * PRECISION), (long) (y * PRECISION),
                (long) (z * PRECISION), (long) (PRECISION));
    }
    private Point(Sort sort, Vector position, boolean nil) {
        super(sort);
        this.position = position;
        this.nil = nil;
    }
    
    // access methods
    
    /**
     * Returns a vector specifying the <b>position</b> of this point.
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector position() { return this.position; }
    
    /**
     * Returns a rational specifying the <b>X</b> coordinate of this point.
     * @return a {@link cassis.struct.Rational} object
     */
    public Rational getX() { return this.position.getX(); }
    /**
     * Returns a rational specifying the <b>Y</b> coordinate of this point.
     * @return a {@link cassis.struct.Rational} object
     */
    public Rational getY() { return this.position.getY(); }
    /**
     * Returns a rational specifying the <b>Z</b> coordinate of this point.
     * @return a {@link cassis.struct.Rational} object
     */
    public Rational getZ() { return this.position.getZ(); }
    
    /**
     * Checks if this point equals <b>nil</b>, i.e., if the nil flag is raised.
     * @return <tt>true</tt> if this point equals nil; <tt>false</tt> otherwise
     */
    public boolean nil() { return this.nil; }
    
    // methods
    
    /**
     * <b>Duplicates</b> this point. It returns a new individual with the same
     * sspecificationstring, defined for the base sort of this point's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
        return new Point(this.ofSort().base(), this.position, this.nil);
    }
    
    /**
     * Checks whether this point has <b>equal value</b> to another individual.
     * This condition applies if both points have equal positions.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a point
     */
    boolean equalValued(Individual other) {
        if (this.nil) return (other.nil());
        return (this.position.equals(((Point) other).position));
    }
    
    /**
     * Compares this point to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a point.
     * Otherwise the result is defined by comparing the positions of both points.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
        if (!(other instanceof Point)) return FAILED;
        return this.position.compare(((Point) other).position);
    }
    
    /**
     * Converts the point <b>to a string</b>. The result is a
     * string representation of the position vector.
     * This string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     * @see cassis.struct.Vector#toString()
     */
    public String toString(Individual assoc) {
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
     * <b>Transforms</b> this point according to the specified transformation matrix.
     * The result is a new point defined for the base sort of this point's sort.
     * @param mat a transformation matrix
     * @return an {@link Individual} object
     * @see cassis.struct.Transform
     * @see cassis.sort.Sort#base
     */
    public Individual transform(Transform mat) {
        if (this.nil) return (Individual) this.duplicate();
        return new Point(this.ofSort().base(), mat.transform(this.position));
    }
    
    /**
     * Reads an SDL description of a point from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this point. This description consists of a vector.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a point
     * @see #toString
     * @see cassis.struct.Vector#parse
     */
    public void parse(ParseReader reader) throws ParseException {
        this.position = Vector.parse(reader);
        this.nil = false;
    }
}
