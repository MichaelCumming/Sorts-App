/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `Form.java'                                               *
 * written by: Rudi Stouffs                                  *
 * last modified: 3.8.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  A FORM is a collection of individuals (see `Individual.java')
//  (or forms) of the same sort (see `Sort.java').
//  The Form "class" specifies the interface for all subclasses.
//  These subclasses specify the appropriate algebraic behavior of forms.
//  There are only a limited number of allowable behaviors.
//  Each such behavior specifies a category of sorts.
//  Each template specifies a category of (primitive) sorts.
//  I distinguish between singly- and multiply-associated sorts.
//  A singly-associated sort specifies a form to contain only a single
//  individual. Multiply-associated sorts allow a form to contain
//  any number of individuals. (see `MultiplyForm.java')
//  I distinguish multiple categories to exist among multiply-associated sorts.
//  The discrete category (see `DiscreteForm.java') applies when
//  any two individuals can always be said to be either identical or disjoint.

package blue.form;

import blue.*;
import blue.sort.Sort;
import blue.sort.AttributeSort;
import blue.sort.DisjunctiveSort;
import blue.sort.RecursiveSort;
import blue.ind.Individual;
import blue.io.*;
import blue.proc.Traversal;

public abstract class Form extends Element {
    // presentation
    private Individual assoc;

    // constructor
    Form(Sort sort) {
        super(sort);
    }

    // access methods
    abstract boolean isMaximal();

    abstract int size();

    public abstract Element current();

    public abstract Element first();

    public abstract void toBegin();

    public abstract void toNext();

    public abstract boolean beyond();

    public abstract void delete();

    public Individual associate() {
        return this.assoc;
    }
    // methods

    /**
     * Sets the sort this form belongs to. This method cannot be used to convert a form to a different sort.
     * @param sort a sort
     * @exception IllegalOverwriteException Occurs if the form had already been assigned a sort.
     */
    public void setSort(Sort sort) throws IllegalOverwriteException {
        if (this.ofSort() != null)
            throw new IllegalOverwriteException("Sort already set");
        super.setSort(sort);
    }

    public void setAssociate(Element assoc) throws IllegalArgumentException, IllegalOverwriteException {
        if (assoc == null)
            throw new IllegalArgumentException("Associate argument cannot be null");
        if (this.used())
            throw new IllegalOverwriteException("Associate cannot be modified");
        if (assoc instanceof Individual)
            this.setIndAssociate((Individual)assoc);
        else if (assoc instanceof MetaForm)
            this.setFormAssociate((MetaForm)assoc);
        else
            throw new IllegalArgumentException("Associate argument of invalid type");
    }

    private void setIndAssociate(Individual assoc) throws IllegalArgumentException, IllegalOverwriteException {
        Sort sort = assoc.ofSort();
        if (sort instanceof RecursiveSort) sort = ((RecursiveSort)sort).instance();
        if (!((AttributeSort)sort).weight().equals(this.ofSort())) {
            System.out.println(((AttributeSort)sort).weight());
            System.out.println(this.ofSort());
            throw new IllegalArgumentException("Associate of incompatible sort");
        }
        // if (this.associate != null)
        //    throw new IllegalOverwriteException("Associate already set");
        this.assoc = assoc;
    }

    private void setFormAssociate(MetaForm assoc) throws IllegalArgumentException, IllegalOverwriteException {
        // if (assoc.associate() == null)
        //    throw new IllegalArgumentException("Associate cannot be null");
        if (!((Sort)assoc.ofSort()).contains(this.ofSort()))
            throw new IllegalArgumentException("Associate argument of incompatible sort");
        this.assoc = assoc.associate();
    }

    // algebra methods
    public abstract void maximalize();

    public abstract boolean sum(Form other);

    public abstract boolean difference(Form other);

    public abstract boolean product(Form other);

    public abstract boolean symdifference(Form other);

    public abstract boolean partition(Form other, Form common);

    public abstract boolean partOf(Form other);

    public abstract void duplicate(Form other);

    public abstract void add(Element ind);

    public abstract Form convert(Sort sort, Element assoc);

    public abstract void parse(ParseReader reader) throws ParseException;

    public abstract void traverse(Traversal proc);
}
