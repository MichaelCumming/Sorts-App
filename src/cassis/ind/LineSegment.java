/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `LineSegment.java'                                        *
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
import cassis.form.IntervalForm;

/**
 * A <b>line-segment</b> is a connected and bounded segment of a line.
 * The line defines the co-descriptor of the segment, the boundary of the
 * segment is defined by the start and end positions of the segment.
 * <p>
 * The <b>LineSegment</b> class defines the characteristic individual for
 * line segments. A line segment is represented as a line with two rational
 * scalars specifying the tail and head relative to the line's root.
 * This characteristic individual accepts no parameters.
 * Forms of line segments adhere to an interval behavior.
 * @see cassis.form.IntervalForm
 */
public class LineSegment extends Line {
    static {
	PrimitiveSort.register(LineSegment.class, IntervalForm.class, Parameter.NONE);
	new cassis.visit.vrml.Proto(LineSegment.class, "icons/label.gif");
    }

    // representation
    private Rational tail, head;

    // constructors

    /**
     * Constructs a nondescript <b>LineSegment</b>. This constructor exists
     * for the purpose of using the <tt>newInstance</tt> method to create a new
     * line segment. This line segment must subsequently be assigned a sort and
     * value.
     * @see Individual#parse
     * @see #parse
     */
    public LineSegment() {
	super();
	this.tail = this.head = NILRATIONAL;
    }
    /**
     * Constructs a <b>LineSegment</b> from two position vectors, for the
     * specified sort.  The sort must allow for planes as individuals.
     * The two vectors specify the endpoints of the segment.
     * @param sort a {@link cassis.sort.Sort} object
     * @param tail a vector specifying an endpoint of the line segment
     * @param head a vector specifying an endpoint of the line segment
     * @throws IllegalArgumentException if the two position vectors are equal
     * @see cassis.struct.Vector
     */
    public LineSegment(Sort sort, Vector tail, Vector head) throws IllegalArgumentException {
	super(sort, tail, head);

	this.tail = this.direction().scalar(tail.subtract(this.root()));
	this.head = this.direction().scalar(head.subtract(this.root()));

	if (this.tail.compare(this.head) == GREATER) {
	    Rational t = this.tail;
	    this.tail = this.head;
	    this.head = t;
	}
    }
    /**
     * Constructs a <b>LineSegment</b> from two, non-equal, points, for
     * the specified sort. The sort must allow for planes as individuals.
     * The two points form the endpoints of the line segment.
     * @param sort a {@link cassis.sort.Sort} object
     * @param tail an endpoint
     * @param head the other endpoint
     * @throws IllegalArgumentException if the two points are equal
     * @see Point
     */
    public LineSegment(Sort sort, Point tail, Point head) {
	this(sort, tail.position(), head.position());
    }

    private LineSegment(Sort sort, Vector direction, Vector root,
			Rational tail, Rational head) {
	super(sort, direction, root, true);

	if (tail.compare(head) == GREATER) {
	    this.tail = head;
	    this.head = tail;
	} else {
	    this.tail = tail;
	    this.head = head;
	}
    }

    // access methods

    /**
     * Returns a scalar specifying the segment's <b>tail</b> (starting point) wrt
     * the parametric equation of the line segment.
     * @return a {@link cassis.struct.Rational} object
     */
    public Rational tail() { return this.tail; }
    /**
     * Returns a scalar specifying the segment's <b>head</b> (ending point) wrt
     * the parametric equation of the line segment.
     * @return a {@link cassis.struct.Rational} object
     */
    public Rational head() { return this.head; }

    /**
     * Returns a vector specifying the segment's <b>tail</b> (starting point).
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector getTail() {
	return this.root().add(this.direction().scale(this.tail));
    }
    /**
     * Returns a vector specifying the segment's <b>head</b> (ending point).
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector getHead() {
	return this.root().add(this.direction().scale(this.head));
    }

    /**
     * Returns the length of this segment.
     * @return a double
     */
    public double length() {
        Vector length = this.direction().scale(this.head.subtract(this.tail));
        return Math.sqrt(length.dotProduct(length).doubleValue());
    }

    // methods

    /**
     * <b>Duplicates</b> this line segment. It returns a new individual with
     * the same specifications, defined for the base sort of this line segment's
     * sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new LineSegment(this.ofSort().base(), this.direction(), this.root(), this.tail, this.head);
    }

    /**
     * Checks whether this line segment has <b>equal value</b> to another
     * individual. This condition applies if both line segments have equal
     * co-descriptors, and start and end positions.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a line segment
     */
    boolean equalValued(Individual other) {
	return (super.equalValued(other) &&
		this.tail.equals(((LineSegment) other).tail) &&
	        this.head.equals(((LineSegment) other).head));
    }

    /**
     * <b>Compares</b> this line segment to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a line
     * segment. Otherwise the result is defined by comparing the co-descriptors
     * and start and end positions of both line segments.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	int c;

	if (!(other instanceof LineSegment)) return FAILED;
	c = super.compare(other);
	if (c != EQUAL) return c;
	c = this.tail.compare(((LineSegment) other).tail);
	if (c != EQUAL) return c;
	return this.head.compare(((LineSegment) other).head);
    }

    /**
     * Tests if this line segment <b>contains</b> another individual. A line segment
     * contains another line segment if these have the same co-descriptor, and the
     * first segment's boundary encloses the second segment.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean contains(Individual other) {
	return ((other instanceof LineSegment) &&
		this.tail.lessOrEqual(((LineSegment) other).tail) &&
		this.head.greaterOrEqual(((LineSegment) other).head));
    }

    /**
     * Tests if this line segment <b>touches</b> another individual. Two line segments
     * touch if these have the same co-descriptor and share a boundary
     * position that is not a start, or, end position to both segments.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean touches(Individual other) {
	return ((other instanceof LineSegment) &&
		(this.tail.equals(((LineSegment) other).head) ||
		 this.head.equals(((LineSegment) other).tail)));
    }

    /**
     * Tests if this line segment is <b>disjoint</b> from another individual.
     * Two line segments are disjoint if these have the same co-descriptor and do not
     * overlap, nor share a boundary position.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean disjoint(Individual other) {
	return ((other instanceof LineSegment) &&
		(this.tail.greaterThan(((LineSegment) other).head) ||
		 this.head.lessThan(((LineSegment) other).tail)));
    }

    /**
     * Tests if this line segment <b>aligns</b> with another individual. Two line
     * segments align if these have the same co-descriptor and share a boundary
     * position that is a start, or, end position to both line segments.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean aligns(Individual other) {
	return ((other instanceof LineSegment) &&
		(this.tail.equals(((LineSegment) other).tail) ||
		 this.head.equals(((LineSegment) other).head)));
    }

    /**
     * <b>Combines</b> this line segment with another individual. Two line segments
     * combine if these have the same co-descriptor and are not disjoint. The result
     * is this line segment with the minimum of both start positions and the maximum
     * of both end positions.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     * @see #disjoint
     */
    public boolean combine(Individual other) {
	if (!(other instanceof LineSegment) ||
	    this.disjoint(other)) return false;

	this.tail = this.tail.minimum(((LineSegment) other).tail);
	this.head = this.head.maximum(((LineSegment) other).head);
	return true;
    }
    /**
     * Determines the <b>common</b> part of this line segment with another
     * individual. Two line segments share a common part if these have the same
     * co-descriptor and are not disjoint, nor touch. The result is
     * this line segment with the maximum of both start positions and the minimum of
     * both end positions.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     * @see #disjoint
     * @see #touches
     */
    public boolean common(Individual other) {
	if (!(other instanceof LineSegment) ||
	    this.disjoint(other) || this.touches(other)) return false;

	this.tail = this.tail.maximum(((LineSegment) other).tail);
	this.head = this.head.minimum(((LineSegment) other).head);
	return true;
    }
    /**
     * Determines the <b>complement</b> parts of this line segment wrt another
     * individual. A line segment has a complement wrt another line segment if both
     * segments have the same co-descriptor and are not disjoint, nor touch, nor
     * the second segment contains the first. The complement parts are constructed
     * and placed in the result argument's array.
     * @param other an {@link Individual} object
     * @param result an {@link Individual} with size 2 or greater
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean complement(Individual other, Individual result[]) {
	int index = 0;

	if (!(other instanceof LineSegment)) return false;

	result[0] = result[1] = null;
	if (this.tail.lessThan(((LineSegment) other).tail) &&
	    this.head.greaterThan(((LineSegment) other).tail)) {
	    result[index] = (Individual) this.duplicate();
	    ((LineSegment) result[index++]).head = ((LineSegment) other).tail;
	}
	if (this.tail.lessThan(((LineSegment) other).head) &&
	    this.head.greaterThan(((LineSegment) other).head)) {
	    result[index] = (Individual) this.duplicate();
	    ((LineSegment) result[index++]).tail = ((LineSegment) other).head;
	}
	return (index > 0);
    }

    /**
     * Converts the line segment <b>to a string</b>. The result is the pair of
     * endposition vectors, separated by comma's and enclosed by angular (<>)
     * brackets. This string can be included in an SDL description and
     * subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil()) return NIL;
	return "<" + this.getTail().toString() + ", " + this.getHead().toString() + ">";
    }

    /**
     * <b>Transforms</b> this line segment according to the specified transformation
     * matrix. The result is a new line segment defined for the base sort of this line
     * segment's sort.
     * @param mat a transformation matrix
     * @return an {@link Individual} object
     * @see cassis.struct.Transform
     * @see cassis.sort.Sort#base
     */
    public Individual transform(Transform mat) {
	if (this.nil()) return (Individual) this.duplicate();
	Vector tail = mat.transform(this.getTail());
	Vector head = mat.transform(this.getHead());
	return new LineSegment(this.ofSort().base(), tail, head);
    }

    /**
     * Reads an SDL description of a line segment from a {@link cassis.parse.ParseReader}
     * object and assigns the value to this segment. This description consists of
     * a list of the endposition vectors separated by comma's, that is enclosed by
     * angular (<>) brackets.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a line segment
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
	    super.set(tail, head);
	    this.tail = this.direction().scalar(tail.subtract(this.root()));
	    this.head = this.direction().scalar(head.subtract(this.root()));

	    if (this.tail.compare(this.head) == GREATER) {
		Rational t = this.tail;
		this.tail = this.head;
		this.head = t;
	    }
	} catch (ArithmeticException e) {
	    throw new ParseException(reader, "arithmetic exception: " + e.getMessage());
	} catch (IllegalArgumentException e) {
	    throw new ParseException(reader, e.getMessage());
	}
    }
}
