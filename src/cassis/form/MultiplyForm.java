/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * `MultiplyForm.java'                                       *
 * written by: Rudi Stouffs                                  *
 * last modified: 28.3.05                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cassis.form;

import cassis.*;
import cassis.struct.List;
import cassis.struct.Marker;
import cassis.ind.Individual;
import cassis.ind.Relation;
import cassis.sort.Sort;
import cassis.sort.AttributeSort;
import cassis.convert.Match;
import cassis.convert.Converter;
import cassis.parse.*;
import cassis.visit.ElementVisitor;

/**
 * A <b>multiply-form</b> denotes a form of a multiply-associated sort.
 * Multiply-forms may contain any number of individuals.
 * Different types of multiply-forms are distinguished based on the operational
 * behavior of their forms. A {@link DiscreteForm} specifies a discrete behavior
 * corresponding to a set: any two individuals are either identical or disjoint.
 * An {@link IntervalForm} specifies a behavior of intervals on a one-dimensional
 * axis: if two intervals overlap, these are combined into a single interval.
 * A {@link RelationalForm} applies to (bi-directional) relations. While its
 * behavior is that of a discrete form, relations are ultimately dependent on
 * the individuals these relate and are removed if any of their associate
 * individuals are removed.
 * A multiply-form is <i>maximal</i> if no two individuals combine into a single
 * individual under the respective operational behavior.
 * <p>
 * The <b>MultiplyForm</b> class implements the form container as an ordered
 * {@link List} and maintains for each form a reference to the attribute sort to
 * which it belongs. It provides common implementations for some interface methods.
 */
public abstract class MultiplyForm extends Form {
    
    // representation
    
    /**
     * A {@link List} representation for the elements in a form.
     */
    List elements;
    /**
     * A boolean specifying whether this form is maximal, i.e, canonical.
     */
    boolean maximal;
    
    // constructors
    
    /**
     * Constructs a <b>MultiplyForm</b>. This constructor exists for the purpose of
     * using the <tt>newInstance</tt> method to create a new subclass object.
     */
    MultiplyForm() { this(null); }
    /**
     * Constructs a <b>MultiplyForm</b> for the specified sort.
     * @param sort a {@link Sort} object
     */
    private MultiplyForm(Sort sort) {
        super(sort);
        this.elements = new List();
        this.maximal = true;
    }
    
    // access methods
    
    /**
     * Checks if this multiply-form <b>is maximal</b>. A form is maximal if it is
     * canonical. A minimal requirement for multiply-forms is that the list of
     * elements is ordered.
     * @return a boolean
     */
    public boolean isMaximal() {
        if (!this.elements.ordered()) this.maximal = false;
        return this.maximal;
    }
    /**
     * Returns the <b>size</b> of this multiply-form. This is the length of its
     * element list.
     * @return an integer
     */
    public int size() { return this.elements.length(); }
    
    /**
     * Returns the <b>current</b> element in this multiply-form.
     * @return an {@link Element} object
     */
    public Element current() { return (Element) this.elements.current(); }
    /**
     * Returns the <b>first</b> element in this multiply-form.
     * @return an {@link Element} object
     */
    public Element first() { return (Element) this.elements.first(); }
    /**
     * Returns the <b>last</b> element in this multiply-form.
     * @return an {@link Element} object
     */
    public Element last() { return (Element) this.elements.last(); }
    /**
     * Returns the <b>next</b> element in this multiply-form.
     * This does not alter the lead.
     * @return an {@link cassis.Element} object
     */
    public Element next() { return (Element) this.elements.next(); }
    /**
     * Returns the <b>previous</b> element in this multiply-form.
     * This does not alter the lead.
     * @return an {@link Element} object
     */
    public Element previous() { return (Element) this.elements.previous(); }
    
    /**
     * Sets the lead <b>to the beginning</b> of this multiply-form.
     * The first element becomes current.
     */
    public void toBegin() { this.elements.toBegin(); }
    /**
     * Sets the lead <b>to the end</b> of this multiply-form.
     * The last element becomes current.
     */
    public void toEnd() { this.elements.toEnd(); }
    /**
     * Sets the lead <b>to the next</b> element in this multiply-form.
     * The next element becomes current.
     */
    public void toNext() { this.elements.toNext(); }
    /**
     * Sets the lead <b>to the previous</b> element in this multiply-form.
     * The previous element becomes current.
     */
    public void toPrev() { this.elements.toPrev(); }
    
    /**
     * Checks if the lead is <b>at the beginning</b> of this multiply-form.
     * In this case, the first element is the current element.
     * @return a boolean
     */
    public boolean atBegin() { return this.elements.atBegin(); }
    /**
     * Checks if the lead is <b>at the end</b> of this multiply-form.
     * In this case, the last element is the current element.
     * @return a boolean
     */
    public boolean atEnd() { return this.elements.atEnd(); }
    /**
     * Checks if the lead is <b>beyond</b> the last element in this multiply-form.
     * In this case, there is no current element.
     * @return a boolean
     */
    public boolean beyond() { return this.elements.beyond(); }
    
    // methods
    
    /**
     * <b>Orders</b> the elements in this multiply-form.
     * @see List#order()
     */
    void order() {
        if (!this.elements.ordered()) this.elements.order();
    }
    
    /**
     * Tests whether this multiply-form <b>equals</b> another object.
     * Two multiply-forms are equal if these belong to the same sort and,
     * when maximalized, have equal element lists.
     * @param other the comparison object
     * @return <tt>true</tt> if the comparison holds, <tt>false</tt> otherwise
     * @see List#equals
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MultiplyForm) ||
                !this.ofSort().equals(((Form) other).ofSort())) return false;
        
        if (!this.isMaximal()) this.maximalize();
        if (!((Form) other).isMaximal()) ((Form) other).maximalize();
        
        return this.elements.equals(((MultiplyForm) other).elements);
    }
    
    /**
     * <b>Compares</b> this multiply-form to another thing. The result equals
     * {@link Thing#FAILED} if the argument is not a multiply-form.
     * Otherwise the result is defined by comparing the sorts and element lists
     * of both multiply-forms.
     * @param other a {@link Thing} object
     * @return an integer value equal to one of {@link Thing#EQUAL},
     * {@link Thing#LESS}, {@link Thing#GREATER}, or {@link Thing#FAILED}
     */
    public int compare(Thing other) {
        if (!(other instanceof MultiplyForm)) return FAILED;
        
        int answer = this.ofSort().compare(((Form) other).ofSort());
        if (answer != EQUAL) return answer;
        
        if (!this.isMaximal()) this.maximalize();
        if (!((Form) other).isMaximal()) ((Form) other).maximalize();
        
        return this.elements.compare(((MultiplyForm) other).elements);
    }
    
    /**
     * <b>Duplicates</b> this multiply-form. It returns a new form with
     * the same elements, defined for this form's sort.
     * @return an {@link cassis.Element} object
     * @see Sort#newForm()
     */
    public Element duplicate() {
        MultiplyForm dup = (MultiplyForm) this.ofSort().newForm();
        dup.duplicate(this);
        return dup;
    }
    
    /**
     * <b>Duplicates</b> another form into this multiply-form.
     * @param other a {@link Form} object
     * @throws IllegalArgumentException if both forms are not defined for
     * the same sort
     */
    public void duplicate(Form other) {
        if (this == other) return;
        if (!this.ofSort().equals(other.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        
        if (this.elements.empty()) {
            if (!other.isMaximal()) other.maximalize();
            this.maximal = true;
        } else this.maximal = false;
        
        other.toBegin();
        while (!other.beyond()) {
            Individual ind = (Individual) ((Individual) other.current()).convert(this.ofSort());
            if ((ind instanceof Relation) && (this.associate() != null))
                ((Relation) ind).setAssociate(this.associate());
            this.append(ind);
            other.toNext();
        }
    }
    
    /**
     * Converts this multiply-form <b>to a string</b>. This results in a list,
     * enclosed with curly brackets, consisting of string representations for
     * each of the form's elements, separated by comma's. Each string representation
     * is retrieved wrt this form's associate individual.
     * The lead of this form's element list remains unaltered.
     * @return a <tt>String</tt> object
     * @see Element#toString(Individual)
     */
    public String toString() {
        String result = "{";
        if (!this.elements.empty()) {
            Marker pebble = this.elements.getMarker();
            this.toBegin();
            result += this.current().toString(this.associate());
            while (!this.atEnd()) {
                this.toNext();
                result += ", " + this.current().toString(this.associate());
            }
            this.elements.returnTo(pebble);
        }
        return result + '}';
    }
    
    /**
     * <b>Accepts</b> a visitor to this multiply-form wrt an associate
     * individual. Calls the <tt>visitEnter</tt> method of the visitor.
     * If the result is <tt>true</tt>, sends the visitor to each individual
     * in this multiply-form, if any. Then, calls the <tt>visitLeave</tt>
     * method of the visitor.
     * @param visitor an {@link ElementVisitor} object
     * @param assoc an {@link Individual} object that is the associate
     * to this form (ignored)
     * @see ElementVisitor#visitEnter(cassis.form.Form)
     * @see ElementVisitor#visitLeave(cassis.form.Form)
     */
    public void accept(ElementVisitor visitor, Individual assoc) {
        if (visitor.visitEnter(this) && !this.elements.empty()) {
            Marker pebble = this.elements.getMarker();
            this.toBegin();
            while (!this.beyond()) {
                this.current().accept(visitor, this.associate());
                this.toNext();
            }
            this.elements.returnTo(pebble);
        }
        visitor.visitLeave(this);
    }
    
    /**
     * xxx PARSE a string and initialize this form to its value xxx
     */
    public void parse(ParseReader reader) throws ParseException {
        if (reader.newToken() == SDL.VARIABLE_PREFIX) {
            if (reader.newToken() != Parsing.IDENTIFIER)
                throw new ParseException(reader, "identifier expected");
            String var = reader.tokenString();
            Element elm = this.ofSort().context().profile().retrieve(var);
            this.concatenate((MultiplyForm) Converter.convert(this.ofSort(), elm));
        } else if (reader.token() == '{') {
            while (reader.previewToken() != '}') {
                this.append(Individual.parse(this.ofSort(), reader));
                if (reader.previewToken() == ',') reader.newToken();
            }
            reader.newToken();
        } else throw new ParseException(reader, "'{' or '$' expected");
    }
    
    /**
     * <b>Concatenates</b> another multiply-form to this multiply-form.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     * @see List#concatenate(cassis.struct.List)
     */
    void concatenate(MultiplyForm other) {
        if ((this == other) || other.nil()) return;
        this.elements.concatenate(other.elements);
    }
    
    /**
     * <b>Merges</b> this multiply-form with another multiply-form.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     */
    void merge(MultiplyForm other) {
        if ((this == other) || other.nil()) return;
        this.elements.merge(other.elements);
    }
    
    /**
     * <b>Inserts from</b> another multiply-form the current element into this
     * multiply-form at its lead.
     * No checks are made to see if both forms' sorts are compatible or if the result
     * is maximal.
     * @param other a {@link MultiplyForm} object
     */
    void insertFrom(MultiplyForm other) {
        if ((this == other) || other.nil() || other.beyond()) return;
        this.elements.insertFrom(other.elements);
    }
    
    /**
     * <b>Inserts</b> an element into this multiply-form at its lead.
     * No checks are made to see if both elements' sorts are compatible or if the
     * result is maximal.
     * @param element an {@link Element} object
     */
    void insert(Element element) {
        /*
    Sort sort = ind.ofSort();
    if (!this.ofSort().equals(sort)) {
        Match match = sort.relate(this.ofSort());
        if (!match.isConcordant() || !match.isEquivalent())
        throw new IllegalArgumentException("Arguments are not of similar sorts");
        ind = (Individual) ind.convert(this.ofSort());
    }
         */
        this.elements.insert(element);
        element.addUse();
    }
    
    /**
     * <b>Inserts into</b> this multiply-form an element in order.
     * The lead of this form's element list remains unaltered.
     * No checks are made to see if both elements' sorts are compatible or if the
     * result is maximal.
     * @param element an {@link cassis.Element} object
     */
    void insertInto(Element element) {
        /*
    Sort sort = ind.ofSort();
    if (!this.ofSort().equals(sort)) {
        Match match = sort.relate(this.ofSort());
        if (!match.isConcordant() || !match.isEquivalent())
        throw new IllegalArgumentException("Arguments are not of similar sorts");
        ind = (Individual) ind.convert(this.ofSort());
    }
         */
        this.elements.insertInto(element);
        element.addUse();
    }
    
    /**
     * <b>Appends</b> an element onto this multiply-form.
     * No checks are made to see if both elements' sorts are compatible or if
     * the result is maximal.
     * @param element an {@link cassis.Element} object
     */
    void append(Element element) {
        this.elements.append(element);
        element.addUse();
    }
    
    /**
     *
     */
    public void delete() {
        if (this.elements.beyond()) return;
        this.current().delUse();
        this.elements.delete();
    }
    
    /**
     *
     */
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
            Element current = (Element) this.elements.current();
            current.delUse();
            this.elements.delete();
            if (!current.used()) current.purge();
        }
    }
    
    // ADD a element to this multiply form
    public void add(Element data) {
        if (data instanceof Individual)
            this.add((Individual) data);
        else this.add((Form) data);
    }
    
    // ADD an individual to this multiply form
    void add(Individual ind) {
        if (!this.ofSort().equals(ind.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        
        this.order();
        
        if (ind.used() && (ind.ofSort() instanceof AttributeSort))
            ind = (Individual) ind.convert(this.ofSort());
        this.toBegin();
        this.insertInto(ind);
        //  this.maximalize();
    }
    
    // ADD a form to this multiply form
    void add(Form form) {
        if (!this.ofSort().equals(form.ofSort()))
            throw new IllegalArgumentException("Arguments are not of the same sort");
        this.concatenate((MultiplyForm) form);
        //throw new IllegalArgumentException("This form only accepts individuals");
    }
/*
    // PREPAREs to maximalize a multiply form
    void prepare() {
    if (!this.elements.sorted()) this.elements.sort();
    this.maximal = true;
    }
 
    // PREPAREs to add an individual to a multiply form
    void prepare(Individual ind) {
    if (!this.ofSort().equals(ind.ofSort()))
        throw new IllegalArgumentException("Arguments are not of the same sort");
 
    if (!this.isMaximal()) this.maximalize();
    }
 
    // PREPAREs two multiply forms for an algebraic operation
    void prepare(Form other) {
    if (!this.ofSort().equals(other.ofSort()))
        throw new IllegalArgumentException("Arguments are not of the same sort");
 
    if (!this.isMaximal()) this.maximalize();
    if (!other.isMaximal()) other.maximalize();
    }
 
    // PREPAREs to partition two multiply forms with respect to one another
    void prepare(Form other, Form common) {
    if (!this.ofSort().equals(other.ofSort()) ||
        !this.ofSort().equals(common.ofSort()))
        throw new IllegalArgumentException("Arguments are not of the same sort");
 
    if (!this.isMaximal()) this.maximalize();
    if (!other.isMaximal()) other.maximalize();
    if (!((MultiplyForm) common).elements.empty()) ((MultiplyForm) common).purge();
    }
 */
/*
    public Form convert(Sort sort, Individual assoc) throws IllegalArgumentException {
    MultiplyForm result = (MultiplyForm) sort.newForm();
    if (assoc != null) result.setAssociate(assoc);
    this.toBegin();
    while (!this.beyond()) {
        Element data = ((Individual) this.current()).convert(sort);
        if (data instanceof Individual)
        result.append(data);
        else this.sum((Form) data);
        this.toNext();
    }
    return result;
    }
 */
}
