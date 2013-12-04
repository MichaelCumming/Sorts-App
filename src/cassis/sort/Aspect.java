/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Aspect.java'                                             *
 * written by: Rudi Stouffs                                  *
 * last modified: 19.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.sort;

import java.util.Vector;

import cassis.struct.Argument;
import cassis.struct.Parameter;
import cassis.form.Form;

/**
 * An <b>Aspect</b> is a simple {@link Sort} that defines a single, unidirectional view
 * of an {@link AspectsSort}, linking two sorts. Each aspects-sort's argument defines
 * exactly one aspect. An aspect always has a name assigned.
 * <p>
 * The <b>Aspect</b> class represents an aspect additionally by the {@link AspectsSort}
 * instance it belongs to.
 * An aspect is defined as part of the definition of its {@link AspectsSort}.
 */
public class Aspect extends Sort implements SimpleSort {

    // representation

    private AspectsSort instance;
    
    // constructor

    /**
     * Creates an <b>aspect</b> to the specified aspects-sort instance.
     * @param context a {@link Sorts} object
     * @param instance an {@link AspectsSort} object
     */
    Aspect(Sorts context, AspectsSort instance) {
	super(context);
	this.instance = instance;
    }

    // access methods

    /**
     * Returns the number of <b>named</b> components of this sort.
     * This value is retrieved from the aspects-sort instance.
     * @return an integer value
     * @see AspectsSort#names
     */
    public int names() { return this.instance.names(); }
    /**
     * Returns the number of <b>simple</b> components of this sort.
     * This value is retrieved from the aspects-sort instance.
     * @return an integer value
     * @see AspectsSort#simples
     */
    public int simples() { return this.instance.simples(); }
    /**
     * Returns the total number of <b>components</b> of this sort.
     * This value is retrieved from the aspects-sort instance.
     * @return an integer value
     * @see AspectsSort#components
     */
    public int components() { return this.instance.components(); }
    /**
     * Returns this aspect's <b>instance</b> sort.
     * @return a <tt>Sort</tt> object
     */
    public Sort instance() { return this.instance; }

    /**
     * Returns the class specifying the <b>characteristic</b> individual
     * for this aspect.
     * This class is retrieved from the aspects-sort instance.
     * @return a <tt>Class</tt> object
     * @see AspectsSort#characteristic()
     */
    public Class characteristic() { return this.instance.characteristic(); }
    /**
     * Returns the <b>arguments</b> of this aspect.
     * These are retrieved from the aspects-sort instance.
     * @return an {@link cassis.struct.Argument} object
     * @see AspectsSort#arguments()
     */
    public Argument arguments() { return this.instance.arguments(); }

    /**
     * Returns an array of all <b>aspects</b> belonging to this aspect's
     * aspects-sort instance.
     * @return an <tt>Aspect</tt> array
     * @see AspectsSort#aspects()
     */
    public Aspect[] aspects() { return this.instance.aspects(); }
    /**
     * Returns the <b>argument</b> sort of this aspect's aspects-sort instance that
     * corresponds to this aspect.
     * @return a {@link Sort} object
     * @see AspectsSort#argument
     */
    public Sort argument() { return this.instance.argument(this); }

    // methods

    /**
     * Throws an illegal-argument-exception. An aspect cannot be duplicated.
     * @throws IllegalArgumentException always
     */
    Sort duplicate(String name) throws IllegalArgumentException {
	throw new IllegalArgumentException("Aspect '" + this.canonical + "'");
    }

    /**
     * Creates a <b>new form</b> for this aspect as a form for its aspect-sort instance.
     * That is, the form is created according to the aspect-sort, but belongs to this aspect.
     * @return a {@link cassis.form.Form} corresponding this sort
     * @see AspectsSort#newForm(Sort)
     */
    public Form newForm() {
	return this.instance.newForm(this);
    }
    /**
     * Creates a <b>new form</b> for this aspect's aspect-sort instance as a form
     * for the parent sort.
     * For example, in the case of an {@link AttributeSort}, the form is created
     * according to the base sort, but belongs to the attribute sort.
     * @param parent a <tt>Sort</tt> object
     * @return a {@link cassis.form.Form} corresponding this sort
     * @see AspectsSort#newForm(Sort)
     */
    Form newForm(Sort parent) {
	return this.instance.newForm(parent);
    }

    /**
     * <b>Combines</b> this aspect with another sort under the attribute
     * operation.
     * @param other a <tt>Sort</tt> object
     * @return the sort resulting from the attribute operation
     * @throws IllegalArgumentException if the other sort is an instance of
     * {@link AspectsSort}
     */
    public Sort combine(Sort other) throws IllegalArgumentException {
        if (other instanceof AspectsSort)
            throw new IllegalArgumentException("Aspects sort '" + other.canonical + "'");
        return new AttributeSort(this, other);
    }
}
