/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Planar.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import cassis.Thing;
import cassis.struct.*;
import cassis.parse.*;
import cassis.sort.Sort;

/**
 * A <b>planar</b> geometry is an abstract, planar, geometric object.
 * <p>
 * The <b>Planar</b> class serves as a framework for planar characteristic
 * individuals. A planar geometry is represented by a normal vector and
 * a rational scalar specifying the root of the plane wrt the origin.
 * A planar geometry also specifies two orthogonal direction vectors of
 * a local coordinate system within the plane. Positions within this
 * local coordinate system are specified using rational couples.
 */
abstract class Planar extends Geometry {

    // constants

    /**
     * A nil couple.
     */
    static final Couple NILCOUPLE = new Couple(Coord.ZERO, Coord.ZERO);
    /**
     * The first unit couple of the local coordinate system.
     */
    static final Couple U = new Couple(Coord.ONE, Coord.ZERO);
    /**
     * The second unit couple of the local coordinate system.
     */
    static final Couple V = new Couple(Coord.ZERO, Coord.ONE);

    // representation
    private Vector normal, root, u, v;
    private Rational scalar;

    // constructors

    /**
     * Constructs a nondescript <b>Planar</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * subclass object.
     */
    Planar() {
	super();
	this.normal = this.root = this.u = this.v = NILVECTOR;
	this.scalar = NILRATIONAL;
    }
    /**
     * Constructs a nondescript <b>Planar</b>. This constructor exists for
     * the purpose of subclassing this class.
     * @param sort a {@link cassis.sort.Sort} object
     */
    Planar(Sort sort) {
	super(sort);
	this.normal = this.root = this.u = this.v = NILVECTOR;
	this.scalar = NILRATIONAL;
    }
    /**
     * Constructs a <b>Planar</b> from three vectors, for the specified sort.
     * The <em>position</em> vector specifies a position on the plane.
     * The other two vectors specify two direction vectors within the plane;
     * these should not be parallel.
     * @param sort a {@link cassis.sort.Sort} object
     * @param position a vector specifying a position on the plane
     * @param dir1 a vector specifying a direction within the plane
     * @param dir2 a vector specifying a direction within the plane
     * @throws IllegalArgumentException if the two direction vectors are parallel
     * @see cassis.struct.Vector
     */
    Planar(Sort sort, Vector position, Vector dir1, Vector dir2) throws IllegalArgumentException {
	super(sort);

	if (dir1.parallel(dir2))
	    throw new IllegalArgumentException("Degenerate planar");

	this.normal = dir1.product(dir2).normalize();
	this.scalar = this.normal.scalar(position);
	this.root = this.normal.scale(this.scalar);
	axes();
    }
    /**
     * Constructs a <tt>Planar</tt> from two vectors, for the specified sort.
     * The <em>normal</em> vector specifies the normal to the plane.
     * The <em>position</em> vector specifies a position on the plane.
     * @param sort a {@link cassis.sort.Sort} object
     * @param normal a vector specifying the normal to the plane
     * @param position a vector specifying a position on the plane
     * @throws IllegalArgumentException if the normal vector is a zero vector
     * @see cassis.struct.Vector
     */
    Planar(Sort sort, Vector normal, Vector position) throws IllegalArgumentException {
	super(sort);
	if (this.normal.isZero())
	    throw new IllegalArgumentException("Normal vector must be non-zero");
	this.normal = normal.normalize();
	this.scalar = this.normal.scalar(position);
	this.root = this.normal.scale(this.scalar);
	axes();
    }
    /**
     * Constructs a <tt>Planar</tt> from a vector and a scalar, for the specified sort.
     * The <em>normal</em> vector specifies the normal to the plane.
     * The scalar specifies the distance of the root of the plane from the
     * origin, relative to the normal vector.
     * @param sort a {@link cassis.sort.Sort} object
     * @param normal a vector specifying the normal to the plane
     * @param scalar a rational specifying the distance of the root of the
     * plane from the origin, relative to the normal vector
     * @throws IllegalArgumentException if the normal vector is a zero vector
     * @see cassis.struct.Vector
     * @see cassis.struct.Rational
     */
    Planar(Sort sort, Vector normal, Rational scalar) throws IllegalArgumentException {
	super(sort);
	if (this.normal.isZero())
	    throw new IllegalArgumentException("Normal vector must be non-zero");
	this.normal = normal;
	this.scalar = this.normal.scalar(normal).multiply(scalar);
	this.root = this.normal.scale(this.scalar);
	axes();
    }

    /**
     * Sets the planar's value. Used by subclass constructors.
     * @param position a vector specifying a position on the plane
     * @param dir1 a vector specifying a direction within the plane
     * @param dir2 a vector specifying a direction within the plane
     * @throws IllegalArgumentException if the two direction vectors are parallel
     * @see cassis.struct.Vector
     */
    void set(Vector position, Vector dir1, Vector dir2) throws IllegalArgumentException {
	if (dir1.parallel(dir2))
	    throw new IllegalArgumentException("Degenerate planar");

	if (this.normal == null) {
	    this.normal = dir1.product(dir2).normalize();
	    this.scalar = this.normal.scalar(position);
	    this.root = this.normal.scale(this.scalar);
	    axes();
	}
    }
    /**
     * Sets the planar's value. Used by subclass constructors.
     * @param normal a vector specifying the normal to the plane
     * @param scalar a rational specifying the distance of the root of the
     * plane from the origin, relative to the normal vector
     * @throws IllegalArgumentException if the normal vector is a zero vector
     * @see cassis.struct.Vector
     * @see cassis.struct.Rational
     */
    void set(Vector normal, Rational scalar) throws IllegalArgumentException {
	if (this.normal.isZero())
	    throw new IllegalArgumentException("Normal vector must be non-zero");
	if (this.normal == null) {
	    this.normal = normal;
	    this.scalar = this.normal.scalar(normal).multiply(scalar);
	    this.root = this.normal.scale(this.scalar);
	    axes();
	}
    }

    private void axes() {
	if (this.root.parallel(Vector.Z)) {
	    this.u = Vector.X;
	    this.v = Vector.Y;
	} else {
	    this.u = this.normal.product(Vector.Z).normalize();
	    this.v = this.normal.product(this.u).normalize();
	}
    }

    // access methods

    /**
     * Returns a vector specifying the <b>normal</b> of the plane
     * @return a (@link cassis.struct.Vector} object
     */
    Vector normal() { return this.normal; }
    /**
     * Returns a rational <b>scalar</b> specifying the the distance of the root of
     * the plane from the origin, relative to the plane's normal vector.
     * @return a (@link cassis.struct.Rational} object
     */
    Rational scalar() { return this.scalar; }
    /**
     * Returns a vector specifying the <b>root</b> of the plane
     * @return a (@link cassis.struct.Vector} object
     */
    Vector root() { return this.root; }

    /**
     * Returns the first direction vector <b>u</b> of the local coordinate system.
     * @return a (@link cassis.struct.Vector} object
     */
    Vector u() { return this.u; }
    /**
     * Returns the second direction vector <b>v</b> of the local coordinate system.
     * @return a (@link cassis.struct.Vector} object
     */
    Vector v() { return this.v; }

    /**
     * Checks if this individual equals nil. A planar geometry equals nil when
     * the normal vector equals the nil vector.
     * @return <tt>true</tt> if this planar equals nil; <tt>false</tt> otherwise
     */
    public boolean nil() { return (this.normal == NILVECTOR); }

    //  methods

    /**
     * Determines the <b>position</b> vector for the specified local coordinates.
     * @param coords a (@link cassis.struct.Couple} of rationals specifying local
     * coordinates
     * @return a (@link cassis.struct.Vector} object
     */
    Vector position(Couple coords) {
	return this.root.add(this.direction(coords));
    }
    /**
     * Determines the <b>direction</b> vector in the plane corresponding to the
     * specified local coordinates.
     * @param coords a (@link cassis.struct.Couple} of rationals specifying local
     * coordinates
     * @return a (@link cassis.struct.Vector} object
     */
    Vector direction(Couple coords) {
	return this.u.scale(coords.u()).add(this.v.scale(coords.v()));
    }

    /**
     * Determines the <b>local</b> coordinates corresponding to a position vector,
     * upon projection onto the plane.
     * @param point a (@link cassis.struct.Vector} object
     * @return a (@link cassis.struct.Couple} of rationals specifying local coordinates
     */
    Couple localize(Vector point) {
	Vector diff = point.subtract(this.root);
	return new Couple(this.u.scalar(diff), this.v.scalar(diff));
    }
    /**
     * Determines the <b>scalars</b> wrt the local coordinate system corresponding to
     * a direction vector, upon projection onto the plane.
     * @param direction a (@link cassis.struct.Vector} object
     * @return a (@link cassis.struct.Couple} of rationals specifying local coordinates
     */
    Couple scalars(Vector direction) {
	return new Couple(this.u.scalar(direction), this.v.scalar(direction));
    }

    /**
     * Determines the sign of the angle within the plane, about the center
     * position, between the first and the second position, in that order.
     * All positions are specified wrt the local coordinate system.
     * @param center the center position in local coordinates
     * @param first the first position in local coordinates
     * @param second the second position in local coordinates
     * @return <tt>1</tt>, <tt>0</tt>, or <tt>-1</tt>, depending on the sign
     * of the angle between both points, about the center.
     * @see cassis.struct.Couple
     */
    int compareAngle(Couple center, Couple first, Couple second) {
	return first.product(second).add(center.product(first.subtract(second))).sign();
    }

    /**
     * Checks whether this planar has <b>equal value</b> to another individual.
     * This condition applies if both planars have equal normal vectors and scalars.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a label
     */
    boolean equalValued(Individual other) {
	return (this.normal.equals(((Planar) other).normal) &&
	        this.scalar.equals(((Planar) other).scalar));
    }

    /**
     * Compares this planar to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a planar.
     * Otherwise the result is defined by comparing the snormal vectors and scalars
     * of both planar geometries.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Planar)) return FAILED;
	int c = this.normal.compare(((Planar) other).normal);
	if (c != EQUAL) return c;
	return this.scalar.compare(((Planar) other).scalar);
    }

    /**
     * Tests if this planar is <b>parallel</b> to another planar geometry.
     * Two planars are parallel if these have equal normal vectors.
     * @param other a planar geometry
     * @return <tt>true</tt> if both planars are parallel; <tt>false</tt> otherwise
     */
    boolean parallel(Planar other) {
	return this.normal.equals(other.normal);
    }

    /**
     * Tests if this planar is <b>perpendicular</b> to another planar geometry.
     * Two planars are perpendicular if these have perpendicular normal vectors.
     * @param other a planar geometry
     * @return <tt>true</tt> if both planars are perpendicular; <tt>false</tt> otherwise
     */
    boolean perpendicular(Planar other) {
	return this.normal.perpendicular(other.normal);
    }

    /**
     * Converts the planar's <b>value to a string</b>. The result
     * is a rational scalar followed by a vector.
     * This string can be included in an SDL description
     * and subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil()) return NIL;
	return this.scalar.toString() + this.normal.toString();
    }

    /**
     * Reads an SDL description of a planar from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this planar. This description consists of 
     * a rational scalar followed by a vector.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a planar
     * @see #toString
     */
    public void parse(ParseReader reader) throws ParseException {
	Rational scalar;
	if (reader.previewToken() == '(')
	    scalar = new Rational(Coord.ONE);
	else scalar = Rational.parse(reader);
	Vector normal = Vector.parse(reader);

	try {
	    set(normal, scalar);
	} catch (IllegalArgumentException e) {
	    throw new ParseException(reader, e.getMessage());
	}
    }
}
