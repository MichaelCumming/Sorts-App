/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `LineSegment.java'                                        *
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
import blue.form.IntervalForm;

/**
 * The <tt>LineSegment</tt> class defines the characteristic individual for
 * line segments. A line segment is represented as a line with two rational
 * scalars specifying the tail and head relative to the line's root.
 * The co-descriptor of a line segment is the line that carries this segment;
 * the two endpoints of the line segment form the boundary of this segment. <p>
 * Forms of line segments adhere to an interval behavior.<p> This characteristic individual accepts no parameters.
 * @see blue.form.IntervalForm
 */
public class LineSegment extends Line {
    static {
        PrimitiveSort.register(LineSegment.class, IntervalForm.class, Parameter.NONE);
        proto(LineSegment.class, "icons/label.gif");
    }

    // representation
    private Rational tail, head;
    // constructors

    /**
     * Constructs a nondescript <tt>LineSegment</tt>. This constructor exists
     * for the purpose of using the <tt>newInstance</tt> method to create a new
     * line segment. This line must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    LineSegment() {
        super();
        this.tail = this.head = NILRATIONAL;
    }

    /**
     * Constructs a <tt>LineSegment</tt> from two position vectors, for the
     * specified sort. The two vectors specify the endpoints of the segment.
     * @param sort a sort
     * @param tail a vector specifying an endpoint of the line segment
     * @param head a vector specifying an endpoint of the line segment
     * @exception IllegalArgumentException Occurs when the two position vectors are equal.
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
     * Constructs a <tt>LineSegment</tt> from two, non-equal, points, for
     * the specified sort. The sort must allow for planes as individuals.
     * The two points form the endpoints of the line segment.
     * @param sort a sort
     * @param tail an endpoint
     * @param head the other endpoint
     * @exception IllegalArgumentException Occurs when the two points are equal.
     */
    public LineSegment(Sort sort, Point tail, Point head) {
        this(sort, tail.position(), head.position());
    }

    private LineSegment(Sort sort, Vector direction, Vector root, Rational tail, Rational head) {
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
     * Returns a scalar specifying the segment's starting point wrt the parametric equation of the line segment.
     * @return a rational
     */
    public Rational tail() { return this.tail; }

    /**
     * Returns a scalar specifying the segment's ending point wrt the parametric equation of the line segment.
     * @return a rational
     */
    public Rational head() { return this.head; }

    /**
     * Returns a vector specifying the segment's starting point.
     * @return a vector
     */
    public Vector getTail() {
        return this.root().add(this.direction().scale(this.tail));
    }

    /**
     * Returns a vector specifying the segment's ending point.
     * @return a vector
     */
    public Vector getHead() {
        return this.root().add(this.direction().scale(this.head));
    }
    // methods

    /**
     * Duplicates this individual. It returns a new <tt>LineSegment</tt> with
     * the same specifications, defined for the base sort of this segment's sort.
     * @return a duplicate line segment
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new LineSegment(this.ofSort().base(), this.direction(), this.root(), this.tail, this.head);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a line segment. The result is <tt>true</tt> if and only if both line segments have equal values.
     * @param other a line segment
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>LineSegment</tt> object.
     */
    boolean equalValued(Individual other) {
        return (this.tail.equals(((LineSegment)other).tail) && this.head.equals(((LineSegment)other).head));
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>LineSegment</tt> object. Otherwise the result is defined by
     * comparing the co-descriptors and respective endpoints.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        int c;
        if (!(other instanceof LineSegment)) return FAILED;
        c = super.compare(other);
        if (c != EQUAL) return c;
        c = this.tail.compare(((LineSegment)other).tail);
        if (c != EQUAL) return c;
        return this.head.compare(((LineSegment)other).head);
    }

    /**
     * Tests if this individual contains the specified individual. A line
     * segment contains another line segment if these have the same
     * co-descriptor, and the first segment's boundary encloses the second segment.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>LineSegment</tt> class and the first segment contains the second; <tt>false</tt> otherwise
     */
    public boolean contains(Individual other) {
        return ((other instanceof LineSegment) && this.tail.lessOrEqual(((LineSegment)other).tail) &&
            this.head.greaterOrEqual(((LineSegment)other).head));
    }

    /**
     * Tests if this individual touches the specified individual. Two line
     * segments touch if these have the same co-descriptor and share an
     * endpoint that is not a starting, or ending, point to both segments.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>LineSegment</tt> class and both arcs touch; <tt>false</tt> otherwise
     */
    public boolean touches(Individual other) {
        return ((other instanceof LineSegment) && (this.tail.equals(((LineSegment)other).head) ||
            this.head.equals(((LineSegment)other).tail)));
    }

    /**
     * Tests if this individual is disjoint from the specified individual.
     * Two line segments are disjoint if these have the same co-descriptor and do not overlap, nor share an endpoint.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>LineSegment</tt> class and both segments are disjoint; <tt>false</tt> otherwise
     */
    public boolean disjoint(Individual other) {
        return ((other instanceof LineSegment) && (this.tail.greaterThan(((LineSegment)other).head) ||
            this.head.lessThan(((LineSegment)other).tail)));
    }

    /**
     * Tests if this individual aligns the specified individual. Two line
     * segments align if these have the same co-descriptor and share an
     * endpoint that is a starting, or ending, point to both segments.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the <tt>LineSegment</tt> class and both segments align;
     * <tt>false</tt> otherwise
     */
    public boolean aligns(Individual other) {
        return ((other instanceof LineSegment) && (this.tail.equals(((LineSegment)other).tail) ||
            this.head.equals(((LineSegment)other).head)));
    }

    /**
     * Combines this individual with the specified individual. Two line
     * segments combine if these have the same co-descriptor and are not
     * disjoint. The result is this segment with the minimum of both starting points and the maximum of both ending points.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>LineSegment</tt> class and both segments combine; <tt>false</tt> otherwise
     * @see Rational#minimum
     * @see Rational#maximum
     */
    public boolean combine(Individual other) {
        if (!(other instanceof LineSegment) || this.disjoint(other)) return false;
        this.tail = this.tail.minimum(((LineSegment)other).tail);
        this.head = this.head.maximum(((LineSegment)other).head);
        return true;
    }

    /**
     * Determines the common part of this individual with the specified
     * individual. Two line segments share a common part if these have the same
     * co-descriptor and are not disjoint, nor touch. The result is
     * this segment with the maximum of both starting points and the minimum of both ending points.
     * @param other an individual
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>LineSegment</tt> class and both segments share a common part; <tt>false</tt> otherwise
     * @see Rational#minimum
     * @see Rational#maximum
     */
    public boolean common(Individual other) {
        if (!(other instanceof LineSegment) || this.disjoint(other) || this.touches(other)) return false;
        this.tail = this.tail.maximum(((LineSegment)other).tail);
        this.head = this.head.minimum(((LineSegment)other).head);
        return true;
    }

    /**
     * Determines the complement part of this individual wrt the specified individual. ****
     * @param other an individual
     * @param result ****
     * @return <tt>true</tt> if the other individual is an instance of the
     * <tt>LineSegment</tt> class and both segments have the same co-descriptor; <tt>false</tt> otherwise
     */
    public boolean complement(Individual other, Individual result[]) {
        int index = 0;
        if (!(other instanceof LineSegment)) return false;
        result[0] = result[1] = null;
        if (this.tail.lessThan(((LineSegment)other).tail) && this.head.greaterThan(((LineSegment)other).tail)) {
            result[index] = (Individual)this.duplicate();
            ((LineSegment)result[index++]).head = ((LineSegment)other).tail;
        }
        if (this.tail.lessThan(((LineSegment)other).head) && this.head.greaterThan(((LineSegment)other).head)) {
            result[index] = (Individual)this.duplicate();
            ((LineSegment)result[index++]).tail = ((LineSegment)other).head;
        }
        return (index > 0);
    }

    /**
     * Draws this line segment wrt the specified graphics context.
     * @param g a graphics context
     * @see GraphicsContext#lineSegment
     */
    public void draw(GraphicsContext g) {
        g.lineSegment(this.getTail(), this.getHead());
    }

    /**
     * Creates a string representation of this individual's value. The result
     * is a comma-separated list of the endpoint vectors, enclosed by angular
     * (<>) brackets. This string can be included in an SDL description and subsequently parsed to reveal the original value.
     * @return a string containing an angular-bracketed vector list
     * @see #parse
     */
    public String valueToString() {
        if (this.nil()) return NIL;
        return "<" + this.getTail().toString() + ", " + this.getHead().toString() + ">";
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a label node with the segment's SDL description as its text.
     * @param gc a VRML context
     * @see #valueToString
     * @see GraphicsContext#label
     */
    void valueToVrml(GraphicsContext gc) {
        gc.label(this.valueToString());
    }

    /**
     * Transforms this line segment according to the specified transformation matrix.
     * @param a transformation matrix
     * @return a new, transformed line segment, defined for the base sort of this segment's sort
     * @see blue.sort.Sort#base
     */
    public Individual transform(Transform mat) {
        if (this.nil()) return (Individual)this.duplicate();
        Vector tail = mat.transform(this.getTail());
        Vector head = mat.transform(this.getHead());
        return new LineSegment(this.ofSort().base(), tail, head);
    }

    /**
     * Reads an SDL description of a line segment from a <tt>ParseReader</tt> object and assigns the value to this segment.
     * @param reader a token reader
     * @exception ParseException Occurs when the description does not correctly describe an line segment.
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
