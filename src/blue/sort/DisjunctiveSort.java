/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `DisjunctiveSort.java'                                    *
 * written by: Rudi Stouffs                                  *
 * last modified: 17.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import blue.struct.List;
import blue.form.Form;
import blue.form.MetaForm;
import blue.io.GraphicsContext;
import blue.io.vrml.Proto;

/**
 * A <b>Disjunctive Sort</b> specifies a disjunctive composition of {@link Sort}s
 * under the <i>sum</i> operation. Under the sum operation, a form of the
 * resulting sort may contain a form of every operand sort. A disjunctive sort may have a name assigned. <p>
 * The <b>DisjunctiveSort</b> class represents a disjunctive sort additionally
 * as an ordered {@link blue.struct.List} of component sorts. In the definition of a composite sort, the operation of sum is
 * denoted by '+'. In its canonical form, component sorts cannot themselves be disjunctive sorts
 * and identical component sorts are reduced to a single component. (a + b) + (a + c) -> a + b + c.
 */
public class DisjunctiveSort extends Sort {
    static {
        new Proto(DisjunctiveSort.class, "icons/sort.gif", Proto.define("DisjunctiveSort", 0.2, "0.6 0.15 0.15"));
    }

    // representation
    private List components;
    // constructor

    /**
     * Creates a general <b>disjunctive sort</b> within the specified context.
     * @param context a {@link Sorts} object
     */
    DisjunctiveSort(Sorts context) {
        super(context);
        this.components = new List();
        this.canonical = null;
    }
    // access methods

    /** Places the lead index at the <b>beginning</b> of the component list. */
    public void toBegin() { this.components.toBegin(); }

    /** Places the lead index at the <b>end</b> of the component list. */
    public void toEnd() { this.components.toEnd(); }

    /** Places the lead index at the <b>next</b> component in the list. */
    public void toNext() { this.components.toNext(); }

    /** Places the lead index at the <b>previous</b> component in the list. */
    public void toPrev() { this.components.toPrev(); }

    /**
     * Checks if the lead index is located <b>at the beginning</b> of the list.
     * @return <tt>true</tt> if at the beginning of the list; <tt>false</tt> otherwise
     */
    public boolean atBegin() { return this.components.atBegin(); }

    /**
     * Checks if the lead index is located <b>at the end</b> of the list.
     * @return <tt>true</tt> if at the end of the list; <tt>false</tt> otherwise
     */
    public boolean atEnd() { return this.components.atEnd(); }

    /**
     * Checks if the lead index is located <b>beyond</b> the end of the list.
     * @return <tt>true</tt> if beyond the end of the list; <tt>false</tt> otherwise
     */
    public boolean beyond() { return this.components.beyond(); }

    /**
     * Returns the <b>current</b> component in the list as specified by the lead index.
     * @return the current component {@link blue.sort.Sort}
     */
    public Sort current() { return (Sort)this.components.current(); }

    /**
     * Returns the <b>next</b> component in the list as specified by the lead index.
     * The lead index is not moved and remains at the current component in the list.
     * @return the next component {@link blue.sort.Sort}
     */
    public Sort next() { return (Sort)this.components.next(); }

    /**
     * Returns the <b>previous</b> component in the list as specified by the lead index.
     * The lead index is not moved and remains at the current component in the list.
     * @return the previous component {@link blue.sort.Sort}
     */
    public Sort previous() { return (Sort)this.components.previous(); }

    /**
     * Returns the <b>first</b> component in the list as specified by the lead index.
     * The lead index is not moved and remains at the current component in the list.
     * @return the first component {@link blue.sort.Sort}
     */
    public Sort first() { return (Sort)this.components.first(); }

    /**
     * Returns the <b>last</b> component in the list as specified by the lead index.
     * The lead index is not moved and remains at the current component in the list.
     * @return the last component {@link blue.sort.Sort}
     */
    public Sort last() { return (Sort)this.components.last(); }

    /**
     * Returns the <b>size</b> of the component list.
     * @return an integer value
     */
    public int size() { return this.components.length(); }
    // methods

    /**
     * <b>Merges</b> this disjunctive sort with another disjunctive sort
     * by merging their component lists. this sort contains the result; the other sort is also altered.
     * @param other a <tt>DisjunctiveSort</tt> object
     * @throws IllegalArgumentException if both sorts do not share the same context
     */
    void merge(DisjunctiveSort other) throws IllegalArgumentException {
        if (this.context() != other.context())
            throw new IllegalArgumentException("DisjunctiveSort.merge: illegal arguments");
        this.components.merge(other.components);
        this.components.reduce();
        this.canonical = this.definition = this.canonicalDescription();
        this.setStats(0, 0, 0);
        this.toBegin();
        while (!this.beyond()) {
            this.includeStats(this.current());
            this.toNext();
        }
    }

    /**
     * <b>Inserts</b> the other sort into this disjunctive sort.
     * If the disjunctive sort already contains the other sort, nothing happens.
     * @param other a {@link Sort} object
     * @throws IllegalArgumentException if both sorts do not share the same context
     */
    void insert(Sort other) throws IllegalArgumentException {
        if (this.context() != other.context())
            throw new IllegalArgumentException("DisjunctiveSort.insert: illegal arguments");
        if (this.components.contains(other)) return;
        this.toBegin();
        this.components.insertInto(other);
        this.canonical = this.definition = this.canonicalDescription();
        this.includeStats(other);
    }

    /**
     * Returns a <b>canonical description</b> for this disjunctive sort.
     * @return a <tt>String</tt> object
     */
    private String canonicalDescription() {
        this.toBegin();
        if (this.beyond()) return "";
        StringBuffer canonical = new StringBuffer(this.current().toString());
        while (!this.atEnd()) {
            this.toNext();
            canonical.append(" + ").append(this.current().toString());
        }
        return canonical.toString();
    }

    /**
     * Creates a <b>duplicate</b> of this disjunctive sort and assigns it
     * the specified name. If this name equals the sort's name, this sort is returned and no duplicate is made.
     * @param name a <tt>String</tt> object
     * @return the duplicate sort
     */
    Sort duplicate(String name) {
        if (name.equals(this.name())) return this;
        DisjunctiveSort result = this;
        if (this.isNamed()) {
            result = new DisjunctiveSort(this.context());
            result.components.duplicate(this.components);
            result.canonical = this.canonical;
            result.definition = this.name();
            if (this.isNamed())
                result.setStats(-1, 0, 0);
            result.includeStats(this);
        }
        result.assign(name);
        return result;
    }

    /**
     * Creates a <b>new form</b> for this disjunctive sort.
     * @return a {@link blue.form.Form} corresponding this sort
     * @see blue.form.MetaForm#MetaForm(Sort)
     */
    public Form newForm() {
        return new MetaForm(this);
    }

    /**
     * Creates a <b>new form</b> for this disjunctive sort as a form for the parent sort.
     * For example, in the case of an {@link AttributeSort}, the form is created
     * according to the base sort, but belongs to the attribute sort.
     * @param parent a <tt>Sort</tt> object
     * @return a {@link blue.form.Form} corresponding this sort
     * @see blue.form.MetaForm#MetaForm(Sort)
     */
    Form newForm(Sort parent) {
        return new MetaForm(parent);
    }

    /**
     * <b>Visualize</b> this disjunctive sort using the specified graphics context.
     * @param gc a {@link blue.io.GraphicsContext} object
     */
    public void visualize(GraphicsContext gc) {
        if (this.size() == 1) {
            this.first().visualize(gc);
            return;
        }
        this.toBegin();
        gc.beginGroup(DisjunctiveSort.class, this.size());
        while (!this.beyond()) {
            gc.satellite(this.current().toString());
            this.current().visualize(gc);
            this.toNext();
        }
        gc.endGroup();
    }

    /**
     * Checks whether this disjunctive sort <b>contains</b> another sort.
     * This applies if any component sort equals or contains the other sort.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort contains the other, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both sorts do not share the same context
     * @see Sort#equals
     * @see Sort#contains
     */
    public boolean contains(Sort other) throws IllegalArgumentException {
        if (this == other) return false;
        if (this.context() != other.context())
            throw new IllegalArgumentException("DisjunctiveSort.contains: illegal arguments");
        this.toBegin();
        while (!this.beyond()) {
            if (this.current().equals(other) || this.current().contains(other))
                return true;
            this.toNext();
        }
        return false;
    }

    /**
     * Checks whether this disjunctive sort is a <b>part of</b> another sort.
     * This applies if each component sort is a part of the other sort.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort contains the other, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both sorts do not share the same context
     * @see Sort#contains
     */
    public boolean partOf(Sort other) throws IllegalArgumentException {
        if (this == other) return false;
        if (this.context() != other.context())
            throw new IllegalArgumentException("DisjunctiveSort.partOf: illegal arguments");
        this.toBegin();
        while (!this.beyond()) {
            if (!other.contains(this.current()))
                return false;
            this.toNext();
        }
        return true;
    }

    /**
     * <b>Combines</b> this disjunctive sort with another sort under the attribute operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     * @throws IllegalArgumentException if both sorts do not share the
     * same context or if the other sort is an {@link AspectsSort} instance
     */
    Sort combine(Sort other) {
        if ((this.context() != other.context()) || (other instanceof AspectsSort))
            throw new IllegalArgumentException("DisjunctiveSort.combine: illegal arguments");
        // if (other instanceof RecursiveSort)
        //     other = ((RecursiveSort) other).instance;
        String name = null;
        if (this.isNamed()) {
            if ((other instanceof DisjunctiveSort) && !other.isNamed()) {
                DisjunctiveSort result = new DisjunctiveSort(this.context());
                ((DisjunctiveSort)other).toBegin();
                while (!((DisjunctiveSort)other).beyond()) {
                    result.insert(this.combine(((DisjunctiveSort)other).current()));
                    ((DisjunctiveSort)other).toNext();
                }
                return result;
            }
            name = AttributeSort.definition(this, other);
            Sort temp = this.context().sortOf(name);
            if (temp != null) return temp;
        }
        DisjunctiveSort result = new DisjunctiveSort(this.context());
        this.toBegin();
        while (!this.beyond()) {
            result.insert(this.current().combine(other));
            this.toNext();
        }
        if (this.isNamed()) result.assign(name);
        return result;
    }

    private Match relateSubsumptive(Sort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = Match.none;
        this.toBegin();
        while (!this.beyond()) {
            result = result.minimum(this.current().relate(other));
            this.toNext();
        }
        if (result.isIncongruous()) return result;
        result = result.assign(this, other);
        result.subsumptive(this.components() - 1);
        return result.approve();
    }

    /**
     * <b>Relates</b> this disjunctive sort to an aspect and returns the resulting match. If no match has already been stored,
     * each component sort is related to the aspect, and the minimum match is stored.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    Match relate(Aspect other) {
        return this.relateSubsumptive(other);
    }

    /**
     * <b>Relates</b> this disjunctive sort to a primitive sort and returns
     * the resulting match. If no match has already been stored, each component sort
     * is related to the primitive sort, and the minimum match is stored.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    Match relate(PrimitiveSort other) {
        return this.relateSubsumptive(other);
    }

    /**
     * <b>Relates</b> this disjunctive sort to an attribute sort and returns
     * the resulting match. If no match has already been stored, each component sort
     * is related to the attribute sort and the disjunctive sort is also related to
     * a new disjunctive sort composed of the operands of the attribute sort. The minimum match of all these is stored.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    Match relate(AttributeSort other) {
        Match result = this.relateSubsumptive(other);
        DisjunctiveSort sort = new DisjunctiveSort(other.context());
        sort.insert(other.base());
        sort.insert(other.weight());
        Match alt = new Match(Match.incompleteLevel, sort, other);
        // alt.rearrangement(2);
        alt = new Match(Match.incompleteLevel, this, other, alt, this.relate(sort)).approve();
        return result.minimum(alt);
    }

    /**
     * <b>Relates</b> this disjunctive sort to another disjunctive sort and returns
     * the resulting match. Two disjunctive sorts are denoted equivalent if their
     * canonical definitions are identical. Otherwise, if no match has already
     * been stored, their respective component list are related, and the minimum match is stored.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     * @see Match#compose
     */
    Match relate(DisjunctiveSort other) {
        if (this.toString().equals(other.toString()))
            return Match.identical(this);
        if (this.canonical.equals(other.canonical))
            return Match.equivalent(this, other);
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        return Match.compose(this, other).approve();
    }

    /**
     * <b>Relates</b> this disjunctive sort to a recursive sort and returns
     * the resulting match. If no match has already been stored, the recursive sort
     * is instead related to the disjunctive sort. If a match is found for this, it is reversed then stored.
     * @param other a {@link RecursiveSort} object
     * @return a {@link Match} object
     * @see RecursiveSort#relate(DisjunctiveSort)
     */
    Match relate(RecursiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }
        
}
