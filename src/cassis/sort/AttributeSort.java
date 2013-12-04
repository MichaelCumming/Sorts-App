/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `AttributeSort.java'                                      *
 * written by: Rudi Stouffs                                  *
 * last modified: 29.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.sort;

import cassis.form.Form;

/**
 * An <b>Attribute Sort</b> specifies a composition of two {@link Sort}s
 * under the <i>attribute</i> operation. Under the attribute operation,
 * each individual of the <i>base</i> sort has a form of the <i>weigth</i>
 * sort assigned as an attribute.
 * An attribute sort may have a name assigned.
 * <p>
 * The <b>AttributeSort</b> class represents an attribute sort additionally by
 * its base sort (left operand), and weight sort (right operand). In the
 * definition of an attribute sort, the attribute operation is denoted by '^'.
 * In its canonical form, an attribute's base specifies a simple sort, i.e.,
 * (a ^ b) ^ c -> a ^ (b ^ c) = a ^ b ^ c.
 */
public final class AttributeSort extends Sort {
    static {
	new cassis.visit.vrml.Proto(AttributeSort.class, "icons/sort.gif");
    }

    private final static String separator = " ^ ";

    // representation

    private Sort base;
    private Sort weight;

    // constructors

    /**
     * Creates an <b>attribute sort</b> with the specified base and weight
     * sorts. Both operand sorts must share the same context, which also
     * becomes the context of the new sort.
     * @param base a {@link Sort} object
     * @param weight a {@link Sort} object
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
     * Returns the <b>base</b> sort of this attribute sort. The base denotes
     * the left operand of the attribute operator.
     * @return a {@link Sort} object
     */
    public Sort base() { return this.base; }
    /**
     * Returns the <b>weight</b> sort of this attribute sort.
     * The weight denotes the right operand of the attribute operator.
     * @return a {@link Sort} object
     */
    public Sort weight() { return this.weight; }

    // methods

    /**
     * Returns a <b>definition</b> for the attribute sort as defined by
     * the specified base and weight sorts.
     * @param base a {@link Sort} object
     * @param weight a {@link Sort} object
     * @return a <tt>String</tt> object
     */
    static String definition(Sort base, Sort weight) {
	String name = base.toString() + separator;
	if ((weight instanceof DisjunctiveSort) && !weight.isNamed())
	    name += '(' + weight.canonical + ')';
	else name += weight.toString();
	return name;
    }

    /**
     * Checks whether an attribute sort already <b>is defined</b> for
     * the specified base and weight sorts.
     * @param base a {@link SimpleSort} object
     * @param weight a {@link Sort} object
     * @return the attribute sort if it is already defined; <tt>null</tt>
     * otherwise
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
     * the specified name. If this name equals the sort's name,
     * this sort is returned and no duplicate is made.
     * @param name a <tt>String</tt> object
     * @return the duplicate sort
     */
    Sort duplicate(String name) {
	if (name.equals(this.name()))
	    return this;
	AttributeSort result = this;
	if (this.isNamed() ||
	    (this == this.context().getAttributeSort(this.canonical))) {
	    result = new AttributeSort(this.base, this.weight);
            if (this.isNamed()) result.definition = this.name();
        }
	result.assign(name);
	return result;
    }

    /**
     * <b>Accepts</b> a sort-visitor to this sort.
     * Firstly, the <tt>visitEnter</tt> method of the sort-visitor is called.
     * If the result is true, then the sort-visitor is sent to the weight of
     * this attribute sort. Finally, the <tt>visitLeave</tt> method of the
     * sort-visitor is called.
     * @param visitor an {@link cassis.visit.SortVisitor} object
     * @see cassis.visit.SortVisitor#visitEnter
     * @see cassis.visit.SortVisitor#visitLeave
     */
    public void accept(cassis.visit.SortVisitor visitor) {
        if (visitor.visitEnter(this)) {
            this.base.accept(visitor);
            this.weight.accept(visitor);
        }
        visitor.visitLeave(this);
    }

    /**
     * Creates a <b>new form</b> for this attribute sort. This form is created
     * according to the base sort but belonging to this sort.
     * @return a {@link cassis.form.Form} corresponding this sort
     */
    public Form newForm() {
        return this.base.newForm(this);
    }
    /**
     * Creates a <b>new form</b> for this attribute sort as a form for
     * the parent sort. This form is created according to the base sort but
     * belonging to the parent sort.
     * @param parent a {@link Sort} object
     * @return a {@link cassis.form.Form} corresponding this sort
     */
    Form newForm(Sort parent) { return this.base.newForm(parent); }

    /**
     * Checks whether this attribute sort <b>contains</b> another sort.
     * This applies if both sorts are attribute sorts with identical base and
     * this sort's weight contains the other sort's weight.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort contains the other,
     * <tt>false</tt> otherwise
     * @see Sort#contains
     */
    public boolean contains(Sort other) {
	if (!(other instanceof AttributeSort) || !this.base.equals(other.base()))
	    return false;
	return this.weight.contains(((AttributeSort) other).weight());
    }

    /**
     * <b>Combines</b> this attribute sort with another sort under
     * the attribute operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     * @throws IllegalArgumentException if both sorts do not share the
     * same context or if the other sort is an {@link AspectsSort} instance
     */
    public Sort combine(Sort other) {
	if ((this.context() != other.context()) || (other instanceof AspectsSort))
	    throw new IllegalArgumentException("AttributeSort.combine: illegal arguments");

	if ((other instanceof DisjunctiveSort) && !other.isNamed()) {
	    DisjunctiveSort result = new DisjunctiveSort(this.context());
	    ((DisjunctiveSort) other).toBegin();
	    while (!((DisjunctiveSort) other).beyond()) {
		result.insert(this.combine(((DisjunctiveSort) other).current()));
		((DisjunctiveSort) other).toNext();
	    }
	    return result;
	}

	//if (!this.isNamed())
	//    return new AttributeSort(this.base, this.weight.combine(other));

	String name = definition(this, other);
	Sort result = this.context().sortOf(name);
	if (result != null) return result;
	result = new AttributeSort(this.base, this.weight.combine(other));
	if (this.isNamed()) result.assign(name);
	return result;
    }
}
