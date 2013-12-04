/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Element.java'                                            *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis;

import cassis.sort.Sort;
import cassis.ind.Individual;
import cassis.visit.ElementVisitor;
import cassis.convert.Converter;

/**
 * An <b>Element</b> specifies an abstract data element to a
 * {@link cassis.sort.Sort}. An <i>element</i> may have either a single value
 * or multiple, alternative, values. An {@link cassis.ind.Individual} defines
 * an element with a single value. A {@link cassis.form.Form} defines
 * a collection of individuals, resulting in an element with multiple values.
 * <br>An element may have an associate individual defined.
 * If an {@link cassis.ind.Individual#attribute} form is assigned to an
 * individual, this individual is the associate to this form. Then, during a
 * (traversal) visit to this form, this associate individual is typically also
 * assigned as the associate to any individuals in this form.
 * <p>
 * The <b>Element</b> class offers an implementation of the {@link Thing} class
 * that is common to all elements of a sort. Every element belongs to
 * a {@link cassis.sort.Sort}, and can be converted to belong to a different sort
 * (upon condition that the sorts can be matched). Additionally, each element
 * has a use counter, which tracks the number of collections this element
 * belongs to. In the case of an individual, these collections are forms,
 * in the case of a form, these collections are {@link cassis.form.MetaForm}s.
 * Finally, each element may have an associate individual specified.
 * @see cassis.visit.ElementVisitor
 */

public abstract class Element implements Thing {
    
    // representation
    private int uses;
    private Sort sort;
    private Individual assoc;
    
    // constructor
    /**
     * Constructs an <b>element</b> for the specified sort and initializes
     * its use counter to zero (specifying that the element is currently not
     * used).
     * @param sort a {@link cassis.sort.Sort} object
     */
    protected Element(Sort sort) {
        this.uses = 0;
        this.sort = sort;
        this.assoc = null;
    }
    
    // access methods
    
    /**
     * Returns the <b>sort</b> to which this element belongs.
     * @return a {@link cassis.sort.Sort} object
     */
    public final Sort ofSort() {
        return this.sort;
    }
    
    /**
     * Returns the <b>associate</b> individual to this element.
     * @return an {@link cassis.ind.Individual} object
     */
    public Individual associate() {
        return this.assoc;
    }
    
    /**
     * Checks whether this element equals <b>nil</b>. There is no unique
     * <i>nil</i> element. Each sort defines an algebra with its own nil
     * element. Furthermore, though the name <tt>nil</tt> is used
     * interchangeably, a distinction must be made between nil as an individual
     * and nil as a form. An element that is a form is nil on condition that
     * the form is empty. The condition upon which an element that is an
     * individual is nil depends on the sort this element belongs to.
     * @return <tt>true</tt> if the element equals nil, <tt>false</tt>
     * otherwise.
     */
    public abstract boolean nil();
    
    // methods
    
    /**
     * Increments the <b>use</b> counter of this element.
     */
    public final void addUse() { this.uses++; }
    
    /**
     * Decrements the <b>use</b> counter of this element.
     * @throws IndexOutOfBoundsException when an attempt is made to
     * decrement a counter with zero value.
     */
    public final void delUse() throws IndexOutOfBoundsException {
        if (this.uses-- <= 0)
            throw new IndexOutOfBoundsException("negative use value");
    }
    
    /**
     * Determines from the use counter whether this element is <b>used</b>.
     * @return <tt>true</tt> if this element is being used, <tt>false</tt>
     * otherwise
     */
    public final boolean used() { return (this.uses > 0); }
    
    /**
     * <b>Sets the sort</b> this element belongs to.
     * @param sort a {@link cassis.sort.Sort} object
     * @throws IllegalArgumentException if the sort is <tt>null</tt>
     */
    protected void setSort(Sort sort) throws IllegalArgumentException {
        if (sort == null)
            throw new IllegalArgumentException("Sort cannot be null");
        this.sort = sort;
    }
    
    /**
     * <b>Sets the associate</b> individual to this element.
     * @param assoc an {@link cassis.ind.Individual} object
     */
    protected void setAssociate(Individual assoc) {
        this.assoc = assoc;
    }
    
    /**
     * <b>Duplicates</b> this element. Contrary to the <tt>clone</tt> method
     * of the <tt>Object</tt> class, duplicating an element does not
     * necessarily maintain all links to other objects that are a part of
     * the representation of this thing.
     * @return a duplicate element
     */
    public abstract Element duplicate();
    
    /**
     * Tests whether this element is strictly <b>less than</b> another thing.
     * The {@link #compare} method is used to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessThan(Thing other) {
        return (this.compare(other) == LESS);
    }
    /**
     * Tests whether this element is strictly <b>greater than</b> another thing.
     * The {@link #compare} method is used to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterThan(Thing other) {
        return (this.compare(other) == GREATER);
    }
    /**
     * Tests whether this element is <b>less than or equal</b> to another thing.
     * The {@link #compare} method is used to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == LESS) || (c == EQUAL));
    }
    /**
     * Tests whether this element is <b>greater than or equal</b> to another
     * thing. The {@link #compare} method is used to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == GREATER) || (c == EQUAL));
    }
    
    /**
     * <b>Purges</b> this element if it is not currently used. Purging an
     * element removes all dependencies on other elements, thereby decrementing
     * their respective use counters. A purged element should be discarded.
     */
    public abstract void purge();
    
    /**
     * Converts this element <b>to a string</b> wrt to the associate individual.
     * @param assoc an {@link cassis.ind.Individual} object
     * @return a <tt>String</tt> object
     */
    public abstract String toString(Individual assoc);
    
    /**
     * <b>Accepts</b> an element-visitor to this element wrt an associate
     * individual.
     * @param visitor an {@link cassis.visit.ElementVisitor} object
     * @param assoc an {@link cassis.ind.Individual} object that is an associate
     * to this element
     */
    public abstract void accept(ElementVisitor visitor, Individual assoc);
    
    /**
     * <b>Converts</b> this element into a new element that belongs to the
     * specified sort. The result of this conversion is dependent on the match
     * that is found between the sort that the element currently belongs to and
     * the new target sort.
     * @param sort a {@link cassis.sort.Sort} object
     * @return an element belonging to the specified sort
     * @see cassis.convert.Converter#convert(cassis.sort.Sort,Element)
     */
    public final Element convert(Sort sort) {
        return Converter.convert(sort, this);
    }
}
