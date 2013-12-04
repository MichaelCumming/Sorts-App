/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `PlaneSegment.java'                                       *
 * written by: Rudi Stouffs                                  *
 * last modified: 18.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.ind;

import cassis.Thing;
import cassis.Element;
import cassis.struct.*;
import cassis.parse.*;
import cassis.sort.Sort;
import cassis.sort.PrimitiveSort;
import cassis.form.Form;
import cassis.form.DiscreteForm;

/**
 * A <b>plane-segment</b> is a connected and bounded rectilinear segment of a
 * plane. The plane defines the co-descriptor of the segment, the boundary of
 * the segment is defined by a set of edges, grouped into an outer boundary and
 * zero, one or more inner boundaries.
 * <p>
 * The <tt>PlaneSegment</tt> class defines the characteristic individual for
 * plane segments. A plane segment is represented as a plane with an array of
 * boundaries (one outer and zero, one or more inner), each a list of edges.
 * This characteristic individual accepts no parameters.
 * Forms of plane segments adhere to a regional behavior.
 * @see cassis.form.DiscreteForm
 */
public class PlaneSegment extends Plane {
    static {
        PrimitiveSort.register(PlaneSegment.class, DiscreteForm.class, Parameter.NONE);
        new cassis.visit.vrml.Proto(PlaneSegment.class, "icons/label.gif");
    }
    
    // constants
    static int MEMBER_MASK = 1;
    static int SIDE_MASK_1 = 2;
    static int SIDE_MASK_2 = 4;
    static int INNER_1 = 0;
    static int OUTER_1 = 1;
    static int INNER_2 = 2;
    static int OUTER_2 = 3;
    static int SAME_SHARED = 4;
    static int OPP_SHARED = 5;
    
    // representation
    private List[] boundaries;
    
    // constructors
    
    /**
     * Constructs a nondescript <b>PlaneSegment</b>. This constructor exists
     * for the purpose of using the <tt>newInstance</tt> method to create a new
     * plane segment. This plane segment must subsequently be assigned a sort
     * and value.
     * @see Individual#parse
     * @see #parse
     */
    PlaneSegment() {
        super();
        this.boundaries = new List[0];
    }
    private PlaneSegment(Sort sort, Vector normal, Rational scalar, List[] boundaries) throws IllegalArgumentException {
        super(sort, normal, scalar);
        this.boundaries = new List[boundaries.length];
        for (int i = 0; i < boundaries.length; i++) {
            this.boundaries[i] = new List();
            this.boundaries[i].duplicate(boundaries[i]);
        }
    }
    
    // methods
    
    /**
     * <b>Duplicates</b> this plane segment. It returns a new individual with
     * the same specifications, defined for the base sort of this plane
     * segment's sort.
     * @return an {@link cassis.Element} object
     * @see cassis.sort.Sort#base()
     */
    public Element duplicate() {
        return new PlaneSegment(this.ofSort().base(), this.normal(), this.scalar(), this.boundaries);
    }
    
    /**
     * Checks whether this plane segment has <b>equal value</b> to another
     * individual. This condition applies if both plane segments have equal
     * co-descriptors and equal lists of boundary line segments.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise.
     * @throws ClassCastException if the argument is not a plane segment
     */
    boolean equalValued(Individual other) {
        return (super.equalValued(other) &&
                this.boundaries.equals(((PlaneSegment) other).boundaries));
    }
    
    /**
     * Compares this plane segment to another thing.
     * The result equals {@link cassis.Thing#FAILED} if the argument is not a
     * plane segment.
     * Otherwise the result is defined by comparing both planar geometries.
     * @param other a {@link cassis.Thing} object
     * @return an integer value equal to one of {@link cassis.Thing#EQUAL},
     * {@link cassis.Thing#LESS}, {@link cassis.Thing#GREATER}, or
     * {@link cassis.Thing#FAILED}
     * @see Planar#compare
     */
    public int compare(Thing other) {
        int c;
        
        if (!(other instanceof PlaneSegment)) return FAILED;
        c = super.compare(other);
        if (c != EQUAL) return c;
        for (int i = 0; (i < this.boundaries.length) && (i < ((PlaneSegment) other).boundaries.length); i++) {
            c = this.boundaries[i].compare(((PlaneSegment) other).boundaries[i]);
            if (c != EQUAL) return c;
        }
        return this.boundaries.length - ((PlaneSegment) other).boundaries.length;
    }
    
    private void classification(PlaneSegment other, List[] classes) {
        SplayTree sweepline = new SplayTree();
        Edge.MINUS_INF.setAlignedWithOne(false);
        Edge.PLUS_INF.setAlignedWithOne(true);
        sweepline.initSentinelTree(Edge.MINUS_INF, Edge.PLUS_INF);
        SplayTree transitions = new SplayTree();
        Edge edge;
        boolean memberOfOther = false;
        PlaneSegment segment = this;
        while (segment != null) {
            for (int i = 0; i < segment.boundaries.length; i++) {
                segment.boundaries[i].toBegin();
                while (!segment.boundaries[i].beyond()) {
                    edge = (Edge) segment.boundaries[i].current();
                    edge.setMemberOf(memberOfOther); // membership wrt planesegment
                    transitions.insert(new Vertex(edge.tail(), edge)); // transition point of type start
                    transitions.insert(new Vertex(edge.head(), Vertex.END)); // transition point of type end
                }
            }
            if (segment == this)
                segment = other;
            else segment = null;
            memberOfOther = true;
        }
        while (!transitions.empty()) {
            TreeNode node, prev, low, high;
            int side;
            boolean shared = false;
            Vertex point = (Vertex) transitions.findMin();
            // locate point within plane slice defined by sweepline status
            node = sweepline.find(point);
            side = node.content().splayCompare(point);
            if (side < 0) {
                low = node;
                high = node.successor();
            } else if (side > 0) {
                low = node.predecessor();
                high = node;
            } else {
                low = node.predecessor();
                // determine set of edges containing current transition point
                // and delete edges ending at transition point
                for (prev = low; node.content().splayCompare(point) == 0; node = prev.successor()) {
                    edge = (Edge) node.content();
                    if (edge.head().equals(point))
                        sweepline.extract(node);
                    else {
                        edge = new Edge(point.position(), edge.head());
                        node.setContent(new Edge(((Edge) node.content()).tail(), point.position()));
                        prev = node;
                    }
                    // classify edge into one of the categories:
                    // inner, outer, same-shared or opposite-shared
                    if (shared) {
                        if (edge.alignedWithOne() == edge.alignedWithOther())
                            classes[SAME_SHARED].append(edge);
                        else classes[OPP_SHARED].append(edge);
                        shared = false;
                    } else if (edge.equals(prev.successor().content()))
                        shared = true;
                    else if (edge.memberOfOther()) {
                        if (edge.alignedWithOther())
                            classes[INNER_2].append(edge);
                        else classes[OUTER_2].append(edge);
                    } else {
                        if (edge.alignedWithOne())
                            classes[INNER_1].append(edge);
                        else classes[OUTER_1].append(edge);
                    }
                }
                high = node;
                // reverse the order of all other edges in the set
                if (prev != low) sweepline.reverse(low.successor(), prev);
            }
            // insert all edges originating from transition points
            Vertex ref = point;
            if (point.type() == Vertex.START)
                sweepline.insert(point.edge());
            for ( ; !transitions.empty() && ref.equals((Vertex) transitions.findMin()); ) {
                point = (Vertex) transitions.extractMin();
                if (point.type() == Vertex.START)
                    // insert edge if transition point of type start
                    sweepline.insert(point.edge());
            }
            // update alignment information
            boolean alignedWithOne = ((Edge) low.content()).alignedWithOne();
            boolean alignedWithOther = ((Edge) low.content()).alignedWithOther();
            for (node = low.successor(); node != high; node = node.successor()) {
                if (((Edge) node.content()).memberOfOther())
                    alignedWithOther = !alignedWithOther;
                else alignedWithOne = !alignedWithOne;
                ((Edge) node.content()).setAlignedWithOne(alignedWithOne);
                ((Edge) node.content()).setAlignedWithOther(alignedWithOther);
            }
            if (((Edge) high.content()).memberOfOther())
                alignedWithOther = !alignedWithOther;
            else alignedWithOne = !alignedWithOne;
            /*
            if ((((Edge) high.content()).alignedWithOne() != alignedWithOne) ||
                (((Edge) high.content()).alignedWithOther() != alignedWithOther))
                throw AlignmentException();
            // check for intersection point between neighboring segment 'low'
            // and immediate successor
            seg1 = node_segment(low);
            seg2 = node_segment(succ_node(low));
            if ((*procedure[INTERSECT][LINES])(seg1, seg2, iel, OPEN, OPEN)) {
                 iel->Xtype = INT_TYPE;
                 pqueue_insert(points, iel); iel = elmake(POINTS);
            }
             *////
        }
    }
    
    /**
     * Tests if this plane segment <b>contains</b> another individual.
     * A plane segment contains another plane segment if these have the same
     * co-descriptor, and the first segment's boundary encloses the second
     * segment.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean contains(Individual other) {
        if (!(other instanceof PlaneSegment)) return false;
        // classes = this.classification(other);
        // return classes.contains();
        return false;
    }
    
    /**
     * Tests if this plane segment <b>touches</b> another individual.
     * Two plane segments touch if these have the same co-descriptor and
     * do not overlap but share a boundary line segment.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean touches(Individual other) {
        if (!(other instanceof PlaneSegment)) return false;
        // classes = this.classification(other);
        // return classes.touches();
        return false;
    }
    
    /**
     * Tests if this plane segment is <b>disjoint</b> from another individual.
     * Two plane segments are disjoint if these have the same co-descriptor and
     * do not overlap, nor share a boundary line segment.
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean disjoint(Individual other) {
        if (!(other instanceof PlaneSegment)) return false;
        // classes = this.classification(other);
        // return classes.disjoint();
        return false;
    }
    
    /**
     * Tests if this plane segment <b>aligns</b> with another individual.
     * Two plane segments align if these have the same co-descriptor and ... .
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean aligns(Individual other) {
        if (!(other instanceof PlaneSegment)) return false;
        // classes = this.classification(other);
        // return classes.aligns();
        return false;
    }
    
    /**
     * <b>Combines</b> this plane segment with another individual.
     * Two plane segments combine if these have the same co-descriptor and
     * are not disjoint. The result is .. .
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     * @see #disjoint
     */
    public boolean combine(Individual other, Form result) {
        if (!(other instanceof PlaneSegment)) return false;
        // classes = this.classification(other);
        // if (classes.disjoint()) return false;
        // result = classes.combine();
        return false;
    }
    /**
     * Determines the <b>common</b> part of this plane segment with another
     * individual. Two plane segments share a common part if these have the
     * same co-descriptor and are not disjoint, nor touch. The result is ... .
     * @param other an {@link Individual} object
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     * @see #disjoint
     * @see #touches
     */
    public boolean common(Individual other, Form result) {
        if (!(other instanceof PlaneSegment)) return false;
        // classes = this.classification(other);
        // if (classes.disjoint() || classes.touches()) return false;
        // result = classes.common();
        return false;
    }
    /**
     * Determines the <b>complement</b> parts of this plane segment wrt another
     * individual. A plane segment has a complement wrt another plane segment
     * if both segments have the same co-descriptor and are not disjoint, nor
     * touch, nor the second segment contains the first. The complement parts
     * are constructed and placed in the result argument's array.
     * @param other an {@link Individual} object
     * @param result an {@link Individual} with size 2 or greater
     * @return <tt>true</tt> if the condition applies; <tt>false</tt> otherwise
     */
    public boolean complement(Individual other, Individual result[]) {
        if (!(other instanceof PlaneSegment)) return false;
        // classes = this.classification(other);
        // if (classes.disjoint() || classes.touches() || classes.isContained())
        //    return false;
        // result = classes.complement();
        return false;
    }
    
    /**
     * Converts the plane segment's <b>value to a string</b>. The result is a
     * list of the endposition vectors separated by comma's, that is enclosed by
     * angular (<>) brackets. This string can be included in an SDL description and
     * subsequently parsed to reveal the original value.
     * @param assoc an {Individual} object
     * @return a <tt>String</tt> object
     * @see #parse
     */
    public String valueToString(Individual assoc) {
        // if (this.nil()) return NIL;
        // return "<" + this.getTail().toString() + ", " + this.getHead().toString() + ">";
        return NIL;
    }
    
    /**
     * <b>Transforms</b> this plane segment according to the specified
     * transformation matrix. The result is a new plane segment defined for
     * the base sort of this plane's sort.
     * @param mat a transformation matrix
     * @return an {@link Individual} object
     * @see cassis.struct.Transform
     * @see cassis.sort.Sort#base
     */
    public Individual transform(Transform mat) {
        if (this.nil()) return (Individual) this.duplicate();
        Vector pos1 = mat.transform(this.root());
        Vector pos2 = mat.transform(this.root().add(this.u()));
        Vector pos3 = mat.transform(this.root().add(this.v()));
        //
        //return new PlaneSegment(this.ofSort().base(), pos1, pos2, pos3, new List());
        return null;
    }
    
    /**
     * Reads an SDL description of a plane segment from a {@link cassis.parse.ParseReader}
     * object and assigns the value to this segment. This description consists of
     * a list of the endposition vectors separated by comma's, that is enclosed by
     * angular (<>) brackets.
     * @param reader a token reader
     * @throws ParseException if the description does not correctly describe a plane segment
     * @see #valueToString
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
            //super.set(tail, head);
            //this.tail = this.direction().scalar(tail.subtract(this.root()));
            //this.head = this.direction().scalar(head.subtract(this.root()));
            
            //if (this.tail.compare(this.head) == GREATER) {
            //Rational t = this.tail;
            //this.tail = this.head;
            //this.head = t;
            //}
        } catch (ArithmeticException e) {
            throw new ParseException(reader, "arithmetic exception: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ParseException(reader, e.getMessage());
        }
    }
}
