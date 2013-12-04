/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `MetaForm.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 04.8.00                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

//  A METAFORM is a collection of forms (see `Form.java'),
//  each of a different simple sort. The composite sort
//  (see `CompositeSort.java') to which this metaform belongs,
//  is a combination under sum of these simple sorts.
//  The functionality of the MetaForm class corresponds mainly
//  to the functionality of the multiply-associated sorts
//  (see `MultiplyForm.java'): a metaform is a (normalized)
//  sorted list of forms with the algebraic operations specified.

package blue.form;

import blue.Element;
import blue.IllegalOverwriteException;
import blue.struct.Marker;
import blue.ind.Individual;
import blue.sort.Sort;
import blue.sort.DisjunctiveSort;
import blue.sort.Match;
import blue.io.*;

public final class MetaForm extends MultiplyForm {
    // constructors
    public MetaForm() {
        super();
    }

    public MetaForm(Sort sort) {
        super();
        this.setSort(sort);
    }

    // access methods
    private Form currentForm() { return (Form)this.elements.current(); }

    private Form nextForm() { return (Form)this.elements.next(); }

    private Form lastForm() { return (Form)this.elements.last(); }

    // return the COUNT of individuals in this metaform
    public int count() {
        int result = 0;
        this.toBegin();
        while (!this.beyond()) {
            result += this.currentForm().size();
            this.toNext();
        }
        return result;
    }

    // Thing interface methods
    public void setAssociate(Element assoc) throws IllegalArgumentException, IllegalOverwriteException {
        super.setAssociate(assoc);
        this.toBegin();
        while (!this.beyond()) {
            Form form = (Form)this.current();
            form.delUse();
            form.setAssociate(this);
            form.addUse();
        }
    }

    // DUPLICATE the specified metaform into this metaform
    public void duplicate(MultiplyForm other) {
        if ((this.ofSort() != other.ofSort()) && !other.ofSort().partOf(this.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (this.elements.empty()) {
            if (!this.isMaximal()) this.maximalize();
            this.maximal = true;
        } else
            this.maximal = false;
        other.toBegin();
        while (!other.beyond()) {
            Form dup = (Form)other.current().ofSort().newForm();
            dup.setAssociate(this);
            dup.duplicate((Form)other.current());
            this.append(dup);
            other.toNext();
        }
    }

    public String toString() {
        String result = "{";
        if (!this.elements.empty()) {
            Marker pebble = this.elements.getMarker();
            this.toBegin();
            Sort sort = this.currentForm().ofSort();
            result += SdlContext.parenthesize(sort.toString()) + ": " + this.currentForm().toString();
            while (!this.elements.atEnd()) {
                this.toNext();
                sort = this.currentForm().ofSort();
                result += ", " + SdlContext.parenthesize(sort.toString()) + ": " + this.currentForm().toString();
            }
            this.elements.returnTo(pebble);
        }
        return result + '}';
    }

    public void visualize(GraphicsContext gc) {
        if (this.nil()) return;
        if (this.elements.length() == 1) {
            this.first().visualize(gc);
            return;
        }
        this.toBegin();
        gc.beginGroup(DisjunctiveSort.class, this.elements.length());
        while (!this.beyond()) {
            gc.satellite(this.current().ofSort().toString());
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
            Sort sort;
            if (reader.newToken() == Parser.IDENTIFIER)
                sort = this.ofSort().context().sortOf(reader.tokenString());
            else if (reader.token() == '(') {
                sort = this.ofSort().context().retrieve(reader);
                if (reader.token() != ')')
                    throw new ParseException(reader, "')' expected");
            } else
                throw new ParseException(reader, "Expected an identifier or a parenthesized expression, followed by ':'");
            if (sort == null)
                throw new ParseException(reader, "This sort is unrecognized");
            if (!this.ofSort().contains(sort))
                throw new ParseException(reader, "Incompatible sort specified");
            if (reader.newToken() != ':')
                throw new ParseException(reader, "':' expected");
            this.toBegin();
            while (!this.beyond() && !sort.equals(this.current().ofSort()))
                this.toNext();
            if (this.beyond()) {
                Form form = sort.newForm();
                form.setAssociate(this);
                form.parse(reader);
                this.add(form);
            } else
                this.currentForm().parse(reader);
            if (reader.previewToken() == ',') reader.newToken();
        }
        reader.newToken();
    }

    // SortedList interface methods
    // The following methods overwrite the methods specified
    // in the SortedList class. These additionally check
    // upon the relationship of the metaforms' sorts.
    // CONCATENATE the other metaform to this metaform
    public void concatenate(MultiplyForm other) {
        if (this == other)
            throw new IllegalArgumentException("Arguments are identical");
        if ((this.ofSort() != other.ofSort()) && !other.ofSort().partOf(this.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (other.elements.empty()) return;
        if (!other.isMaximal() || (!this.elements.empty() && !other.first().greaterThan(this.last())))
            this.maximal = false;
        // this.elements.concatenate(other.elements);
        Form form;
        other.toBegin();
        while (!other.beyond()) {
            form = (Form)other.current();
            other.elements.delete();
            form.setAssociate(this);
            this.elements.append(form);
        }
    }

    // MERGE the other metaform with this metaform
    public void merge(MultiplyForm other) {
        if (this == other)
            throw new IllegalArgumentException("Arguments are identical");
        if ((this.ofSort() != other.ofSort()) && !other.ofSort().partOf(this.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (!this.elements.sorted()) this.elements.sort();
        if (!other.elements.sorted()) this.elements.sort();
        // this.elements.merge(other.elements);
        Form form;
        other.toBegin();
        while (!other.beyond()) {
            form = (Form)other.current();
            other.elements.delete();
            form.setAssociate(this);
            this.elements.insertInto(form);
        }
    }

    // INSERT the current form FROM the other metaform into this metaform
    public void insertFrom(MultiplyForm other) {
        if (this == other) return;
        if (other.elements.empty()) return;
        if (other.beyond()) other.toBegin();
        if (!this.ofSort().contains(other.current().ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (this.isMaximal() && (!this.elements.atBegin() && !other.current().greaterThan(this.previous())) ||
            (!this.beyond() && !other.current().lessThan(this.current())))
                this.maximal = false;
        Form form = (Form)other.current();
        other.elements.delete();
        form.setAssociate(this);
        this.elements.insert(form);
    }

    // INSERT a form into this metaform
    public void insert(Form form) {
        if (!this.ofSort().contains(form.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (form.used()) form = (Form)form.duplicate();
        if (this.isMaximal() && (!this.elements.atBegin() && !form.greaterThan(this.previous())) ||
            (!this.beyond() && !form.lessThan(this.current())))
                this.maximal = false;
        form.setAssociate(this);
        this.elements.insert(form);
        form.addUse();
    }

    // INSERT a form INTO this metaform, at the appropriate location
    // the lead position is maintained
    public void insertInto(Form form) {
        if (!this.ofSort().contains(form.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (form.used()) form = (Form)form.duplicate();
        this.maximal = false;
        form.setAssociate(this);
        this.elements.insertInto(form);
        form.addUse();
    }

    // APPEND an individual to this form
    public void append(Form form) {
        if (!this.ofSort().contains(form.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (form.used()) form = (Form)form.duplicate();
        if (this.isMaximal() && !this.elements.empty() && !form.greaterThan(this.last()))
            this.maximal = false;
        form.setAssociate(this);
        this.elements.append(form);
        form.addUse();
    }

    // check whether this metaform CONTAINS the specified form
    public boolean contains(Form form) {
        if (!this.elements.sorted()) this.elements.sort();
        this.toBegin();
        while (!this.beyond())
            switch (form.compare(this.current())) {
                case LESS:
                    return false;
                case EQUAL:
                    if (form.partOf(this.currentForm()))
                        return true;
                    else
                        return false;
                case GREATER:
                    this.toNext();
                    break;
            }
        return false;
    }

    // Form interface methods
    // MAXIMALIZE this metaform
    public void maximalize() {
        //	if (this.isMaximal()) return;
        if (!this.elements.sorted()) this.elements.sort();
        this.maximal = true;
        if (this.elements.empty()) return;
        this.toBegin();
        while (!this.elements.atEnd()) {
            if (this.current().lessThan(this.next())) {
                this.currentForm().maximalize();
                if (this.current().nil())
                    this.delete();
                else
                    this.toNext();
            } else {
                this.nextForm().sum(this.currentForm());
                this.delete();
            }
        }
        this.lastForm().maximalize();
        if (this.last().nil())
            this.delete();
    }

    // return the SUM of these two metaforms
    // the other metaform is purged
    public boolean sum(Form other) {
        if (this == other) return true;
        if ((this.ofSort() != other.ofSort()) && !other.ofSort().partOf(this.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    this.insertFrom((MetaForm)other);
                    break;
                case EQUAL:
                    this.currentForm().sum((Form)other.current());
                    this.toNext();
                    other.delete();
                    break;
            }
        }
        if (!((MetaForm)other).elements.empty())
            this.concatenate((MetaForm)other);
        return (!this.elements.empty());
    }

    // return the DIFFERENCE of these two metaforms
    // the other metaform remains unaltered
    public boolean difference(Form other) {
        if (this == other)
            throw new IllegalArgumentException("Arguments are identical");
        if ((this.ofSort() != other.ofSort()) && !this.ofSort().partOf(other.ofSort()) &&
            !other.ofSort().partOf(this.ofSort()))
                throw new IllegalArgumentException("Argument sorts are not compatible");
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    other.toNext();
                    break;
                case EQUAL:
                    if (this.currentForm().difference((Form)other.current()))
                        this.toNext();
                    else
                        this.delete();
                    other.toNext();
                    break;
            }
        }
        return (!this.elements.empty());
    }

    // returns the PRODUCT of two discrete forms
    // the other metaform is purged
    public boolean product(Form other) {
        if (this == other) return true;
        if ((this.ofSort() != other.ofSort()) && !this.ofSort().partOf(other.ofSort()) &&
            !other.ofSort().partOf(this.ofSort()))
                throw new IllegalArgumentException("Argument sorts are not compatible");
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.delete();
                    break;
                case GREATER:
                    other.delete();
                    break;
                case EQUAL:
                    if (this.currentForm().product((Form)other.current()))
                        this.toNext();
                    else
                        this.delete();
                    other.delete();
                    break;
            }
        }
        while (!this.beyond()) this.delete();
        other.purge();
        return (!this.elements.empty());
    }

    // returns the SYMmetric DIFFERENCE of two discrete forms
    // the other metaform is purged
    public boolean symdifference(Form other) {
        if (this == other) {
            this.purge();
            return false;
        }
        if ((this.ofSort() != other.ofSort()) && !other.ofSort().partOf(this.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    this.insertFrom((MetaForm)other);
                    break;
                case EQUAL:
                    if (this.currentForm().symdifference((Form)other.current()))
                        this.toNext();
                    else
                        this.delete();
                    other.delete();
            }
        }
        if (!((MetaForm)other).elements.empty()) this.concatenate((MetaForm)other);
        return (!this.elements.empty());
    }

    // PARTITION two discrete forms with respect to one another
    public boolean partition(Form other, Form common) {
        if (this == other) {
            ((MetaForm)common).concatenate(this);
            return false;
        }
        if ((this.ofSort() != other.ofSort()) && !this.ofSort().partOf(other.ofSort()) &&
            !other.ofSort().partOf(this.ofSort()))
                throw new IllegalArgumentException("Argument sorts are not compatible");
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        if (!((MetaForm)common).elements.empty()) common.purge();
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().compare(other.current())) {
                case LESS:
                    this.toNext();
                    break;
                case GREATER:
                    other.toNext();
                    break;
                case EQUAL:
                    ((MetaForm)common).append(this.current().ofSort().newForm());
                    this.currentForm().partition((Form)other.current(), ((MetaForm)common).lastForm());
                    if (this.current().nil())
                        this.delete();
                    else
                        this.toNext();
                    if (other.current().nil())
                        other.delete();
                    else
                        other.toNext();
                    if (((MetaForm)common).last().nil()) {
                        ((MetaForm)common).elements.atEnd();
                        common.delete();
                    }
            }
        }
        return (!this.elements.empty());
    }

    // Check if this metaform is a PART OF the other
    public boolean partOf(Form other) {
        if (this == other) return true;
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) this.maximalize();
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond())
            switch (this.current().compare(other.current())) {
                case LESS:
                    return false;
                case EQUAL:
                    if (!this.currentForm().partOf((Form)other.current()))
                        return false;
                    this.toNext();
                case GREATER:
                    other.toNext();
                    break;
            }
        return (this.beyond());
    }

    // ADD a form to this metaform
    void add(Form form) {
        Sort sort = form.ofSort();
        if (!this.ofSort().contains(sort)) {
            DisjunctiveSort comp = (DisjunctiveSort)this.ofSort();
            comp.toBegin();
            while (!comp.beyond() && !sort.equals(comp.current().base()))
                comp.toNext();
            if (!comp.beyond())
                throw new IllegalArgumentException("Argument sorts do not adhere to containment relationship");
            form = (Form)form.convert(comp.current());
            form = (Form)form.convert(comp, this.associate());
            this.sum(form);
            return;
        }
        if (!this.isMaximal()) this.maximalize();
        this.toBegin();
        while (!this.beyond()) {
            switch (this.current().compare(form)) {
                case LESS:
                    this.toNext();
                    break;
                case EQUAL:
                    this.currentForm().sum(form);
                    return;
                case GREATER:
                    this.insert(form);
                    return;
            }
        }
        this.append(form);
    }

    // ADD an individual to this metaform
    void add(Individual ind) {
        Sort sort = ind.ofSort();
        if (!this.ofSort().contains(sort)) {
/*
	    Match match = sort.relate(this.ofSort());
	    if (match.isConcordant() && match.isEquivalent()) {
		ind = ind.convert(this.ofSort());
	    } else if (match.isEquivalent() && !sort.named()) {
		sort = match.matchingSort(sort);
		ind = ind.convert(sort);
	    } else
*/

            throw new IllegalArgumentException("Argument sorts do not adhere to containment relationship");
        }
        Form form = ind.ofSort().newForm();
        form.setAssociate(this);
        form.add(ind);
        this.add(form);
    }

    public Form convert(Sort sort, Element assoc) throws IllegalArgumentException {
        MetaForm result = (MetaForm)sort.newForm();
        result.setAssociate(assoc);
        if (this.ofSort() == sort) {
            result.duplicate(this);
            return result;
        }
        System.out.println("new sort: " + sort.toString());
        System.out.println("old form: " + this.toString());

/*	this.toBegin();
	while (!this.beyond()) {
	    result.append(((Form) this.current()).convert(...));
	    this.toNext();
	} */

        return result;
    }
}
