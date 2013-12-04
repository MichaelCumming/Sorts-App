/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `AttributeSort.java'                                      *
 * written by: Rudi Stouffs                                  *
 * last modified: 17.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import blue.form.Form;
import blue.io.GraphicsContext;
import blue.io.vrml.Proto;

/**
 * An <b>Attribute Sort</b> specifies a composition of two {@link Sort}s
 * under the <i>attribute</i> operation. Under the attribute operation,
 * each individual of the <i>base</i> sort has a form of the <i>weigth</i> sort assigned as an attribute.
 * An attribute sort may have a name assigned. <p> The <b>AttributeSort</b> class represents an attribute sort additionally by
 * its base sort (left operand), and weight sort (right operand).
 * In the definition of an attribute sort, the attribute operation is denoted by '^'.
 * In its canonical form, an attribute's base specifies a simple sort, i.e., (a ^ b) ^ c -> a ^ (b ^ c) = a ^ b ^ c.
 */
public final class AttributeSort extends Sort {
    static {
        new Proto(AttributeSort.class, "icons/sort.gif", Proto.define("AttributeSort", 0.2, "0.6 0.15 0.15"));
    }

    private final static String separator = " ^ ";
    // representation
    private Sort base;
    private Sort weight;
    // constructors

    /**
     * Creates an <b>attribute sort</b> with the specified base and weight sorts.
     * Both operand sorts must share the same context, which also becomes the context of the new sort.
     * @param base a {@link Sort} object
     * @param base a {@link Sort} object
     * @throws IllegalArgumentException if both operand sorts do not share the
     * same context or if the base sort is not a {@link SimpleSort} instance
     */
    AttributeSort(Sort base, Sort weight) throws IllegalArgumentException {
        super(base.context());
        if ((base.context() != weight.context()) || !(base instanceof SimpleSort))
            throw new IllegalArgumentException("AttributeSort: illegal arguments");
        this.base = base;
        this.weight = weight;
        this.canonical = this.definition = definition(base, weight);
        this.setStats(base.names(), base.simples(), 0);
        this.includeStats(weight);
        if (this.context().getAttributeSort(this.canonical) == null)
            this.context().putAttributeSort(this.canonical, this);
    }
    // access methods

    /**
     * Returns the <b>base</b> sort of this attribute sort. The base denotes the left operand of the attribute operator.
     * @return a {@link Sort} object
     */
    public Sort base() { return this.base; }

    /**
     * Returns the <b>weight</b> sort of this attribute sort. The weight denotes the right operand of the attribute operator.
     * @return a {@link Sort} object
     */
    public Sort weight() { return this.weight; }
    // methods

    /**
     * Returns a <b>definition</b> for the attribute sort as defined by the specified base and weight sorts.
     * @param base a {@link Sort} object
     * @param weight a {@link Sort} object
     * @return a <tt>String</tt> object
     */
    static String definition(Sort base, Sort weight) {
        String name = base.toString() + separator;
        if ((weight instanceof DisjunctiveSort) && !weight.isNamed())
            name += '(' + weight.canonical + ')';
        else
            name += weight.toString();
        return name;
    }

    /**
     * Checks whether an attribute sort already <b>is defined</b> for the specified base and weight sorts.
     * @param base a {@link SimpleSort} object
     * @param weight a {@link Sort} object
     * @return the attribute sort if it is already defined; <tt>null</tt> otherwise
     * @throws IllegalArgumentException if both operand sorts do not share the
     * same context or if the base sort is not a {@link SimpleSort} instance
     */
    public static AttributeSort isDefined(Sort base, Sort weight) {
        if ((base.context() != weight.context()) || !(base instanceof SimpleSort))
            throw new IllegalArgumentException("AttributeSort.isDefined: illegal arguments");
        return base.context().getAttributeSort(definition(base, weight));
    }

    /**
     * Creates a <b>duplicate</b> of this attribute sort and assigns it
     * the specified name. If this name equals the sort's name, this sort is returned and no duplicate is made.
     * @param name a <tt>String</tt> object
     * @return the duplicate sort
     */
    Sort duplicate(String name) {
        if (name.equals(this.name()))
            return this;
        AttributeSort result = this;
        if (this.isNamed() || (this == this.context().getAttributeSort(this.canonical))) {
            result = new AttributeSort(this.base, this.weight);
            if (this.isNamed()) result.definition = this.name();
        }
        result.assign(name);
        return result;
    }

    /**
     * Creates a <b>new form</b> for this attribute sort.
     * This form is created according to the base sort but belonging to this sort.
     * @return a {@link blue.form.Form} corresponding this sort
     */
    public Form newForm() {
        return this.base.newForm(this);
    }

    /**
     * Creates a <b>new form</b> for this attribute sort as a form for the parent sort.
     * This form is created according to the base sort but belonging to the parent sort.
     * @param parent a {@link Sort} object
     * @return a {@link blue.form.Form} corresponding this sort
     */
    Form newForm(Sort parent) { return this.base.newForm(parent); }

    /**
     * <b>Visualize</b> this attribute sort using the specified graphics context.
     * @param gc a {@link blue.io.GraphicsContext} object
     */
    public void visualize(GraphicsContext gc) {
        gc.beginGroup(AttributeSort.class, 2);
        gc.label(this.base.toString());
        gc.label(this.weight.toString());
        this.weight.visualize(gc);
        gc.endGroup();
    }

    /**
     * Checks whether this attribute sort <b>contains</b> another sort.
     * This applies if both sorts are attribute sorts with identical base and
     * this sort's weight contains the other sort's weight.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort contains the other, <tt>false</tt> otherwise
     * @see Sort#contains
     */
    public boolean contains(Sort other) {
        if (!(other instanceof AttributeSort) || !this.base.equals(other.base()))
            return false;
        return this.weight.contains(((AttributeSort)other).weight());
    }

    /**
     * <b>Combines</b> this attribute sort with another sort under the attribute operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     * @throws IllegalArgumentException if both sorts do not share the
     * same context or if the other sort is an {@link AspectsSort} instance
     */
    Sort combine(Sort other) {
        if ((this.context() != other.context()) || (other instanceof AspectsSort))
            throw new IllegalArgumentException("AttributeSort.combine: illegal arguments");
        if ((other instanceof DisjunctiveSort) && !other.isNamed()) {
            DisjunctiveSort result = new DisjunctiveSort(this.context());
            ((DisjunctiveSort)other).toBegin();
            while (!((DisjunctiveSort)other).beyond()) {
                result.insert(this.combine(((DisjunctiveSort)other).current()));
                ((DisjunctiveSort)other).toNext();
            }
            return result;
        }
        if (!this.isNamed())
            return new AttributeSort(this.base, this.weight.combine(other));
        String name = definition(this, other);
        Sort result = this.context().sortOf(name);
        if (result != null) return result;
        result = new AttributeSort(this.base, this.weight.combine(other));
        result.assign(name);
        return result;
    }

    private Match attributeMatch(AttributeSort other, Match result) {
        Sort head = this.base, tail = this.weight, temp;
        Match alt, alt2, local;
        for (int n = 1; !(tail instanceof DisjunctiveSort); n++) {
            local = tail.base().relate(other.base);
            if (!local.isIncongruous()) {
                if (tail instanceof AttributeSort)
                    temp = head.combine(((AttributeSort)tail).weight);
                else
                    temp = head;
                alt = local.combine(temp.relate(other.weight), this, other);
                alt.rearrangement(n, 1);
                result = result.minimum(alt);
                if (tail instanceof AttributeSort) {
                    Sort ohead = other.base(), otail = other.weight;
                    Sort shead = tail.base(), stail = ((AttributeSort)tail).weight;
                    int direction = -1, dir;
                    for (int m = 1; otail instanceof AttributeSort; m++) {
                        ohead = ohead.combine(otail.base());
                        otail = ((AttributeSort)otail).weight;
                        shead = shead.combine(stail.base());
                        if (stail instanceof AttributeSort)
                            temp = head.combine(((AttributeSort)stail).weight);
                        else
                            temp = head;
                        alt2 = shead.relate(ohead).combine(temp.relate(otail), this, other);
                        alt2.rearrangement(n, m);
                        result = result.minimum(alt2);
                        dir = alt.compare(alt2);
                        if ((direction > 0) && (dir > 0)) break;
                        if (!(stail instanceof AttributeSort)) break;
                        stail = ((AttributeSort)stail).weight;
                        alt = alt2; direction = dir;
                    }
                }
            }
            if (tail instanceof SimpleSort) break;
            head = head.combine(tail.base());
            tail = ((AttributeSort)tail).weight;
        }
        return result;
    }

    /**
     * <b>Relates</b> this attribute sort to an aspect and returns the resulting match.
     * If no match has already been stored, alternatively, the base and weight sorts
     * of the attribute sort are related to the aspect, and the minimum match is stored.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    Match relate(Aspect other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = this.base.relate(other).assign(this, other).diminution(this.weight.simples());
        Match alt = this.weight.relate(other).assign(this, other).diminution(1);
        result = result.minimum(alt);
        if (result.isIncongruous()) return result;
        return result.approve();
    }

    /**
     * <b>Relates</b> this attribute sort to a primitive sort and returns the
     * resulting match. If no match has already been stored, alternatively,
     * the base and weight sorts of the attribute sort are related to the primitive sort, and the minimum match is stored.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    Match relate(PrimitiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = this.base.relate(other).assign(this, other).diminution(this.weight.simples());
        Match alt = this.weight.relate(other).assign(this, other).diminution(1);
        result = result.minimum(alt);
        if (result.isIncongruous()) return result;
        return result.approve();
    }

    /**
     * <b>Relates</b> this attribute sort to another attribute sort and returns
     * the resulting match. Two attribute sorts are denoted equivalent if their
     * canonical definitions are identical. Two attribute sorts are denoted
     * similar if both their respective base and weight sorts are similar. They are denoted convertible if ...
     * They are denoted incomplete if ... Otherwise, no match exists.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    Match relate(AttributeSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        if (this.equals(other))
            return Match.identical(this).approve();
        if (this.canonical.equals(other.canonical))
            return Match.equivalent(this, other).approve();
        result = this.base.relate(other.base).combine(this.weight.relate(other.weight), this, other);
        if (result.isSimilar()) return result.approve();
        // Convertible
        Match reverse = result.reverse();
        result = this.attributeMatch(other, result);
        reverse = other.attributeMatch(this, reverse);
        if (result.isConvertible()) {
            reverse.approve();
            return result.approve();
        }
        // Incomplete
        Match store = result, alt;
        alt = this.relate(other.weight()).assign(this, other).augmentation(1);
        result = result.minimum(alt);
        alt = this.weight.relate(other).assign(this, other).diminution(1);
        result = result.minimum(alt);
        if (result == store)
            reverse.approve();
        return result.approve();
    }

    /**
     * <b>Relates</b> this attribute sort to a disjunctive sort and returns
     * the resulting match. If no match has already been stored, the disjunctive sort
     * is instead related to the attribute sort. If a match is found for this, it is reversed then stored.
     * @param other a {@link DisjunctiveSort} object
     * @return a {@link Match} object
     * @see DisjunctiveSort#relate(AttributeSort)
     */
    Match relate(DisjunctiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }

    /**
     * <b>Relates</b> this attribute sort to a recursive sort and returns
     * the resulting match. If no match has already been stored, the recursive sort
     * is instead related to the attribute sort. If a match is found for this, it is reversed then stored.
     * @param other a {@link RecursiveSort} object
     * @return a {@link Match} object
     * @see RecursiveSort#relate(AttributeSort)
     */
    Match relate(RecursiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = other.relate(this);
        if (result.isIncongruous()) return result;
        return result.reverse().approve();
    }
    
    
}
