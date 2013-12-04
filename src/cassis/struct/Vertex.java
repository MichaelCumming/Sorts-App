/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Vertex.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 10.10.04                                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.struct;

/**
 *
 */
public class Vertex implements Splayable {
    
    // constants
    /**
     *
     */
    public static int START = 0;
    /**
     *
     */
    public static int END = 1;
    /**
     *
     */
    public static int INTERSECTION = 2;

    // representation
    private Vector position;
    private Edge edge;
    private int type;

    // constructors

    /**
     * Creates a new instance of Vertex
     */
    public Vertex(Vector position, int type) {
        this.position = position;
        this.edge = null;
        this.type = type;
    }
    public Vertex(Vector position, Edge edge) {
        this.position = position;
        this.edge = edge;
        this.type = START;
    }
    
    // access methods

    /**
     * Returns the <b>position</b> vector of this vertex.
     * @return a {@link Vector} object
     */
    public Vector position() { return this.position; }

    /**
     * Returns the <b>type</b> of this vertex.
     * @return an integer value
     */
    public int type() { return this.type; }

    /**
     * Returns the <b>edge</b> this vertex belongs to.
     * @return an {@link Edge} object
     */
    public Edge edge() { return this.edge; }

    // methods

    /**
     * Checks whether this vertex <b>equals</b> another object.
     * Two vertices are equal if their respective position vectors are equal.
     * @param other the comparison object
     * @return a boolean value
     */
    public boolean equals(Object other) {
        return ((other instanceof Vertex) &&
		this.position.equals(((Vertex) other).position));
        }

    /**
     * <b>Compares</b> this vertex to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not
     * a vertex. Otherwise the result is defined by comparing the respective
     * position vectors.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(cassis.Thing other) {
	if (!(other instanceof Vertex)) return FAILED;
        return this.position.compare(other);
    }
    
    /**
     * Tests whether this vertex is strictly <b>less than</b> another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public boolean lessThan(cassis.Thing other) {
	if (!(other instanceof Vertex)) return false;
	return (this.position.compare(other) == LESS);
    }

    /**
     * Tests whether this vertex is strictly <b>greater than</b> another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public boolean greaterThan(cassis.Thing other) {
	if (!(other instanceof Vertex)) return false;
	return (this.position.compare(other) == GREATER);
    }
    
    /**
     * Tests whether this vertex is strictly <b>less than or equal</b> to
     * another thing. Relies on the {@link #compare} method to achieve this
     * result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public boolean lessOrEqual(cassis.Thing other) {
	if (!(other instanceof Vertex)) return false;
	int c = this.position.compare(other);
	return ((c == LESS) || (c == EQUAL));
    }
    
    /**
     * Tests whether this vertex is strictly <b>greater than or equal</b> to
     * another thing. Relies on the {@link #compare} method to achieve this
     * result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public boolean greaterOrEqual(cassis.Thing other) {
	if (!(other instanceof Vertex)) return false;
	int c = this.position.compare(other);
	return ((c == GREATER) || (c == EQUAL));
    }
    
    /**
     * <b>Compares</b> this thing with another thing for the purpose of splaying.
     * This method is identical to the {@link #compare} method.
     * @param other the comparison splayable (thing)
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER},
     * or {@link cassis.Thing#FAILED}
     */
    public int splayCompare(Splayable other) {
	if (!(other instanceof Vertex)) return FAILED;
        return this.position.compare(other);
    }
}
