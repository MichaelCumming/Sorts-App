/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Edge.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.struct;

import cassis.Thing;

/**
 * 
 */
public class Edge implements Splayable {

    // constants
    
    private final static Vector MINUS_INF_Y = new Vector(Coord.ZERO, Coord.ONE.negate(), Coord.ZERO, Coord.ZERO);
    private final static Vector PLUS_INF_Y = new Vector(Coord.ZERO, Coord.ONE, Coord.ZERO, Coord.ZERO);
    private final static Vector PLUS_INF_X = new Vector(Coord.ONE, Coord.ZERO, Coord.ZERO, Coord.ZERO);

    /**
     * An edge representing the line at <b>minus infinity</b>.
     */
    public final static Edge MINUS_INF = new Edge(MINUS_INF_Y, PLUS_INF_X);
    /**
     * An edge representing the line at <b>plus infinity</b>.
     */
    public final static Edge PLUS_INF = new Edge(PLUS_INF_Y, PLUS_INF_X);

    // representation
    private Vector tail, head;
    private boolean membership, alignedWithOne, alignedWithOther;

    // constructors

    /**
     * Constructs an <b>Edge</b> from two position vectors.
     * The two vectors specify the endpoints of the edge.
     * @param tail a vector specifying an endpoint of the edge
     * @param head a vector specifying an endpoint of the edge
     * @throws IllegalArgumentException if the two position vectors are equal
     * @see cassis.struct.Vector
     */
    public Edge(Vector tail, Vector head) throws IllegalArgumentException {
	super();

	this.tail = tail;
	this.head = head;
	if (this.tail.compare(this.head) == GREATER) {
	    Vector t = this.tail;
	    this.tail = this.head;
	    this.head = t;
	}
        this.membership = false;
        this.alignedWithOne = false;
        this.alignedWithOther = false;
    }

    // access methods

    /**
     * Returns a vector specifying the edge's <b>tail</b> (start position).
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector tail() { return this.tail; }
    /**
     * Returns a vector specifying the edge's <b>head</b> (end position).
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector head() { return this.head; }
    
    /**
     * Returns the <b>direction</b> vector of this edge. This is the result
     * from subtracting the start position vector form the end position vector.
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector direction() { return this.head.subtract(this.tail); }

    /**
     * Returns the <b>length</b> of this edge.
     * @return a double
     */
    public double length() {
        Vector length = this.head.subtract(this.tail);
        return Math.sqrt(length.dotProduct(length).doubleValue());
    }

    // methods

    /**
     * Returns whether this edge is a <b>member of the other</b> boundary
     * or not.
     * @return a boolean value
     */
    public boolean memberOfOther() { return this.membership; }
    /**
     * Returns whether this edge is <b>aligned with</b> the inside of the
     * <b>one</b> boundary.
     * @return a boolean value
     */
    public boolean alignedWithOne() { return this.alignedWithOne; }
    /**
     * Returns whether this edge is <b>aligned with</b> the inside of the
     * <b>other</b> boundary.
     * @return a boolean value
     */
    public boolean alignedWithOther() { return this.alignedWithOther; }
    /**
     * <b>Sets</b> whether this edge is a <b>member of</b> one or the other
     * <b>boundary</b>.
     * @param boundary a boolean value
     */
    public void setMemberOf(boolean boundary) { this.membership = boundary; }
    /**
     * <b>Sets</b> whether this edge is <b>aligned with</b> the inside of the
     * <b>one</b> boundary.
     * @return a boolean value
     */
    public void setAlignedWithOne(boolean aligned) { this.alignedWithOne = aligned; }
    /**
     * <b>Sets</b> whether this edge is <b>aligned with</b> the inside of the
     * <b>other</b> boundary.
     * @return a boolean value
     */
    public void setAlignedWithOther(boolean aligned) { this.alignedWithOther = aligned; }

    /**
     * Checks whether this edge <b>equals</b> another object.
     * Two edges are equal if their respective start and end position vectors
     * are equal.
     * @param other the comparison object
     * @return a boolean value
     */
    public boolean equals(Object other) {
        return ((other instanceof Edge) &&
		this.tail.equals(((Edge) other).tail) &&
		this.head.equals(((Edge) other).head));
        }

    /**
     * <b>Compares</b> this edge to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not
     * an edge. Otherwise the result is defined by comparing the respective
     * end position vectors of both edges.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	int c;

	if (!(other instanceof Edge)) return FAILED;
        c = this.tail.compare(((Edge) other).tail);
	if (c != EQUAL) return c;
	return this.head.compare(((Edge) other).head);
    }

    /**
     * Tests whether this edge is strictly <b>less than</b> another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessThan(Thing other) {
	return (this.compare(other) == LESS);
    }
    /**
     * Tests whether this edge is strictly <b>greater than</b> another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterThan(Thing other) {
	return (this.compare(other) == GREATER);
    }
    /**
     * Tests whether this edge is strictly <b>less than or equal</b> to another
     * thing. Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessOrEqual(Thing other) {
	int c = this.compare(other);
	return ((c == LESS) || (c == EQUAL));
    }
    /**
     * Tests whether this edge is strictly <b>greater than or equal</b> to another
     * thing. Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterOrEqual(Thing other) {
	int c = this.compare(other);
	return ((c == GREATER) || (c == EQUAL));
    }

    /**
     * <b>Compares</b> this edge to another thing for the purpose of splaying.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not
     * an edge. Otherwise the result is defined by comparing the location of
     * one of the end position vectors of the other edge with respect to
     * this edge. This is achieved by determining the vector product of the
     * direction vector of this edge with the direction vector from this edge's
     * start position vector to an end position vector of the other edge,
     * and checking the sign of resulting vector. Two colinear edges are
     * compared to be equal.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int splayCompare(Splayable other) {
	int c;

        if (other instanceof Vector)
            return this.direction().product(((Vector) other).subtract(this.tail)).sign();
	if (!(other instanceof Edge)) return FAILED;
        c = this.direction().product(((Edge) other).tail.subtract(this.tail)).sign();
	if (c != EQUAL) return c;
	return this.direction().product(((Edge) other).head.subtract(this.tail)).sign();
    }
}
