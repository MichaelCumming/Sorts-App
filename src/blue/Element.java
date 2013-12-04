/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Element.java'                                            *
 * written by: Rudi Stouffs                                  *
 * last modified: 06.2.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue;

import blue.sort.Sort;
import blue.io.SdlContext;
import blue.io.GraphicsContext;

/**
 * An <b>Element</b> specifies an abstract data element to a {@link blue.sort.Sort}.
 * An <i>element</i> may have either a singular value or multiple, alternative, values.
 * An {@link blue.ind.Individual} defines an element with a singular value.
 * A {@link blue.form.Form} defines a collection of individuals, resulting in an element with multiple values. <p>
 * The <b>Element</b> class offers an implementation of the {@link Thing} class that is
 * common to all elements of a sort. Every element belongs to a {@link blue.sort.Sort},
 * and can be converted to belong to a different sort (upon condition that the sorts
 * can be matched). Additionally, each element has a use counter, which tracks
 * the number of collections this element belongs to. In the case of an individual,
 * these collections are forms, in the case of forms, these collections are {@link blue.form.MetaForm}s.
 */
public abstract class Element implements Thing {
    // representation
    private int uses;
    private Sort sort;
    // constructor

    /**
     * Constructs an <b>element</b> for the specified sort and initializes
     * its use counter to zero (specifying that the element is currently not used).
     * @param sort a {@link blue.sort.Sort} object
     */
    public Element(Sort sort) {
        this.uses = 0;
        this.sort = sort;
    }
    // access methods

    /**
     * Returns the <b>sort</b> to which this element belongs.
     * @return a {@link blue.sort.Sort} object
     */
    public final Sort ofSort() {
        return this.sort;
    }

    /**
     * Checks whether this element equals <b>nil</b>. There is no unique <i>nil</i>
     * element. Each sort defines an algebra with its own nil element. Furthermore,
     * though the name <tt>nil</tt> is used interchangeably, a distinction must
     * be made between nil as an individual and nil as a form. An element that is a form is nil on condition that the form is
     * empty. The condition upon which an element that is an individual is nil depends on the sort this element belongs to.
     * @return <tt>true</tt> if the element equals nil, <tt>false</tt> otherwise.
     */
    public abstract boolean nil();
    // methods

    /** Increments the use counter of this thing. */
    public final void addUse() { this.uses++; }

    /**
     * Decrements the use counter of this thing.
     * @throws IndexOutOfBoundsException when an attempt is made to decrement a counter with zero value.
     */
    public void delUse() throws IndexOutOfBoundsException {
        if (this.uses-- <= 0)
            throw new IndexOutOfBoundsException("negative use value");
    }

    /**
     * Determines from the use counter whether this thing is being <b>used</b>.
     * @return <tt>true</tt> if this thing is being used, <tt>false</tt> otherwise
     */
    public final boolean used() { return (this.uses > 0); }

    /**
     * <b>Sets the sort</b> this element belongs to.
     * @param sort a sort
     */
    protected void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * <b>Duplicates</b> this element. Contrary to the <tt>clone</tt> method of the
     * <tt>Object</tt> class, duplicating an element may duplicate other
     * elements that are part of the representation of this element.
     * @return a duplicate element
     */
    public abstract Element duplicate();

    /**
     * Tests whether this element is strictly <b>less than</b> another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessThan(Thing other) {
        return (this.compare(other) == LESS);
    }

    /**
     * Tests whether this element is strictly <b>greater than</b> another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterThan(Thing other) {
        return (this.compare(other) == GREATER);
    }

    /**
     * Tests whether this element is <b>less than or equal</b> to another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean lessOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == LESS) || (c == EQUAL));
    }

    /**
     * Tests whether this element is <b>greater than or equal</b> to another thing.
     * Relies on the {@link #compare} method to achieve this result.
     * @param other the comparison thing
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     */
    public final boolean greaterOrEqual(Thing other) {
        int c = this.compare(other);
        return ((c == GREATER) || (c == EQUAL));
    }

    /**
     * <b>Purges</b> this element if it is not currently used. Purging an element
     * removes all dependencies on other elements, thereby decrementing their
     * respective use counters. A purged element must be discarded.
     */
    public abstract void purge();

    /**
     * Builds an SDL description of this element within the specified context.
     * @param sdl an SDL context
     * @see blue.io.SdlContext
     */
    public abstract void print(SdlContext sdl);

    /**
     * Builds a graphical description of this element within the specified context.
     * @param gc a graphics context
     * @see blue.io.GraphicsContext
     */
    public abstract void visualize(GraphicsContext gc);

    /**
     * <b>Converts</b> this element to a new element that belongs to the specified
     * sort. This sort must match with the sort this element currently belongs to.
     * @param sort a {@link blue.sort.Sort} object
     * @return an element belonging to the specified sort
     * @see blue.sort.Match
     */
    public abstract Element convert(Sort sort);
}
