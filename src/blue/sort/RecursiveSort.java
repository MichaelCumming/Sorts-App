/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `RecursiveSort.java'                                      *
 * written by: Rudi Stouffs                                  *
 * last modified: 17.1.02                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.sort;

import blue.IllegalOverwriteException;
import blue.form.Form;
import blue.struct.Argument;
import blue.io.GraphicsContext;

/**
 * A <b>Recursive Sort</b> specifies any sort that is recursively defined.
 * Necessarily, a recursive sort always has a name assigned. <p>
 * The <b>RecursiveSort</b> class represents a recursive sort additionally by its instance sort.
 */
public class RecursiveSort extends Sort {
    // representation
    Sort instance;
    // constructor

    /**
     * Creates a <b>recursive sort</b> with a specified name within the specified
     * context. The sort's instance must be specified later, when the definition is completed.
     * @param context a {@link Sorts} object
     * @param name a <tt>String</tt> object
     * @see #setInstance
     */
    RecursiveSort(Sorts context, String name) {
        super(context, name);
        this.instance = null;
    }

    /**
     * <b>Sets the instance</b> of this recursive sort. This completes the definition of this recursive sort.
     * @param instance a {@link Sort} object
     * @throws IllegalOverwriteException if the specified instance is <tt>null</tt>
     */
    void setInstance(Sort instance) throws IllegalOverwriteException {
        if (this.instance != null)
            throw new IllegalOverwriteException("This recursive sort has already been specified an instance");
        this.instance = instance;
        this.canonical = this.instance.canonical;
        this.definition = this.instance.definition;
    }
    // access methods

    /**
     * Returns the number of <b>named</b> components of this recursive sort.
     * This value is retrieved from the recursive sort's instance sort.
     * @return an integer value
     */
    int names() { return this.instance.names(); }

    /**
     * Returns the number of <b>simple</b> components of this recursive sort.
     * This value is retrieved from the recursive sort's instance sort.
     * @return an integer value
     */
    int simples() { return this.instance.simples(); }

    /**
     * Returns the total number of <b>components</b> of this recursive sort.
     * This value is retrieved from the recursive sort's instance sort.
     * @return an integer value
     */
    int components() { return this.instance.components(); }

    /**
     * Returns this recursive sort's <b>instance</b> sort.
     * @return a <tt>Sort</tt> object
     */
    public Sort instance() { return this.instance; }

    /**
     * Returns the <b>base</b> sort of this recursive sort's instance sort.
     * @return a <tt>Sort</tt> object
     * @see Sort#base
     */
    public Sort base() { return this.instance.base(); }
    // public Class characteristic() { return this.instance.characteristic(); }
    // public Argument arguments() { return this.instance.arguments(); }

    /**
     * Creates a <b>duplicate</b> of this recursive sort and assigns it
     * the specified name. If this name equals the sort's name, this sort is returned and no duplicate is made.
     * @param name a <tt>String</tt> object
     * @return the duplicate sort
     */
    Sort duplicate(String name) {
        if (name.equals(this.name()))
            return this;
        return this.instance.duplicate(name);
    }

    /**
     * Creates a <b>new form</b> for this recursive sort. This form is created according to the instance sort but
     * belonging to this sort.
     * @return a {@link blue.form.Form} corresponding this sort
     */
    public Form newForm() {
        return this.instance.newForm(this);
    }

    /**
     * Creates a <b>new form</b> for this recursive sort. The argument sort is ignored
     * @param parent a {@link Sort} object
     * @return a {@link blue.form.Form} corresponding this sort
     */
    Form newForm(Sort parent) { return this.instance.newForm(this); }

    /**
     * <b>Visualize</b> this recursive sort using the specified graphics context.
     * @param gc a {@link blue.io.GraphicsContext} object
     */
    public void visualize(GraphicsContext gc) {
        // this.instance.visualize(gc);
    }

    /**
     * Checks whether this recursive sort <b>contains</b> another sort.
     * This applies if this sort's instance contains the other sort.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort contains the other, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both sorts do not share the same context
     * @see Sort#contains
     */
    public boolean contains(Sort other) throws IllegalArgumentException {
        if (this == other) return false;
        if (this.context() != other.context())
            throw new IllegalArgumentException("RecursiveSort.contains: illegal arguments");
        return this.instance.contains(other);
    }

    /**
     * Checks whether this recursive sort is a <b>part of</b> another sort.
     * This applies if this sort's instance is a part of the other sort.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort contains the other, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both sorts do not share the same context
     * @see Sort#partOf
     */
    public boolean partOf(Sort other) throws IllegalArgumentException {
        if (this == other) return false;
        if (this.context() != other.context())
            throw new IllegalArgumentException("RecursiveSort.contains: illegal arguments");
        return this.instance.partOf(other);
    }

    /**
     * <b>Combines</b> this recursive sort with another sort under the attribute operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     * @throws IllegalArgumentException if both sorts do not share the
     * same context or if the other sort is an {@link AspectsSort} instance
     */
    Sort combine(Sort other) throws IllegalArgumentException {
        if ((this.context() != other.context()) || (other instanceof AspectsSort))
            throw new IllegalArgumentException("AttributeSort.combine: illegal arguments");
        String name = AttributeSort.definition(this, other);
        Sort result = this.instance.combine(other);
        result.assign(name);
        return result;
    }

    /**
     * <b>Relates</b> this recursive sort to an aspect and returns the resulting match.
     * If no match has already been stored, the recursive sort's instance sort
     * is related to the other sort and the resulting match is stored.
     * A temporary match is used to avoid an endless recursion.
     * @param other an {@link Aspect} object
     * @return a {@link Match} object
     */
    Match relate(Aspect other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        new Match(Match.incongruousLevel, this, other).approve();
        result = this.instance.relate(other);
        Match.disapprove(this, other);
        if (result.isIncongruous()) return result;
        return result.assign(this, other).approve();
    }

    /**
     * <b>Relates</b> this recursive sort to a primitive sort and returns the
     * resulting match. If no match has already been stored, the recursive sort's
     * instance sort is related to the other sort and the resulting match is stored.
     * A temporary match is used to avoid an endless recursion.
     * @param other a {@link PrimitiveSort} object
     * @return a {@link Match} object
     */
    Match relate(PrimitiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        new Match(Match.incongruousLevel, this, other).approve();
        result = this.instance.relate(other);
        Match.disapprove(this, other);
        if (result.isIncongruous()) return result;
        return result.assign(this, other).approve();
    }

    /**
     * <b>Relates</b> this recursive sort to an attribute sort and returns the
     * resulting match. If no match has already been stored, alternatively,
     * the recursive sort's instance sort is related to the other sort and
     * the recursive sort is related to the attribute sort's weight sort. The minimum of both matches is stored.
     * A temporary match is used to avoid an endless recursion.
     * @param other an {@link AttributeSort} object
     * @return a {@link Match} object
     */
    Match relate(AttributeSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        result = this.relate(other.weight()).assign(this, other).diminution(1);
        new Match(Match.incongruousLevel, this, other).approve();
        Match alt = this.instance.relate(other).assign(this, other);
        Match.disapprove(this, other);
        result = result.minimum(alt);
        if (result.isIncongruous()) return result;
        return result.approve();
    }

    /**
     * <b>Relates</b> this recursive sort to a disjunctive sort and returns the
     * resulting match. If no match has already been stored, the recursive sort's
     * instance sort is related to the other sort and the resulting match is stored.
     * A temporary match is used to avoid an endless recursion.
     * @param other a {@link DisjunctiveSort} object
     * @return a {@link Match} object
     */
    Match relate(DisjunctiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        new Match(Match.incongruousLevel, this, other).approve();
        result = this.instance.relate(other);
        Match.disapprove(this, other);
        if (result.isIncongruous()) return result;
        return result.assign(this, other).approve();
    }

    /**
     * <b>Relates</b> this recursive sort to a recursive sort and returns the
     * resulting match. If no match has already been stored, this sort's
     * instance sort is related to the other sort and the resulting match is stored.
     * A temporary match is used to avoid an endless recursion.
     * @param other a {@link RecursiveSort} object
     * @return a {@link Match} object
     */
    Match relate(RecursiveSort other) {
        Match result = Match.lookup(this, other);
        if (result != null) return result;
        new Match(Match.incongruousLevel, this, other).approve();
        result = this.instance.relate(other);
        Match.disapprove(this, other);
        if (result.isIncongruous()) return result;
        return result.assign(this, other).approve();
    }
}
