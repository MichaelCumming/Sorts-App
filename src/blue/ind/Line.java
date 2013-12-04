/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Line.java'                                               *
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
 * The <tt>Line</tt> class defines the characteristic individual for lines.
 * A line is represented as a geometry with a direction vector and a position
 * vector specifying the root of the line. The co-descriptor of a line is the line itself; a line has no boundary. <p>
 * Forms of lines adhere to a discrete behavior.<p> This characteristic individual accepts no parameters.
 * @see blue.form.DiscreteForm
 */
public class Line extends Geometry {
    static {
        PrimitiveSort.register(Line.class, DiscreteForm.class, Parameter.NONE);
        proto(Line.class, "icons/label.gif");
    }

    // representation
    private Vector direction, root;
    // constructors

    /**
     * Constructs a nondescript <tt>Line</tt>. This constructor exists for
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
     * Constructs a <tt>Line</tt> from two position vectors, for the specified
     * sort. The two vectors specify two different points on the line.
     * @param sort a sort
     * @param tail a vector specifying a position on the line
     * @param head a vector specifying a position on the line
     * @exception IllegalArgumentException Occurs when the two position vectors are equal.
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
     * Constructs a <tt>Line</tt> from two, non-equal, points, for
     * the specified sort. The sort must allow for planes as individuals.
     * @param sort a sort
     * @param tail a first point
     * @param head a second point
     * @exception IllegalArgumentException Occurs when the two points are equal.
     */
    public Line(Sort sort, Point tail, Point head) throws IllegalArgumentException {
        this(sort, tail.position(), head.position());
    }

    /**
     * Constructs a <tt>Line</tt> from a direction vector and a position
     * vector, for the specified sort. The position vector specifies the root of the line.
     * @param sort a sort
     * @param direction the direction vector of the line
     * @param root the root vector of the line
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
     * @exception IllegalArgumentException Occurs when the two position vectors are equal.
     */
    void set(Vector tail, Vector head) throws IllegalArgumentException {
        if (tail.equals(head))
            throw new IllegalArgumentException("Defining points must be different");
        if (this.direction == null) {
            this.direction = head.subtract(tail).normalize();
            this.root = tail.subtract(this.direction.scale(tail));
            if (root.isZero()) this.root = this.root.normalize();
        }
    }
    // vectors access methods

    /**
     * Returns a vector specifying the direction of the line
     * @return a vector
     */
    public Vector direction() { return this.direction; }

    /**
     * Returns a vector specifying the root of the line. This is the
     * intersection point of the line and a perpendicular line through the origin.
     * @return a vector
     */
    public Vector root() { return this.root; }

    /**
     * Checks if this individual equals nil. A line equals nil when the direction vector equals the nil vector.
     * @return <tt>true</tt> if this line equals nil; <tt>false</tt> otherwise
     */
    public boolean nil() { return (this.direction == NILVECTOR); }
    // Individual interface methods

    /**
     * Duplicates this individual. It returns a new <tt>Line</tt> with
     * the same specifications, defined for the base sort of this line's sort.
     * @return a duplicate line
     * @see blue.sort.Sort#base
     */
    public Element duplicate() {
        return new Line(this.ofSort().base(), this.direction, this.root, true);
    }

    /**
     * Compares this individual to the specified individual. The argument is
     * assumed to specify a line. The result is <tt>true</tt> if and only if both lines have equal values.
     * @param other a line
     * @return <tt>true</tt> if both individuals have the same value; <tt>false</tt> otherwise.
     * @exception ClassCastException Occurs when the argument is not a <tt>Line</tt> object.
     */
    boolean equalValued(Individual other) {
        return (this.direction.equals(((Line)other).direction) && this.root.equals(((Line)other).root));
    }

    /**
     * Compares this thing to the specified thing. The result is one of
     * <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>.
     * The result equals <tt>FAILED</tt> if and only if the argument is not
     * a <tt>Line</tt> object. Otherwise the result is defined by comparing the direction and root vectors of both lines.
     * @param other a thing
     * @return an integer value equal to one of <tt>EQUAL</tt>, <tt>LESS</tt>, <tt>GREATER</tt>, or <tt>FAILED</tt>
     * @see blue.Thing#EQUAL
     * @see blue.Thing#LESS
     * @see blue.Thing#GREATER
     * @see blue.Thing#FAILED
     */
    public int compare(Thing other) {
        if (!(other instanceof Line)) return FAILED;
        int c = this.direction.compare(((Line)other).direction);
        if (c != EQUAL) return c;
        return this.root.compare(((Line)other).root);
    }

    /**
     * Tests if this line is parallel to the specified line. Two lines are parallel if these have equal direction vectors.
     * @param other a line
     * @return <tt>true</tt> if both lines are parallel; <tt>false</tt> otherwise
     */
    public boolean parallel(Line other) {
        return this.direction.equals(other.direction);
    }

    /**
     * Tests if this line is coplanar wrt the specified line. Two lines
     * are coplanar if these the product of both direction vectors is perpendicular to the line between both root positions.
     * @param other a line
     * @return <tt>true</tt> if both lines are coplanar; <tt>false</tt> otherwise
     */
    public boolean coPlanar(Line other) {
        return this.direction.product(other.direction).dotProduct(this.root.subtract(other.root)).isZero();
    }

    /**
     * Tests if this line intersects the specified line. Two lines intersect if these are coplanar and not parallel.
     * @param other a line
     * @return <tt>true</tt> if both lines intersect; <tt>false</tt> otherwise
     */
    public boolean intersect(Line other) {
        return (this.coPlanar(other) && !this.parallel(other));
    }

    /**
     * Tests if this line is perpendicular to the specified line.
     * Two lines are perpendicular if these have perpendicular direction vectors.
     * @param other a line
     * @return <tt>true</tt> if both lines are perpendicular; <tt>false</tt> otherwise
     */
    public boolean perpendicular(Line other) {
        return this.direction.perpendicular(other.direction);
    }

    /**
     * Draws this line wrt the specified graphics context.
     * @param g a graphics context
     */
    public void draw(GraphicsContext g) { }

    /**
     * Creates a string representation of this individual's value. The result
     * is a comma-separated list of the root position and a second position vector, enclosed by angular (<>) brackets.
     * This string can be included in an SDL description and subsequently parsed to reveal the original value.
     * @return a string containing an angular-bracketed vector list
     * @see #parse
     */
    public String valueToString() {
        if (this.nil()) return NIL;
        return "<" + this.root.toString() + ", " + this.root.add(this.direction).toString() + ">";
    }

    /**
     * Builds a VRML description of this individual's value within the
     * specified context. This description consists of a label node with the line's SDL description as its text.
     * @param gc a VRML context
     * @see #valueToString
     * @see GraphicsContext#label
     */
    void visualizeValue(GraphicsContext gc) {
        gc.label(this.valueToString());
    }

    /**
     * Transforms this line according to the specified transformation matrix.
     * @param a transformation matrix
     * @return a new, transformed line, defined for the base sort of this line's sort
     * @see blue.sort.Sort#base
     */
    public Individual transform(Transform mat) {
        if (this.nil()) return (Individual)this.duplicate();
        Vector tail = mat.transform(this.root);
        Vector head = mat.transform(this.root.add(this.direction));
        return new Line(this.ofSort().base(), tail, head);
    }

    /**
     * Reads an SDL description of a line from a <tt>ParseReader</tt> object and assigns the value to this line.
     * @param reader a token reader
     * @exception ParseException Occurs when the description does not correctly describe a line.
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
     * Determines the point of intersection of two lines. This line is
     * specified as a pair of rationals. Each rational defines the point wrt the parametric equation of the respective line.
     * @param other a line
     * @return an array of two rationals
     * @exception IllegalArgumentException Occurs when either plane equals nil or when both planes are parallel.
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
