/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Plane.java'                                              *
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
 * A <b>plane</b> is a connected, non-bounded planar geometry. The co-descriptor
 * of a plane is the plane itself; it has no boundary.
 * <p>
 * The <tt>Plane</tt> class defines the characteristic individual for planes.
 * A plane is represented as a planar geometry.
 * This characteristic individual accepts no parameters.
 * Forms of planes adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 */
public class Plane extends Planar {
    static {
	PrimitiveSort.register(Plane.class, DiscreteForm.class, Parameter.NONE);
	new cassis.visit.vrml.Proto(Plane.class, "icons/label.gif");
    }

    // constructors

    /**
     * Constructs a nondescript <b>Plane</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * plane. This plane must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Plane() {
	super();
    }
    /**
     * Constructs a <b>Plane</b> from three vectors, for the specified sort.
     * This sort must allow for planes as individuals.
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
    public Plane(Sort sort, Vector position, Vector dir1, Vector dir2) throws IllegalArgumentException {
	super(sort, position, dir1, dir2);
    }
    /**
     * Constructs a <tt>Planar</tt> from two vectors, for the specified sort.
     * This sort must allow for planes as individuals.
     * The <em>normal</em> vector specifies the normal to the plane.
     * The <em>position</em> vector specifies a position on the plane.
     * @param sort a {@link cassis.sort.Sort} object
     * @param normal a vector specifying the normal to the plane
     * @param position a vector specifying a position on the plane
     * @throws IllegalArgumentException if the normal vector is a zero vector
     * @see cassis.struct.Vector
     */
    public Plane(Sort sort, Vector normal, Vector position) throws IllegalArgumentException {
	super(sort, normal, position);
    }
    /**
     * Constructs a <tt>Plane</tt> from three, not all colinear, points, for
     * the specified sort. The sort must allow for planes as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param pt1 a first point
     * @param pt2 a second point
     * @param pt3 a third point
     * @throws IllegalArgumentException if the three points are colinear
     * @see Point
     */
    public Plane(Sort sort, Point pt1, Point pt2, Point pt3) throws IllegalArgumentException {
	this(sort, pt1.position(), pt2.position().subtract(pt1.position()), pt3.position().subtract(pt1.position()));
    }
    /**
     * Constructs a <tt>Plane</tt> from a line and a non-colinear point, for
     * the specified sort. The sort must allow for planes as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param line a {@link Line} object
     * @param point a {@link Point} object
     * @throws IllegalArgumentException if the point is colinear with the line
     */
    public Plane(Sort sort, Line line, Point point) throws IllegalArgumentException {
	this(sort, line.root(), line.direction(), point.position().subtract(line.root()));
    }
    Plane(Sort sort, Vector normal, Rational scalar) throws IllegalArgumentException {
	super(sort, normal, scalar);
    }

    // methods

    /**
     * <b>Duplicates</b> this plane. It returns a new individual with
     * the same specifications, defined for the base sort of this plane's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Plane(this.ofSort().base(), this.normal(), this.scalar());
    }

    /**
     * Compares this plane to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a plane.
     * Otherwise the result is defined by comparing both planar geometries.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     * @see Planar#compare
     */
    public int compare(Thing other) {
	if (!(other instanceof Plane)) return FAILED;
	return super.compare(other);
    }

    /**
     * Tests if this plane <b>intersects</b> another plane.
     * Two planes intersect if these are not parallel.
     * @param other a plane
     * @return <tt>true</tt> if both planes intersect; <tt>false</tt> otherwise
     */
    public boolean intersect(Plane other) {
	return !super.parallel(other);
    }

    /**
     * <b>Transforms</b> this plane according to the specified transformation matrix.
     * The result is a new plane defined for the base sort of this plane's sort.
     * @param mat a transformation matrix
     * @return an {@link Individual} object
     * @see cassis.struct.Transform
     * @see cassis.sort.Sort#base
     */
    public Individual transform(Transform mat) {
	if (this.nil()) return (Individual) this.duplicate();
	Vector pos1 = mat.transform(this.root());
	Vector pos2 = mat.transform(this.root().add(this.u()));
	Vector pos3 = mat.transform(this.root().add(this.v()));
	return new Plane(this.ofSort().base(), pos1, pos2, pos3);
    }

    /**
     * Determines the line of <b>intersection</b> of two planes. This line is
     * specified as a pair of vectors, the first is the normal vector,
     * the second the root vector of the line.
     * @param other a plane
     * @return an array of two vectors (normal vector, root vector)
     * @throws IllegalArgumentException if either plane equals nil
     * or both planes are parallel
     * @see cassis.struct.Vector
     */
    public Vector[] intersection(Plane other) throws IllegalArgumentException {
	Rational factor;
	Vector prod, diff;
	Vector result[];

	if (super.nil() || other.nil())
	    throw new IllegalArgumentException("NIL is not a valid plane");
	if (super.parallel(other))
	    throw new IllegalArgumentException("Planes may not be parallel");

	prod = this.normal().product(other.normal());
	diff = this.root().subtract(other.root());
	result = new Vector[2];

	diff = diff.product(prod.product(other.normal()));
	if (!prod.x().isZero())
	    factor = new Rational(diff.x(), prod.x());
	else if (!prod.y().isZero())
	    factor = new Rational(diff.y(), prod.y());
	else
	    factor = new Rational(diff.z(), prod.z());
	factor = factor.multiply(diff.w().divide(prod.w()));

	result[0] = prod.normalize();
	result[1] = this.root().add(prod.product(this.normal()).scale(this.scalar()));
	return result;
    }
}
