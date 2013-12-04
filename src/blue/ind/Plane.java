/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Plane.java'                                              *
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
 * The <tt>Plane</tt> class defines the characteristic individual for planes.
 * A plane is represented as a planar geometry. The co-descriptor of a plane is the plane itself; a plane has no boundary. <p>
 * Forms of planes adhere to a discrete behavior.<p> This characteristic individual accepts no parameters.
 * @see blue.form.DiscreteForm
 */
public class Plane extends Planar {
    static {
        PrimitiveSort.register(Plane.class, DiscreteForm.class, Parameter.NONE);
        proto(Plane.class, "icons/label.gif");
    }
    // constructors

    /**
     * Constructs a nondescript <tt>Plane</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * plane. This plane must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Plane() {
        super();
    }

    /**
     * Constructs a <tt>Plane</tt> from three vectors, for the specified sort.
     * The <em>position</em> vector specifies a position on the plane.
     * The other two vectors specify two direction vectors within the plane; these should not be parallel.
     * @param sort a sort
     * @param position a vector specifying a position on the plane
     * @param dir1 a vector specifying a direction within the plane
     * @param dir2 a vector specifying a direction within the plane
     * @exception IllegalArgumentException Occurs when the two direction vectors are parallel.
     */
    public Plane(Sort sort, Vector position, Vector dir1, Vector dir2) throws IllegalArgumentException {
        super(sort, position, dir1, dir2);
    }

    /**
     * Constructs a <tt>Planar</tt> from two vectors, for the specified sort.
     * The <em>normal</em> vector specifies the normal to the plane.
     * The <em>position</em> vector specifies a position on the plane.
     * @param sort a sort
     * @param normal a vector specifying the normal to the plane
     * @param position a vector specifying a position on the plane
     * @exception IllegalArgumentException Occurs when the normal vector is a zero vector.
     */
    public Plane(Sort sort, Vector normal, Vector position) throws IllegalArgumentException {
        super(sort, normal, position);
    }

    /**
     * Constructs a <tt>Plane</tt> from three, not all colinear, points, for
     * the specified sort. The sort must allow for planes as individuals.
     * @param sort a sort
     * @param pt1 a first point
     * @param pt2 a second point
     * @param pt3 a third point
     * @exception IllegalArgumentException Occurs when the three points are colinear.
     */
    public Plane(Sort sort, Point pt1, Point pt2, Point pt3) throws IllegalArgumentException {
        this(sort, pt1.position(), pt2.position().subtract(pt1.position()), pt3.position().subtract(pt1.position()));
    }

    /**
     * Constructs a <tt>Plane</tt> from a line and a non-colinear point, for
     * the specified sort. The sort must allow for planes as individuals.
     * @param sort a sort
     * @param line a line or line segment
     * @param point a point
     * @exception IllegalArgumentException Occurs when the point is colinear with the line.
     */
    public Plane(Sort sort, Line line, Point point) throws IllegalArgumentException {
        this(sort, line.root(), line.direction(), point.position().subtract(line.root()));
    }

    private Plane(Sort sort, Vector normal, Rational scalar) throws IllegalArgumentException {
        super(sort, normal, scalar);
    }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>Plane</tt> with
     * the same specifications, defined for the base sort of this plane's sort.
     * @return a duplicate plane
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Plane(this.ofSort().base(), this.normal(), this.scalar());
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Plane</tt> object. Otherwise the result is defined by comparing the normal vectors and scalars of both planes.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Plane)) return FAILED;
        return super.compare(other);
    }

    /**
     * Tests if this plane intersects the specified plane. Two planes intersect if these are not parallel.
     * @param other a plane
     * @return <tt>true</tt> if both planes intersect; <tt>false</tt> otherwise
     */
    public boolean intersect(Plane other) {
        return !super.parallel(other);
    }

    /**
     * Draws this plane wrt the specified graphics context.
     * @param g a graphics context
     */
    public void draw(GraphicsContext g) { }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a label node with this plane's SDL description as its text.
     * @param gc a VRML context
     * @see Planar#valueToString
     * @see GraphicsContext#label
     */
    void visualizeValue(GraphicsContext gc) {
        gc.label(super.valueToString());
    }

    /**
     * Transforms this plane according to the specified transformation matrix.
     * @param a transformation matrix
     * @return a new, transformed plane, defined for the base sort of this plane's sort
     * @see blue.sort.Sort#base
     */
    public Individual transform(Transform mat) {
        if (this.nil()) return (Individual)this.duplicate();
        Vector pos1 = mat.transform(this.root());
        Vector pos2 = mat.transform(this.root().add(this.u()));
        Vector pos3 = mat.transform(this.root().add(this.v()));
        return new Plane(this.ofSort().base(), pos1, pos2, pos3);
    }

    /**
     * Determines the line of intersection of two planes. This line is
     * specified as a pair of vectors, the first is the normal vector, the second the root vector of the line.
     * @param other a plane
     * @return an array of two vectors (normal vector, root vector)
     * @exception IllegalArgumentException Occurs when either plane equals nil or when both planes are parallel.
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
