/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `RecursiveSort.java'                                      *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.sort;

import cassis.IllegalOverwriteException;
import cassis.form.Form;
import cassis.struct.Argument;

/**
 * A <b>Recursive Sort</b> specifies any sort that is recursively defined.
 * Necessarily, a recursive sort always has a name assigned.
 * <p>
 * The <b>RecursiveSort</b> class represents a recursive sort additionally by
 * its instance sort.
 */
public class RecursiveSort extends Sort {

    // representation

    Sort instance;

    // constructor
    
    /**
     * Creates a <b>recursive sort</b> with a specified name within the specified
     * context. The sort's instance must be specified later, when the definition
     * is completed.
     * @param context a {@link Sorts} object
     * @param name a <tt>String</tt> object
     * @see #setInstance
     */
    RecursiveSort(Sorts context, String name) {
	super(context, name);
	this.instance = null;
    }

    /**
     * <b>Sets the instance</b> of this recursive sort. This completes the definition
     * of this recursive sort.
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
    public int names() { return this.instance.names(); }
    /**
     * Returns the number of <b>simple</b> components of this recursive sort.
     * This value is retrieved from the recursive sort's instance sort.
     * @return an integer value
     */
    public int simples() { return this.instance.simples(); }
    /**
     * Returns the total number of <b>components</b> of this recursive sort.
     * This value is retrieved from the recursive sort's instance sort.
     * @return an integer value
     */
    public int components() { return this.instance.components(); }
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
     * the specified name. If this name equals the sort's name,
     * this sort is returned and no duplicate is made.
     * @param name a <tt>String</tt> object
     * @return the duplicate sort
     */
    Sort duplicate(String name) {
	if (name.equals(this.name()))
	    return this;
	return this.instance.duplicate(name);
    }

    /**
     * Creates a <b>new form</b> for this recursive sort.
     * This form is created according to the instance sort but belonging to this sort.
     * @return a {@link cassis.form.Form} corresponding this sort
     */
    public Form newForm() {
        return this.instance.newForm(this);
    }
    /**
     * Creates a <b>new form</b> for this recursive sort.
     * The argument sort is ignored
     * @param parent a {@link Sort} object
     * @return a {@link cassis.form.Form} corresponding this sort
     */
    Form newForm(Sort parent) { return this.instance.newForm(this); }

    /**
     * Checks whether this recursive sort <b>contains</b> another sort.
     * This applies if this sort's instance contains the other sort.
     * @param other a sort for comparison
     * @return <tt>true</tt> if this sort contains the other,
     * <tt>false</tt> otherwise
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
     * @return <tt>true</tt> if this sort contains the other,
     * <tt>false</tt> otherwise
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
     * <b>Combines</b> this recursive sort with another sort under the attribute
     * operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     * @throws IllegalArgumentException if both sorts do not share the
     * same context or if the other sort is an {@link AspectsSort} instance
     */
    public Sort combine(Sort other) throws IllegalArgumentException {
	if ((this.context() != other.context()) || (other instanceof AspectsSort))
	    throw new IllegalArgumentException("AttributeSort.combine: illegal arguments");

        String name = AttributeSort.definition(this, other);
        Sort result = this.instance.combine(other);
        result.assign(name);
        return result;
    }
}
