/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Line.java'                                               *
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
 * A <b>line</b> is a linear, connected, non-bounded planar curve. The co-descriptor
 * of a line is the line itself; it has no boundary.
 * <p>
 * The <b>Line</b> class defines the characteristic individual for lines.
 * A line is represented as a geometry with a direction vector and a position
 * vector specifying the root of the line.
 * This characteristic individual accepts no parameters.
 * Forms of lines adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 */
public class Line extends Geometry {
    static {
	PrimitiveSort.register(Line.class, DiscreteForm.class, Parameter.NONE);
	new cassis.visit.vrml.Proto(Line.class, "icons/label.gif");
    }

    // representation
    private Vector direction, root;

    // constructors

    /**
     * Constructs a nondescript <b>Line</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * line. This line must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Line() {
	super();
	this.direction = this.root = NILVECTOR;
    }
    /**
     * Constructs a <b>Line</b> from two position vectors, for the specified
     * sort. The sort must allow for planes as individuals.
     * The two vectors specify two different points on the line.
     * @param sort a {@link cassis.sort.Sort} object
     * @param tail a vector specifying a position on the line
     * @param head a vector specifying a position on the line
     * @throws IllegalArgumentException if the two position vectors are equal
     * @see cassis.struct.Vector
     */
    public Line(Sort sort, Vector tail, Vector head) throws IllegalArgumentException {
	super(sort);

	if (tail.equals(head))
	    throw new IllegalArgumentException("Defining points must be different");

	this.direction = head.subtract(tail).normalize();
	this.root = tail.subtract(this.direction.scale(tail));
	if (root.isZero()) this.root = this.root.normalize();
    }
    /**
     * Constructs a <b>Line</b> from two, non-equal, points, for
     * the specified sort. The sort must allow for planes as individuals.
     * @param sort a {@link cassis.sort.Sort} object
     * @param tail a point on the line
     * @param head a point on the line
     * @throws IllegalArgumentException if the two points are equal
     * @see Point
     */
    public Line(Sort sort, Point tail, Point head) throws IllegalArgumentException {
	this(sort, tail.position(), head.position());
    }
    /**
     * Constructs a <b>Line</b> from a direction vector and a position
     * vector, for the specified sort. The sort must allow for planes as individuals.
     * The position vector specifies the root of the line.
     * @param sort a {@link cassis.sort.Sort} object
     * @param direction the direction vector of the line
     * @param root the root vector of the line
     * @see cassis.struct.Vector
     */
    Line(Sort sort, Vector direction, Vector root, boolean flag) {
	super(sort);
	this.direction = direction.normalize();
	this.root = root;
    }

    /**
     * Sets the line's value. Used by subclass constructors.
     * @param tail a vector specifying a position on the line
     * @param head a vector specifying a position on the line
     * @throws IllegalArgumentException if the two position vectors are equal
     * @see cassis.struct.Vector
     */
    void set(Vector tail, Vector head) throws IllegalArgumentException {
	if (tail.equals(head))
	    throw new IllegalArgumentException("Defining points must be different");

	if (this.direction == NILVECTOR) {
	    this.direction = head.subtract(tail).normalize();
	    this.root = tail.subtract(this.direction.scale(tail));
	    if (root.isZero()) this.root = this.root.normalize();
	}
    }

    // vectors access methods

    /**
     * Returns a vector specifying the <b>direction</b> of the line
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector direction() { return this.direction; }

    /**
     * Returns a vector specifying the <b>root</b> of the line. This is the
     * intersection point of the line and a perpendicular line through
     * the origin.
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector root() { return this.root; }

    /**
     * Checks if this individual equals <b>nil</b>, i.e., if
     * the direction vector equals the nil vector.
     * @return <tt>true</tt> if this line equals nil; <tt>false</tt> otherwise
     */
    public boolean nil() { return (this.direction == NILVECTOR); }

    // Individual interface methods

    /**
     * <b>Duplicates</b> this line. It returns a new individual with
     * the same specifications, defined for the base sort of this line's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Line(this.ofSort().base(), this.direction, this.root, true);
    }

    /**
     * Checks whether this line has <b>equal value</b> to another individual.
     * This condition applies if both lines have equal direction and root vectors.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a line
     */
    boolean equalValued(Individual other) {
	return (this.direction.equals(((Line) other).direction) &&
		this.root.equals(((Line) other).root));
    }

    /**
     * Compares this line to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a line.
     * Otherwise the result is defined by comparing the direction and root vectors
     * of both lines.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Line)) return FAILED;
	int c = this.direction.compare(((Line) other).direction);
	if (c != EQUAL) return c;
	return this.root.compare(((Line) other).root);
    }

    /**
     * Tests if this line is <b>parallel</b> to another line.
     * Two lines are parallel if these have equal direction vectors.
     * @param other a line
     * @return <tt>true</tt> if both lines are parallel; <tt>false</tt> otherwise
     */
    public boolean parallel(Line other) {
	return this.direction.equals(other.direction);
    }

    /**
     * Tests if this line is <b>coplanar</b> to another line. Two lines
     * are coplanar if the product of their direction vectors is
     * perpendicular to the direction specified by both root positions.
     * @param other a line
     * @return <tt>true</tt> if both lines are coplanar; <tt>false</tt> otherwise
     */
    public boolean coPlanar(Line other) {
	return this.direction.product(other.direction).dotProduct(this.root.subtract(other.root)).isZero();
    }

    /**
     * Tests if this line <b>intersects</b> another line.
     * Two lines intersect if these are coplanar and not parallel.
     * @param other a line
     * @return <tt>true</tt> if both lines intersect; <tt>false</tt> otherwise
     */
    public boolean intersect(Line other) {
	return (this.coPlanar(other) && !this.parallel(other));
    }

    /**
     * Tests if this line is <b>perpendicular</b> to another line.
     * Two lines are perpendicular if these have perpendicular direction vectors.
     * @param other a line
     * @return <tt>true</tt> if both lines are perpendicular; <tt>false</tt> otherwise
     */
    public boolean perpendicular(Line other) {
	return this.direction.perpendicular(other.direction);
    }

    /**
     * Converts the line's <b>value to a string</b>. The result
     * is a comma-separated list of the root position and a second position
     * vector, enclosed by angular (<>) brackets.
     * This string can be included in an SDL description and subsequently
     * parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil()) return NIL;
	return "<" + this.root.toString() + ", " + this.root.add(this.direction).toString() + ">";
    }

    /**
     * <b>Transforms</b> this line according to the specified transformation matrix.
     * The result is a new line defined for the base sort of this line's sort.
     * @param mat a transformation matrix
     * @return an {@link Individual} object
     * @see cassis.struct.Transform
     * @see cassis.sort.Sort#base
     */
    public Individual transform(Transform mat) {
	if (this.nil()) return (Individual) this.duplicate();
	Vector tail = mat.transform(this.root);
	Vector head = mat.transform(this.root.add(this.direction));
	return new Line(this.ofSort().base(), tail, head);
    }

    /**
     * Reads an SDL description of a line from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this line. This description consists of a
     * comma-separated list of two position vectors, enclosed by angular (<>) brackets.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a line
     * @see #toString
     */
    public void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != '<')
	    throw new ParseException(reader, "'<' expected");
	Vector tail = Vector.parse(reader);
	if (reader.newToken() != ',')
	    throw new ParseException(reader, "',' expected");
	Vector head = Vector.parse(reader);
	if (reader.newToken() != '>')
	    throw new ParseException(reader, "'>' expected");

	try {
	    set(tail, head);
	} catch (ArithmeticException e) {
	    throw new ParseException(reader, "arithmetic exception: " + e.getMessage());
	} catch (IllegalArgumentException e) {
	    throw new ParseException(reader, e.getMessage());
	}
    }

    /**
     * Determines the point of <b>intersection</b> of this line with another line.
     * This point is specified as a pair of rationals. Each rational specifies
     * the point wrt the parametric equation of the respective line.
     * @param other a line
     * @return an array of two rationals
     * @throws IllegalArgumentException if either plane equals nil
     * or both planes are parallel
     * @see cassis.struct.Rational
     */
    public Rational[] intersection(Line other) {
	Vector denom, diff, numer1, numer2;
	Rational result[];

	if (!this.coPlanar(other))
	    throw new IllegalArgumentException("Lines must be co-planar");

	denom = this.direction.product(other.direction);
	diff = other.root.subtract(this.root);
	numer1 = diff.product(other.direction);
	numer2 = diff.product(this.direction);
	result = new Rational[2];

	if (!denom.x().isZero()) {
	    result[0] = (new Rational(numer1.x(), denom.x()));
	    result[1] = (new Rational(numer2.x(), denom.x()));
	} else if (!denom.y().isZero()) {
	    result[0] = (new Rational(numer1.y(), denom.y()));
	    result[1] = (new Rational(numer2.y(), denom.y()));
	} else {
	    result[0] = (new Rational(numer1.z(), denom.z()));
	    result[1] = (new Rational(numer2.z(), denom.z()));
	}
	result[0] = result[0].multiply(numer1.w().divide(denom.w()));
	result[1] = result[1].multiply(numer2.w().divide(denom.w()));
	return result;
    }
}
