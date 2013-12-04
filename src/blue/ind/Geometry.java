/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Geometry.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.ind;

import blue.struct.*;
import blue.sort.Sort;
import blue.io.GraphicsContext;

/**
 * The <tt>Geometry</tt> class serves as a framework for geometric
 * characteristic individuals. A geometry is generally specified by a carrier
 * and a boundary. The carrier has the same dimensionality as the geometric
 * object and it's representation is denoted the object's co-descriptor. The
 * boundary always has a lower dimensionality. Both carrier and boundary may
 * be considered geometric objects with their own characteristic individuals. <p>
 * The <tt>Geometry</tt> class specifies a number of additional methods for
 * comparing co-descriptors and boundaries, as well as for transforming and drawing geometries.
 */
public abstract class Geometry extends Individual {
    /** A nil vector. */
    static final Vector NILVECTOR = new Vector(Coord.ZERO, Coord.ZERO, Coord.ZERO);

    /** A nil rational. */
    static final Rational NILRATIONAL = new Rational(Coord.ZERO);
    // constructors

    /**
     * Constructs a nondescript <tt>Geometry</tt>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new subclass object.
     */
    Geometry() {
        super();
    }

    /** Constructs a nondescript <tt>Geometry</tt>. This constructor exists for the purpose of subclassing this class. */
    Geometry(Sort sort) {
        super(sort);
    }
    // methods

    /**
     * Compares this individual to the specified individual for co-equality.
     * Co-equality requires the argument to be an instance of the same class
     * as this geometry and both geometries to have equal co-descriptors.
     * @param other an individual
     * @return <tt>true</tt> if both individuals are co-equal; <tt>false</tt> otherwise
     * @see #equalValued
     */
    public boolean coEquals(Individual other) {
        return (this.getClass().isInstance(other) && this.equalValued(other));
    }

    /**
     * Compares this individual to the specified individual based on the co-descriptors of these geometries.
     * @param other an individual
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see #compare
     */
    public int coCompare(Individual other) {
        return this.compare(other);
    }

    /**
     * Tests if this individual is disjoint from the specified individual.
     * Two geometries are disjoint if the argument is an instance of the same
     * class as this geometry and these have non-equal co-descriptors.
     * @param other an individual
     * @return <tt>true</tt> if both individual are disjoint; <tt>false</tt> otherwise
     * @see #equalValued
     */
    public boolean disjoint(Individual other) {
        return (this.getClass().isInstance(other) && !this.equalValued(other));
    }

    /**
     * Tests if this individual touches the specified individual. Two
     * geometries of the same class touch if these have the same co-descriptor
     * and share boundary such that both geometries do not overlap at this
     * boundary. This default implementation always return <tt>false</tt>.
     * @param other an individual
     * @return <tt>false</tt>
     */
    public boolean touches(Individual other) {
        return false;
    }

    /**
     * Tests if this individual aligns the specified individual. Two geometries
     * of the same class align if these have the same co-descriptor and share
     * boundary such that both geometries do overlap at this boundary.
     * This default implementation always return <tt>false</tt>.
     * @param other an individual
     * @return <tt>false</tt>
     */
    public boolean aligns(Individual other) {
        return false;
    }

    /**
     * Transforms this geometry according to the specified transformation matrix.
     * @param a transformation matrix
     * @return a new, transformed individual, defined for the base sort of this individual's sort
     * @see blue.sort.Sort#base
     */
    public abstract Individual transform(Transform mat);
}
