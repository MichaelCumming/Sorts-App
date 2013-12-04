/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Form.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.form;

import cassis.*;
import cassis.sort.*;
import cassis.ind.Individual;
import cassis.visit.ElementVisitor;
import cassis.parse.*;

/**
 * A <b>form</b> is a collection of {@link Individual}s (or forms) of the same
 * {@link Sort}. Forms belong to a few types, where each type specifies
 * a particular behavior for forms under common arithmetic operations such as
 * sum, difference, and product. Each characteristic {@link Individual} has
 * a behavior assigned.
 * <p>
 * <i>Form</i> types distinguish between singly- and multiply-associated sorts.
 * A {@link SinglyForm} specifies a form to contain only a single individual,
 * resulting in singly-associated sorts. An example is the {@link OrdinalForm}
 * specifying an ordinal behavior on numbers: any set of numbers is reduced to
 * the single maximum number, all other numbers in the original set are a part
 * of this maximum number.<br>
 * A {@link MultiplyForm} allows a form to contain any number of individuals,
 * resulting in multiply-associated sorts. A {@link DiscreteForm} specifies a
 * discrete behavior corresponding to a set: any two individuals are either
 * identical or disjoint. An {@link IntervalForm} specifies a behavior of
 * intervals on a one-dimensional axis: if two intervals overlap, these are
 * combined into a single interval. A {@link RelationalForm} applies to
 * (bi-directional) relations. While its behavior is that of a discrete form,
 * relations are ultimately dependent on the individuals these relate and are
 * removed if any of their associate individuals are removed.<br>
 * Finally, a {@link MetaForm} specifies a form consisting itself of forms from
 * different sorts. This corresponds to a {@link DisjunctiveSort} as
 * a disjunctive combination of various sorts. Any individual belongs to
 * exactly one of the operand sorts.
 * <p>
 * The <b>Form</b> class specifies the interface as well as some common
 * functionality for all subclasses. Each subclass specifies a particular
 * behavior for forms. A form is commonly represented as a list of elements,
 * either individuals or forms. If a form defines an attribute form to
 * an individual, this individual is denoted the associate to this form and
 * is represented as such.
 * A form is denoted maximal if it is represented in its canonical form.
 * That is, the elements are ordered and no two elements in this form can be
 * combined into a single element of the same class. Forms are always
 * maximalized before these are compared or output.
 */
public abstract class Form extends Element {
    
    // presentation
    //private Individual assoc;
    
    // constructor
    
    /**
     * Constructs a general <b>form</b> for the specified sort.
     * @param sort a {@link Sort} object
     */
    Form(Sort sort) {
        super(sort);
    }
    
    // access methods
    
    /**
     * Checks if this form <b>is maximal</b>. A form is maximal if it is
     * canonical.
     * @return a boolean
     */
    abstract boolean isMaximal();
    /**
     * Returns the size of this form.
     * @return an integer
     */
    public abstract int size();
    
    /**
     * Returns the <b>current</b> element in this form.
     * @return an {@link Element} object
     */
    public abstract Element current();
    /**
     * Returns the <b>first</b> element in this form.
     * @return an {@link Element} object
     */
    public abstract Element first();
    /**
     * Sets the lead <b>to the beginning</b> of this form.
     * The first element becomes current.
     */
    public abstract void toBegin();
    /**
     * Sets the lead <b>to the next</b> element in this form.
     * The next element becomes current.
     */
    public abstract void toNext();
    /**
     * Checks if the lead is <b>beyond</b> the last element in this form.
     * In this case, there is no current element.
     * @return a boolean
     */
    public abstract boolean beyond();
    
    // methods
    
    /**
     * <b>Sets the sort</b> this form belongs to. This method cannot be used
     * to convert a form to a different sort.
     * @param sort a sort
     * @throws IllegalOverwriteException if the form has already been assigned
     * a sort
     */
    public void setSort(Sort sort) throws IllegalOverwriteException {
        if (this.ofSort() != null)
            throw new IllegalOverwriteException("Sort already set");
        super.setSort(sort);
    }
    
    /**
     * <b>Sets the associate</b> to this form. The associate of a form is
     * typically the individual this form is an attribute of. If the associate
     * individual is specified to be <tt>null</tt>, then nothing happens.
     * @param assoc an {@link Individual} object
     * @throws IllegalArgumentException if the associate individual
     * is of an incompatible sort
     * @throws IllegalOverwriteException if the form is already used, i.e.,
     * belongs to a meta-form
     * @see #associate()
     * @see Element#setAssociate
     */
    public void setAssociate(Individual assoc) throws IllegalArgumentException, IllegalOverwriteException {
        if (assoc == null) return;
        if (this.used())
            throw new IllegalOverwriteException("Associate cannot be modified");
        Sort sort = assoc.ofSort();
        if (sort instanceof RecursiveSort) sort = ((RecursiveSort) sort).instance();
        if (!((AttributeSort) sort).weight().equals(this.ofSort()) && !((AttributeSort) sort).weight().contains(this.ofSort()))
            throw new IllegalArgumentException("Associate of incompatible sort");
        // if (this.associate != null)
        //    throw new IllegalOverwriteException("Associate already set");
        super.setAssociate(assoc);
    }
    
    /**
     * <b>Sets the associate</b> to this form to be the associate of the
     * specified meta-form.
     * @param assoc an {@link MetaForm} object
     * @throws IllegalArgumentException if the meta-form is of an incompatible
     * sort
     * @see #setAssociate(Individual)
     */
    void setAssociate(MetaForm assoc) throws IllegalArgumentException {
        //if (!((Sort) assoc.ofSort()).contains(this.ofSort()))
        //    throw new IllegalArgumentException("Associate argument of incompatible sort");
        this.setAssociate(assoc.associate());
    }
    
    // behavioral methods
    
    /**
     * <b>Maximalizes</b> this form. This converts it into a canonical form.
     */
    public abstract void maximalize();
    /**
     * Determines the <b>sum</b> of this form with another form. This form
     * contains the result, the other form is also modified.
     * @param other a form
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     */
    public abstract boolean sum(Form other);
    /**
     * Determines the <b>difference</b> of this form with another form.
     * This form contains the result, the other form is also modified.
     * @param other a form
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     */
    public abstract boolean difference(Form other);
    /**
     * Determines the <b>product</b> of this form with another form.
     * This form contains the result, the other form is also modified.
     * @param other a form
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     */
    public abstract boolean product(Form other);
    /**
     * Determines the <b>symmetric difference</b> of this form with another
     * form. This form contains the result, the other form is also modified.
     * @param other a form
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     */
    public abstract boolean symdifference(Form other);
    /**
     * <b>Partitions</b> this form wrt another form. The result is specified
     * in three parts. This form contains only all elements that did not belong
     * to the other form. Similarly, the other form contains only all elements
     * that did not belong to this form. Finally, all elements that belonged to
     * both forms are contained in a third form.
     * @param other a second form
     * @param common a third form
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     */
    public abstract boolean partition(Form other, Form common);
    /**
     * Checks if this form is a <b>part of</b> another form.
     * @param other a form
     * @return <tt>true</tt> if it is a part, <tt>false</tt> otherwise
     */
    public abstract boolean partOf(Form other);
    /**
     * <b>Duplicates</b> another form into this form.
     * @param other a form
     */
    public abstract void duplicate(Form other);
    /**
     * <b>Adds</b> an element to this form.
     * @param data an {@link cassis.Element} object
     */
    public abstract void add(Element data);
    /**
     * <b>Deletes</b> the current element from this form.
     */
    public abstract void delete();
    
    /**
     * Converts this element <b>to a string</b>.
     * @param assoc an {@link cassis.ind.Individual} object
     * @return a <tt>String</tt> object
     */
    public final String toString(Individual assoc) {
        return this.toString();
    }
    
    /**
     * <b>Parses</b> xxx
     */
    public abstract void parse(ParseReader reader) throws ParseException;
}
