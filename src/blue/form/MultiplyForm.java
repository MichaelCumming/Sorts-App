/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `MultiplyForm.java'                                       *
 * written by: Rudi Stouffs                                  *
 * last modified: 25.7.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  MULTIPLYFORM denotes a form of a multiply-associated sort.
//  This means that such a form may contain any number of individuals.
//  Multiply-associated sorts belong to a number of categories
//  (e.g., DiscreteForm, see `SimplySort.java'). Each of these categories
//  is represented as a subclass of this MultiplyForm class.
//  The multiply-form container is implemented as a sorted list
//  (see `SortedList.java').
//  The MultiplyForm class maintains for each form a reference
//  to the attribute sort to which it belongs, and implements some Form
//  interface methods that are identical for all multiply-associated sorts.

package blue.form;

import blue.*;
import blue.struct.List;
import blue.ind.Individual;
import blue.sort.Sort;
import blue.sort.AttributeSort;
import blue.sort.Match;
import blue.io.*;
import blue.proc.Traversal;

public abstract class MultiplyForm extends Form {
    // representation
    List elements;
    boolean maximal;

    // constructors
    MultiplyForm() { this(null); }

    private MultiplyForm(Sort sort) {
        super(sort);
        this.elements = new List();
        this.maximal = true;
    }

    // access methods
    public int size() { return this.elements.length(); }

    public boolean isMaximal() {
        if (!this.elements.sorted()) this.maximal = false;
        return this.maximal;
    }

    public Element current() { return (Element)this.elements.current(); }

    public Element first() { return (Element)this.elements.first(); }

    public Element last() { return (Element)this.elements.last(); }

    public Element next() { return (Element)this.elements.next(); }

    public Element previous() { return (Element)this.elements.previous(); }

    public void toBegin() { this.elements.toBegin(); }

    public void toEnd() { this.elements.toEnd(); }

    public void toNext() { this.elements.toNext(); }

    public void toPrev() { this.elements.toPrev(); }

    public boolean atBegin() { return this.elements.atBegin(); }

    public boolean atEnd() { return this.elements.atEnd(); }

    public boolean beyond() { return this.elements.beyond(); }

    // methods
    // compare two multiply forms for EQUALITY
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MultiplyForm) || !this.ofSort().equals(((Form)other).ofSort())) return false;
        if (!this.isMaximal()) this.maximalize();
        if (!((Form)other).isMaximal()) ((Form)other).maximalize();
        return super.equals((List)other);
    }

    // COMPARE two multiply forms for the purpose of sorting
    // returns one of EQUAL, LESS, GREATER, or FAILED
    public int compare(Thing other) {
        return this.ofSort().compare(((Form)other).ofSort());
    }

    // DUPLICATE this multiply form
    public Element duplicate() {
        MultiplyForm dup = (MultiplyForm)this.ofSort().newForm();
        dup.duplicate(this);
        return dup;
    }

    // DUPLICATE the specified multiply form into this multiply form
    public void duplicate(Form other) {
        if (this == other) return;
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (this.elements.empty()) {
            if (!other.isMaximal()) other.maximalize();
            this.maximal = true;
        } else
            this.maximal = false;
        other.toBegin();
        while (!other.beyond()) {
            this.append((Individual)other.current().convert(this.ofSort()));
            other.toNext();
        }
    }

    // PRINT this multiply form to SDL
    public void print(SdlContext sdl) {
        if (!this.isMaximal()) this.maximalize();
        sdl.setSort(this.ofSort());
        sdl.beginGroup();
        if (!this.elements.empty()) {
            this.toBegin();
            this.current().print(sdl);
            while (!this.elements.atEnd()) {
                sdl.separator();
                this.toNext();
                this.current().print(sdl);
            }
        }
        sdl.endGroup();
    }

    public void visualize(GraphicsContext gc) {
        if (this.nil()) return;
        this.toBegin();
        gc.beginGroup(this.current().getClass(), this.size());
        while (!this.beyond()) {
            this.current().visualize(gc);
            this.toNext();
        }
        gc.endGroup();
    }

    // PARSE a string and initialize this form to its value
    public void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != '{')
            throw new ParseException(reader, "'{' expected");
        while (reader.previewToken() != '}') {
            this.append(Individual.parse(this.ofSort(), reader));
            if (reader.previewToken() == ',') reader.newToken();
        }
        reader.newToken();
    }

    // The following methods overwrite the same methods specified
    // in the List class. These additionally check
    // if both srguments are of the same sort.
    // CONCATENATE the other form to this form
    public void concatenate(MultiplyForm other) {
        if (this == other) return;
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (other.elements.empty()) return;
        if (!other.isMaximal() || (!this.elements.empty() && !other.first().greaterThan(this.last())))
            this.maximal = false;
        this.elements.concatenate(other.elements);
    }

    // MERGE the other form with this form
    public void merge(MultiplyForm other) {
        if (this == other) return;
        if (!other.ofSort().equals(this.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (!this.elements.sorted()) this.elements.sort();
        if (!other.elements.sorted()) this.elements.sort();
        this.maximal = false;
        this.elements.merge(other.elements);
    }

    // INSERT the current individual FROM the other form into this form
    public void insertFrom(MultiplyForm other) {
        if (this == other) return;
        if (other.elements.empty()) return;
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (other.beyond()) other.toBegin();
        if (this.isMaximal() && (!this.elements.atBegin() && !other.current().greaterThan(this.previous())) ||
            (!this.beyond() && !other.current().lessThan(this.current())))
                this.maximal = false;
        this.elements.insertFrom(other.elements);
    }

    // INSERT an individual into this form
    public void insert(Individual ind) {
        Sort sort = ind.ofSort();
        if (!this.ofSort().equals(sort)) {
            Match match = sort.relate(this.ofSort());
            if (!match.isConcordant() || !match.isEquivalent())
                throw new IllegalArgumentException("Arguments are not of similar sorts");
            ind = (Individual)ind.convert(this.ofSort());
        }
        if (this.isMaximal() && (!this.elements.atBegin() && !ind.greaterThan(this.previous())) ||
            (!this.beyond() && !ind.lessThan(this.current())))
                this.maximal = false;
        this.elements.insert(ind);
        ind.addUse();
    }

    // INSERT an individual INTO this form, at the appropriate location
    // the lead position is maintained
    public void insertInto(Individual ind) {
        Sort sort = ind.ofSort();
        if (!this.ofSort().equals(sort)) {
            Match match = sort.relate(this.ofSort());
            if (!match.isConcordant() || !match.isEquivalent())
                throw new IllegalArgumentException("Arguments are not of similar sorts");
            ind = (Individual)ind.convert(this.ofSort());
        }
        this.maximal = false;
        this.elements.insertInto(ind);
        ind.addUse();
    }

    // APPEND an individual to this form
    public void append(Individual ind) {
        Sort sort = ind.ofSort();
        if (!this.ofSort().equals(sort))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (this.isMaximal() && !this.elements.empty() && !ind.greaterThan(this.last()))
            this.maximal = false;
        this.elements.append(ind);
        ind.addUse();
    }

    public void delete() {
        if (this.elements.beyond()) return;
        this.current().delUse();
        this.elements.delete();
    }

    public void deleteNext() {
        if (this.elements.beyond() || this.elements.atEnd()) return;
        this.next().delUse();
        this.elements.deleteNext();
    }

    // Form interface methods
    // The NIL form is the empty form
    public boolean nil() {
        return this.elements.empty();
    }

    // PURGE this form
    public void purge() {
        this.toBegin();
        while (!this.elements.empty()) {
            Element current = (Element)this.elements.current();
            current.delUse();
            this.elements.delete();
            if (!current.used()) current.purge();
        }
    }

    // returns the SUM of two multiply forms
    public boolean sum(Form other) { return this.sum((MultiplyForm)other); }

    private boolean sum(MultiplyForm other) {
        if (this == other) return (!this.elements.empty());
        if (this.getClass() != other.getClass())
            throw new IllegalArgumentException("Arguments are not of the same class");
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (!this.elements.sorted()) this.elements.sort();
        if (!other.elements.sorted()) other.elements.sort();
        if (this.elements.empty())
            this.concatenate(other);
        else if (!other.elements.empty()) {
            // first merge the two forms together
            // then maximalize the result
            this.merge(other);
            //  this.maximalize();
        }
        return (!this.elements.empty());
    }

    // ADD an element to this multiply form
    public void add(Element data) {
        if (data instanceof Individual)
            this.add((Individual)data);
        else
            this.add((Form)data);
    }

    // ADD an individual to this multiply form
    void add(Individual ind) {
        if (!this.ofSort().equals(ind.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (!this.elements.sorted()) this.elements.sort();
        if (ind.used() && (ind.ofSort() instanceof AttributeSort))
            ind = (Individual)ind.convert(this.ofSort());
        this.toBegin();
        this.insertInto(ind);
        //  this.maximalize();
    }

    // ADD a form to this multiply form
    void add(Form form) {
        throw new IllegalArgumentException("This form only accepts individuals");
    }

    // PREPAREs to maximalize a multiply form
    public void prepare() {
        if (!this.elements.sorted()) this.elements.sort();
        this.maximal = true;
    }

    // PREPAREs to add an individual to a multiply form
    public void prepare(Individual ind) {
        if (!this.ofSort().equals(ind.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (!this.isMaximal()) this.maximalize();
    }

    // PREPAREs two multiply forms for an algebraic operation
    public void prepare(Form other) {
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
    }

    // PREPAREs to partition two multiply forms with respect to one another
    public void prepare(Form other, Form common) {
        if (!this.ofSort().equals(other.ofSort()) || !this.ofSort().equals(common.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        if (!((MultiplyForm)common).elements.empty()) ((MultiplyForm)common).purge();
    }

    public void traverse(Traversal proc) {
        if (!this.isMaximal()) this.maximalize();
        this.toBegin();
        while (!this.beyond()) {
            proc.visit((Individual)this.current());
            if (((Individual)this.current()).attrDefined())
                ((Individual)this.current()).attribute().traverse(proc);
            proc.leave((Individual)this.current());
            this.toNext();
        }
    }

    public Element convert(Sort sort) throws IllegalArgumentException {
        return this.convert(sort, null);
    }

    public Form convert(Sort sort, Element assoc) throws IllegalArgumentException {
        MultiplyForm result = (MultiplyForm)sort.newForm();
        if (assoc != null) result.setAssociate(assoc);
        this.toBegin();
        while (!this.beyond()) {
            Element data = ((Individual)this.current()).convert(sort);
            if (data instanceof Individual)
                result.append((Individual)data);
            else
                this.sum((Form)data);
            this.toNext();
        }
        return result;
    }
}
