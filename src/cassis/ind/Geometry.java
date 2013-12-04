/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Geometry.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import cassis.struct.*;
import cassis.sort.Sort;

/**
 * A <b>geometry</b> is an abstract, geometric object in 3 dimensions, specified by
 * a carrier and boundary. The carrier has the same dimensionality as the geometric
 * object and it's representation is denoted the object's co-descriptor. The
 * boundary always has a lower dimensionality. Both carrier and boundary may
 * be considered geometric objects with their own characteristic individuals.
 * <p>
 * The <b>Geometry</b> class defines a framework for geometric characteristic
 * individuals. A geometry has no representation but specifies methods for
 * comparing co-descriptors and boundaries, as well as for transforming and
 * drawing geometries.
 */
public abstract class Geometry extends Individual {

    /**
     * A nil vector.
     */
    static final Vector NILVECTOR = new Vector(Coord.ZERO, Coord.ZERO, Coord.ZERO);
    /**
     * A nil rational.
     */
    static final Rational NILRATIONAL = new Rational(Coord.ZERO);

    // constructors

    /**
     * Constructs a nondescript <b>Geometry</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * subclass object.
     */
    Geometry() {
	super();
    }
    /**
     * Constructs a nondescript <b>Geometry</b>. This constructor exists for
     * the purpose of subclassing this class.
     * @param sort a {@link cassis.sort.Sort} object
     */
    Geometry(Sort sort) {
	super(sort);
    }

    // methods

    /**
     * Checks if this geometry <b>co-equals</b> another individual.
     * Co-equality requires the individual to be an instance of the same class
     * as the geometry and both geometries to have equal co-descriptors.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @see #equalValued
     */
    public boolean coEquals(Individual other) {
	return (this.getClass().isInstance(other) && this.equalValued(other));
    }

    /**
     * <b>Compares</b> this geometry to an individual based on co-descriptors.
     * @param other an {@link Individual} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     * @see #compare
     */
    public int coCompare(Individual other) {
	return this.compare(other);
    }

    /**
     * Tests if this geometry is <b>disjoint</b> from another individual.
     * Two geometries are disjoint if these are instances of the same class and
     * have different co-descriptors.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     * @see #equalValued
     */
    public boolean disjoint(Individual other) {
	return (this.getClass().isInstance(other) && !this.equalValued(other));
    }
    /**
     * Tests if this geometry <b>touches</b> another individual. Two geometries
     * touch if these are instances of the same class, have the same co-descriptor
     * and share boundary such that both geometries do not overlap at this boundary.
     * @param other an {@link Individual} object
     * @return <tt>false</tt>
     */
    public boolean touches(Individual other) {
	return false;
    }
    /**
     * Tests if this geometry <b>aligns</b> another individual. Two geometries
     * touch if these are instances of the same class, have the same co-descriptor
     * and share boundary such that both geometries do overlap at this boundary.
     * @param other an {@link Individual} object
     * @return <tt>false</tt>
     */
    public boolean aligns(Individual other) {
	return false;
    }

    /**
     * <b>Transforms</b> this geometry according to the specified transformation matrix.
     * @param mat a transformation matrix
     * @return an {@link Individual} object
     * @see cassis.struct.Transform
     */
    public abstract Individual transform(Transform mat);
}
