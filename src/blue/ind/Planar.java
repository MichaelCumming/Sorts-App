/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Planar.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import blue.Thing;
import blue.struct.*;
import blue.io.*;
import blue.sort.Sort;

/**
 * The <tt>Planar</tt> class serves as a framework for planar characteristic
 * individuals. A planar geometry is represented by a normal vector and
 * a rational scalar specifying the root of the plane wrt the origin.
 * A planar geometry also specifies two orthogonal direction vectors of
 * a local coordinate system within the plane. Positions within this
 * local coordinate system are specified using rational couples.
 */
abstract class Planar extends Geometry {
    // constants

    /** A nil couple. */
    static final Couple NILCOUPLE = new Couple(Coord.ZERO, Coord.ZERO);

    /** The first unit couple of the local coordinate system. */
    static final Couple U = new Couple(Coord.ONE, Coord.ZERO);

    /** The second unit couple of the local coordinate system. */
    static final Couple V = new Couple(Coord.ZERO, Coord.ONE);
    // representation
    private Vector normal, root, u, v;
    private Rational scalar;
    // constructors

    /**
     * Constructs a nondescript <tt>Planar</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new subclass object.
     */
    Planar() {
        super();
        this.normal = this.root = this.u = this.v = NILVECTOR;
        this.scalar = NILRATIONAL;
    }

    /** Constructs a nondescript <tt>Planar</tt>. This constructor exists for the purpose of subclassing this class. */
    Planar(Sort sort) {
        super(sort);
        this.normal = this.root = this.u = this.v = NILVECTOR;
        this.scalar = NILRATIONAL;
    }

    /**
     * Constructs a <tt>Planar</tt> from three vectors. The <em>position</em> vector specifies a position on the plane.
     * The other two vectors specify two direction vectors within the plane; these should not be parallel.
     * @param sort a sort
     * @param position a vector specifying a position on the plane
     * @param dir1 a vector specifying a direction within the plane
     * @param dir2 a vector specifying a direction within the plane
     * @exception IllegalArgumentException Occurs when the two direction vectors are parallel.
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
     * Constructs a <tt>Planar</tt> from two vectors. The <em>normal</em> vector specifies the normal to the plane.
     * The <em>position</em> vector specifies a position on the plane.
     * @param sort a sort
     * @param normal a vector specifying the normal to the plane
     * @param position a vector specifying a position on the plane
     * @exception IllegalArgumentException Occurs when the normal vector is a zero vector.
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
     * Constructs a <tt>Planar</tt> from a vector and a scalar. The <em>normal</em> vector specifies the normal to the plane.
     * The scalar specifies the distance of the root of the plane from the origin, relative to the normal vector.
     * @param sort a sort
     * @param normal a vector specifying the normal to the plane
     * @param scalar a rational specifying the distance of the root of the
     * plane from the origin, relative to the normal vector
     * @exception IllegalArgumentException Occurs when the normal vector is a zero vector.
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
     * @exception IllegalArgumentException Occurs when the two direction vectors are parallel.
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
     * @exception IllegalArgumentException Occurs when the normal vector is a zero vector.
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
     * Returns a vector specifying the normal of the plane
     * @return a vector
     */
    Vector normal() { return this.normal; }

    /**
     * Returns a rational specifying the the distance of the root of the
     * plane from the origin, relative to the plane's normal vector.
     * @return a rational
     */
    Rational scalar() { return this.scalar; }

    /**
     * Returns a vector specifying the root of the plane
     * @return a vector
     */
    Vector root() { return this.root; }

    /**
     * Returns the first direction vector of the local coordinate system.
     * @return a vector
     */
    Vector u() { return this.u; }

    /**
     * Returns the second direction vector of the local coordinate system.
     * @return a vector
     */
    Vector v() { return this.v; }

    /**
     * Checks if this individual equals nil. A planar geometry equals nil when the normal vector equals the nil vector.
     * @return <tt>true</tt> if this planar equals nil; <tt>false</tt> otherwise
     */
    public boolean nil() { return (this.normal == NILVECTOR); }
    //  methods

    /**
     * Determines the position vector corresponding to the position in the
     * local coordinate system as specified by the couple of rationals.
     * @param coords a position in the local coordinate system
     * @return a position vector
     */
    Vector position(Couple coords) {
        return this.root.add(this.direction(coords));
    }

    /**
     * Determines the direction vector in the plane corresponding to the specified local coordinates.
     * @param coords a couple of rationals specifying local coordinates
     * @return a direction vector
     */
    Vector direction(Couple coords) {
        return this.u.scale(coords.u()).add(this.v.scale(coords.v()));
    }

    /**
     * Determines the local coordinates for the specified position vector, upon projection onto the plane.
     * @param point a position vector
     * @return a couple of rationals specifying local coordinates
     */
    Couple localize(Vector point) {
        Vector diff = point.subtract(this.root);
        return new Couple(this.u.scalar(diff), this.v.scalar(diff));
    }

    /**
     * Determines the scalars wrt the local coordinate system for the
     * specified direction vector, upon projection onto the plane.
     * @param point a direction vector
     * @return a couple of rationals specifying local coordinates
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
     */
    int compareAngle(Couple center, Couple first, Couple second) {
        return first.product(second).add(center.product(first.subtract(second))).sign();
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a planar. The result is <tt>true</tt> if and only if both planars have equal values.
     * @param other a planar geometry
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Planar</tt> object.
     */
    boolean equalValued(Individual other) {
        return (this.normal.equals(((Planar)other).normal) && this.scalar.equals(((Planar)other).scalar));
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Planar</tt> object. Otherwise the result is defined by comparing
     * the normal vectors and scalars of both planar geometries.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see Individual#EQUAL
     * @see Individual#LESS
     * @see Individual#GREATER
     * @see Individual#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Planar)) return FAILED;
        int c = this.normal.compare(((Planar)other).normal);
        if (c != EQUAL) return c;
        return this.scalar.compare(((Planar)other).scalar);
    }

    /**
     * Tests if this planar is parallel to the specified planar geometry.
     * Two planars are parallel if these have equal normal vectors.
     * @param other a planar geometry
     * @return <tt>true</tt> if both planars are parallel; <tt>false</tt> otherwise
     */
    boolean parallel(Planar other) {
        return this.normal.equals(other.normal);
    }

    /**
     * Tests if this planar is perpendicular to the specified planar geometry.
     * Two planars are perpendicular if these have perpendicular normal vectors.
     * @param other a planar geometry
     * @return <tt>true</tt> if both planars are perpendicular; <tt>false</tt> otherwise
     */
    boolean perpendicular(Planar other) {
        return this.normal.perpendicular(other.normal);
    }

    /**
     * Creates a string representation of this individual's value. The result is a rational scalar followed by a vector.
     * This string can be included in an SDL description and subsequently parsed to reveal the original value.
     * @return a string containing a scalar and vector
     * @see #parse
     */
    String valueToString() {
        if (this.nil()) return NIL;
        return this.scalar.toString() + this.normal.toString();
    }

    /**
     * Reads an SDL description of a circle from a <tt>ParseReader</tt> object and assigns the value to this circle.
     * @param reader a token reader
     * @exception ParseException Occurs when the description does not correctly describe a circle.
     */
    public void parse(ParseReader reader) throws ParseException {
        Rational scalar;
        if (reader.previewToken() == '(')
            scalar = new Rational(Coord.ONE);
        else
            scalar = Rational.parse(reader);
        Vector normal = Vector.parse(reader);
        try {
            set(normal, scalar);
        } catch (IllegalArgumentException e) {
            throw new ParseException(reader, e.getMessage());
        }
    }
}
