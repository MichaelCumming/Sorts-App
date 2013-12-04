/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `SinglyForm.java'                                         *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package blue.form;

import blue.*;
import blue.ind.Individual;
import blue.sort.*;
import blue.io.*;
import blue.proc.Traversal;

abstract class SinglyForm extends Form {
    // representation
    private Individual individual;
    private boolean beyond;

    // constructors
    SinglyForm() { this(null); } 

    private SinglyForm(Sort sort) {
        super(sort);
        this.individual = null;
        this.beyond = true;
    }

    // access methods
    public Individual individual() { return this.individual; }

    // methods
    void setIndividual(Individual ind) {
        Sort sort = ind.ofSort();
        if (!this.ofSort().equals(sort)) {
            Match match = sort.relate(this.ofSort());
            if (!match.isConcordant() || !match.isEquivalent())
                throw new IllegalArgumentException("Arguments are not of similar sorts");
            ind = (Individual)ind.convert(this.ofSort());
        } else if (ind.used() && (sort instanceof AttributeSort))
            ind = (Individual)ind.convert(sort);
        if (this.individual != null)
            this.individual.delUse();
        this.individual = ind;
        this.individual.addUse();
    }

    // Check whether this singly form EQUALS the other singly form
    public boolean equals(Object other) {
        if (!(other instanceof SinglyForm) || !this.ofSort().equals(((Form)other).ofSort()))
            return false;
        if ((this.individual != null) && (((SinglyForm)other).individual != null))
            return this.individual.equals(((SinglyForm)other).individual);
        return (this.individual == ((SinglyForm)other).individual);
    }

    // COMPARE two singly forms
    // and return whether these are EQUAL, LESS or GREATER
    public int compare(Thing other) {
        return this.ofSort().compare(((Form)other).ofSort());
    }

    // DUPLICATE this singly form
    public Element duplicate() {
        SinglyForm dup = (SinglyForm)this.ofSort().newForm();
        dup.duplicate(this);
        return dup;
    }

    // DUPLICATE the specified singly form into this singly form
    public void duplicate(Form other) {
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        this.setIndividual((Individual)((SinglyForm)other).individual.convert(this.ofSort()));
    }

    // PRINT this singly form to SDL
    public void print(SdlContext sdl) {
        sdl.setSort(this.ofSort());
        sdl.beginGroup();
        if (!this.nil()) {
            this.individual.print(sdl);
        }
        sdl.endGroup();
    }

    public String toString() {
        if (this.nil()) return "{ }";
        return "{" + this.individual.toString() + " }";
    }

    public void visualize(GraphicsContext gc) {
        if (this.nil()) return;
        gc.beginGroup(this.individual.getClass(), 1);
        this.individual.visualize(gc);
        gc.endGroup();
    }

    // PARSE a string and initialize this form to its value
    public void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != '{')
            throw new ParseException(reader, "'{' expected");
        if (reader.previewToken() != '}')
            this.setIndividual(Individual.parse(this.ofSort(), reader));
        if (reader.newToken() != '}')
            throw new ParseException(reader, "'}' expected");
    }

    // Form interface methods
    public Element current() { return this.individual; }

    public Element first() { return this.individual; }

    public void toBegin() { this.beyond = false; }

    public void toNext() { this.beyond = true; }

    public boolean beyond() { return this.beyond; }

    public boolean isMaximal() { return true; }

    public void maximalize() { }

    // The NIL singly form contains an individual with empty attribute
    public boolean nil() {
        if (this.individual == null) return true;
        if (this.individual.attrDefined())
            return this.individual.attribute().nil();
        return this.individual.nil();
    }

    public int size() {
        if (this.nil()) return 0;
        return 1;
    }

    // DELETE a singly form
    public void delete() {
        if (!this.beyond) this.purge();
    }

    // PURGE a singly form
    public void purge() {
        if (this.individual == null) return;
        this.individual.delUse();
        this.individual.purge();
        this.individual = null;
    }

    // PREPAREs to add an individual to a multiply form
    public void prepare(Individual ind) throws IllegalArgumentException {
        Sort sort = ind.ofSort();
        if (!this.ofSort().equals(sort))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        else if (ind.used() && (sort instanceof AttributeSort))
            ind = (Individual)ind.convert(sort);
    }

    // PREPAREs two multiply forms for an algebraic operation
    public void prepare(Form other) throws IllegalArgumentException {
        if (this == other)
            throw new IllegalArgumentException("Arguments are identical");
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
    }

    // PREPAREs to partition two multiply forms with respect to one another
    public void prepare(Form other, Form common) throws IllegalArgumentException {
        if (this == other)
            throw new IllegalArgumentException("Arguments are identical");
        if (!this.ofSort().equals(other.ofSort()) || !this.ofSort().equals(common.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
    }

    public void traverse(Traversal proc) {
        proc.visit(this.individual);
        if (this.individual.attrDefined())
            this.individual.attribute().traverse(proc);
        proc.leave(this.individual);
    }

    public Element convert(Sort sort) throws IllegalArgumentException {
        return this.convert(sort, null);
    }

    public Form convert(Sort sort, Element assoc) throws IllegalArgumentException {
        SinglyForm result = (SinglyForm)sort.newForm();
        result.setAssociate(assoc);
        result.setIndividual((Individual)this.individual.convert(sort));
        return result;
    }
}
