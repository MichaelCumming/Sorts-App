/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Arc.java'                                                *
 * written by: Rudi Stouffs                                  *
 * last modified: 08.2.02                                    *
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
 * A circular <b>arc</b> is a connected and bounded segment of a circle.
 * The circle defines the co-descriptor of the arc, the boundary of the arc is
 * defined by the start and end directions of the segment. <p> The <b>Arc</b> class defines the characteristic individual for
 * circular arcs. An arc is represented as a {@link Circle} with additionally a start and end
 * direction. This characteristic individual accepts no parameters. Forms of arcs adhere to an interval behavior.
 * @see blue.form.IntervalForm
 */
public final class Arc extends Circle {
    static {
        PrimitiveSort.register(Arc.class, IntervalForm.class, Parameter.NONE);
        proto(Arc.class, "icons/label.gif");
    }

    // representation
    private Couple start, end;
    // constructors

    /**
     * Constructs a nondescript <b>Arc</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * arc. This arc must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Arc() {
        super();
        this.start = this.end = NILCOUPLE;
    }

    private Arc(Sort sort, Vector normal, Rational scalar, Couple center, Couple pin, Couple start, Couple end) {
        super(sort, normal, scalar, center, pin);
        this.start = start;
        this.end = end;
    }

    /**
     * Constructs an <tt>Arc</tt> from five, not all parallel, vectors, for the
     * specified sort. This sort must allow for arcs as individuals.
     * The <em>center</em> vector specifies the center position of the arc.
     * The <em>pin</em> vector specifies a position on the circle to which
     * the arc belongs. The <em>dir</em> vector specifies another point on the
     * plane to which the circle belongs. The <em>start</em> and <em>end</em>
     * vectors define, relative to the center, the arc's start and end
     * directions. If the <em>pin</em> and <em>dir</em> vectors are parallel,
     * either the <em>start</em> or <em>end</em> vector is used to define the
     * plane. The arc is assigned to belong to the specified sort.
     * @param sort a {@link blue.sort.Sort} object
     * @param center a vector specifying the center of the arc
     * @param pin a vector specifying a position on the arc's circle
     * @param dir a vector specifying a third position on the arc's plane
     * @param start a vector defining the start of the arc
     * @param end a vector defining the end of the arc
     * @throws IllegalArgumentException if all five argument vectors are parallel
     * @see Individual#Individual(blue.sort.Sort)
     * @see blue.struct.Vector
     */
    public Arc(Sort sort, Vector center, Vector pin, Vector dir, Vector start, Vector end) throws IllegalArgumentException {
        super(sort);
        if (dir.parallel(pin)) {
            if (!start.parallel(pin))
                dir = start;
            else if (!end.parallel(pin))
                dir = end;
            else
                throw new IllegalArgumentException("Degenerate arc");
        }
        super.set(center, pin, dir);
        this.start = this.scalars(center.add(start));
        this.end = this.scalars(center.add(end));
    }

    /**
     * Constructs an <tt>Arc</tt> with the specified center, start, and end
     * points, for the specified sort. This sort must allow for arcs as individuals.
     * The start point also defines the <em>pin</em> vector, while the end vector also defines the <em>dir</em> vector.
     * @param sort a {@link blue.sort.Sort} object
     * @param center a point defining the center of the arc
     * @param start a point defining the pin position and start direction of the arc
     * @param end a point defining the dir position and end direction of the arc
     * @throws IllegalArgumentException if all three argument points are colinear
     * @see #Arc(blue.sort.Sort, blue.struct.Vector, blue.struct.Vector,
     * blue.struct.Vector, blue.struct.Vector, blue.struct.Vector)
     * @see Point
     */
    public Arc(Sort sort, Point center, Point start, Point end) throws IllegalArgumentException {
        this(sort, center.position(), start.position().subtract(center.position()), end.position().subtract(center.position()),
            start.position().subtract(center.position()), end.position().subtract(center.position()));
    }

    /**
     * Constructs an <tt>Arc</tt> with the specified center, pin, start, and
     * end points, for the specified sort. The sort must allow for arcs as
     * individuals. The start point also defines the <em>dir</em> vector.
     * @param sort a {@link blue.sort.Sort} object
     * @param center a point defining the center of the arc
     * @param pin a point defining a position on the arc's circle
     * @param start a point defining the dir position and start direction of the arc
     * @param end a point defining the end direction of the arc
     * @throws IllegalArgumentException if all four argument points are colinear
     * @see #Arc(blue.sort.Sort, blue.struct.Vector, blue.struct.Vector,
     * blue.struct.Vector, blue.struct.Vector, blue.struct.Vector)
     * @see Point
     */
    public Arc(Sort sort, Point center, Point pin, Point start, Point end) throws IllegalArgumentException {
        this(sort, center.position(), pin.position().subtract(center.position()), start.position().subtract(center.position()),
            start.position().subtract(center.position()), end.position().subtract(center.position()));
    }

    /**
     * Constructs an <tt>Arc</tt> with the specified center, pin, assist,
     * start, and end points, for the specified sort. The sort must allow for arcs as individuals.
     * @param sort a {@link blue.sort.Sort} object
     * @param center a point defining the center of the arc
     * @param pin a point defining a position on the arc's circle
     * @param assist a vector specifying a third position on the arc's plane
     * @param start a point defining the dir position and start direction of the arc
     * @param end a point defining the end direction of the arc
     * @throws IllegalArgumentException if all five argument points are colinear
     * @see #Arc(blue.sort.Sort, blue.struct.Vector, blue.struct.Vector,
     * blue.struct.Vector, blue.struct.Vector, blue.struct.Vector)
     * @see Point
     */
    public Arc(Sort sort, Point center, Point pin, Point assist, Point start, Point end) throws IllegalArgumentException {
        this(sort, center.position(), pin.position().subtract(center.position()), assist.position().subtract(center.position()),
            start.position().subtract(center.position()), end.position().subtract(center.position()));
    }
    // access methods

    /**
     * Returns a vector specifying the arc's <b>start</b> direction.
     * @return a {@link blue.struct.Vector} object
     */
    public Vector getStart() { return this.position(this.start); }

    /**
     * Returns a vector specifying the arc's <b>end</b> direction.
     * @return a {@link blue.struct.Vector} object
     */
    public Vector getEnd() { return this.position(this.end); }

    /**
     * Returns a couple of rationals specifying the arc's <b>start</b> direction
     * with respect to the plane's local coordinate system.
     * @return a {@link blue.struct.Couple} of rationals
     */
    Couple start() { return this.start; }

    /**
     * Returns a couple of rationals specifying the arc's <b>end</b> direction
     * with respect to the plane's local coordinate system.
     * @return a {@link blue.struct.Couple} of rationals
     */
    Couple end() { return this.end; }
    // methods

    /**
     * <b>Duplicates</b> this arc. It returns a new individual with
     * the same specifications, defined for the base sort of this arc's sort.
     * @return an {@link blue.Element} object
     * @see blue.sort.Sort#base()
     */
    public Element duplicate() {
        return new Arc(this.ofSort().base(), this.normal(), this.scalar(), this.center(), this.pin(), this.start, this.end);
    }

    /**
     * Checks whether this arc has <b>equal value</b> to another individual.
     * This condition applies if both arcs have equal co-descriptors, and start and end directions.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not an arc
     */
    boolean equalValued(Individual other) {
        return (super.equalValued(other) && this.start.equals(((Arc)other).start) && this.end.equals(((Arc)other).end));
    }

    /**
     * <b>Compares</b> this arc to another thing. The result equals {@link blue.Thing#FAILED} if the argument is not arc.
     * Otherwise the result is defined by comparing both arcs' co-descriptors, and their start and end directions.
     * @param other a {@link blue.Thing} object
     * @return an integer value equal to one of {@link blue.Thing#EQUAL},
     * {@link blue.Thing#LESS}, {@link blue.Thing#GREATER}, or {@link blue.Thing#FAILED}
     */
    public int compare(Thing other) {
        if (!(other instanceof Arc)) return FAILED;
        int c = super.compare(other);
        if (c != EQUAL) return c;
        c = this.compareAbsAngle(this.start, ((Arc)other).start);
        if (c != EQUAL) return c;
        return this.compareRelAngle(this.start, this.end, ((Arc)other).end);
    }

    private int compareAngle(Couple first, Couple second) {
        return super.compareAngle(this.center(), first, second);
    }

    private int compareAbsAngle(Couple first, Couple second) {
        return compareRelAngle(this.center().add(U), first, second);
    }

    private int compareRelAngle(Couple ref, Couple first, Couple second) {
        int a = this.compareAngle(ref, first);
        int b = this.compareAngle(ref, second);
        if (a == b)
            return this.compareAngle(first, second);
        if (a == EQUAL) return GREATER;
        if (b == EQUAL) return LESS;
        return a;
    }

    /**
     * Tests if this arc <b>contains</b> another individual. An arc
     * contains another arc if these have the same co-descriptor, and the first arc's boundary encloses the second arc.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean contains(Individual other) {
        return ((other instanceof Arc) && super.equalValued(other) &&
            (this.compareRelAngle(this.start, ((Arc)other).start, this.end) == LESS) &&
            (this.compareRelAngle(((Arc)other).start, ((Arc)other).end, this.end) != GREATER));
    }

    /**
     * Tests if this arc <b>touches</b> another individual. Two arcs
     * touch if these have the same co-descriptor and share a boundary
     * direction that is not a start, or, end direction to both arcs.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean touches(Individual other) {
        return ((other instanceof Arc) && super.equalValued(other) &&
            ((this.compareAngle(this.start, ((Arc)other).end) == EQUAL) ||
            (this.compareAngle(this.end, ((Arc)other).start) == EQUAL)));
    }

    /**
     * Tests if this arc is <b>disjoint</b> from another individual.
     * Two arcs are disjoint if these have the same co-descriptor and do not overlap, nor share a boundary direction.
     */
    public boolean disjoint(Individual other) {
        return ((other instanceof Arc) && super.equalValued(other) &&
            (this.compareRelAngle(this.start, ((Arc)other).start, this.end) == GREATER) &&
            (this.compareRelAngle(((Arc)other).start, this.start, ((Arc)other).end) == GREATER));
    }

    /**
     * Tests if this arc <b>aligns</b> with another individual. Two arcs
     * align if these have the same co-descriptor and share a boundary
     * direction that is a start, or, end direction to both arcs.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean aligns(Individual other) {
        return ((other instanceof Arc) && super.equalValued(other) &&
            ((this.compareAngle(this.start, ((Arc)other).start) == EQUAL) ||
            (this.compareAngle(this.end, ((Arc)other).end) == EQUAL)));
    }

    /**
     * <b>Combines</b> this arc with another individual. Two arcs combine
     * if these have the same co-descriptor and are not disjoint. The result is
     * this arc with the minimum of both start directions and the maximum of both end directions.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     * @see #disjoint
     */
    public boolean combine(Individual other) {
        if (!(other instanceof Arc) || !super.equalValued(other) || this.disjoint(other)) return false;
        // **** this does not take into account the cyclic nature of directions
        this.start = this.start.minimum(((Arc)other).start);
        this.end = this.end.maximum(((Arc)other).end);
        return true;
    }

    /**
     * Determines the <b>common</b> part of this arc with another
     * individual. Two arcs share a common part if these have the same
     * co-descriptor and are not disjoint, nor touch. The result is
     * this arc with the maximum of both start directions and the minimum of both end directions.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     * @see #disjoint
     * @see #touches
     */
    public boolean common(Individual other) {
        if (!(other instanceof Arc) || !super.equalValued(other) || this.disjoint(other) || this.touches(other)) return false;
        // **** this does not take into account the cyclic nature of directions
        this.start = this.start.maximum(((Arc)other).start);
        this.end = this.end.minimum(((Arc)other).end);
        return true;
    }

    /**
     * Determines the <b>complement</b> parts of this arc wrt another individual.
     * An arc has a complement wrt another arc if both arcs have the same
     * co-descriptor and are not disjoint, nor touch, nor the second arc contains
     * the first. The complement parts are constructed and placed in the result argument's array.
     * @param other an {@link Individual} object
     * @param result an {@link Individual} with size 2 or greater
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean complement(Individual other, Individual result[]) {
        int index = 0;
        if (!(other instanceof Arc) || !super.equalValued(other)) return false;
        result[0] = result[1] = null;
        if (this.start.lessThan(((Arc)other).start) && this.end.greaterThan(((Arc)other).start)) {
            result[index] = (Individual)this.duplicate();
            ((Arc)result[index++]).end = ((Arc)other).start;
        }
        if (this.start.lessThan(((Arc)other).end) && this.end.greaterThan(((Arc)other).end)) {
            result[index] = (Individual)this.duplicate();
            ((Arc)result[index++]).start = ((Arc)other).end;
        }
        return (index > 0);
    }

    /**
     * Converts the arc's <b>value to a string</b>. The result is a comma-separated list of the center, pin, and third position
     * vectors, followed by another comma-separated list of the start and end
     * position vectors. Both lists are enclosed by angular (<>) brackets.
     * This string can be included in an SDL description and subsequently parsed to reveal the original value.
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String valueToString() {
        if (this.nil()) return NIL;
        String result = "<" + this.getCenter().toString() + "," + this.getPin().toString();
        if (this.end.subtract(this.start).parallel(this.pin().subtract(this.center())))
            result += "," + this.normal().product(this.direction(this.pin())).normalize().add(this.getCenter()).toString();
        return result + "> <" + this.position(this.start).toString() + "," + this.position(this.end).toString() + ">";
    }

    /**
     * <b>Transforms</b> this arc according to the specified transformation matrix.
     * The result is a new arc defined for the base sort of this arc's sort.
     * @param a transformation matrix
     * @return an {@link Individual} object
     * @see blue.struct.Transform
     * @see blue.sort.Sort#base
     */
    public Individual transform(Transform mat) {
        if (this.nil()) return (Individual)this.duplicate();
        Vector center = this.getCenter();
        Vector pin = this.getPin();
        Vector start = this.getStart();
        Vector end = this.getEnd();
        Vector assist = this.root();
        if (center.colinear(pin, assist)) {
            assist = this.position(U);
            if (center.colinear(pin, assist))
                assist = this.position(V);
        }
        center = mat.transform(center);
        pin = mat.transform(pin);
        assist = mat.transform(assist);
        start = mat.transform(start);
        end = mat.transform(end);
        return new Arc(this.ofSort().base(), center, pin.subtract(center), assist.subtract(center), start.subtract(center), end.subtract(center));
    }

    /**
     * Reads an SDL description of an arc from a {@link blue.io.ParseReader} object
     * and assigns the value to this arc. This description consists of two lists,
     * each enclosed by angular (<>) brackets. The first contains the center, pin,
     * and third position vectors, separated by comma's, the second the start and end
     * position vectors, also separated by comma's.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe an arc
     * @see #valueToString
     */
    public void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != '<')
            throw new ParseException(reader, "'<' expected");
        Vector center = Vector.parse(reader);
        if (reader.newToken() != ',')
            throw new ParseException(reader, "',' expected");
        Vector pin = Vector.parse(reader);
        Vector assist = null;
        if (reader.newToken() == ',') {
            assist = Vector.parse(reader);
            reader.newToken();
        }
        if (reader.token() != '>')
            throw new ParseException(reader, "'>' expected");
        if (reader.newToken() != '<')
            throw new ParseException(reader, "'<' expected");
        Vector start = Vector.parse(reader);
        if (reader.newToken() != ',')
            throw new ParseException(reader, "',' expected");
        Vector end = Vector.parse(reader);
        if (reader.token() != '>')
            throw new ParseException(reader, "'>' expected");
        try {
            pin = pin.subtract(center);
            if (assist != null)
                super.set(center, pin, assist.subtract(center));
            else if (!pin.parallel(start.subtract(center)))
                super.set(center, pin, start.subtract(center));
            else if (!pin.parallel(end.subtract(center)))
                super.set(center, pin, end.subtract(center));
            else
                throw new ParseException(reader, "Not all points can be colinear");
            this.start = this.scalars(start.subtract(this.root()));
            this.end = this.scalars(end.subtract(this.root()));
        } catch (ArithmeticException e) {
            throw new ParseException(reader, "arithmetic exception: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ParseException(reader, e.getMessage());
        }
    }
}
