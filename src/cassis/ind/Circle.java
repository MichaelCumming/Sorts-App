/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Circle.java'                                             *
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
 * A <b>circle</b> is a circular, connected, non-bounded planar curve.
 * The co-descriptor of a circle is the circle itself; it has no boundary.
 * <p>
 * The <b>Circle</b> class defines the characteristic individual for circles.
 * A circle is represented as a {@link Planar} geometry with additionally one
 * vector specifying the center of the circle and another vector specifying a
 * position on the circle. This characteristic individual accepts no parameters.
 * Forms of circles adhere to a discrete behavior.
 * @see cassis.form.DiscreteForm
 */
public class Circle extends Planar {
    static {
	PrimitiveSort.register(Circle.class, DiscreteForm.class, Parameter.NONE);
	new cassis.visit.vrml.Proto(Circle.class, "icons/label.gif");
    }

    // representation
    private Couple center, pin;

    // constructors

    /**
     * Constructs a nondescript <b>Circle</b>. This constructor exists for
     * the purpose of using the <tt>newInstance</tt> method to create a new
     * circle. This circle must subsequently be assigned a sort and value.
     * @see Individual#parse
     * @see #parse
     */
    Circle() {
	super();
	this.center = this.pin = NILCOUPLE;
    }
    /**
     * Constructs a nondescript <b>Circle</b> for the specified sort.
     * This sort must allow for circles as individuals. This constructor exists for
     * the purpose of subclassing this class. This circle must subsequently
     * be assigned a value.
     * @param sort a {@link cassis.sort.Sort} object
     * @see #set
     */
    Circle(Sort sort) {
	super(sort);
	this.center = this.pin = NILCOUPLE;
    }
    /**
     * Constructs a <b>Circle</b> from a vector, specifying the <i>normal</i> to
     * the plane, a rational <i>scalar</i>, specifying the root of the plane wrt
     * the origin, and two couples of rationals, respectively, specifying the circle's
     * <i>center</i> position and a <i>pin</i> position on the circle, both wrt the
     * plane's local coordinate system. The specified sort must allow for circles as
     * individuals
     * @param sort a {@link cassis.sort.Sort} object
     * @param normal a vector defining the normal to the plane of the circle
     * @param scalar a rational defining the root of the plane wrt the origin
     * @param center a couple of rationals defining the center of the circle wrt the
     * plane's local coordinate system
     * @param pin a couple of rationals defining a position on the circle wrt the
     * plane's local coordinate system
     * @see cassis.struct.Vector
     * @see cassis.struct.Rational
     * @see cassis.struct.Couple
     */
    Circle(Sort sort, Vector normal, Rational scalar, Couple center, Couple pin) {
	super(sort, normal, scalar);
	this.center = center;
	this.pin = pin;
    }
    /**
     * Constructs a <tt>Circle</tt> from three, not all parallel, vectors, for the
     * specified sort. The sort must allow for circles as individuals.
     * The <em>center</em> vector specifies the center position of the circle.
     * The <em>pin</em> vector specifies a position on the circle.
     * The <em>dir</em> vector specifies another position on the plane to which
     * the circle belongs.
     * @param sort a {@link cassis.sort.Sort} object
     * @param center a vector specifying the center of the circle
     * @param pin a vector specifying a position on the circle
     * @param dir a vector specifying a third position on the circle's plane
     * @throws IllegalArgumentException if all three argument vectors are parallel
     * @see cassis.struct.Vector
     */
    public Circle(Sort sort, Vector center, Vector pin, Vector dir) throws IllegalArgumentException {
	super(sort, center, pin, dir);
	center = center.subtract(this.root());
	this.center = this.scalars(center);
	this.pin = this.scalars(center.add(pin));
    }
    /**
     * Constructs a <tt>Circle</tt> with the specified center, pin, and assist
     * points, for the specified sort. The sort must allow for circles as
     * individuals.
     * The <em>center</em> point specifies the center position of the circle.
     * The <em>pin</em> point specifies a position on the circle.
     * The <em>assist</em> point specifies another position on the plane to which
     * the circle belongs.
     * @param sort a {@link cassis.sort.Sort} object
     * @param center a point defining the center of the circle
     * @param pin a point defining a position on the circle
     * @param assist a point defining a position in the circle's plane
     * @throws IllegalArgumentException if all three argument points are colinear.
     * @see Point
     */
    public Circle(Sort sort, Point center, Point pin, Point assist) throws IllegalArgumentException {
	this(sort, center.position(), pin.position().subtract(center.position()), assist.position().subtract(center.position()));
    }

    /**
     * Sets the circle's value. Used by subclass constructors.
     * @param center a vector specifying the center of the circle
     * @param pin a vector specifying a position on the circle
     * @param dir a vector specifying a third position on the circle's plane
     * @throws IllegalArgumentException if all three argument vectors are parallel
     * @see cassis.struct.Vector
     */
    void set(Vector center, Vector pin, Vector dir) throws IllegalArgumentException {
	super.set(center, pin, dir);
	center = center.subtract(this.root());
	this.center = this.scalars(center);
	this.pin = this.scalars(center.add(pin));
    }

    // access methods

    /**
     * Returns a vector specifying the <b>center</b> of the circle
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector getCenter() { return this.position(center); }
    /**
     * Returns a vector specifying a <b>pin</b> position on the circle
     * @return a {@link cassis.struct.Vector} object
     */
    public Vector getPin() { return this.position(pin); }

    /**
     * Returns a couple of rationals specifying the <b>center</b> of the circle
     * with respect to the plane's local coordinate system.
     * @return a {@link cassis.struct.Couple} of rationals
     */
    Couple center() { return this.center; }
    /**
     * Returns a couple of rationals specifying a <b>pin</b> position on the circle
     * with respect to the plane's local coordinate system.
     * @return a {@link cassis.struct.Couple} of rationals
     */
    Couple pin() { return this.pin; }

    /**
     * Returns the radius of this circle.
     * @return a double
     */
    public double radius() {
        Couple dist = this.pin.subtract(this.center);
        return Math.sqrt(dist.dotProduct(dist).doubleValue());
    }

    // methods

    /**
     * <b>Duplicates</b> this circle. It returns a new individual with
     * the same specifications, defined for the base sort of this circle's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
	return new Circle(this.ofSort().base(), this.normal(), this.scalar(), this.center, this.pin);
    }

    /**
     * Checks whether this circle has <b>equal value</b> to another individual.
     * This condition applies if both circles are coplanar and have equal centers
     * and radii.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a circle
     */
    boolean equalValued(Individual other) {
        return (super.equalValued(other) &&
		this.center.equals(((Circle) other).center) &&
		this.center.scale(new Coord(2)).subtract(this.pin.add(((Circle) other).pin)).dotProduct(this.pin.subtract(((Circle) other).pin)).isZero());
    }

    /**
     * Compares this circle to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a circle.
     * Otherwise the result is defined by comparing the center vectors and radii
     * of both circles.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     */
    public int compare(Thing other) {
	if (!(other instanceof Circle)) return FAILED;
	int c = super.compare(other);
	if (c != EQUAL) return c;
	c = this.center.compare(((Circle) other).center);
	if (c != EQUAL) return c;
	return this.center.scale(new Coord(2)).subtract(this.pin.add(((Circle) other).pin)).dotProduct(this.pin.subtract(((Circle) other).pin)).sign();
    }

    /**
     * Tests if this circle is <b>concentric</b> with another circle. Two circles
     * are concentric if these have the same center vector. These must not
     * necessarily be coplanar.
     * @param other a circle
     * @return <tt>true</tt> if both circles are concentric; <tt>false</tt> otherwise
     */
    public final boolean concentric(Circle other) {
	return this.getCenter().equals(other.getCenter());
    }

    /**
     * Tests if this circle is <b>coplanar</b> with another circle. Two circles
     * are coplanar if these belong to the same plane.
     * @param other a circle
     * @return <tt>true</tt> if both circles are coplanar; <tt>false</tt> otherwise
     * @see Planar#equalValued
     */
    public final boolean coPlanar(Circle other) {
	return super.equalValued(other);
    }

    /**
     * Converts the circle's <b>value to a string</b>. The result
     * is a comma-separated list of the center, pin, and third position
     * vectors, enclosed by angular (<>) brackets.
     * This string can be included in an SDL description and subsequently
     * parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String toString(Individual assoc) {
	if (this.nil()) return NIL;
	Vector assist = this.normal().product(this.direction(this.pin)).normalize().add(this.getCenter());
	return "<" + this.getCenter().toString() + "," + this.getPin().toString() + "," + assist.toString() + ">";
    }

    /**
     * <b>Transforms</b> this circle according to the specified transformation matrix.
     * The result is a new circle defined for the base sort of this circle's sort.
     * @param mat a transformation matrix
     * @return an {@link Individual} object
     * @see cassis.struct.Transform
     * @see cassis.sort.Sort#base
     */
    public Individual transform(Transform mat) {
	if (this.nil()) return (Individual) this.duplicate();
	Vector center = this.getCenter();
	Vector pin = this.getPin();
	Vector assist = this.root();
	if (center.colinear(pin, assist)) {
	    assist = this.position(U);
	    if (center.colinear(pin, assist))
		assist = this.position(V);
	}
	center = mat.transform(center);
	pin = mat.transform(pin);
	assist = mat.transform(assist);
	return new Circle(this.ofSort().base(), center, pin.subtract(center), assist.subtract(center));
    }

    /**
     * Reads an SDL description of a circle from a {@link cassis.parse.ParseReader} object
     * and assigns the value to this circle. This description consists of 
     * a comma-separated list of the center, pin, and third position
     * vectors, enclosed by angular (<>) brackets.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a circle
     * @see #toString
     */
    public void parse(ParseReader reader) throws ParseException {
	if (reader.newToken() != '<')
	    throw new ParseException(reader, "'<' expected");
	Vector center = Vector.parse(reader);
	if (reader.newToken() != ',')
	    throw new ParseException(reader, "',' expected");
	Vector pin = Vector.parse(reader);
	if (reader.newToken() != ',')
	    throw new ParseException(reader, "',' expected");
	Vector assist = Vector.parse(reader);
	if (reader.newToken() != '>')
	    throw new ParseException(reader, "'>' expected");

	try {
	    super.set(center, pin.subtract(center), assist.subtract(center));
	    this.center = this.scalars(center.subtract(this.root()));
	    this.pin = this.scalars(pin.subtract(this.root()));
	} catch (ArithmeticException e) {
	    throw new ParseException(reader, "arithmetic exception: " + e.getMessage());
	} catch (IllegalArgumentException e) {
	    throw new ParseException(reader, e.getMessage());
	}
    }
}
