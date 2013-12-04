/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `MetaForm.java'                                           *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.form;

import cassis.Element;
import cassis.IllegalOverwriteException;
import cassis.struct.Marker;
import cassis.ind.Individual;
import cassis.sort.Sort;
import cassis.sort.DisjunctiveSort;
import cassis.parse.*;
import cassis.visit.SdlVisitor;

/**
 * A <b>meta-form</b> denotes a form of a {@link DisjunctiveSort}. It is composed of
 * zero, one or more forms corresponding the component sorts of the disjunctive sort.
 * The operational behavior of a meta-form is defined by the individual behaviors of
 * the element forms. A meta-form is <i>maximal</i> if it contains at most one form
 * corresponding each component sort and each element form is maximal.
 * <p>
 * The <b>MetaForm</b> class extends on the {@link MultiplyForm} class and specifies
 * the operational behavior of meta-forms in terms of the behaviors of its constituent
 * element forms.
 */
public final class MetaForm extends MultiplyForm {
    
    // constructors
    
    /**
     * Constructs a <b>MetaForm</b> for the specified sort.
     * @param sort a {@link cassis.sort.Sort} object
     */
    public MetaForm(Sort sort) {
        super();
        this.setSort(sort);
    }
    
    /**
     * Returns the <b>count</b> of individuals in this meta-form.
     * This is the sum of the sizes of its element forms.
     * @return an integer
     * @see Form#size()
     */
    public int count() {
        int result = 0;
        
        this.toBegin();
        while (!this.beyond()) {
            result += ((Form) this.current()).size();
            this.toNext();
        }
        return result;
    }
    
    // methods
    
    /**
     * <b>Sets the associate</b> for this meta-form. This associate is also
     * specified as the associate of each element form of this meta-form.
     * @param assoc an {@link Element} object
     * @throws IllegalArgumentException if the associate individual is
     * <tt>null</tt>, or it is of an incompatible sort
     * @throws IllegalOverwriteException if the form is already used, i.e.,
     * belongs to a meta-form
     * @see Form#setAssociate(Individual)
     */
    public void setAssociate(Individual assoc) throws IllegalArgumentException, IllegalOverwriteException {
        super.setAssociate(assoc);
        this.toBegin();
        while (!this.beyond()) {
            Form form = (Form) this.current();
            form.delUse();
            form.setAssociate(assoc);
            form.addUse();
            this.toNext();
        }
    }
    
    /**
     * <b>Duplicates</b> another form into this meta-form.
     * @param other a {@link Form} object
     * @throws IllegalArgumentException if both forms' sorts are not equal or
     * the other form's sort is not part of this meta-form's sort.
     */
    public void duplicate(Form other) {
        if (this == other) return;
        if (!this.ofSort().equals(other.ofSort()) &&
                !other.ofSort().partOf(this.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        
        if (this.nil()) {
            if (!other.isMaximal()) other.maximalize();
            this.maximal = true;
        } else this.maximal = false;
        
        other.toBegin();
        while (!other.beyond()) {
            Form dup = (Form) other.current().ofSort().newForm();
            dup.setAssociate(this);
            dup.duplicate((Form) other.current());
            this.append(dup);
            other.toNext();
        }
    }
    
    /**
     * Converts this meta-form <b>to a string</b>. This results in a list,
     * enclosed with curly brackets, consisting of string representations for
     * each of the element forms, separated by comma's. Each element form's string
     * representation is preceeded by a parentesized string representation of
     * the element's sort name and a separating colon.
     * The lead of this meta-form's element list remains unaltered.
     * @return a <tt>String</tt> object
     * @see Element#toString()
     * @see SdlVisitor#parenthesize(String)
     */
    public String toString() {
        String result = "{";
        if (!this.nil()) {
            Marker pebble = this.elements.getMarker();
            this.toBegin();
            Sort sort = this.current().ofSort();
            result += SdlVisitor.parenthesize(sort.toString()) + ": " + this.current().toString();
            while (!this.atEnd()) {
                this.toNext();
                sort = this.current().ofSort();
                result += ", " + SdlVisitor.parenthesize(sort.toString()) + ": " + this.current().toString();
            }
            this.elements.returnTo(pebble);
        }
        return result + '}';
    }
    
    /**
     * xxx PARSE a string and initialize this form to its value xxx
     */
    public void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() != '{')
            throw new ParseException(reader, "'{' expected");
        while (reader.previewToken() != '}') {
            Sort sort;
            if (reader.newToken() == Parsing.IDENTIFIER)
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
                ((Form) this.current()).parse(reader);
            if (reader.previewToken() == ',') reader.newToken();
        }
        reader.newToken();
    }
    
    /**
     * <b>Concatenates</b> another form to this meta-form. Each element form of the
     * other meta-form is deleted from the other meta-form and appended to this
     * meta-form.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     */
    void concatenate(MultiplyForm other) {
        if ((this == other) || other.nil()) return;
        
        Form form;
        other.toBegin();
        while (!other.beyond()) {
            form = (Form) other.current();
            other.delete();
            form.setAssociate(this);
            super.append(form);
        }
    }
    
    /**
     * <b>Merges</b> this meta-form with another form. Each element form of the
     * other meta-form is deleted from the other meta-form and inserted in order
     * into this meta-form.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     */
    void merge(MultiplyForm other) {
        if ((this == other) || other.nil()) return;
        this.order();
        other.order();
        
        Form form;
        other.toBegin();
        while (!other.beyond()) {
            form = (Form) other.current();
            other.delete();
            form.setAssociate(this);
            super.insertInto(form);
        }
    }
    
    /**
     * <b>Inserts from</b> another form the current element form into this meta-form
     * at its lead.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     */
    void insertFrom(MultiplyForm other) {
        if ((this == other) || other.nil() || other.beyond()) return;
        Form form = (Form) other.current();
        other.delete();
        form.setAssociate(this);
        super.insert(form);
    }
    
    /**
     * <b>Inserts</b> another form into this meta-form at its lead. If the other form
     * is already used, a duplicate form is inserted into this meta-form.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param form a {@link Form} object
     * @see Element#used()
     */
    void insert(Form form) {
        if (form.used()) form = (Form) form.duplicate();
        form.setAssociate(this);
        super.insert(form);
    }
    
    /**
     * <b>Inserts into</b> this meta-form another form in order. If the other form
     * is already used, a duplicate form is inserted into this meta-form.
     * The lead of this meta-form's element list remains unaltered.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param form a {@link Form} object
     * @see Element#used()
     */
    void insertInto(Form form) {
        if (form.used()) form = (Form) form.duplicate();
        form.setAssociate(this);
        super.insertInto(form);
    }
    
    /**
     * <b>Appends</b> another form onto this meta-form. If the other form is already
     * used, a duplicate form is appended onto this meta-form.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param form a {@link Form} object
     * @see Element#used()
     */
    void append(Form form) {
        if (form.used()) form = (Form) form.duplicate();
        form.setAssociate(this);
        super.append(form);
    }
    
    /**
     * Checks whether this meta-form <b>contains</b> another form. This is the case
     * if the other form is a part of any element form of this meta-form.
     * This form is ordered.
     * @param form a {@link Form} object
     * @return a boolean value
     * @see Form#partOf(Form)
     */
    public boolean contains(Form form) {
        this.order();
        
        this.toBegin();
        while (!this.beyond())
            switch (form.compare(this.current())) {
                case LESS:
                    return false;
                case EQUAL:
                    return form.partOf((Form) this.current());
                case GREATER:
                    this.toNext();
                    break;
            }
            return false;
    }
    
    // operational behavior methods
    
    /**
     * <b>Maximalizes</b> this meta-form by adding any element forms that belong
     * to the same sort under the operation of sum, and maximalizing all other
     * element forms. All operations on element forms adhere to the behavior of
     * the respective sort.
     * @see Form#sum(Form)
     * @see Form#maximalize()
     */
    public void maximalize() {
        if (this.isMaximal()) return;
        this.maximal = true;
        if (this.nil()) return;
        this.order();
        
        this.toBegin();
        while (!this.atEnd()) {
            if (this.current().ofSort().lessThan(this.next().ofSort())) {
                ((Form) this.current()).maximalize();
                if (this.current().nil())
                    this.delete();
                else this.toNext();
            } else {
                ((Form) this.next()).sum((Form) this.current());
                this.delete();
            }
        }
        ((Form) this.last()).maximalize();
        if (this.last().nil())
            this.delete();
    }
    
    /**
     * Determines the <b>sum</b> of this meta-form with another form.
     * The sum of two meta-forms is the maximalized meta-form of all element forms
     * from either meta-form. The result is contained in this meta-form.
     * The other form is nil.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are not of the same sort or
     * the other form's sort is not a part of this form's sort
     * @see #maximalize()
     * @see #nil()
     */
    public boolean sum(Form other) {
        if (this == other) return (!this.nil());
        if (!this.ofSort().equals(other.ofSort()) &&
                !other.ofSort().partOf(this.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().ofSort().compare(other.current().ofSort())) {
                case LESS:
                    this.toNext(); break;
                case GREATER:
                    this.insertFrom((MetaForm) other); break;
                case EQUAL:
                    ((Form) this.current()).sum((Form) other.current());
                    this.toNext();
                    other.delete();
                    break;
            }
        }
        if (!other.nil())
            this.concatenate((MetaForm) other);
        return (!this.nil());
    }
    
    /**
     * Determines the <b>difference</b> of this meta-form with another form.
     * The difference of two meta-forms is the maximalized meta-form of all element
     * forms from the first meta-form, upon subtracting, under the operation of
     * difference, any element form of the second meta-form that is of the same sort.
     * If the resulting element form of such subtraction is nil, this element form
     * is removed from the result.
     * The result is contained in this meta-form. The other form is maximalized.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are the same form
     * @see #maximalize()
     * @see #nil()
     */
    public boolean difference(Form other) {
        if (this == other)
            throw new IllegalArgumentException("Arguments are identical");
        
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().ofSort().compare(other.current().ofSort())) {
                case LESS:
                    this.toNext(); break;
                case GREATER:
                    other.toNext(); break;
                case EQUAL:
                    if (((Form) this.current()).difference((Form) other.current()))
                        this.toNext();
                    else
                        this.delete();
                    other.toNext();
                    break;
            }
        }
        return (!this.nil());
    }
    
    /**
     * Determines the <b>product</b> of this meta-form with another form.
     * The product of two meta-forms is the maximalized form of all common parts
     * of element forms from the respective meta-forms. If respective element forms
     * are of the same sort, these are combined under the respective operation of
     * product. If the resulting element form is nil, this element form is removed
     * from the result.
     * The result is contained in this meta-form. The other form is nil.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     * @see #maximalize()
     * @see #nil()
     */
    public boolean product(Form other) {
        if (this == other) return (!this.nil());
        
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().ofSort().compare(other.current().ofSort())) {
                case LESS:
                    this.delete(); break;
                case GREATER:
                    other.delete(); break;
                case EQUAL:
                    if (((Form) this.current()).product((Form) other.current()))
                        this.toNext();
                    else
                        this.delete();
                    other.delete();
                    break;
            }
        }
        while (!this.beyond()) this.delete();
        other.purge();
        return (!this.nil());
    }
    
    /**
     * Determines the <b>symmetric difference</b> of this meta-form with another
     * form. The symmetric difference of two meta-forms is the maximalized form
     * of all element forms from either meta-form minus any common parts from
     * element forms of the respective meta-forms. If respective element forms
     * are of the same sort, these are combined under the respective operation of
     * symmetric difference. If the resulting element form is nil, this element
     * form is removed from the result.
     * The result is contained in this form. The other form is nil.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the result is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are the same form or are not
     * of the same sort or the other form's sort is not a part of this form's sort
     * @see #maximalize()
     * @see #nil()
     */
    public boolean symdifference(Form other) {
        if (this == other)
            throw new IllegalArgumentException("Arguments are identical");
        if (!this.ofSort().equals(other.ofSort()) &&
                !other.ofSort().partOf(this.ofSort()))
            throw new IllegalArgumentException("Argument sorts are not compatible");
        
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().ofSort().compare(other.current().ofSort())) {
                case LESS:
                    this.toNext(); break;
                case GREATER:
                    this.insertFrom((MetaForm) other); break;
                case EQUAL:
                    if (((Form) this.current()).symdifference((Form) other.current()))
                        this.toNext();
                    else
                        this.delete();
                    other.delete();
            }
        }
        if (!other.nil()) this.concatenate((MetaForm) other);
        return (!this.nil());
    }
    
    /**
     * <b>Partitions</b> this meta-form wrt another form.
     * The result is specified in three parts: the first part corresponds to the
     * difference of this meta-form with the other meta-form; the second part to the
     * difference of the other meta-form with this meta-form; and the third part to
     * the product of both meta-forms. The first part of the result is contained
     * in this meta-form, the second part in the other form, and the third part in
     * the common form.
     * @param other a {@link Form} object
     * @param common a {@link Form} object
     * @return <tt>true</tt> if this form is not nil, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if this and the other form are the same form
     * or if all forms are not of the same sort
     * @see #nil()
     */
    public boolean partition(Form other, Form common) {
        if (this == other)
            throw new IllegalArgumentException("Arguments are identical");
        if (!this.ofSort().equals(other.ofSort()) ||
                !this.ofSort().equals(common.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        if (!common.nil()) common.purge();
        
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond()) {
            switch (this.current().ofSort().compare(other.current().ofSort())) {
                case LESS:
                    this.toNext(); break;
                case GREATER:
                    other.toNext(); break;
                case EQUAL:
                    ((MetaForm) common).append(this.current().ofSort().newForm());
                    ((Form) this.current()).partition((Form) other.current(),
                            (Form) ((MetaForm) common).last());
                    if (this.current().nil())
                        this.delete();
                    else this.toNext();
                    if (other.current().nil())
                        other.delete();
                    else other.toNext();
                    if (((MetaForm) common).last().nil()) {
                        ((MetaForm) common).atEnd();
                        common.delete();
                    }
            }
        }
        return (!this.nil());
    }
    
    /**
     * Checks if this meta-form is a <b>part of</b> another form. This is the
     * case if each element form of this form is part of an element form of the other
     * meta-form. Both forms are maximalized.
     * @param other a {@link Form} object
     * @return <tt>true</tt> if the condition holds, <tt>false</tt> otherwise
     * @throws IllegalArgumentException if both forms are not of the same sort or
     * this form's sort is not a part of the other form's sort
     * @see #maximalize()
     */
    public boolean partOf(Form other) {
        if (this == other) return true;
        if (!this.ofSort().equals(other.ofSort()) &&
                !this.ofSort().partOf(other.ofSort()))
            throw new IllegalArgumentException("Arguments sorts are not compatible");
        
        if (!this.isMaximal()) this.maximalize();
        if (!other.isMaximal()) other.maximalize();
        
        this.toBegin();
        other.toBegin();
        while (!this.beyond() && !other.beyond())
            switch (this.current().ofSort().compare(other.current().ofSort())) {
                case LESS:
                    return false;
                case EQUAL:
                    if (!((Form) this.current()).partOf((Form) other.current()))
                        return false;
                    this.toNext();
                case GREATER:
                    other.toNext();
                    break;
            }
            return (this.beyond());
    }
    
    /**
     * <b>Adds</b> a form to this meta-form. The resulting form is maximal.
     * @param form a {@link Form} object
     * @throws IllegalArgumentException if this meta-form's sort does not contain the
     * other form's sort
     */
    public void add(Form form) {
        Sort sort = form.ofSort();
        if (!this.ofSort().contains(sort)) {
            DisjunctiveSort comp = (DisjunctiveSort) this.ofSort();
            comp.toBegin();
            while (!comp.beyond() && !sort.equals(comp.current().base()))
                comp.toNext();
            if (!comp.beyond())
                throw new IllegalArgumentException("Argument sorts do not adhere to containment relationship");
            form = (Form) form.convert(comp.current());
            //form = (Form) form.convert(comp, this.associate()); !!!!!!!!!!!!!!!!!!!!!!
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
                    ((Form) this.current()).sum(form);
                    return;
                case GREATER:
                    this.insert(form);
                    return;
            }
        }
        this.append(form);
    }
    
    /**
     * <b>Adds</b> an individual to this meta-form. The resulting form is maximal.
     * @param ind an {@link cassis.ind.Individual} object
     * @throws IllegalArgumentException if the form's sort does not contain the
     * individual's sort
     */
    public void add(Individual ind) {
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
/*
    public Form convert(Sort sort, Individual assoc) throws IllegalArgumentException {
    MetaForm result = (MetaForm) sort.newForm();
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
//	return result;
//    }
    
}
